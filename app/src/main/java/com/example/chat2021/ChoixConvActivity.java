package com.example.chat2021;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class ChoixConvActivity extends AppCompatActivity implements ConvRecyclerViewAdapter.ItemClickListener{

    private SharedPreferences preferences;
    private RecyclerView rvListConv;
    private String hash;
    private ConvRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.rvListConv = findViewById(R.id.rvConv);

        Bundle bdl = this.getIntent().getExtras();
        this.hash = bdl.getString("hash");

        if(this.hash != null) this.setUpConvList();
        else Utils.alerter(ChoixConvActivity.this, "Erreur lors de la récupération des informations de connexion");

    }

    /**
     * Rempli la liste des conversations
     */
    private void setUpConvList(){
        RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        String url = this.preferences.getString("urlData", "http://tomnab.fr/chat-api/");
        url += "conversations";
        Map<String, String> headers = new HashMap<>();
        headers.put("hash", this.hash);

        GsonRequest<ListConversation> mRequest = new GsonRequest<>(Request.Method.GET,
                url,
                ListConversation.class,
                headers,
                createMyReqSuccessListener(),
                createMyReqErrorListener());

        RequestQueueSingleton.getInstance(this).addToRequestQueue(mRequest);
    }

    /**
     * Réponse recu de la requete
     * @return la reponse recue
     */
    private Response.Listener<ListConversation> createMyReqSuccessListener() {
        return response -> {
            this.adapter = new ConvRecyclerViewAdapter(ChoixConvActivity.this, response);
            this.rvListConv.setLayoutManager(new LinearLayoutManager(ChoixConvActivity.this));
            this.adapter.setClickListener(ChoixConvActivity.this);
            this.rvListConv.setAdapter(adapter);
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return error -> Utils.alerter(ChoixConvActivity.this, "Erreur lors de la récupération des conversations" );
    }

    @Override
    public void onItemClick(View view, int position) {
        Utils.alerter(ChoixConvActivity.this,  "You clicked " + adapter.getItem(position) + " on row number " + position);
    }
}
