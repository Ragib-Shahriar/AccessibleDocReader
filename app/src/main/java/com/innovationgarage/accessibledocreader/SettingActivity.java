package com.innovationgarage.accessibledocreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.innovationgarage.accessibledocreader.Adapter.LangAdapter;
import com.innovationgarage.accessibledocreader.Implements.RecyclerItemClickListener;
import com.innovationgarage.accessibledocreader.View.LangItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import newage.rs.filereader.R;

public class SettingActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<LangItem> langItems=new ArrayList<>();
    LangAdapter langAdapter;

    HashMap<String,String> hashMap;

    Button showLangButton,redButton,greenButton,blueButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        hashMap=new HashMap<String, String>();
        recyclerView=findViewById(R.id.recycleView);

        initHashMap();
        initRecycleView();

        showLangButton=findViewById(R.id.showLangButton);
        showLangButton.setText("Show Language List");
        showLangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout.LayoutParams layoutParams;
                if(showLangButton.getText()=="Show Language List") {
                    layoutParams = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setLayoutParams(layoutParams);
                            showLangButton.setText("Hide Language List");
                            recyclerView.requestLayout();
                        }
                    });
                }
                else{
                    layoutParams = new LinearLayout.LayoutParams(0,0);
                    recyclerView.setLayoutParams(layoutParams);
                    showLangButton.setText("Show Language List");
                }
            }
        });

        redButton=findViewById(R.id.redButton);
        greenButton=findViewById(R.id.greenButton);
        blueButton=findViewById(R.id.blueButton);
        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.color=R.color.redColor;
                Toast.makeText(getApplicationContext(),"Red highlight selected",Toast.LENGTH_SHORT).show();
            }
        });
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.color=R.color.greenColor;
                Toast.makeText(getApplicationContext(),"Green highlight selected",Toast.LENGTH_SHORT).show();
            }
        });
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.color=R.color.blueColor;
                Toast.makeText(getApplicationContext(),"Blue highlight selected",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initRecycleView(){

        //langItems.add(new LangItem("name","id1"));
        langAdapter=new LangAdapter(langItems);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(langAdapter);

        for(Map.Entry<String,String> hashMap1:hashMap.entrySet()){
            langItems.add(new LangItem(hashMap1.getKey(),hashMap1.getValue()));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (langItems){
                    langItems.notifyAll();
                }
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),recyclerView,new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int position) {
                MainActivity.speakLanguage=((TextView)view.findViewById(R.id.textView8)).getText().toString();
                Toast.makeText(getApplicationContext(),((TextView)view.findViewById(R.id.textView7)).getText()+" Selected",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void initHashMap() {
        hashMap.put("Afrikaans (South Africa)","af_ZA");
        hashMap.put("Amharic (Ethiopia)","am_ET");
        hashMap.put("Armenian (Armenia)","hy_AM");
        hashMap.put("Azerbaijani (Azerbaijan)","az_AZ");
        hashMap.put("Indonesian (Indonesia)","id_ID");
        hashMap.put("Malay (Malaysia)","ms_MY");
        hashMap.put("Bengali (Bangladesh)","bn_BD");
        hashMap.put("Bengali (India)","bn_IN");
        hashMap.put("Catalan (Spain)","ca_ES");
        hashMap.put("Czech (Czech Republic)","cs_CZ");
        hashMap.put("Danish (Denmark)","da_DK");
        hashMap.put("German (Germany)","de_DE");
        hashMap.put("English (Australia)","en_AU");
        hashMap.put("English (Canada)","en_CA");
        hashMap.put("English (Ghana)","en_GH");
        hashMap.put("English (United Kingdom)","en_GB");
        hashMap.put("English (India)","en_IN");
        hashMap.put("English (Ireland)","en_IE");
        hashMap.put("English (Kenya)","en_KE");
        hashMap.put("English (New Zealand)","en_NZ");
        hashMap.put("English (Nigeria)","en_NG");
        hashMap.put("English (Philippines)","en_PH");
        hashMap.put("English (South Africa)","en_ZA");
        hashMap.put("English (Tanzania)","en_TZ");
        hashMap.put("English (United States)","en_US");
        hashMap.put("Spanish (Argentina)","es_AR");
        hashMap.put("Spanish (Bolivia)","es_BO");
        hashMap.put("Spanish (Chile)","es_CL");
        hashMap.put("Spanish (Colombia)","es_CO");
        hashMap.put("Spanish (Costa Rica)","es_CR");
        hashMap.put("Spanish (Ecuador)","es_EC");
        hashMap.put("Spanish (El Salvador)","es_SV");
        hashMap.put("Spanish (Spain)","es_ES");
        hashMap.put("Spanish (United States)","es_US");
        hashMap.put("Spanish (Guatemala)","es_GT");
        hashMap.put("Spanish (Honduras)","es_HN");
        hashMap.put("Spanish (Mexico)","es_MX");
        hashMap.put("Spanish (Nicaragua)","es_NI");
        hashMap.put("Spanish (Panama)","es_PA");
        hashMap.put("Spanish (Paraguay)","es_PY");
        hashMap.put("Spanish (Peru)","es_PE");
        hashMap.put("Spanish (Puerto Rico)","es_PR");
        hashMap.put("Spanish (Dominican Republic)","es_DO");
        hashMap.put("Spanish (Uruguay)","es_UY");
        hashMap.put("Spanish (Venezuela)","es_VE");
        hashMap.put("Basque (Spain)","eu_ES");
        hashMap.put("Filipino (Philippines)","fil_PH");
        hashMap.put("French (Canada)","fr_CA");
        hashMap.put("French (France)","fr_FR");
        hashMap.put("Galician (Spain)","gl_ES");
        hashMap.put("Georgian (Georgia)","ka_GE");
        hashMap.put("Gujarati (India)","gu_IN");
        hashMap.put("Croatian (Croatia)","hr_HR");
        hashMap.put("Zulu (South Africa)","zu_ZA");
        hashMap.put("Icelandic (Iceland)","is_IS");
        hashMap.put("Italian (Italy)","it_IT");
        hashMap.put("Javanese (Indonesia)","jv_ID");
        hashMap.put("Kannada (India)","kn_IN");
        hashMap.put("Khmer (Cambodia)","km_KH");
        hashMap.put("Lao (Laos)","lo_LA");
        hashMap.put("Latvian (Latvia)","lv_LV");
        hashMap.put("Lithuanian (Lithuania)","lt_LT");
        hashMap.put("Hungarian (Hungary)","hu_HU");
        hashMap.put("Malayalam (India)","ml_IN");
        hashMap.put("Marathi (India)","mr_IN");
        hashMap.put("Dutch (Netherlands)","nl_NL");
        hashMap.put("Nepali (Nepal)","ne_NP");
        hashMap.put("Norwegian Bokmål (Norway)","nb_NO");
        hashMap.put("Polish (Poland)","pl_PL");
        hashMap.put("Portuguese (Brazil)","pt_BR");
        hashMap.put("Portuguese (Portugal)","pt_PT");
        hashMap.put("Romanian (Romania)","ro_RO");
        hashMap.put("Sinhala (Sri Lanka)","si_LK");
        hashMap.put("Slovak (Slovakia)","sk_SK");
        hashMap.put("Slovenian (Slovenia)","sl_SI");
        hashMap.put("Sundanese (Indonesia)","su_ID");
        hashMap.put("Swahili (Tanzania)","sw_TZ");
        hashMap.put("Swahili (Kenya)","sw_KE");
        hashMap.put("Finnish (Finland)","fi_FI");
        hashMap.put("Swedish (Sweden)","sv_SE");
        hashMap.put("Tamil (India)","ta_IN");
        hashMap.put("Tamil (Singapore)","ta_SG");
        hashMap.put("Tamil (Sri Lanka)","ta_LK");
        hashMap.put("Tamil (Malaysia)","ta_MY");
        hashMap.put("Telugu (India)","te_IN");
        hashMap.put("Vietnamese (Vietnam)","vi_VN");
        hashMap.put("Turkish (Turkey)","tr_TR");
        hashMap.put("Urdu (Pakistan)","ur_PK");
        hashMap.put("Urdu (India)","ur_IN");
        hashMap.put("Greek (Greece)","el_GR");
        hashMap.put("Bulgarian (Bulgaria)","bg_BG");
        hashMap.put("Russian (Russia)","ru_RU");
        hashMap.put("Serbian (Serbia)","sr_RS");
        hashMap.put("Ukrainian (Ukraine)","uk_UA");
        hashMap.put("Hebrew (Israel)","he_IL");
        hashMap.put("Arabic (Israel)","ar_IL");
        hashMap.put("Arabic (Jordan)","ar_JO");
        hashMap.put("Arabic (United Arab Emirates)","ar_AE");
        hashMap.put("Arabic (Bahrain)","ar_BH");
        hashMap.put("Arabic (Algeria)","ar_DZ");
        hashMap.put("Arabic (Saudi Arabia)","ar_SA");
        hashMap.put("Arabic (Iraq)","ar_IQ");
        hashMap.put("Arabic (Kuwait)","ar_KW");
        hashMap.put("Arabic (Morocco)","ar_MA");
        hashMap.put("Arabic (Tunisia)","ar_TN");
        hashMap.put("Arabic (Oman)","ar_OM");
        hashMap.put("Arabic (State of Palestine)","ar_PS");
        hashMap.put("Arabic (Qatar)","ar_QA");
        hashMap.put("Arabic (Lebanon)","ar_LB");
        hashMap.put("Arabic (Egypt)","ar_EG");
        hashMap.put("Persian (Iran)","fa_IR");
        hashMap.put("Hindi (India)","hi_IN");
        hashMap.put("Thai (Thailand)","th_TH");
        hashMap.put("Korean (South Korea)","ko_KR");
        hashMap.put("Chinese, Mandarin (Traditional, Taiwan)","cmn_Hant_TW");
        hashMap.put("Chinese, Cantonese (Traditional, Hong Kong)","yue_Hant_HK");
        hashMap.put("Japanese (Japan)","ja_JP");
        hashMap.put("Chinese, Mandarin (Simplified, Hong Kong)","cmn_Hans_HK");
        hashMap.put("Chinese, Mandarin (Simplified, China)","cmn_Hans_CN");
   }


}
