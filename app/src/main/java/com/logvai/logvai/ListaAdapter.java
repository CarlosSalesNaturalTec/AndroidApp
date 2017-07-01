package com.logvai.logvai;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//ArrayAdapter será responsável por administrar e retornar as Views para a nossa lista
public class ListaAdapter extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] ids;
    private final String[] titulos;
    private final String[] subtitulos;
    private final String[] subtitulos1;

    //O construtor pode receber quantos parametros forem necessários mas um array de String deve ser passado como parametro do construtor da super-classe
    public ListaAdapter(Activity context, String[] ids, String[] titulos, String[] subtitulos, String[] subtitulos1)
    {
        super(context, R.layout.lista_itens, ids);
        this.context = context;
        this.ids = ids;
        this.titulos = titulos;
        this.subtitulos = subtitulos;
        this.subtitulos1 = subtitulos1;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        //Aqui retornamos o layout para podermos administrar as Views da tela
        View rowView= inflater.inflate(R.layout.lista_itens, null, true);

        //---retorne a referencia de todos os objetos do layout
        TextView txtID = (TextView) rowView.findViewById(R.id.txtID);
        TextView txtTitulo = (TextView) rowView.findViewById(R.id.txtTitulo);
        TextView txtSubtitulo = (TextView)rowView.findViewById(R.id.txtSubTitulo);
        TextView txtSubtitulo1 = (TextView)rowView.findViewById(R.id.txtSubTitulo1);

        //---passe os textos baseados na posição atual do listView
        txtID.setText("ID: " + ids[position]);

        // formata endereço exibindo somente nome do bairro (até a primeira vírgula)
        String OrigemF1 = titulos[position];
        int pos = OrigemF1.indexOf(",");
        String OrigemF2="";
        try {
            OrigemF2 = "Origem: " + OrigemF1.substring(0, pos);
        } catch (Exception ex) {
            OrigemF2 = "Origem: ";
        }
        txtTitulo.setText(OrigemF2);

        String DestinoF1 = subtitulos[position];
        String DestinoF2 ="";
        int pos1 = DestinoF1.indexOf(",");
        try {
            DestinoF2 = "Destino: " + DestinoF1.substring(0, pos1);
        } catch (Exception ex) {
            DestinoF2 = "Destino:";
        }
        txtSubtitulo.setText(DestinoF2);

        txtSubtitulo1.setText("Distância: " + subtitulos1[position] + "Km");
        return rowView;
    }
}