package com.logvai.logvai;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListaAdapter2 extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] ids;
    private final String[] titulos;
    private final String[] subtitulos;
    private final String[] subtitulos1;

    //O construtor pode receber quantos parametros forem necessários mas um array de String deve ser passado como parametro do construtor da super-classe
    public ListaAdapter2(Activity context, String[] ids, String[] titulos, String[] subtitulos, String[] subtitulos1)
    {
        super(context, R.layout.lista_itens2, ids);
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
        View rowView= inflater.inflate(R.layout.lista_itens2, null, true);

        //---retorne a referencia de todos os objetos do layout
        TextView txtID = (TextView) rowView.findViewById(R.id.txtID);
        TextView txtTitulo = (TextView) rowView.findViewById(R.id.txtTitulo);
        TextView txtSubtitulo = (TextView)rowView.findViewById(R.id.txtSubTitulo);
        TextView txtSubtitulo1 = (TextView)rowView.findViewById(R.id.txtSubTitulo1);

        //---passe os textos baseados na posição atual do listView
        txtID.setText("ID: " + ids[position]);
        txtTitulo.setText(titulos[position]);
        txtSubtitulo.setText("Número: " + subtitulos[position]);
        txtSubtitulo1.setText(subtitulos1[position]);
        return rowView;
    }
}