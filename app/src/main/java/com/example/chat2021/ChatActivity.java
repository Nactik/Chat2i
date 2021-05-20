package com.example.chat2021;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private String hash;
    private String idConv;
    private ChatRecyclerViewAdapter adapter;
    private RecyclerView rv;
    private TextView themeView;
    private ImageButton sendbtn;
    private TextInputLayout inputMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.rv = findViewById(R.id.rvChat);
        this.rv.setLayoutManager(new LinearLayoutManager(this));

        Bundle bdl = this.getIntent().getExtras();
        Log.i(Utils.CAT,bdl.getString("hash"));
        this.hash = bdl.getString("hash");
        this.idConv = bdl.getString("idConv");
        String theme = bdl.getString("theme");

        this.themeView = findViewById(R.id.chatTheme);
        this.inputMessage = findViewById(R.id.inputMessage);
        this.sendbtn = findViewById(R.id.sendBtn);
        this.sendbtn.setOnClickListener(this);

        this.themeView.setText(theme);

        RequestQueue queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        Map<String, String> headers = new HashMap<>();
        headers.put("hash", this.hash);

        GsonRequest<MessageList> mRequest = new GsonRequest<MessageList>(Request.Method.GET,
                "http://tomnab.fr/chat-api/conversations/"+idConv+"/messages",
                MessageList.class,
                headers,
                createMyReqSuccessListener(),
                createMyReqErrorListener());

        RequestQueueSingleton.getInstance(this).addToRequestQueue(mRequest);
    }

    private Response.Listener<MessageList> createMyReqSuccessListener(){

        return response -> {
            this.adapter = new ChatRecyclerViewAdapter(ChatActivity.this, response);
            this.rv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
            this.rv.setAdapter(adapter);
        };
    }


    private ErrorListener createMyReqErrorListener() {
        return new  ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendBtn:
                String toSend = this.inputMessage.getEditText().getText().toString().trim();
                Log.i(Utils.CAT,toSend);
                this.sendMessage(toSend);
                break;
        }
    }

    private void sendMessage(String message){
        RequestQueue queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        Map<String, String> headers = new HashMap<>();
        headers.put("hash", this.hash);
        headers.put("contenu", message);

        GsonRequest<Message> mRequest = new GsonRequest<Message>(Request.Method.POST,
                "http://tomnab.fr/chat-api/conversations/"+idConv+"/messages",
                Message.class,
                headers,
                createMessageSuccessListener(),
                createMessageErrorListener()){
            @NotNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("contenu", message);
                return params;
            }
        };

        RequestQueueSingleton.getInstance(this).addToRequestQueue(mRequest);
    }

    private Response.Listener<Message> createMessageSuccessListener(){
        return response -> {
            this.adapter.addMessage(response);
            this.adapter.notifyDataSetChanged();
            this.rv.smoothScrollToPosition(this.adapter.getItemCount() - 1);
            this.inputMessage.getEditText().setText("");
        };
    }

    private ErrorListener createMessageErrorListener() {
        return new  ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
    }

}



