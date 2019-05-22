package com.example.self_chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Map;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyAdapter.LongClick{

    public EditText input;
    public Button button;
    public MyAdapter adapter;
    public RecyclerView recyclerView;
    public SharedPreferences SharedPrefrance;
    public SharedPreferences.Editor SharedPrefranceEditor;
    public FirebaseFirestore fireBase;
    public CollectionReference collectionReference;
    public static final String SHAREDPREFRENCE_FIRST = "first_launch";
    public static final String DATA_LIST = "sent";
    public static final String DATA_SIZE_KYE = "Datasize";
    public static final String INPUT_KEY = "Datasize";
    private static final String ID_MSG_DOC_KEY = "ID_MSG_DOC";
    private static final String TIME_STAMP_KEY = "TimeStamp" ;
    private static final String COLLECTION = "sampleData";
    private static final String TEXT_KEY = "Text";
    private static final String ID_KEY = "Id";

    @Override
    @SuppressLint("CommitPrefEdits")


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        input = (EditText) findViewById(R.id.EditText);
        button = (Button) findViewById(R.id.Button);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SharedPrefrance = PreferenceManager.getDefaultSharedPreferences(this);
        FirebaseApp.initializeApp(MainActivity.this);
        fireBase = FirebaseFirestore.getInstance();
        collectionReference = fireBase.collection(COLLECTION);
        SharedPrefranceEditor = SharedPrefrance.edit();
        int size = SharedPrefrance.getInt(DATA_SIZE_KYE, 0);
        adapter = new MyAdapter(size , SharedPrefrance, SharedPrefranceEditor,fireBase);
        adapter.setClickListener((MyAdapter.LongClick) this);
        new fireBaseId().execute();
        recyclerView.setAdapter(adapter);


        if(size != 0 ) {
            adapter.updateData();
        }else if  (SharedPrefrance.getBoolean(SHAREDPREFRENCE_FIRST, true)) {
            new syncLocalToRemoteFireBase().execute();
            SharedPrefranceEditor.putBoolean(SHAREDPREFRENCE_FIRST, false);
            SharedPrefranceEditor.apply();
        }

        if(savedInstanceState != null)
        {
            String previusMessages = (savedInstanceState.getString(INPUT_KEY));
            input.setText(previusMessages);
            adapter.updateData();
            adapter.notifyDataSetChanged();
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
                else
                    {
                new insertToDataBase().execute(massage);
                    }

            }
        });
    }
    @Override
    public void msgClicked(View view, final int position) {
        new AlertDialog.Builder(this).setTitle("Delete message?").setMessage("Are you sure?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.deleteMessage(position);
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

@Override
protected void onStart()
{
    super.onStart();
    adapter.setData(new ArrayList<MyMessage>());
    collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                            @Nullable FirebaseFirestoreException e) {
            if(e != null) { return; }
            for(DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges())
            {
                DocumentSnapshot documentSnapshot = documentChange.getDocument();
                String id = documentSnapshot.getId();
                if(documentChange.getNewIndex() != -1 && !
                        documentSnapshot.getId().equals(ID_MSG_DOC_KEY))
                {
                    Map<String, Object> new_doc_data = documentSnapshot.getData();
                    String[] data = new String[3];
                    data[0] = new_doc_data.get(TEXT_KEY)+"";
                    data[1] = new_doc_data.get(ID_KEY)+"";
                    data[2] = new_doc_data.get(TIME_STAMP_KEY)+"";
                    adapter.addMsg(data);
                }
               else if ( documentChange.getOldIndex() != -1)
                {
                    for(int j = 0 ; j < adapter.messages.size(); j++)
                        if (adapter.messages.get(j).getMsgId().equals(id)) {
                            adapter.deleteMessage(j);
                            break;
                        }
                }

            }
        }
    });
}

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INPUT_KEY, input.getText().toString());
    }


    public class syncLocalToRemoteFireBase extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            adapter.updateFB();
            return null;
        }
    }
    private class fireBaseId extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            adapter.getId();
            return null;
        }
    }

    private class insertToDataBase extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            adapter.addToFB(strings[0]);
            return null;
        }
    }




}



