package com.logvai.logvai;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseJSON2 {

    public static final String JSON_ARRAY = "entregas2";

    public static String[] IDs;
    public static String[] Titulos;
    public static String[] SubTitulos;
    public static String[] SubTitulos1;

    // defina aqui os campos a serem lidos
    public static final String KEY_ID = "ID_Entrega";
    public static final String KEY_TITULO = "Endereco";
    public static final String KEY_SUBTITULO = "Numero";
    public static final String KEY_SUBTITULO1 = "Complemento";

    private JSONArray users = null;
    private String json;

    public ParseJSON2(String json){
        this.json = json;
    }

    protected void parseJSON2(){

        JSONObject jsonObject=null;
        try {
            jsonObject = new JSONObject(json);
            users = jsonObject.getJSONArray(JSON_ARRAY);

            Titulos = new String[users.length()];
            SubTitulos = new String[users.length()];
            SubTitulos1 = new String[users.length()];
            IDs = new String[users.length()];

            for(int i=0;i<users.length();i++){
                JSONObject jo = users.getJSONObject(i);
                Titulos[i] = jo.getString(KEY_TITULO);
                SubTitulos[i] = jo.getString(KEY_SUBTITULO);
                SubTitulos1[i] = jo.getString(KEY_SUBTITULO1);
                IDs[i] = jo.getString(KEY_ID);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}