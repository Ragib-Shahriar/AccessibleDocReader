package com.innovationgarage.accessibledocreader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.ColorInt;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import newage.rs.filereader.R;

public class MainActivity extends AppCompatActivity {

    static TextToSpeech tts;
    static boolean hasStarted=false;//After starting hasStarted will be false. Rotating the screen calls onCreate() method.

    static Boolean readMode=false;
    static Boolean goLeft=false,goRight=false;
    static int wordMode=0;//0 means default,1 means read word, 2 means read letter,3 means paragraph
    static String chosenMode="default";

    static boolean transitionToNextPageInReadMode=false;
    static Activity activity;

    static TextView textView;//The main textview
    static TextView textTitle;//App title
    static Spinner spinner;
    ScrollView scrollView;

    static ProgressBar progressBar;//When loading file

    static int pageIndex=0;//page location
    static int wordIndex=0;//word location in sentence
    static int paraIndex=0;//para location in page
    static int sentenceIndex=0;//sentence location in a page
    static int letterIndex=0;

    static String TAG="------------------";
    static String pageInfo="";//Docx files paper size will be in it.

    public static List<List> pageLst=new ArrayList<List>();//contains pages
    static List<String> sentenceLst=new ArrayList<String>();//contains sentences in a page
    static List<String> wordLst=new ArrayList<String>();//words in a sentence
    static List<String> paragraphLst=new ArrayList<String>();
    static List<String> letterLst=new ArrayList<String>();
    static int fileType=-1;//0=docx,1=txt

    //this are for textView touch listener variables
    final static float STEP = 200;
    float mRatio = 1.0f;
    int mBaseDist;
    float mBaseRatio;

    static Context con;
    Waiter waiter;

    static boolean stopButtonPressed=false;
    String lastMode="";

    String KEEP_SCREEN_ON="keep the screen on";
    String currentOpenedFileName="";
    static BookMarkPage bookMarkPage;

    static TextView pageNoTextView;

    static String speakLanguage="bn_BD";

    static int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        con=getApplicationContext();
        bookMarkPage=new BookMarkPage(getApplicationContext());
        color=R.color.greenColor;

//        Intent  i=new Intent(getApplicationContext(),DeveloperPage.class);;
//        startActivity(i);
        verifyStoragePermissions(this);//verifing the storage permission

        myClass.tagINIT();//myClass needs the tags to be initialized;

        //Setting up the custom Action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);


