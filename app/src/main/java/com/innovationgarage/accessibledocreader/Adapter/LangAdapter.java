package com.innovationgarage.accessibledocreader.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innovationgarage.accessibledocreader.View.LangItem;

import java.util.ArrayList;
import java.util.List;

import newage.rs.filereader.R;

/**
 * Created by ragib on 01-Oct-18.
 */

public class LangAdapter extends RecyclerView.Adapter<LangAdapter.ItemViewHolder> {
    List<LangItem> itemList;

    public LangAdapter(List<LangItem> itemList) {
        this.itemList=itemList;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        LangItem langItem=itemList.get(position);
        holder.textView1.setText(langItem.getName());
        holder.textView2.setText(langItem.getId());
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_language_list_view, parent, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView1,textView2;
        public ItemViewHolder(View itemView) {
            super(itemView);
            textView1=itemView.findViewById(R.id.textView7);
            textView2=itemView.findViewById(R.id.textView8);
        }
    }
}
