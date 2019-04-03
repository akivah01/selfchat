package com.example.self_chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView text_1;
    EditText text_2;
    Button button_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         text_1=(TextView) findViewById(R.id.textView);
         text_2=(EditText) findViewById(R.id.plain_text_input);
         button_1=(Button) findViewById(R.id.button);
         button_1.setOnClickListener(this);
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button:
                text_1.setText(text_2.getText().toString());
                text_2.setText("");
                break;

        }
    }
}