        textTitle=findViewById(R.id.text_title);
        textView=findViewById(R.id.textView);
        scrollView=findViewById(R.id.scrollView);

        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.INVISIBLE);

        pageNoTextView=findViewById(R.id.pageNoTextView);

        textTitle.setText("AccessibleDocReader");

        //for setting the textView everytime the activity is created
        if(hasStarted){
           if(!pageLst.isEmpty()) {
               String st=myClass.paraViewPage(pageLst.get(pageIndex)).toString();
               textView.setText(st);
           }
        }
        else{
            //Initializing TTS
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(new Locale("bn_BD"));



//                       Set<Locale> ln=tts.getAvailableLanguages();
//                       //et.setText(ln.toString());
//
//                       boolean banglaFound=false;
//                       Locale lc=null;
//                       for (Locale s:ln) {
//                           if (s.toString().contains("bn_BD")) {
//                               lc=s;
//                               tts.setLanguage(lc);
//                               banglaFound=true;
//                           }
//                       }
//
//                       if(!banglaFound){
//                           tts.speak("BANGLA LANGUAGE NOT FOUND",TextToSpeech.QUEUE_FLUSH,null);
//                       }
                    }
                }
            });

        }
        waiter=new Waiter();
        waiter.execute("hi there");

        //For zooming, setting up the onTouchListener
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    int action = event.getAction();
                    int pureaction = action & MotionEvent.ACTION_MASK;
                    if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                        mBaseDist = getDistance(event);
                        mBaseRatio = mRatio;
                    } else {
                        float delta = (getDistance(event) - mBaseDist) / STEP;
                        float multi = (float) Math.pow(2, delta);
                        mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
                        textView.setTextSize(mRatio + 13);
                    }
                }
                return true;
            }
        });

        //will only be false at start
        hasStarted=true;

        //setting up the dropDown button ,along with listener
        spinner=findViewById(R.id.spinner4);
        ArrayAdapter<CharSequence> arrayAdapter=ArrayAdapter.createFromResource(this,R.array.planets_array,R.layout.spinner_item_selected);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenMode((int)parent.getItemIdAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //This function is used for zoom of the main textview.
    static int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    //Picking the file
    public void filePick(View view){
        tts.stop();
        ttSpeak("Browse Button Selected");
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1)
                .withFilter(Pattern.compile("(.*\\.docx$)|(.*\\.txt$)")) // Filtering files and directories by file name using regexp
                //.withFilterDirectories(true) // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            ReadTask rd = new ReadTask();
            rd.execute(filePath);//Giving the file path to AsyncTask
            Log.d(TAG, "FILE PICKED SUCCESSFULLY");
        }
    }




    class ReadTask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... strings) {
            //setting progressBar visible
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });

            if(strings[0].substring(strings[0].length()-5,strings[0].length()).toLowerCase().equals(".docx")) {
                fileType = 0;//0 means docx file

                ttSpeakThisClass("loding");
                pageLst = loadDocFile(strings[0]);
            }
            else if(strings[0].substring(strings[0].length()-4,strings[0].length()).toLowerCase().equals(".txt")) {
                fileType = 1;//1 means txt file
                ttSpeakThisClass("loding");
                pageLst=loadTxtFile(strings[0]);
            }
            else
                fileType=-1;//-1 means no openable file found

            // after getting the content setting progressBar invisible
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

            //if file is opened setting up the necessary opening conditions
            if(fileType!=-1 && !pageLst.isEmpty()) {
                currentOpenedFileName=strings[0];
                //setting the variables as default
                //setting up the indexes
                int savedBookMark=bookMarkPage.getBookMark(currentOpenedFileName);

                if(savedBookMark!=-1 && pageLst.size()>savedBookMark){
                    pageIndex=savedBookMark;
                }
                else {
                    pageIndex=0;
                }


                sentenceIndex=0;wordIndex=0;paraIndex=0;letterIndex=0;
                if(pageIndex<pageLst.size())
                    paragraphLst=myClass.separateByParagraph(pageLst.get(pageIndex));
                if(paraIndex<paragraphLst.size())
                    sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));
                if(sentenceIndex<sentenceLst.size())
                    wordLst=myClass.separateByWord(sentenceLst.get(sentenceIndex));
                if(wordIndex<wordLst.size())
                    letterLst=myClass.separateByLetter(wordLst.get(wordIndex));

                //setting the indicators
                wordMode=0;readMode=false;goLeft=false;goRight=false;
                stopButtonPressed=false;
                //set the word button to DEFAULT


                int pageNum=pageIndex+1;

                ttSpeakThisClass("page "+ pageNum +" by " + pageLst.size());//Speaking the page number
                loadFileToTextView();////Loading the file to textview
            }
            return null;
        }

        public void ttSpeakThisClass(String speak){
            tts.speak(speak, TextToSpeech.QUEUE_FLUSH,null);
        }
    }


   private void setPageNo(final int pageNum, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pageNoTextView.setText(""+pageNum+"/"+size);
            }
        });
    }


    public static List<List> loadDocFile(String path){
        String file="------COULDN'T LOAD-------";
        List<String> contentLst=new ArrayList<String>();
        List<List> tempLst=new ArrayList<List>();

        file=myClass.getZIPXML(path);//getting the XML of the .docx file
        contentLst=myClass.decodeXML(file);//decoding the .docx file to get necessary contents.

        if(!contentLst.isEmpty()) {
            pageInfo = contentLst.get(0);//first element in contentLst is the size of the page.
            contentLst.remove(0);//after getting pageinfo, removing the first element of contentLst
        }
        tempLst=myClass.separateByPage(contentLst);//Separating the pages from content
        return tempLst;// returning pages as list
    }

    public static List<List> loadTxtFile(String path){
        List<List> tempLst=new ArrayList<List>();
        String content=myClass.readFromFile(path);// reading the text file
        tempLst=myClass.separateByPageForTextFile(content);//separating the text file by page, by 10 lines per page.
        return tempLst;//returning the page;
    }

    //Loads the current page to textView
    public  void  loadFileToTextView(){
        setPageNo(pageIndex+1,pageLst.size());
        Log.d(TAG,"loadFileToTextView");
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       //paraViewPage retuns the element of pageLst as a string
//                        String st=myClass.paraViewPage(pageLst.get(pageIndex)).toString();
	      //this is the coloring funtion
                        viewText();

//                        String txt=showText();
//                        textView.setText("hi there");
//                        textView.append(getColoredString(getApplicationContext(), "Hi!", ContextCompat.getColor(getApplication(), R.color.highlight)));
                        savePageAsBookMark(currentOpenedFileName,pageIndex);
                    }
                });
            }
        });
        th.start();
        Log.i(TAG,"loadFileToTextView COMPLETED");
    }

