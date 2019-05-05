package com.example.self_chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;


class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {



    ArrayList<String> messages;
    LongClick click;
    int DataLength;
    Gson G_son;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public  interface  LongClick {
        void itemLongClick(View view, final int position);
    }


    public void setData(ArrayList<String> list) {
        messages = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = ((TextView) v.findViewById(R.id.TextView));
            v.setOnLongClickListener(this);
        }

        public void display(String message) {
            textView.setText(message);
        }

        public boolean onLongClick(View v) {
            if (click != null) {
                click.itemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }


    public MyAdapter( int size, SharedPreferences sp, SharedPreferences.Editor edit){
        messages = new ArrayList<String>();
        G_son = new Gson();
        DataLength = size;
        sharedPreferences = sp;
        editor = edit;
    }


    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewsignalmsg, parent, false);

        MyViewHolder VH = new MyViewHolder(v);
        return VH;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.display(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void saveEditions()
    {
        editor.putInt("Datasize", DataLength);
        String json = G_son.toJson(messages);
        editor.putString("sent", json);
        editor.apply();
    }

    public void setClickListener(LongClick itemClick) {
        this.click = itemClick;
    }

    public ArrayList<String> getData() {
        return messages;
    }

    public void updateData( ){
        String j_son = sharedPreferences.getString("sent", "");
        Type typeToken = new TypeToken<List<String>>() {
        }.getType();
        messages = G_son.fromJson(j_son, typeToken);
    }
}