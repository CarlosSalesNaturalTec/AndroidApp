package com.logvai.logvai;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetalhesActivity extends Activity  {

    Button botaoConcluir, botaoMapa, botaoStartTravel;
    TextView txtNome, txtBairro, txtEnderec, txtPref, txtCidade, txtTelefone, txtObs, txtStartTravel;
    ProgressDialog progressDialog;
    DateFormat dateFormat, horaFormat;
    Spinner spinner;

    public static String JSON_URL = "", MapLat, MapLongt;
    public String IdEntrega="",StatusEntrega="";

    //======================================================================================================================
    //Ciclo da Activity - on Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        //inicializa componentes que receberão dados da entrega
        botaoConcluir = (Button) findViewById(R.id.btConcluida);
        botaoMapa = (Button) findViewById(R.id.btMap);
        botaoStartTravel = (Button) findViewById(R.id.btStart);

        txtNome = (TextView) findViewById(R.id.txtNome);
        txtEnderec = (TextView) findViewById(R.id.txtEnderec);
        txtBairro = (TextView) findViewById(R.id.txtBairro);
        txtCidade = (TextView) findViewById(R.id.txtCidade);
        txtPref= (TextView) findViewById(R.id.txtPref);
        txtCidade = (TextView) findViewById(R.id.txtCidade);
        txtTelefone = (TextView) findViewById(R.id.txtTelefone);
        txtObs = (TextView) findViewById(R.id.txtObs);
        txtStartTravel = (TextView) findViewById(R.id.txtStartTravel);

        //monta Spinner (combo com lista de opções)
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this,
                R.array.status_array, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //aguarda seleção do usuario no Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position==0){
                    botaoConcluir.setEnabled(false);
                    botaoStartTravel.setEnabled(true);
                    botaoMapa.setEnabled(true);
                }else{
                    botaoConcluir.setEnabled(true);
                    botaoStartTravel.setEnabled(false);
                    botaoMapa.setEnabled(false);
                }

                StatusEntrega = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //recupera dados passados da Activity anterior - ID da Entrega
        Bundle b = getIntent().getExtras();
        IdEntrega = b.getString("IDEntrega");

        //requisita detalhes de entrega
        JSON_URL = "http://logwebservice.azurewebsites.net/wservice.asmx/DetalhesEntrega?IdEntrega=" + IdEntrega;
        volleyStringRequst(JSON_URL);

    }


    //======================================================================================================================
    //Consulta Web-Service - Detalhes da Entrega (Volley library)
    public void volleyStringRequst(String url){

        String  REQUEST_TAG = "br.com.loglogistica.logappdetail";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Aguarde...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Formata retorno obtido do web-service (padrão parsing JSON)
                String str1 =  "{\"detalhes\":" + response.toString().substring(63);
                int tamanho=str1.length() -9 ;
                String str2 = str1.substring(0,tamanho) + "}";

                //envia retorno formatado para processo de Parsing
                ParseDetalhes pj = new ParseDetalhes(str2);
                pj.parseDetalhes();

                txtNome.setText(ParseDetalhes.Campo1);
                txtBairro.setText(ParseDetalhes.Campo2);
                txtEnderec.setText(ParseDetalhes.Campo3);
                txtPref.setText(ParseDetalhes.Campo4);
                txtCidade.setText(ParseDetalhes.Campo5);
                txtTelefone.setText("Telefone: " + ParseDetalhes.Campo6);
                txtObs.setText(ParseDetalhes.Campo7);

                int tamanho2=ParseDetalhes.Campo10.length();
                if (tamanho2==0){

                }else {
                    txtStartTravel.setText("Inicio da Viagem: " + ParseDetalhes.Campo10);
                    botaoStartTravel.setEnabled(false);
                }

                MapLat = ParseDetalhes.Campo8;
                MapLongt = ParseDetalhes.Campo9;

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
    // Mapa do local da entrega
    public void btMapaDetalhe(View view){

        //transferencia de dados entre Activitys - coordenadas do local da entrega
        Bundle b = new Bundle();
        b.putString("MapLatitude",MapLat);
        b.putString("MapLongitude",MapLongt);

        //inicia nova Activity
        //Intent proximatela = new Intent(getApplicationContext(),MapsDetalhe.class);
        //proximatela.putExtras(b);
        //startActivity(proximatela);

    }


    //======================================================================================================================
    // Atualiza Status - Inicio e Final de Viagem
    public void btStartTravel (View view){

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        horaFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        // ATENÇÃO!!!! pegar localização corrente do motoboy. corrigir
        String lat ="0",lon="0";

        // envia requisição para atualizar status da entrega: VIAGEM INICIADA
        JSON_URL="http://logwebservice.azurewebsites.net/wservice.asmx/StartTravel?IdEntrega="+ IdEntrega + "&latitude=" + lat + "&longitude=" + lon + "&dataleitura=" + dateFormat.format(date) + "%20" + horaFormat.format(date);
        volleyUpdateTravel(JSON_URL);

        txtStartTravel.setText("Inicio da Viagem: " + horaFormat.format(date));
        botaoStartTravel.setEnabled(false);

    }

    public void btEndTravel (View view){

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        horaFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        // ATENÇÃO!!!! pegar localização corrente do motoboy. corrigir
        String lat ="0",lon="0";
        String mStatus = StatusEntrega.substring(0,2);

        // envia requisição para atualizar status da entrega: VIAGEM CONCLUIDA
        JSON_URL="http://logwebservice.azurewebsites.net/wservice.asmx/EndTravel?IdEntrega="+ IdEntrega +
                "&latitude=" + lat + "&longitude=" + lon + "&dataLeitura=" + dateFormat.format(date) + "%20" + horaFormat.format(date)+ "&Status=" + mStatus;
        volleyUpdateTravel(JSON_URL);

        txtStartTravel.setText("Final da Viagem: " + horaFormat.format(date));
        botaoConcluir.setEnabled(false);
        spinner.setEnabled(false);


    }


    //======================================================================================================================
    // Comunicação com Web-service (Volley Library)
    public void volleyUpdateTravel(String url){

        String  REQUEST_TAG = "br.com.loglogistica.updateTravel";

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

}