//this fuction colors the text, step by step
    private void viewText() {
        if(pageIndex<pageLst.size())
            paragraphLst=myClass.separateByParagraph(pageLst.get(pageIndex));
        if(paraIndex<paragraphLst.size())
            sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));
        if(sentenceIndex<sentenceLst.size())
            wordLst=myClass.separateByWord(sentenceLst.get(sentenceIndex));
        if(wordIndex<wordLst.size())
            letterLst=myClass.separateByLetter(wordLst.get(wordIndex));

        boolean viewWithColor=false;
        int tempParaIndex=paraIndex,tempSentenceIndex=sentenceIndex,tempWordIndex=wordIndex,tempLetterIndex=letterIndex;
        int i=0,j=0,k=0,l=0;

        if(chosenMode.equals("paragraph") && paraIndex>0 && goRight){
            tempParaIndex--;
        }
        else if(((chosenMode.equals("default")&& goRight)||readMode) && sentenceIndex>0 ){
            tempSentenceIndex--;
        }
        else if(chosenMode.equals("word") && wordIndex>0 && goRight){
            tempWordIndex--;
        }
        else if(chosenMode.equals("letter") && letterIndex>0 && goRight){
            tempLetterIndex--;
        }

        if(chosenMode.equals("paragraph") && tempParaIndex>=0 && tempParaIndex<paragraphLst.size()){
            viewWithColor=true;
        }
        else if((chosenMode.equals("default")|| readMode) && tempSentenceIndex>=0 && tempSentenceIndex<sentenceLst.size()){
            viewWithColor=true;
        }
        else if(chosenMode.equals("word") && tempWordIndex>=0 && tempWordIndex<wordLst.size()){
            viewWithColor=true;
        }
        else if(chosenMode.equals("letter") && tempLetterIndex>=0 && tempLetterIndex<letterLst.size()){
            viewWithColor=true;
        }

        textView.setText("");

        if(viewWithColor){
            for(i=0;i<tempParaIndex;i++){
                textView.append(paragraphLst.get(i));
                textView.append("\n");
            }
            if(chosenMode.equals("paragraph")){
                textView.append(getColoredString( paragraphLst.get(i++), ContextCompat.getColor(getApplication(),color)));
                textView.append("\n");
            }
            else {
                i++;
                for(j=0;j<tempSentenceIndex;j++){
                    textView.append(sentenceLst.get(j));
                }
                if(chosenMode.equals("default")){
                    textView.append(getColoredString(sentenceLst.get(j++), ContextCompat.getColor(getApplication(), color)));//ContextCompat.getColor(getApplication(), color)));
                }
                else{
                    j++;
                    for(k=0;k<tempWordIndex;k++){
                        textView.append(wordLst.get(k));
                    }
                    if(chosenMode.equals("word")){
                        textView.append(getColoredString(wordLst.get(k++), ContextCompat.getColor(getApplication(), color)));
                    }
                    else {
                        k++;
                        for(l=0;l<tempLetterIndex;l++){
                            textView.append(letterLst.get(l));
                        }
                        if(chosenMode.equals("letter")){
                            textView.append(getColoredString( letterLst.get(l++), ContextCompat.getColor(getApplication(), color)));
                        }
                        for(;l<letterLst.size();l++){
                            textView.append(letterLst.get(l));
                        }
                    }
                    for(;k<wordLst.size();k++){
                        textView.append(wordLst.get(k));
                    }
                }
                for(;j<sentenceLst.size();j++){
                    textView.append(sentenceLst.get(j));
                }
                textView.append("\n");
            }
            for(;i<paragraphLst.size();i++){
                textView.append(paragraphLst.get(i));
                textView.append("\n");
            }
        }
        else{
            textView.setText(myClass.paraViewPage(pageLst.get(pageIndex)).toString());
        }
    }

    //This function is called from GoToPageActivity, when page number is entered
    //GoToPageActivity sets the pageIndex of the destination page.
    public void loadFromGoToPage() {
        sentenceIndex = 0;wordIndex = 0;paraIndex=0;letterIndex=0;
        speakOutCurrentPageNo();//speaks out the current page number
        loadFileToTextView();
    }

    //Go to next page when next page button is clicked
    public void nextPage(View view){
        if(pageLst.size()>0) {
            if (pageIndex < pageLst.size() - 1) {
                pageIndex++;
                paraIndex=0;sentenceIndex = 0;wordIndex = 0;letterIndex=0;
                loadFileToTextView();
                scrollView.scrollTo(0,0);
                speakOutCurrentPageNo();
            } else if (pageIndex == pageLst.size() - 1) {
                ttSpeak("No more page available");
            }
        }
    }
    //Go to previous page when prev page button is clicked
    public void prevPage(View view){
        if(pageLst.size()>0) {
            if (pageIndex > 0) {
                pageIndex--;
                paraIndex=0;sentenceIndex = 0;wordIndex = 0;letterIndex=0;
                loadFileToTextView();
                scrollView.scrollTo(0,0);
                speakOutCurrentPageNo();
            } else if (pageIndex == 0) {
                ttSpeak("Reading at page one");
            }
        }
    }

    //when read button is clicked
    public void read(View view){
        ttSpeak("Read Mode Selected");
        if(pageLst.size()>0) {
            read();//It will take some time for the tts to speak "Read Mode Selected".
            //when tts will finish , the runnable in read() will call the function readAccordingly(). Which will keep reading it self.
        }
    }

    //this will call the readAccordingly().
    //in readMode readAccordingly() will keep reading itself by a handler.
    public void read(){
        stopButtonPressed=false;
        waiter.cancel(true);
        Handler h=new Handler();
        Runnable r=new Runnable() {
            @Override
            public void run() {
                if(!tts.isSpeaking()) {
                    readMode=true;

                    //pressing right button in default wordMode will increment the value of sentenceIndex
                    // after reading the current sentence
                    //To read from the current sentence the sentenceIndex mus be decremented.
//                    if((chosenMode.equals("default") && goRight) && sentenceIndex>0){
//                        sentenceIndex--;
//                    }
//
//                    if(chosenMode.equals("paragraph") && goRight){
//                        if(paraIndex>0)
//                            paraIndex--;
//                        if(sentenceIndex>0)
//                            sentenceIndex--;
//                    }

                    goLeft=false;goRight=false;
                    readAccordingly();
                }
                else{
                    read();
                }
            }
        };
        h.postDelayed(r,500);
    }

    public void stop(View view){
        ttSpeak("Reading Stopped");
        //After reading a sentence readMode increment the sentenceIndex.
        //So here after stopping we are decrementing the sentenceIndex.So readMode will start from the same line again.
        if(pageLst.size()>0) {
            waiter.cancel(true);

//            if(readMode && sentenceIndex>0){
//                sentenceIndex--;
//            }
            //goRight=false;goLeft=false;
            readMode = false;
            stopButtonPressed=true;
        }
    }

    public void chosenMode(int mode){
        if(pageLst.size()>0){
            if(waiter.isCancelled())
                waiter.cancel(true);


           lastMode=chosenMode;

            if(mode==0){chosenMode="default";ttSpeak("Default mode selected");wordIndex=0;letterIndex=0;}
            else if(mode==1){chosenMode="paragraph";ttSpeak("Paragraph mode selected");sentenceIndex=0;wordIndex=0;letterIndex=0;}
            else if(mode==2){chosenMode="word";ttSpeak("Word mode selected");letterIndex=0;}
            else if(mode==3){chosenMode="letter";ttSpeak("Letter Mode selecterd");}


            if(readMode && sentenceIndex>0)
                sentenceIndex--;

            if(!readMode) {
                if (lastMode.equals("default") && (chosenMode.equals("word")||chosenMode.equals("letter")) && goRight){
                    if(sentenceIndex>0)
                        sentenceIndex--;
                }
                else if (lastMode.equals("word")&& chosenMode.equals("letter") && goRight) {
                    if(wordIndex>0)
                        wordIndex--;
                }
            }
            if(!readMode && lastMode.equals("paragraph") && !chosenMode.equals("paragraph")){
                if(paraIndex>0 && goRight) {
                    paraIndex--;
                }
                if(sentenceIndex>0 ){
                    sentenceIndex--;
                }
            }

            stopButtonPressed=false;
            readMode = false;//pressing wordMode will stop reading automatically
            goLeft = false;
            goRight = false;
        }
    }

    //when left button is pressed
    public void left(View view){
        if(pageLst.size()>0) {
            if(goRight){
                if(chosenMode.equals("default")){
                    sentenceIndex--;
                }
                else if(chosenMode.equals("paragraph")){
                    paraIndex--;
                }
                else if(chosenMode.equals("word")){
                    wordIndex--;
                }
                else if(chosenMode.equals("letter")){
                    letterIndex--;
                }
            }

            goLeft=true;
            goRight=false;
            readMode=false;
            readAccordingly();//this is the function where,what to read is sorted out.
        }
    }
    //when right button is pressed
    public void right(View view){
        if(pageLst.size()>0) {
            //if(readMode){
               // if(chosenMode.equals("default") && sentenceIndex > 0)sentenceIndex--;//decrementing the sentenceIndex to readFrom the same sentence.
                //else if(chosenMode.equals("paragraph") && paraIndex > 0) paraIndex--;
            //}
//            if(stopButtonPressed){
//                stopButtonPressed=false;
//                if (chosenMode.equals("default") && goRight && sentenceIndex > 0)
//                    sentenceIndex--;
//
//                if(chosenMode.equals("paragraph") && goRight){
//                    if(paraIndex>0)
//                        paraIndex--;
//                    if(sentenceIndex>0)
//                        sentenceIndex--;
//                }
//            }
            if(goLeft){
                if(chosenMode.equals("default")){
                    sentenceIndex++;
                }
                else if(chosenMode.equals("paragraph")){
                    paraIndex++;
                }
                else if(chosenMode.equals("word")){
                    wordIndex++;
                }
                else if(chosenMode.equals("letter")){
                    letterIndex++;
                }
            }

            goLeft=false;
            goRight=true;
            readMode=false;
            readAccordingly();
        }
    }

    //when Top Of File Button is pressed
    public void topOfTheFile(View view){
        ttSpeak("Top Of The File Buttom Selected");
        if(pageLst.size()>0) {
            pageIndex=0;sentenceIndex=0;wordIndex=0;paraIndex=0;letterIndex=0;
            readMode=false;//goLeft=false;goRight=false;
            goRight=true;//this is needed to show colored text
            loadFileToTextView();
            scrollView.scrollTo(0,0);
        }
    }

    //when Button Of File Button is pressed
    public void bottomOfTheFile(View view){
        ttSpeak("Bottom Of The File Buttom Selected");
        if(pageLst.size()>0){
            pageIndex=pageLst.size()-1;
            sentenceIndex=0;wordIndex=0;paraIndex=0;letterIndex=0;
            readMode=false;//goLeft=false;goRight=false;
            goRight=true;//this is needed to show colored text
            loadFileToTextView();
            scrollView.scrollTo(0,0);
        }
    }

    //in readMode when transitioning to nextpage, the handler in the function will keep checking if reading has finished
    //in the current page. After finishing reading in the current page. it will go to the next page
    private void transitToNextPageInReadMode(){
        final Handler h=new Handler();
        final Runnable run=new Runnable() {
            @Override
            public void run() {
                if (!tts.isSpeaking()) {//while looping when speaking is finished load the next page
                    if (pageIndex< pageLst.size() - 1) {
                        transitionToNextPageInReadMode=false;
                        pageIndex++;
                        sentenceIndex = 0;wordIndex = 0;paraIndex=0;letterIndex=0;
                        speakOutCurrentPageNo();

                        loadFileToTextView();
                        scrollView.scrollTo(0,0);
                    }
                } else {
                    transitToNextPageInReadMode();//calls it self.
                }
            }
        };
        h.postDelayed(run,300);//loopin in 300ms
    }

    //this will load the next page setting up the indexes.
    private void transitToPageNormally(){
        if(pageIndex<pageLst.size()-1){
                pageIndex++;
        }

        sentenceIndex = 0;wordIndex = 0;paraIndex=0;letterIndex=0;
        speakOutCurrentPageNo();
        loadFileToTextView();
        scrollView.scrollTo(0,0);
    }


