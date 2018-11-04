package com.innovationgarage.accessibledocreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import newage.rs.filereader.R;

public class DeveloperPage extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_page);

        textView=findViewById(R.id.textView5);

        String st="Innovation Garage Limited is a distinctive effort toward developing barrier-free, accessible solution as a contribution to digital aria. Our mission is to come up with ideas and real life solutions. Our innovations are result of inquisitive research and the tenacity to keep going until we are done. We possess expertise to include extra feature to the software and system we develop for our client, that make it accessible for people with visual disabilities and it is what sets us apart from others. ‘To deal with challenging problems and inventing solutions for those’- is a duty incumbent upon Innovation Garage team.\n" +
                "\n" +
                "Objectives\n" +
                "\n" +
                "Our aim is to develop scalable, available and complex but user friendly solution. We provide need-based customized systems for individuals or groups. Keeping in mind, the emerging needs of people from different business, we work on concrete solutions for them that help them to cut-off the redundancies. Apart from business purpose, we research on system that could have beneficial effects on persons with disabilities. Our objective is to ensure speed, accuracy, security, accessibility for everyone through our innovation and accelerate the digital transformation of Bangladesh.    \n" +
                "\n" +
                "Our Engineers:\n" +
                "T. A. M. Ragib Shahriar"+"\n"+
                "Noushad Sojib"+"\n"+
                "\nDeveloped By: "+"\n"+
                "Inovation Garage"+"\n"+
                "\n" +
                "Contact\n" +
                "Innovation Garage Limited\n" +
                "25, PC Culture Housing Society\n" +
                "Road # 1, Block- “Kha”\n" +
                "Adabor, Dhaka-1207\n" +
                "Phone: +8801762286738\n" +
                "E-mail: innovationgarage.bd@gmail.com\n" +
                "Face Book: facebook.com/innovationgarage1\n";

        textView.setText(st);
    }
}
