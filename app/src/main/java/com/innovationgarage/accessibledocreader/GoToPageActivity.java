package com.innovationgarage.accessibledocreader;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import newage.rs.filereader.R;


public class GoToPageActivity extends AppCompatActivity {
    EditText GoToPageEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to_page);

        GoToPageEditText=findViewById(R.id.goToPageEditText);
    }

    //this function is called after pressing the gotoPage button
    public void goToPage(View view){
        String st=GoToPageEditText.getText().toString();
        if(st.matches("\\d+")){
            int i=Integer.parseInt(st);
            if(MainActivity.pageLst.isEmpty()){
                ttSpeakThisClass("At first open a file");
                Toast.makeText(getApplicationContext(),"AT FIRST OPEN A FILE ",Toast.LENGTH_SHORT).show();
            }
            else if(MainActivity.pageLst.size()>=i && i>0){
                MainActivity.pageIndex=i-1;
                MainActivity m=new MainActivity();
                m.loadFromGoToPage();
                finish();
            }
            else if(i==0){
                ttSpeakThisClass("Zero is a invalid page number");
                Toast.makeText(getApplicationContext(),"ZERO IS INVALID PAGE NUMBER",Toast.LENGTH_SHORT).show();
            }
            else{
                ttSpeakThisClass("exceeds maximum page number");
                Toast.makeText(getApplicationContext(),String.valueOf(i)+" EXCEEDS PAGE NUMBER",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            ttSpeakThisClass("enter a number in the box");
            Toast.makeText(getApplicationContext(),"ENTER A NUMBER IN THE BOX",Toast.LENGTH_SHORT).show();
        }


    }

    public void ttSpeakThisClass(String speak){
        MainActivity.tts.speak(speak, TextToSpeech.QUEUE_FLUSH,null);
    }

}
