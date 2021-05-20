package com.example.chat2021;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChoixConvActivity extends AppCompatActivity {

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
//                adapter.setClickListener(this);
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
}
