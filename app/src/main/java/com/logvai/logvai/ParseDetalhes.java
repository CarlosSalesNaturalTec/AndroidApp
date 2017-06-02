package com.logvai.logvai;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseDetalhes {

    public static final String JSON_ARRAY = "detalhes";

    public static String Campo1;
    public static String Campo2;
    public static String Campo3;
    public static String Campo4;
    public static String Campo5;
    public static String Campo6;
    public static String Campo7;
    public static String Campo8;
    public static String Campo9;
    public static String Campo10;

    // defina aqui os campos a serem lidos
    public static final String KEY1 = "Nome_Destinatario";
    public static final String KEY2 = "Bairro";
    public static final String KEY3 = "Endereco";
    public static final String KEY4 = "Ponto_Ref";
    public static final String KEY5 = "Cidade";
    public static final String KEY6 = "Telefone";
    public static final String KEY7 = "Observacoes";
    public static final String KEY8 = "Latitude";
    public static final String KEY9 = "Longitude";
    public static final String KEY10 = "Partida_Data";

    private JSONArray users = null;
    private String json;

    public ParseDetalhes(String json){
        this.json = json;
    }

    protected void parseDetalhes(){

        // Esta classe considera que só existe um registro no arquivo JSON
        JSONObject jsonObject=null;
        try {
            jsonObject = new JSONObject(json);
            users = jsonObject.getJSONArray(JSON_ARRAY);

            for(int i=0;i<users.length();i++){
                JSONObject jo = users.getJSONObject(i);
                Campo1 = jo.getString(KEY1);
                Campo2 = jo.getString(KEY2);
                Campo3 = jo.getString(KEY3);
                Campo4 = jo.getString(KEY4);
                Campo5 = jo.getString(KEY5);
                Campo6 = jo.getString(KEY6);
                Campo7 = jo.getString(KEY7);
                Campo8 = jo.getString(KEY8);
                Campo9 = jo.getString(KEY9);
                Campo10 = jo.getString(KEY10);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}