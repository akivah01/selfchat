package com.example.self_chat;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.SharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import static com.example.self_chat.MainActivity.DATA_LIST;


class MyAdapter extends RecyclerView.Adapter<MyAdapter.viewHold> {


    private static final int EMPTYING = 0;
    private static final String ID_MSG_DOC_KEY = "ID_MSG_DOC";
    private static final String ID_DOC_FIELD_KEY = "ID_COUNTER";
    private static final String TIME_STAMP_KEY = "TimeStamp" ;
    private static final String COLLECTION = "sampleData";
    private static final String TEXT_KEY = "Text";
    private static final String ID_KEY = "Id";
    private FirebaseFirestore dataBase;

    public int counterID = 0;
    public ArrayList<MyMessage> messages;
    public LongClick click;
    public int DataLength;
    public Gson G_son;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    public MyAdapter( int size, SharedPreferences sp, SharedPreferences.Editor edit, FirebaseFirestore dataBase){
        this.messages = new ArrayList<MyMessage>();
        this.G_son = new Gson();
        this.DataLength = size;
        this.sharedPreferences = sp;
        this.editor = edit;
        this.dataBase = dataBase;
    }

    public  interface  LongClick {
        void msgClicked(View view, final int position);
    }


    public void setData(ArrayList<MyMessage> list) {
        messages = list;
    }

    public class viewHold extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView time;
        TextView textView;

        public viewHold(View v) {
            super(v);
            textView = ((TextView) v.findViewById(R.id.TextView));
            time = ((TextView) v.findViewById(R.id.timeStamp));
            v.setOnLongClickListener(this);
        }
        public boolean onLongClick(View v) {
            if (click != null) {
                click.msgClicked(v, getAdapterPosition());
            }
            return true;
        }

    }


    @NonNull
    @Override
    public viewHold onCreateViewHolder( ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewsignalmsg, parent, false);

        viewHold VH = new viewHold(v);
        return VH; }

    @Override
    public void onBindViewHolder( viewHold holder, int position) {
        String message= messages.get(position).getMsgText();
        String timestamp = messages.get(position).getMsgTimeStamp();
        holder.textView.setText(message);
        holder.time.setText(timestamp);

    }
    @Override
    public int getItemCount() {
        if(messages != null){
        return messages.size();
        }
        else {
            messages = new ArrayList<>();
            return EMPTYING;
        }
    }

    public void saveEditions()
    {
        editor.putInt("Datasize", DataLength);
        String json = G_son.toJson(messages);
        editor.putString(DATA_LIST, json);
        editor.apply();
    }


    void addMsg(String[] data) {
        messages.add(new MyMessage(data[1], data[2], data[0]));
        DataLength =DataLength+1;
        saveEditions();
        notifyDataSetChanged();

    }

    void deleteMessage(int position) {
        new DeleteFromFB().execute(this.messages.get(position).getMsgId());
        this.messages.remove(position);
        DataLength = DataLength-1;
        saveEditions();
        notifyItemRemoved(position);
    }

    public void setClickListener(LongClick itemClick) {
        this.click = itemClick;
    }

    public void updateData( ){
        String j_son = sharedPreferences.getString(DATA_LIST, "");
        Type typeToken = new TypeToken<List<String>>() {
        }.getType();
        messages = G_son.fromJson(j_son, typeToken);
    }


    public static String clock() {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat 
                = new SimpleDateFormat("kk:mm");
        return dateFormat.format(new Date());
    }


    public void addToFB(final String message)
    {
        String currentTime = clock();
        updateFBId(counterID);

        Map<String, Object> sent_message = new HashMap<>();
        int increment_id = counterID + 1;

        sent_message.put(TEXT_KEY, message);
        sent_message.put(TIME_STAMP_KEY,currentTime);
        sent_message.put(ID_KEY, increment_id);

        dataBase.collection(COLLECTION)
                .document(increment_id + "")
                .set(sent_message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(" ", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(" ", "Error writing document", e);
                    }
                });
        counterID++;
        updateFBId(counterID);
    }


    public void updateFBId(int id)
    {
        DocumentReference washingtonRef = dataBase.collection(COLLECTION).
                document(ID_MSG_DOC_KEY);

        washingtonRef
                .update(ID_DOC_FIELD_KEY, id + 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Error updating document", e);
                    }
                });
    }


    public void getId()
    {
        DocumentReference docRef = dataBase.collection(COLLECTION).
                document(ID_MSG_DOC_KEY);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final String id = document.getData().get(ID_DOC_FIELD_KEY) + "";
                        counterID = Integer.parseInt(id);
                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });
    }

    private class DeleteFromFB extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            dataBase.collection(COLLECTION).document(strings[0])
                    .delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(" ", "Error deleting document", e);
                        }
                    });
            return null;
        }
    }

    public  void updateFB()
    {
        final ArrayList<MyMessage> dataArray = new ArrayList<MyMessage>();
        dataBase.collection(COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String id, timestamp, content;
                            Map<String, Object> one_message;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!document.getId().equals(ID_MSG_DOC_KEY))
                                {
                                    one_message = document.getData();
                                    id = one_message.get(ID_KEY) + "";
                                    timestamp = one_message.get(TIME_STAMP_KEY) + "";
                                    content = one_message.get(TEXT_KEY) + "";
                                    dataArray.add(new MyMessage(id, timestamp, content));
                                }
                            }
                            for (MyMessage msg: dataArray)
                            {

                                String[] data = new String[3];
                                data[0] = msg.getMsgId();
                                data[1] =  msg.getMsgTimeStamp();
                                data[2] =msg.getMsgId();
                                addMsg(data);
                                }
                            updateData();

                        } else {
                            Log.d(" ", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}