package com.example.chat2021;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.material.textfield.TextInputLayout;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private EditText edtLogin;
    private EditText edtPasse;
    private CheckBox cbRemember;
    private Button btnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

        edtLogin = findViewById(R.id.login_edtLogin);
        edtPasse = findViewById(R.id.login_edtPasse);
        cbRemember = findViewById(R.id.login_cbRemember);
        btnOK = findViewById(R.id.login_btnOK);

        btnOK.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (this.preferences.getBoolean("remember",false)) {
            this.cbRemember.setChecked(true);
            this.edtLogin.setText(this.preferences.getString("login",""));
            this.edtPasse.setText(this.preferences.getString("passe",""));
        }

        // Vérifier l'état du réseau
        if (Utils.verifReseau(LoginActivity.this)) {
            btnOK.setEnabled(true); // activation du bouton
        } else {
            btnOK.setEnabled(false); // désactivation du bouton
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.login_btnOK){
            SharedPreferences.Editor editor = this.preferences.edit();
            if (cbRemember.isChecked()) {
                editor.putBoolean("remember",true);
                editor.putString("login", edtLogin.getText().toString());
                editor.putString("passe", edtPasse.getText().toString());
            } else {
                editor.clear();
            }
            editor.apply();

            this.connection();
        }
    }

    private void connection(){
        RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        String url = this.preferences.getString("urlData", "http://tomnab.fr/chat-api/");
        url += "authenticate?user="+this.edtLogin.getText()+"&password="+this.edtPasse.getText();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, createMyReqSuccessListener(), createMyReqErrorListener());

        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return response -> {
            boolean success;
            try {
                success = response.getBoolean("success");
                if(!success){
                    Utils.alerter(LoginActivity.this, "Erreur de connexion");
                } else {
                    String hash = response.getString("hash");
                    Intent iVersChoixConv = new Intent(LoginActivity.this, ChoixConvActivity.class);
                    Bundle bdl = new Bundle();
                    bdl.putString("hash",hash);
                    iVersChoixConv.putExtras(bdl);
                    startActivity(iVersChoixConv);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.alerter(LoginActivity.this, "Erreur de connexion");
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return error -> Utils.alerter(LoginActivity.this, "Erreur de connexion" );
    }

    // Afficher les éléments du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Utiliser menu.xml pour créer le menu (Préférences, Mon Compte)
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Gestionnaire d'événement pour le menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings :
                // Changer d'activité pour afficher PrefsActivity
                Intent change2Prefs = new Intent(this,PrefsActivity.class);
                startActivity(change2Prefs);
                break;
            case R.id.action_account :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}