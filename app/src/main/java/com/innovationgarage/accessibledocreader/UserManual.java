package com.innovationgarage.accessibledocreader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import newage.rs.filereader.R;

public class UserManual extends AppCompatActivity {

    //all the vies are load

    static TextView txtTitle,textViewBrowse,textViewTopOfFile,textViewBottomOfFile,textViewWord,textViewRead,textViewLeft,textViewRight,textViewStop,textViewPrev,textViewNext;
    static TextView txtViewGenUse;
    static String strToRead;
    MainActivity mainActivity;
    String KEEP_SCREEN_ON="keep the screen on";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);

        mainActivity=new MainActivity();

        //view are initialized
        txtTitle=findViewById(R.id.txtTitle);
        textViewBrowse=findViewById(R.id.textViewBrowse);
        textViewTopOfFile=findViewById(R.id.textViewTopOfFIle);
        textViewBottomOfFile=findViewById(R.id.textViewBottomOfFile);
        textViewWord=findViewById(R.id.textViewWord);
        textViewRead=findViewById(R.id.textViewRead);
        textViewLeft=findViewById(R.id.textViewLeft);
        textViewRight=findViewById(R.id.textViewRight);
        textViewStop=findViewById(R.id.textViewStop);
        textViewPrev=findViewById(R.id.textViewPrev);
        textViewNext=findViewById(R.id.textViewNext);

        txtViewGenUse=findViewById(R.id.textViewGenUse);

        setStringForMan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.tts.stop();
    }

    void setStringForMan(){

        String setTitle="USER MANUAL";
        String setGenUse="The bright green area is the text field. Upon loading a file using the BROWSE button, the text " +
                "field will show the contents of the file. Currently .txt and .docx files are openable.\n" +
                "The text field is zoomable.\n" +
                "The detailed information of the buttons are given bellow."
                ;
        String setBrowse="Browse : The Blue colored button is the Browse button. Use it to open file.";
        String setWord="Sentence, Word, Letter : The Pink colored button is the mode selector. Use it to select default mode,word mode or letter mode." +
                "\nDEFAULT Mode : After selecting default mode, by pressing the right button next sentence will be read and by pressing the left button previous sentence will be read" +
                "\nWORD Mode : After selecting word mode, by pressing the right button next word will be read and by pressing the left button previous word will be read" +
                "\nLETTER Mode : After selecting letter mode, by pressing the right button next letter will be read and by pressing the left button previous letter will be read";
        String setTopOfFile="Top Of File : The Aqua or Light Blue colored button is the Top of file button. Use it to go to the top of the file";
        String setBottomOfFile="Bottom Of File : The Yello colored button is the bottom of file button. Use it to go to the bottom of the file";
        String setRead="Read : The Green colored button is the read button. Use it to automatically read the file";
        String setStop="Stop : The Red colored button is the stop button. Use it to stop reading";
        String setLeft="Left : The Violet colored button is the left button. Use it to read the left sentence or left word or left letter " +
                "depending on the selected word mode";
        String setRight="Right : The Silver colored button is the right button. Use it to read the right sentence or right word or right letter " +
                "depending on the selected word mode";
        String setPrev="Previous page : The Orange colored button is the previous page button. Use it to go to the previous page";
        String setNext="Next page : The Brown colored button is the next page button. Use it to go to the next page";

        strToRead=setTitle+" "+setGenUse+" buttons description "+setBrowse+" "+setWord+" "+setTopOfFile+" "+setBottomOfFile+" "+setRead+" "+setStop+" "+setLeft+" "+setRight+" "+setPrev+" "+setNext;

        txtTitle.setText(setTitle);
        txtViewGenUse.setText(setGenUse);

        textViewBrowse.setText(setBrowse);
        textViewWord.setText(setWord);
        textViewTopOfFile.setText(setTopOfFile);
        textViewBottomOfFile.setText(setBottomOfFile);
        textViewRead.setText(setRead);
        textViewStop.setText(setStop);
        textViewLeft.setText(setLeft);
        textViewRight.setText(setRight);
        textViewPrev.setText(setPrev);
        textViewNext.setText(setNext);
    }

    public void ttSpeak(String speak){
        MainActivity.tts.speak(speak, TextToSpeech.QUEUE_FLUSH,null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Waiter waiter=new Waiter();
        waiter.execute(KEEP_SCREEN_ON);
    }

    public class Waiter extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... s) {
            while(MainActivity.tts.isSpeaking()){
                try{Thread.sleep(10);}catch (Exception e){}
            }
            return s[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals(KEEP_SCREEN_ON)){
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            //Toast.makeText(con,s,Toast.LENGTH_SHORT).show();
        }
    }
    //read the whole description
    public void readMan(View view){
        ttSpeak(strToRead);
    }

    public void stopMan(View view){
       MainActivity.tts.stop();
    }

}
