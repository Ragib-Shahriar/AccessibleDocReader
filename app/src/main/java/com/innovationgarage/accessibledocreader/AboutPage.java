package com.innovationgarage.accessibledocreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import newage.rs.filereader.R;

public class AboutPage extends AppCompatActivity {

    static String TAG="------------------";
    public static TextView aboutTextView;
    public static String st;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"In the activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        aboutTextView=findViewById(R.id.textView2);
        st ="Innovation Garage Limited is a distinctive effort toward developing barrier-free, accessible solution as a contribution " +
                "to digital aria. Our mission is to come up with ideas and real life solutions. Our innovations are result of inquisitive" +
                " research and the tenacity to keep going until we are done. We possess expertise to include extra feature to the software" +
                " and system we develop for our client, that make it accessible for people with visual disabilities and it is what sets us" +
                " apart from others. ‘To deal with challenging problems and inventing solutions for those’- is a duty incumbent upon " +
                "Innovation Garage team." +"\n\n"+
            "Objectives : "+"\n\n"+
                "Our aim is to develop scalable, available and complex but user friendly solution. " +
                "We provide need-based customized systems for individuals or groups. Keeping in mind, the emerging needs of people from" +
                " different business, we work on concrete solutions for them that help them to cut-off the redundancies. Apart from " +
                "business purpose, we research on system that could have beneficial effects on persons with disabilities. Our objective " +
                "is to ensure speed, accuracy, security, accessibility for everyone through our innovation and accelerate the digital " +
                "transformation of Bangladesh."+"\n\n"+
            "Our Engineers:"+"\n"+
            "T. A. M. Ragib Shahriar"+"\n"+
            "Noushad Sojib"+"\n"+
            "\nDeveloped By: "+"\n"+
            "Inovation Garage"+"\n"+
            "Contact\n" +
                "Innovation Garage Limited\n" +
                "25, PC Culture Housing Society\n" +
                "Road # 1, Block- “Kha”\n" +
                "Adabor, Dhaka-1207\n" +
                "Phone: +8801762286738\n" +
                "E-mail: innovationgarage.bd@gmail.com\n" +
                "Face Book: facebook.com/innovationgarage1"
            ;

        Log.d(TAG,"Content layout set");
        Log.d(TAG,st);

        String st2="ACCESSIBLE DOC READER\n" +
                "Version 1.4\n\n" +
                "1st February, 2018 in collaboration with VIEW Foundation, Innovation Garage Limited  has developed an important innovation for persons with visual disability The  Accessible Doc Reader overcomes a previously insurmountable problem for people with a visual impairment who want to read and work with doc, rtf and text files in Bangla and English language. This innovative product is now available free of charge to Android users. \n" +
                "\nThe Accessible Doc Reader has a range of functions which make DOC, RTF and TXT  documents accessible to visually impaired smartphones and mini tablets users and also to older people and those with learning disabilities. The Doc Reader filters the text out of a document and presents it in a user-friendly interface.\n" +
                "The development of the  Accessible Doc Reader has been supported by View Foundation.\n";

        aboutTextView.setText(st2);
    }
    public void goBack(View view){
        finish();
    }

}
