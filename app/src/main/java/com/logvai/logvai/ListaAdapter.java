package com.logvai.logvai;

/**
 * Created by Carlos on 29/05/2017.
 */

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

    //O construtor pode receber quantos parametros forem necessários mas um array de String deve ser passado como parametro do construtor da super-classe
    public ListaAdapter(Activity context, String[] ids, String[] titulos, String[] subtitulos)
    {
        super(context, R.layout.lista_itens, ids);
        this.context = context;
        this.ids = ids;
        this.titulos = titulos;
        this.subtitulos = subtitulos;
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

        //---passe os textos baseados na posição atual do listView
        txtID.setText(ids[position]);
        txtTitulo.setText(titulos[position]);
        txtSubtitulo.setText(subtitulos[position]);
        return rowView;
    }
}