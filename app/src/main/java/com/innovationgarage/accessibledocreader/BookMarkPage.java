package com.innovationgarage.accessibledocreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by ragib on 11-Jul-18.
 */

public class BookMarkPage {
    Context context;
    String prefName="BOOKMARKS";
    public BookMarkPage(Context context){
        this.context=context;
    }
    public void putBookMark(String key,int value){
        SharedPreferences.Editor edit=context.getSharedPreferences(prefName,Context.MODE_PRIVATE).edit();
        edit.putInt(key,value);
        edit.commit();
    }
    public int getBookMark(String key){
        SharedPreferences pref=context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        return pref.getInt(key,-1);
    }
}