//this function determines what to read, considering on which button is pressed
/**
 * this is the main part where what to read is sorted out
 * main thing to consider is, it has 5 lists
 * they are:
 * 1.pageLst(contains the pages of the file)
 * 2.paragraphLst(contains the paragraphs of a single page), eg. paragraphLst=myClass.separateByParagraph(pageLst.get(pageIndex))
 * 3.sentenceLst (contains the sentences of the paragraphLst)
 * 4.wordLst (contains the words of the sentenceLst)
 * 5.letterLst (contains the letters of the wordLst)
 *
 * the indexes pageIndex,paraIndex,sentenceIndex,wordIndex,letterIndex keeps track of where to,and increment or
 * decrement where to read next
 */


    public  void readAccordingly() {


        if(!pageLst.isEmpty()) {
            waiter.cancel(true);
            //getting the the lists from pageList
            if(pageIndex<pageLst.size())
                paragraphLst=myClass.separateByParagraph(pageLst.get(pageIndex));
            if(paraIndex<paragraphLst.size())
                sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));
            if(sentenceIndex>=0 && sentenceIndex<sentenceLst.size())
                wordLst=myClass.separateByWord(sentenceLst.get(sentenceIndex));
            if(wordIndex<wordLst.size())
                letterLst=myClass.separateByLetter(wordLst.get(wordIndex));



            //if readMode is off
            if(!readMode) {
                if(goRight){
                    while (paraIndex<paragraphLst.size() && paragraphLst.get(paraIndex).equals("")){
                        paraIndex++;
                    }
                    if(paraIndex<paragraphLst.size())
                        sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));

                    while (sentenceIndex<sentenceLst.size() && sentenceLst.get(sentenceIndex).equals(" ")){
                        sentenceIndex++;
                    }
                    if(sentenceIndex<sentenceLst.size())
                        wordLst=myClass.separateByWord(sentenceLst.get(sentenceIndex));

                    while (wordIndex<wordLst.size() && wordLst.get(wordIndex).equals(" ")){
                        wordIndex++;
                    }
                    while (letterIndex<letterLst.size() && letterLst.get(letterIndex).equals(" ")){
                        letterIndex++;
                    }
