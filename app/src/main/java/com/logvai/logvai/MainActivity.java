package com.logvai.logvai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //==============================================================================================
    // DECLARAÇÕES DIVERSAS
    String IdMotoboy="0";
    TextView txtID, txtMSGTitulo, txtMSGTitulo2;
    Switch swctOnOff;
    Button btDetalhes;
    public String OnOff = "On";

    DateFormat dateFormat,horaFormat;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;

    // timer
    Timer timer;
    MyTimerTask myTimerTask;

    // Volley conectividade
    private static String STRING_REQUEST_URL;
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

        txtMSGTitulo = (TextView) findViewById(R.id.txtMSGTitulo);
        txtMSGTitulo2 = (TextView) findViewById(R.id.txtMSGTitulo2);
        btDetalhes = (Button) findViewById(R.id.btDetalhes);

        //Google API
        buildGoogleApiClient();

        // Identifica ID do Motoboy
        IdentificaID();

        // Ativa TIMER
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 0, 20000); //atualiza a cada 10 segundos

        // verifica estado do Swicht
        swctOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // ativa timer
                    OnOff = "On";
                    timer = new Timer();
                    myTimerTask = new MyTimerTask();
                    timer.schedule(myTimerTask, 0, 20000); //atualiza a cada 20 segundos

                }else{
                    //desativa timer
                    OnOff = "Off";
                    if (timer!=null){
                        timer.cancel();
                        timer = null;
                    }
                    AvisoApagar();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
    // =============================================================================================





    // =============================================================================================
    // Google Play API Services
    synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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





    //======================================================================================================================
    //GEOLOCALIZAÇÃO
    //======================================================================================================================
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(30000); // Atualizaçao a cada : 30 segundos

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "O Aplicativo necessita de permissão de localização. Ative em Configurações", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }

        if (OnOff == "Off") {
            return;
        }

        // envia dados de localização utilizando Volley
        // ==============================================================================================================
        STRING_REQUEST_URL="http://logvaiws.azurewebsites.net/Webservice.asmx/Localizacao?param1=" + IdMotoboy +
                "&param2=" + lat + "&param3=" + lon;
        volleyStringRequst(STRING_REQUEST_URL);
        // ==============================================================================================================

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (OnOff == "Off") {
            return;
        }

        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

        // envia dados de localização utilizando Volley library
        // ==============================================================================================================
        STRING_REQUEST_URL="http://logvaiws.azurewebsites.net/Webservice.asmx/Localizacao?param1=" + IdMotoboy +
                "&param2=" + lat + "&param3=" + lon;
        volleyStringRequst(STRING_REQUEST_URL);
        // ==============================================================================================================

    }
    //======================================================================================================================



    //==============================================================================================
    //TIMER - TAREFAS
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable(){

                @Override
                public void run() {

                    // Verifica chamados em aberto - A cada X segundos faz requisição em WebService
                    STRING_REQUEST_URL="http://logvaiws.azurewebsites.net/Webservice.asmx/VerificaEntregas?IdMotoboy=" + Global.globalID ;
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

                int retorno = response.indexOf("9999");

                if (retorno == -1){

                    // verifica se é um retorno do envio de localização
                    retorno = response.indexOf("OK");
                    if (retorno != -1){
                        return;
                    }

                    AvisoEntrega();

                } else {
                    AvisoApagar();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, "Falha de Comunicação", Toast.LENGTH_SHORT).show();
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

    public void AvisoEntrega(){
        txtMSGTitulo.setVisibility(View.VISIBLE);
        txtMSGTitulo2.setVisibility(View.VISIBLE);
        btDetalhes.setVisibility(View.VISIBLE);

        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 400);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK,100);

    }

    public void AvisoApagar(){
        txtMSGTitulo.setVisibility(View.INVISIBLE);
        txtMSGTitulo2.setVisibility(View.INVISIBLE);
        btDetalhes.setVisibility(View.INVISIBLE);
    }

    public void DetalhesEntregas (View view){
        Intent it = new Intent(this, ListaActivity.class);
        startActivity(it);
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
