package com.example.chat2021;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChoixConvActivity extends AppCompatActivity implements ConvRecyclerViewAdapter.ItemClickListener {

    private SharedPreferences preferences;
    private RecyclerView rvListConv;
    private String hash;
    private ConvRecyclerViewAdapter adapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.rvListConv = findViewById(R.id.rvConv);
        this.fab = findViewById(R.id.fab);

        Bundle bdl = this.getIntent().getExtras();
        this.hash = bdl.getString("hash");

        if(this.hash != null) this.setUpConvList();
        else Utils.alerter(ChoixConvActivity.this, "Erreur lors de la récupération des informations de connexion");

        this.fab.setOnClickListener(this.addNewConvDialog);

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
                successGetConvListListener(),
                errorGetConvListListener());

        RequestQueueSingleton.getInstance(this).addToRequestQueue(mRequest);
    }

    /**
     * Réponse recu de la requete
     * @return la reponse recue
     */
    private Response.Listener<ListConversation> successGetConvListListener() {
        return response -> {
            this.adapter = new ConvRecyclerViewAdapter(ChoixConvActivity.this, response);
            this.rvListConv.setLayoutManager(new LinearLayoutManager(ChoixConvActivity.this));
            this.adapter.setClickListener(ChoixConvActivity.this);
            this.rvListConv.setAdapter(adapter);
        };
    }

    /**
     * Gère les erreurs
     * @return l'erreur recu
     */
    private Response.ErrorListener errorGetConvListListener() {
        return error -> Utils.alerter(ChoixConvActivity.this, "Erreur lors de la récupération des conversations" );
    }

    @Override
    public void onItemClick(View view, int position) {
        if(view.getId() == R.id.convDel){
            this.delConv(Integer.parseInt(adapter.getId(position)));
        } else {
            Utils.alerter(ChoixConvActivity.this,  "You clicked " + adapter.getId(position) + " on row number " + position);

            Intent toChat = new Intent(this,ChatActivity.class);

            Bundle bdl = new Bundle();
            bdl.putString("hash",hash);
            bdl.putString("idConv", adapter.getId(position));
            bdl.putString("theme", adapter.getTheme(position));

            toChat.putExtras(bdl);
            startActivity(toChat);
        }
    }

    /**
     * Création d'une boite de dialogue pour le nom de la conversation
     */
    private View.OnClickListener addNewConvDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LayoutInflater li = LayoutInflater.from(ChoixConvActivity.this);
            View promptsView = li.inflate(R.layout.conv_name_dialogbox, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    ChoixConvActivity.this);

            alertDialogBuilder.setView(promptsView);

            final EditText convNameEt = promptsView
                    .findViewById(R.id.inputNewConvName);

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Ajouter",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    String convName = convNameEt.getText().toString();
                                    if(convName.trim().isEmpty()){
                                        Utils.alerter(ChoixConvActivity.this, "Rentrez un nom correct svp");
                                        return;
                                    } else {
                                        addNewConv(convName);
                                    }
                                }
                            })
                    .setNegativeButton("Annuler",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    };

    private void addNewConv(String convName){
        RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        String url = this.preferences.getString("urlData", "http://tomnab.fr/chat-api/");
        url += "conversations?theme="+convName;

        Map<String, String> headers = new HashMap<>();
        headers.put("hash", this.hash);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, successCreateNewConvListener(), errorCreateNewConvListener()){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };


        RequestQueueSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private Response.Listener<String> successCreateNewConvListener() {
        return response -> {
            JSONObject jsonResponse = new JSONObject();
            try {
                jsonResponse = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            boolean success;
            try {
                success = jsonResponse.getBoolean("success");
                if(!success){
                    Utils.alerter(ChoixConvActivity.this, "Erreur lors de la création de conversation");
                } else {
                    JSONObject jsonConv = jsonResponse.getJSONObject("conversation");
                    int id = Integer.parseInt(this.adapter.getId(this.adapter.getItemCount()-1))+1;
                    Conversation newConv = new Conversation(Integer.toString(id), jsonConv.getString("active"), jsonConv.getString("theme"));
                    this.adapter.addConv(newConv);
                    this.adapter.notifyDataSetChanged();
                    this.rvListConv.smoothScrollToPosition(this.adapter.getItemCount() - 1);
                    Utils.alerter(ChoixConvActivity.this, "Conversation créée");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.alerter(ChoixConvActivity.this, "Erreur lors de la création de conversation");
            }
        };
    }

    private Response.ErrorListener errorCreateNewConvListener() {
        return error ->{
            Utils.alerter(ChoixConvActivity.this, "Erreur lors de la création de conversation");
        };
    }

    private void delConv(int convId){
        RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        String url = this.preferences.getString("urlData", "http://tomnab.fr/chat-api/");
        url += "conversations/"+convId;

        Map<String, String> headers = new HashMap<>();
        headers.put("hash", this.hash);

        Utils.alerter(this, this.hash);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, new JSONObject(headers), successDelConvListener(), errorDelConvListener());

        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> successDelConvListener() {
        return response -> {
            boolean success;
            try {
                success = response.getBoolean("success");
                if(!success){
                    Utils.alerter(ChoixConvActivity.this, "Erreur lors de la suppresion de conversation");
                } else {
                    Utils.alerter(ChoixConvActivity.this, "Conversation supprimée");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.alerter(ChoixConvActivity.this, "Erreur lors de la suppresion de conversation");
            }
        };
    }

    private Response.ErrorListener errorDelConvListener() {
        return error -> {
            Utils.alerter(ChoixConvActivity.this, "Erreur lors de la suppresion de conversation");
        };
    }
}