//                    if(pageIndex<pageLst.size())
//                        paragraphLst=myClass.separateByParagraph(pageLst.get(pageIndex));


                    if(wordIndex<wordLst.size())
                        letterLst=myClass.separateByLetter(wordLst.get(wordIndex));
                    //in choseMode when right button is pressed
                    boolean someThingToReadWasFound=false;//this variable records if somethings was spoken or not
                    if(chosenMode.equals("default")){//in default mode, reading next line when right button is pressed
                        if (sentenceIndex < sentenceLst.size()) {//check if sentence avaliable in the current page
                            ttSpeak(sentenceLst.get(sentenceIndex));
                            sentenceIndex++;
                            loadFileToTextView();
                            someThingToReadWasFound=true;
                        }
                    }
                    else if(chosenMode.equals("word")){
                        if (wordIndex < wordLst.size()) {//check if more word available in the word list
                            ttSpeak(wordLst.get(wordIndex));
                            wordIndex++;
                            loadFileToTextView();
                            someThingToReadWasFound=true;
                        }
                    }
                    else if(chosenMode.equals("letter")){
                        if(letterIndex< letterLst.size()){//check if more letter available in the letter list
                            ttSpeak(letterLst.get((letterIndex)));
                            letterIndex++;
                            loadFileToTextView();
                            someThingToReadWasFound=true;
                        }
                    }
                    else if(chosenMode.equals("paragraph")){//check if more paragraph available in the paragraph list
                        if(paraIndex<paragraphLst.size()){
                            sentenceIndex=0;
                            ttSpeakParagraph();
//                            ttSpeak(paragraphLst.get(paraIndex));
                            paraIndex++;
                            loadFileToTextView();
                            someThingToReadWasFound=true;
                        }
                    }


                    int currentChosenMode=5;
                    //here is complex trick used to shorten the code
                    //currentChosenMode is selected by the hierarchy of the lists
                    //eg paragraph>default>word>letter
                    if(chosenMode.equals("letter"))currentChosenMode=1;
                    else if(chosenMode.equals("word"))currentChosenMode=2;
                    else if(chosenMode.equals("default"))currentChosenMode=3;
                    else if(chosenMode.equals("paragraph"))currentChosenMode=4;

                    //-->after finishing letter list it will search for the next word list to read from.
                    //if not found it will search for the word list in the next sentence
                    //if not fount it will search for the word list in the next sentence of the next paragraph
                    //if not found it will search for the word list in the next senctenc of the next paragraph of the next page
                    //-->word mode,default mode, paragraph mode will also act in the same hierarchical way.
                    if(!someThingToReadWasFound){//check if nothing was spoken
                        if(currentChosenMode<=1 && wordIndex<wordLst.size()-1){
                            wordIndex++;
                            letterIndex=0;
                            readAccordingly();
                        }
                        else if(currentChosenMode<=2 && sentenceIndex<sentenceLst.size()-1){
                            sentenceIndex++;
                            letterIndex=0;wordIndex=0;
                            readAccordingly();
                        }
                        else if(currentChosenMode<=3 &&  paraIndex<paragraphLst.size()-1){
                            paraIndex++;
                            letterIndex=0;wordIndex=0;sentenceIndex=0;
                            readAccordingly();
                        }else if (currentChosenMode<=4 &&pageIndex < pageLst.size() - 1) {// else check if any more page available
                            transitToPageNormally();//then go to next page;
                        } else {// else speak no more page available
                            tts.speak("No more page available", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                } else if (goLeft) {
                    boolean inside=false;
                    try {
                        while (paraIndex>0 && paragraphLst.get(paraIndex).equals("")){
                            paraIndex--;
                            inside=true;
                        }
                    }
                    catch (Throwable e){

                    }

//                    if(paraIndex<paragraphLst.size()) {
//                        //boolean flag=false;
//                        while (paraIndex > 0 && paragraphLst.get(paraIndex).equals("")) {
//                            paraIndex--;
//                         //   flag=true;
//                        }
////                        if(flag){
////                            sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));
////                            sentenceIndex=sentenceLst.size();
////                        }
//                    }
////                    sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));
//                    if(sentenceIndex<sentenceLst.size())
//                    while (sentenceIndex>0 && sentenceLst.get(sentenceIndex).equals(" ")){
//                        sentenceIndex--;
//                    }
//                    if(wordIndex<wordLst.size())
//                    while (wordIndex>0 && wordLst.get(wordIndex).equals(" ")){
//                        wordIndex--;
//                    }
//                    if(letterIndex<letterLst.size())
//                    while (letterIndex>0 && letterLst.get(letterIndex).equals(" ")){
//                        letterIndex--;
//                    }
                    if(pageIndex<pageLst.size())
                        paragraphLst=myClass.separateByParagraph(pageLst.get(pageIndex));
                    if(paraIndex<paragraphLst.size())
                        sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));
                    if(sentenceIndex>0 && sentenceIndex<sentenceLst.size())
                        wordLst=myClass.separateByWord(sentenceLst.get(sentenceIndex));
                    if(wordIndex<wordLst.size())
                        letterLst=myClass.separateByLetter(wordLst.get(wordIndex));

                    if(inside){
                        sentenceIndex=sentenceLst.size();
                    }
                    // the algorith for going left is almost the same
                    boolean someThingToReadWasFound=false;
                    if(chosenMode.equals("default")){//in default mode, reading next line when right button is pressed
                        if (sentenceIndex > 0) {//check if sentence avaliable in the current page
                            sentenceIndex--;
                            ttSpeak(sentenceLst.get(sentenceIndex).toString());
                            loadFileToTextView();
                            someThingToReadWasFound=true;
                        }
                    }
                    else if(chosenMode.equals("word")){
                        if (wordIndex > 0) {//check if more word available in the word list
                            wordIndex--;
                            ttSpeak(wordLst.get(wordIndex).toString());
                            loadFileToTextView();
                            someThingToReadWasFound=true;
                        }
                    }
                    else if(chosenMode.equals("letter")){
                        if(letterIndex > 0){
                            letterIndex--;
                            ttSpeak(letterLst.get((letterIndex)));
                            loadFileToTextView();
                            someThingToReadWasFound=true;
                        }
                    }
                    else if(chosenMode.equals("paragraph")){
                        if(paraIndex>0){
                            paraIndex--;
                            //ttSpeak(paragraphLst.get(paraIndex));
                            sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));
                            sentenceIndex=0;
                            ttSpeakParagraph();
                            loadFileToTextView();
                            someThingToReadWasFound=true;
                        }
                    }


                    int currentChosenMode=5;

                    if(chosenMode.equals("letter"))currentChosenMode=1;
                    else if(chosenMode.equals("word"))currentChosenMode=2;
                    else if(chosenMode.equals("default"))currentChosenMode=3;
                    else if(chosenMode.equals("paragraph"))currentChosenMode=4;

                    //if nothing was spoken then depending upon the hierarchy of letter,word,default,paragraph
                    //necessary action will be taken to go to the previous letter or word of sentence or paragraph
                    if(!someThingToReadWasFound){
                        if(currentChosenMode<=1 && wordIndex>0){
                            wordIndex--;
                            letterLst=myClass.separateByLetter(wordLst.get(wordIndex));
                            letterIndex=letterLst.size()-1;

                            if(chosenMode.equals("letter"))letterIndex++;

                            readAccordingly();
                        }
                        else if(currentChosenMode<=2 && sentenceIndex>0){
                            sentenceIndex--;
                            wordLst=myClass.separateByWord(sentenceLst.get(sentenceIndex));
                            wordIndex=wordLst.size()-1;
                            letterLst=myClass.separateByLetter(wordLst.get(wordIndex));
                            letterIndex=letterLst.size()-1;

                            if(chosenMode.equals("letter"))letterIndex++;
                            else if(chosenMode.equals("word"))wordIndex++;

                            readAccordingly();
                        }
                        else if(currentChosenMode<=3 &&  paraIndex>0){
                            paraIndex--;
                            sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));
                            sentenceIndex=sentenceLst.size()-1;
                            wordLst=myClass.separateByWord(sentenceLst.get(sentenceIndex));
                            wordIndex=wordLst.size()-1;
                            letterLst=myClass.separateByLetter(wordLst.get(wordIndex));
                            letterIndex=letterLst.size()-1;

                            if(chosenMode.equals("letter"))letterIndex++;
                            else if(chosenMode.equals("word"))wordIndex++;
                            else if(chosenMode.equals("default"))sentenceIndex++;

                            readAccordingly();
                        }else if (currentChosenMode<=4 && pageIndex >0) {// else check if any more page available
                            pageIndex--;
                            paragraphLst=myClass.separateByParagraph(pageLst.get(pageIndex));
                            paraIndex=paragraphLst.size()-1;
                            sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));
                            sentenceIndex=sentenceLst.size()-1;
                            wordLst=myClass.separateByWord(sentenceLst.get(sentenceIndex));
                            wordIndex=wordLst.size()-1;
                            letterLst=myClass.separateByLetter(wordLst.get(wordIndex));
                            letterIndex=letterLst.size()-1;

                            if(chosenMode.equals("letter"))letterIndex++;
                            else if(chosenMode.equals("word"))wordIndex++;
                            else if(chosenMode.equals("default"))sentenceIndex++;
                            else if(chosenMode.equals("paragraph"))paraIndex++;

                            speakOutCurrentPageNo();
                            loadFileToTextView();
                            scrollView.scrollTo(0,0);
                        } else {// else speak no more page available
                            tts.speak("No more page available", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                }
            }else if (readMode) {
                while (paraIndex<paragraphLst.size() && paragraphLst.get(paraIndex).equals("")){
                    paraIndex++;
                    sentenceIndex=0;wordIndex=0;letterIndex=0;
                }
//                while (sentenceIndex<sentenceLst.size() && sentenceLst.get(sentenceIndex).equals(" ")){
//                    sentenceIndex++;
//                    wordIndex=0;letterIndex=0;
//                }
//                while (wordIndex<wordLst.size() && wordLst.get(wordIndex).equals(" ")){
//                    wordIndex++;
//                    letterIndex=0;
//                }
//                while (letterIndex<letterLst.size() && letterLst.get(letterIndex).equals(" ")){
//                    letterIndex++;
//                }
                if(pageIndex<pageLst.size())
                    paragraphLst=myClass.separateByParagraph(pageLst.get(pageIndex));
                if(paraIndex<paragraphLst.size())
                    sentenceLst=myClass.separateByLine(paragraphLst.get(paraIndex));
                if(sentenceIndex>=0 && sentenceIndex<sentenceLst.size())
                    wordLst=myClass.separateByWord(sentenceLst.get(sentenceIndex));
                if(wordIndex<wordLst.size())
                    letterLst=myClass.separateByLetter(wordLst.get(wordIndex));

                if (!transitionToNextPageInReadMode && !tts.isSpeaking()) {//transitionToNextPageInReadMode will be true on when going to next page
                    if (sentenceIndex < sentenceLst.size()) {//check if sentence available in the current page
                        ttSpeak(sentenceLst.get(sentenceIndex));
                        sentenceIndex++;
                        loadFileToTextView();
                    }else if(paraIndex<paragraphLst.size()-1){
                        paraIndex++;
                        sentenceIndex=0;
                    }
                    else if (pageIndex < pageLst.size() - 1) {//check if page is available
                        transitionToNextPageInReadMode = true;//after going to next page via transitToNextPageInReadMode()
                                                                // transitionToNextPageInReadMode will be false again.
                        transitToNextPageInReadMode();
                    } else {//else Speak "No more page available"
                        tts.speak("No more page available", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
                //this is the runnable which loops until documents end is reached.
                if (!(pageIndex == pageLst.size() - 1 && sentenceIndex > sentenceLst.size() - 1)) {
                    waiter=new Waiter();
                    waiter.execute("readmode");
//                    Handler h = new Handler();
//                    Runnable run = new Runnable() {
//                        @Override
//                        public void run() {
//                            readAccordingly();
//                        }
//                    };
//                    h.postDelayed(run, 100);
                }
            }
        }
    }

    private void ttSpeakParagraph() {
        if(sentenceIndex<sentenceLst.size()) {
            ttSpeak(sentenceLst.get(sentenceIndex));
            sentenceIndex++;
            waiter=new Waiter();
            waiter.execute("paragraph");
        }
    }


    public class Waiter extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... s) {
            while(tts.isSpeaking()){
                try{Thread.sleep(10);}catch (Exception e){}
            }
            return s[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("paragraph") && (goRight || goLeft)){
                ttSpeakParagraph();
            }
            else if(s.equals("readmode")){
                readAccordingly();
            }
            else if(s.equals(KEEP_SCREEN_ON)){
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            //Toast.makeText(con,s,Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Speak completed");
        }
    }

    //this will speak out the current page number.
    public  static  void speakOutCurrentPageNo(){
        if(!pageLst.isEmpty()) {
            String st = "Page " + (pageIndex + 1) + " by " + pageLst.size();
            tts.speak(st, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    //will speak the given string
    public void ttSpeak(String speak){
        tts.setLanguage(new Locale(speakLanguage));
        tts.speak(speak, TextToSpeech.QUEUE_FLUSH,null);
        getHeight();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        waiter.cancel(true);
        waiter=new Waiter();
        waiter.execute(KEEP_SCREEN_ON);
    }

    double prevTop=0;
    public void getHeight(){
        if(!pageLst.isEmpty()) {
            double heightTop = 0,heightBottom=0;
            for (int i = 0; i <= paraIndex && paragraphLst.size()>i; i++) {
                String text = paragraphLst.get(i);
                TextPaint textPaint = textView.getPaint();
                int boundedWidth = textView.getWidth();
                StaticLayout layout = new StaticLayout(text, textPaint, boundedWidth , Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if(i!=paraIndex)
                  heightTop=heightTop+layout.getHeight();
                //heightBottom=heightBottom+layout.getHeight();
            }
            heightBottom=heightTop;
            for(int i=0;i<=sentenceIndex && i<sentenceLst.size();i++){
                String text = sentenceLst.get(i);
                TextPaint textPaint = textView.getPaint();
                int boundedWidth = textView.getWidth();
                StaticLayout layout = new StaticLayout(text, textPaint, boundedWidth , Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if(i!=sentenceIndex)
                    heightTop=heightTop+layout.getHeight();
                heightBottom=heightBottom+layout.getHeight();
            }
            scrollView.scrollTo(0,(int) (heightBottom-scrollView.getHeight()));
//            if(goRight || readMode) {
//                    scrollView.scrollTo(0, (int) Math.ceil(heightBottom-scrollView.getHeight()));
//                }
// //           }
//            if(goLeft){
//                scrollView.scrollTo(0,(int) (heightBottom-scrollView.getHeight()));
//            }
//            prevTop=heightTop;
        }
    }


    public void savePageAsBookMark(String fileName,int pageNum){
       bookMarkPage.putBookMark(fileName,pageNum);
    }






    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.about || id==R.id.usermanual || id==R.id.gotopage || id==R.id.developerPage || id==R.id.settings){
           tts.stop();
           if(readMode && sentenceIndex>0)
                 sentenceIndex--;//decrementing for readMode
            readMode=false;//stop reading automatically
            Intent i=new Intent();
            switch (id){
                case R.id.about:
                    i=new Intent(this,AboutPage.class);
                    startActivity(i);break;
                case R.id.usermanual:
                    i=new Intent(this,UserManual.class);
                    startActivity(i);break;
                case R.id.gotopage:
                    //i=new Intent(getApplicationContext(),GoToPageActivity.class);
                    doGoToPage(); break;
                case R.id.developerPage:
                    i=new Intent(getApplicationContext(),DeveloperPage.class);
                    startActivity(i);
                    break;
                case R.id.settings:
                    i=new Intent(getApplicationContext(),SettingActivity.class);
                    startActivity(i);
                    break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void doGoToPage() {
        if(MainActivity.pageLst.isEmpty()){
            ttSpeak("At first open a file");
            Toast.makeText(getApplicationContext(),"AT FIRST OPEN A FILE ",Toast.LENGTH_SHORT).show();
            return;
        }


        AlertDialog.Builder alert = new AlertDialog.Builder(this,  R.style.MyDialogTheme);
        alert.setTitle("Enter Page Number");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        input.setBackgroundColor(Color.GRAY);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for OK button here
                try {
                    String nmp = input.getText().toString();
                    int pn = Integer.parseInt(nmp);
                    System.out.println("pn="+pn+" pagelst.size()="+pageLst.size());
                    if(pn<1 || pn > pageLst.size()){
                        System.out.println("exceeds maximum page");
                        ttSpeak("exceeds maximum page number");
                        Toast.makeText(getApplicationContext(),String.valueOf(pn)+" EXCEEDS PAGE NUMBER",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pageIndex=pn-1;
                    loadFromGoToPage();
                }catch (Exception ex) {

                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ttSpeak("Input Cancelled.");
            }
        });
        alert.show();
    }

    public Spannable getColoredString(CharSequence text, int color) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new BackgroundColorSpan(color), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(),"Resumed",Toast.LENGTH_SHORT);
        if(!pageLst.isEmpty())
            loadFileToTextView();
    }
}