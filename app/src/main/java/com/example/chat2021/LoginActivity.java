package com.example.chat2021;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sp;
    EditText edtLogin;
    EditText edtPasse;
    CheckBox cbRemember;
    Button btnOK;
    SharedPreferences.Editor editor;

    class JSONAsyncTask extends AsyncTask<String, Void, String> {
        // Params, Progress, Result

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(Utils.CAT,"onPreExecute");
        }

        @Override
        protected String doInBackground(String... qs) {
            // String... : ellipse
            // Lors de l'appel, on fournit les arguments à la suite, séparés par des virgules
            // On récupère ces arguments dans un tableau
            // pas d'interaction avec l'UI Thread ici
            Log.i(Utils.CAT,"doInBackground");
            Log.i(Utils.CAT,qs[0]);
            Log.i(Utils.CAT,qs[1]);
            String result = Utils.requete(qs[0], qs[1]);
            Log.i(Utils.CAT,result);
            String hash = "";
            //String hash="4e28dafe87d65cca1482d21e76c61a06";

            // TODO : ne traite pas les erreurs de connexion !

            try {

                JSONObject obR = new JSONObject(result);
                hash = obR.getString("hash");

                /*
                    String res = "{\"promo\":\"2020-2021\",\"enseignants\":[{\"prenom\":\"Mohamed\",\"nom\":\"Boukadir\"},{\"prenom\":\"Thomas\",\"nom\":\"Bourdeaud'huy\"}]}";
                    JSONObject ob = new JSONObject(res);
                    String promo = ob.getString("promo");
                    JSONArray profs = ob.getJSONArray("enseignants");
                    JSONObject tom = profs.getJSONObject(1);
                    String prenom = tom.getString("prenom");
                    Log.i(Utils.CAT,"promo:" + promo + " prenom:" + prenom);

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .disableHtmlEscaping()
                            .setPrettyPrinting()
                            .create();

                    String res2 = gson.toJson(ob);
                    Log.i(Utils.CAT,"chaine recue:" + res);
                    Log.i(Utils.CAT,"chaine avec gson:" + res2);

                    Promo unePromo = gson.fromJson(res,Promo.class);
                    Log.i(Utils.CAT,unePromo.toString());
                */


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return hash;
        }

        protected void onPostExecute(String hash) {
            Log.i(Utils.CAT,"onPostExecute");
            Log.i(Utils.CAT,hash);
            Utils.alerter(LoginActivity.this, hash);


            Intent iVersChoixConv = new Intent(LoginActivity.this,ChoixConvActivity.class);
            Bundle bdl = new Bundle();
            bdl.putString("hash",hash);
            iVersChoixConv.putExtras(bdl);
            startActivity(iVersChoixConv);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        edtLogin = findViewById(R.id.login_edtLogin);
        edtPasse = findViewById(R.id.login_edtPasse);
        cbRemember = findViewById(R.id.login_cbRemember);
        btnOK = findViewById(R.id.login_btnOK);

        btnOK.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Lire les préférences partagées
        if (sp.getBoolean("remember",false)) {
            // et remplir (si nécessaire) les champs pseudo, passe, case à cocher
            cbRemember.setChecked(true);
            edtLogin.setText(sp.getString("login",""));
            edtPasse.setText(sp.getString("passe",""));
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
        // Lors de l'appui sur le bouton OK
        // si case est cochée, enregistrer les données dans les préférences
        Utils.alerter(LoginActivity.this, "click sur OK");
        if (cbRemember.isChecked()) {
            editor.putBoolean("remember",true);
            editor.putString("login", edtLogin.getText().toString());
            editor.putString("passe", edtPasse.getText().toString());
            editor.commit();
        } else {
            editor.clear();
            editor.commit();
        }

        // On envoie une requete HTTP
        JSONAsyncTask jsonT = new JSONAsyncTask();
        jsonT.execute(sp.getString("urlData","http://tomnab.fr/chat-api/")+"authenticate",
                        "user=" + edtLogin.getText().toString()
                        + "&password=" + edtPasse.getText().toString());

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