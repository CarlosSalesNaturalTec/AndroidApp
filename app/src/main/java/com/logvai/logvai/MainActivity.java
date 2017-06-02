package com.logvai.logvai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
    String IdMotoboy="0",IdEntrega="0";
    TextView txtID;
    Switch swctOnOff;
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

        //Google API
        buildGoogleApiClient();

        // Identifica ID do Motoboy
        IdentificaID();

        // Ativa TIMER
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 0, 10000); //atualiza a cada 10 segundos

        // verifica estado do Swicht
        swctOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // ativa timer
                    OnOff = "On";
                    timer = new Timer();
                    myTimerTask = new MyTimerTask();
                    timer.schedule(myTimerTask, 0, 30000); //atualiza a cada 10 segundos

                }else{
                    //desativa timer
                    OnOff = "Off";
                    if (timer!=null){
                        timer.cancel();
                        timer = null;
                    }
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
        mLocationRequest.setInterval(20000); // Atualizaçao a cada : 20 segundos

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Opaaaaa", Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(MainActivity.this, "Lat:" + lat + " Lng:" + lon, Toast.LENGTH_SHORT).show();
        }

        // envia dados de localização utilizando Volley Library
        // ==============================================================================================================
        //dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //horaFormat = new SimpleDateFormat("HH:mm:ss");
        //Date date = new Date();

        //STRING_REQUEST_URL="http://logwebservice.azurewebsites.net/wservice.asmx/Historico?IDMotoboy="+ IdMotoboy + "&identrega="
        //        + IdEntrega + "&latitude=" + lat + "&longitude=" + lon + "&dataleitura=" + dateFormat.format(date) + "%20" + horaFormat.format(date);
        //volleyStringRequst(STRING_REQUEST_URL);
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

        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

        if (OnOff == "Off") {
            return;
        } else {
            Toast.makeText(MainActivity.this, "Lat:" + lat + " Lng:" + lon, Toast.LENGTH_SHORT).show();
        }

        // envia dados de localização utilizando Volley library
        // ==============================================================================================================
        //dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //horaFormat = new SimpleDateFormat("HH:mm:ss");
        //Date date = new Date();

        //STRING_REQUEST_URL="http://logwebservice.azurewebsites.net/wservice.asmx/Historico?IDMotoboy="+ IdMotoboy + "&identrega="
        //        + IdEntrega + "&latitude=" + lat + "&longitude=" + lon + "&dataleitura=" + dateFormat.format(date) + "%20" + horaFormat.format(date);
        //volleyStringRequst(STRING_REQUEST_URL);
        // ==============================================================================================================

    }




    //==============================================================================================
    //TIMER - TAREFAS
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable(){

                @Override
                public void run() {

                    // A cada X segundos faz requisição em WebService - verifica chamados em aberto
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
                    //encontrou entregas
                    Toast.makeText(MainActivity.this, "Encontrou Entregas", Toast.LENGTH_SHORT).show();
                } else {
                    // não existem entregas
                    Toast.makeText(MainActivity.this, "SEM ENTREGAS", Toast.LENGTH_SHORT).show();
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
