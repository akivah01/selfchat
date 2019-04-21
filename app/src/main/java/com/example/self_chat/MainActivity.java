package com.example.self_chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText input;
    Button button;
    MyAdapter adapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        input = (EditText) findViewById(R.id.EditText);
        button = (Button) findViewById(R.id.Button);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null)
        {
            input.setText(savedInstanceState.getString("input"));
            adapter.setData(savedInstanceState.getStringArrayList("list"));
            adapter.notifyDataSetChanged();
        }

        button.setOnClickListener(this);

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("input", input.getText().toString());
        outState.putStringArrayList("list", adapter.getData());
    }

    @Override
    public void onClick(View view) {
        String massage;
        massage = input.getText().toString();
        input.setText("");
        if (massage.length() == 0 ){
            Toast.makeText(getApplicationContext(), "EMPTY MASSAGE !"
                    , Toast.LENGTH_LONG).show();
            return;
        }
        adapter.messages.add(massage);
        adapter.notifyDataSetChanged();



    }
}