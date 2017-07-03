package com.logvai.logvai;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetalhesActivity2 extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Button botaoConcluir, botaoMapa, botaoStartTravel;
    TextView txtEndereco, txtnumero, txtContactar, txtDetalhes, txtTelefone, txtBanco, txtStartTravel, txtDinheiro;
    ProgressDialog progressDialog;
    DateFormat dateFormat, horaFormat;
    Spinner spinner;

    public static String JSON_URL = "", MapLat, MapLongt;
    public String IdEntrega="", IDPai="", StatusEntrega="", Ordem="";

    // Localização
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;

    // Volley conectividade
    private static String STRING_REQUEST_URL;

    //======================================================================================================================
    //Ciclo da Activity - on Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes2);

        //inicializa componentes que receberão dados da entrega
        botaoConcluir = (Button) findViewById(R.id.btConcluida);
        botaoMapa = (Button) findViewById(R.id.btMap);
        botaoStartTravel = (Button) findViewById(R.id.btStart);

        txtEndereco = (TextView) findViewById(R.id.txtEndereco);
        txtnumero = (TextView) findViewById(R.id.txtnumero);
        txtContactar = (TextView) findViewById(R.id.txtContactar);
        txtDetalhes= (TextView) findViewById(R.id.txtDetalhes);

        txtBanco = (TextView) findViewById(R.id.txtBanco);
        txtDinheiro = (TextView) findViewById(R.id.txtDinheiro);
        txtStartTravel = (TextView) findViewById(R.id.txtStartTravel);

        //monta Spinner (combo com lista de opções)
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this,
                R.array.status_array, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Google API
        buildGoogleApiClient();

        //aguarda seleção do usuario no Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position==0){
                    botaoConcluir.setEnabled(false);
                }else{
                    botaoConcluir.setEnabled(true);
                }

                StatusEntrega = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //recupera dados passados da Activity anterior - ID da Entrega
        Bundle b = getIntent().getExtras();
        IdEntrega = b.getString("idFilho");
        IDPai = b.getString("IDPai");
        Ordem = b.getString("Ordem");

        //requisita detalhes de entrega
        JSON_URL = "http://logvaiws.azurewebsites.net/Webservice.asmx/DetalhesEntrega?param1=" + IdEntrega;
        volleyStringRequst(JSON_URL);

    }


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
            Toast.makeText(DetalhesActivity2.this, "O Aplicativo necessita de permissão de localização. Ative em Configurações", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }

        // envia dados de localização utilizando Volley
        // ==============================================================================================================
        STRING_REQUEST_URL="http://logvaiws.azurewebsites.net/Webservice.asmx/Localizacao?param1=" + Global.globalID +
                "&param2=" + lat + "&param3=" + lon;
        volleyLocation(STRING_REQUEST_URL);
        // ==============================================================================================================
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

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        // envia dados de localização utilizando Volley library
        // ==============================================================================================================
        STRING_REQUEST_URL="http://logvaiws.azurewebsites.net/Webservice.asmx/Localizacao?param1=" + Global.globalID +
                "&param2=" + lat + "&param3=" + lon;
        volleyLocation(STRING_REQUEST_URL);
        // ==============================================================================================================

    }
    //======================================================================================================================


    //======================================================================================================================
    //Consulta Web-Service - Detalhes da Entrega (Volley library)
    public void volleyStringRequst(String url){

        String  REQUEST_TAG = "com.logvai.detalhes2";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Aguarde...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Formata retorno obtido do web-service (padrão parsing JSON)
                String str1 =  "{\"detalhes\":" + response.toString().substring(91);
                int tamanho = str1.length() -9 ;
                String str2 = str1.substring(0,tamanho) + "}";

                //envia retorno formatado para processo de Parsing
                ParseDetalhes pj = new ParseDetalhes(str2);
                pj.parseDetalhes();

                txtEndereco.setText(ParseDetalhes.Campo1);
                txtnumero.setText("Número: " + ParseDetalhes.Campo2 + " / " + ParseDetalhes.Campo3);
                txtContactar.setText("Contactar: " + ParseDetalhes.Campo4 + " / " +  ParseDetalhes.Campo7);
                txtDetalhes.setText("Obs.: " + ParseDetalhes.Campo5);
                txtBanco.setText(ParseDetalhes.Campo6);
                txtDinheiro.setText(ParseDetalhes.Campo12);

                if ( !Ordem.equals("0") ){
                    botaoStartTravel.setVisibility(View.INVISIBLE);
                    botaoConcluir.setVisibility(View.INVISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                } else {

                    if (ParseDetalhes.Campo11.equals("0")) {

                        // entrega NÃO INICIADA
                        botaoConcluir.setVisibility(View.INVISIBLE);
                        spinner.setVisibility(View.INVISIBLE);

                    } else {

                        if (ParseDetalhes.Campo11.equals(Global.globalID)) {

                            // entrega sendo realizado pelo PRÓPRIO Motoboy
                            txtStartTravel.setText("Inicio da Viagem: " + ParseDetalhes.Campo8);
                            botaoStartTravel.setVisibility(View.INVISIBLE);

                            spinner.setVisibility(View.VISIBLE);
                            spinner.setEnabled(true);

                            botaoConcluir.setVisibility(View.VISIBLE);
                            botaoConcluir.setEnabled(true);

                        } else {

                            // entrega sendo realizado por OUTRO Motoboy
                            txtStartTravel.setText("Entrega já iniciada por outro Motoboy");

                            botaoMapa.setVisibility(View.INVISIBLE);
                            botaoStartTravel.setVisibility(View.INVISIBLE);
                            botaoConcluir.setVisibility(View.INVISIBLE);
                            spinner.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                MapLat = ParseDetalhes.Campo9;
                MapLongt = ParseDetalhes.Campo10;

                progressDialog.hide();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.hide();
            }
        });
        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, REQUEST_TAG);
    }

    public void volleyLocation(String url){

        String  REQUEST_TAG = "com.logvai.location2";
        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //não precisa fazer nada, após envio de coordenada
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


    //======================================================================================================================
    // Mapa do local da entrega
    public void btMapaDetalhe(View view){

        //transferencia de dados entre Activitys - coordenadas do local da entrega
        Bundle b = new Bundle();
        b.putString("MapLatitude",MapLat);
        b.putString("MapLongitude",MapLongt);

        //inicia nova Activity
        Intent proximatela = new Intent(getApplicationContext(),MapsActivity.class);
        proximatela.putExtras(b);
        startActivity(proximatela);

    }
    //======================================================================================================================



    //======================================================================================================================
    // Atualiza Status - Inicio de Viagem
    public void btStartTravel (View view){

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        horaFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        // envia requisição para atualizar status da entrega: VIAGEM INICIADA
        JSON_URL="http://logvaiws.azurewebsites.net/Webservice.asmx/StartTravel" +
                "?IDMotoboy=" + Global.globalID +
                "&IDEntrega=" +  IdEntrega +
                "&IDPai=" + IDPai +
                "&dataLeitura=" + dateFormat.format(date) + "%20" + horaFormat.format(date);

        volleyUpdateTravel(JSON_URL);

        txtStartTravel.setText("Inicio da Viagem: " + horaFormat.format(date));
        botaoStartTravel.setEnabled(false);

        spinner.setVisibility(View.VISIBLE);
        spinner.setEnabled(true);

        botaoConcluir.setVisibility(View.VISIBLE);
        botaoConcluir.setEnabled(true);

    }

    public void btEndTravel (View view){

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        horaFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        String mStatus = StatusEntrega.substring(0,2);

        if (StatusEntrega.equals("SELECIONE STATUS")) {
            Toast.makeText(DetalhesActivity2.this, "Selecione um Status", Toast.LENGTH_SHORT).show();
            return;
        }

        // envia requisição para atualizar status da entrega: VIAGEM CONCLUIDA
        JSON_URL="http://logvaiws.azurewebsites.net/Webservice.asmx/EndTravel"+
                "?IDMotoboy=" + Global.globalID +
                "&IDEntrega=" +  IdEntrega +
                "&IDPai=" + IDPai +
                "&dataLeitura=" + dateFormat.format(date) + "%20" + horaFormat.format(date) +
                "&Status=" + mStatus;

        volleyUpdateTravel(JSON_URL);

        txtStartTravel.setText("Final da Viagem: " + horaFormat.format(date));
        botaoConcluir.setEnabled(false);
        spinner.setEnabled(false);

    }

    public void volleyUpdateTravel(String url){

        String  REQUEST_TAG = "com.logvai.updateTravel";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Aguarde...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
            }
        });
        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, REQUEST_TAG);
    }
    //======================================================================================================================




}
