package com.innovationgarage.accessibledocreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import newage.rs.filereader.R;

/**
 * Created by ragib on 28-Sep-18.
 */

public class LanguageAdapter extends ArrayAdapter<LanguageItem>{
    Context mContext;
    int resourceLayout;
    ArrayList<LanguageItem> languageItems=new ArrayList<>();
    public LanguageAdapter(@NonNull Context context, int resource, ArrayList<LanguageItem> languageItems) {
        super(context, resource,languageItems);
        this.mContext=context;
        this.resourceLayout=resource;
        this.languageItems=languageItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
           v=LayoutInflater.from(mContext).inflate(R.layout.custom_language_list_view,parent,false);
           TextView textView1=v.findViewById(R.id.textView7);
           TextView textView2=v.findViewById(R.id.textView8);

           textView1.setText(languageItems.get(position).getName());
           textView2.setText(languageItems.get(position).getId());
        }
        return v;
    }
}
