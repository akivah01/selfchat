package com.example.self_chat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MyAdapter.LongClick{

    EditText input;
    Button button;
    MyAdapter adapter;
    RecyclerView recyclerView;

    SharedPreferences MySharedPrefrance;
    SharedPreferences.Editor MyEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        input = (EditText) findViewById(R.id.EditText);
        button = (Button) findViewById(R.id.Button);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MySharedPrefrance = PreferenceManager.getDefaultSharedPreferences(this);


        int size = MySharedPrefrance.getInt("Datasize", 0);
        MyEditor = MySharedPrefrance.edit();
        adapter = new MyAdapter(size , MySharedPrefrance, MyEditor);
        adapter.setClickListener((MyAdapter.LongClick) this);
        recyclerView.setAdapter(adapter);


        if (savedInstanceState != null)
        {
            String previusMessages = (savedInstanceState.getString("input"));
            adapter.setData(savedInstanceState.getStringArrayList("list"));
            input.setText(previusMessages);
            adapter.notifyDataSetChanged();
        } if(size != 0 ) {
            adapter.updateData();
        }

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                String massage;
                massage = input.getText().toString();
                input.setText("");
                if (massage.length() == 0) {
                    Toast.makeText(getApplicationContext(), "EMPTY MASSAGE !"
                            , Toast.LENGTH_LONG).show();
                    return;
                }
                adapter. messages.add(massage);
                adapter.DataLength = adapter.DataLength+1;
                adapter.saveEditions();
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void itemLongClick(View view, final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete message?")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.messages.remove(position);
                        adapter. DataLength =  adapter.DataLength-1;
                        adapter. saveEditions();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(),
                                "Message deleted", Toast.LENGTH_LONG).show();

                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"Not deleted",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("input", input.getText().toString());
        outState.putStringArrayList("list", adapter.getData());
    }
}