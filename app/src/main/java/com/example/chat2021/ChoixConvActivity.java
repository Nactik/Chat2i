package com.example.chat2021;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
// import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChoixConvActivity extends AppCompatActivity implements ConvRecyclerViewAdapter.ItemClickListener{

    private static final String CAT = "LE4-SI";
    APIInterface apiService;
    String hash;
    private ListConversation conversations;
    private ConvRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);

        RecyclerView rv = findViewById(R.id.rvConv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Bundle bdl = this.getIntent().getExtras();
        Log.i(CAT,bdl.getString("hash"));
        hash = bdl.getString("hash");

        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ListConversation> call1 = apiService.doGetListConversation(hash);
        call1.enqueue(new Callback<ListConversation>() {
            @Override
            public void onResponse(Call<ListConversation> call, Response<ListConversation> response) {
                ListConversation lc = response.body();
                Log.i(CAT,lc.toString());

                adapter = new ConvRecyclerViewAdapter(ChoixConvActivity.this, lc);
                rv.setLayoutManager(new LinearLayoutManager(ChoixConvActivity.this));
                adapter.setClickListener(ChoixConvActivity.this);
                rv.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<ListConversation> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onItemClick(View view, int position) {
        //Récupère le singleton de volley pour effectuer les requêtes
        RequestQueue queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        Map<String, String> headers = new HashMap<>();
        headers.put("hash", "4e28dafe87d65cca1482d21e76c61a06");

        GsonRequest<Conversation> mRequest = new GsonRequest<>(Request.Method.GET,
                "http://tomnab.fr/chat-api/conversations/"+adapter.getItem(position),
                Conversation.class,
                headers,
                createMyReqSuccessListener(),
                createMyReqErrorListener());

        RequestQueueSingleton.getInstance(this).addToRequestQueue(mRequest);


        Utils.alerter(ChoixConvActivity.this,  "You clicked " + adapter.getItem(position) + " on row number " + position);
    }

    private  com.android.volley.Response.Listener<Conversation> createMyReqSuccessListener() {
        return response -> {
            // Do whatever you want to do with response;
            // Like response.tags.getListing_count(); etc. etc.
            Log.i(Utils.CAT, response.toString());
        };
    }

    private com.android.volley.Response.ErrorListener createMyReqErrorListener() {
        return new  com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
            }
        };
    }
}
