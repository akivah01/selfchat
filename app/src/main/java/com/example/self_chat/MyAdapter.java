package com.example.self_chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;



class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public void setData(ArrayList<String> list) {
        messages = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        MyViewHolder(View v) {
            super(v);
            textView = ((TextView) v.findViewById(R.id.TextView));
        }

        public void display(String message) {
            textView.setText(message);
        }

    }

    ArrayList<String> messages;

    public MyAdapter(){
        messages = new ArrayList<String>();
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewsignalmsg, parent, false);

        MyViewHolder viewHolderh = new MyViewHolder(v);
        return viewHolderh;


    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.display(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public ArrayList<String> getData() {
        return messages;
    }

}
