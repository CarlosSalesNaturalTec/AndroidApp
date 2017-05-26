package com.logvai.logvai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    //==============================================================================================
    // DECLARAÇÕES DIVERSAS
    String IdMotoboy="0";
    TextView txtID;
    Switch swctOnOff;

    // timer
    Timer timer;
    MyTimerTask myTimerTask;

    // Volley conectividade
    private static String STRING_REQUEST_URL="http://logvaiws.azurewebsites.net/Webservice.asmx/testeCom?param1=1";
    private static final String TAG = "MainActivity";
    //==============================================================================================


    // =============================================================================================
    // CICLO DA ACTIVITY
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtID = (TextView) findViewById(R.id.txtID);
        swctOnOff = (Switch) findViewById(R.id.switch1);

        // Identifica ID do Motoboy
        IdentificaID();

        // ativa timer
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 0, 10000); //atualiza a cada 5 segundos

        // verifica estado do Swicht
        swctOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // ativa timer
                    timer = new Timer();
                    myTimerTask = new MyTimerTask();
                    timer.schedule(myTimerTask, 0, 10000); //atualiza a cada 10 segundos
                }else{
                    //desativa timer
                    if (timer!=null){
                        timer.cancel();
                        timer = null;
                    }
                }
            }
        });
    }
    // =============================================================================================



    //==============================================================================================
    // Identifica Motoboy
    public void IdentificaID(){
        SharedPreferences preferences = getSharedPreferences("LOGVAI_CONFIG", Context.MODE_PRIVATE);
        if (preferences.contains(("IDMotoboy"))){
            // Salva ID em variável Global para ser utilizado nas outras Activitys
            Global.globalID = preferences.getString("IDMotoboy","0");
            IdMotoboy = Global.globalID;
            txtID.setText("ID: " + Global.globalID);
        }else{
            swctOnOff.setEnabled(false);
        }
    }
    //==============================================================================================



    //==============================================================================================
    //TIMER - TAREFAS
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    // Faz requisição em WebService - verifica chamados em aberto
                    volleyStringRequst(STRING_REQUEST_URL);
                }});
        }
    }
    //==============================================================================================




    //==============================================================================================
    //VOLLEY CONECTIVIDADE - TROCA DE DADOS COM WEB-SERVICE
    public void volleyStringRequst(String url){

        String  REQUEST_TAG = "com.logvai.logvai";

        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "Recebido: " + response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, "Falha de Comunicalção", Toast.LENGTH_SHORT).show();
            }
        });
        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, REQUEST_TAG);
    }

    public void volleyInvalidateCache(String url){
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().invalidate(url, true);
    }

    public void volleyDeleteCache(String url){
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().remove(url);
    }

    public void volleyClearCache(){
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().clear();
    }
    // ==============================================================================================================


    //======================================================================================================================
    //MENU
    //======================================================================================================================
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuItem op1 = menu.add(0,0,0,"Configurações");
        MenuItem op2 = menu.add(0,1,1,"Mapa");

        op1.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int panel, MenuItem item){
        switch (item.getItemId()){
            case 0 :
                Intent it = new Intent(this, ConfigActivity.class);
                startActivity(it);
                break;
            case 1 :
                Intent it1 = new Intent(this, MapsActivity.class);
                startActivity(it1);
                break;
        }
        return true;
    }
    // ==============================================================================================================



}
