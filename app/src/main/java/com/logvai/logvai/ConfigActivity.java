package com.logvai.logvai;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigActivity extends Activity {

    EditText editID,editsenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        editID = (EditText) findViewById(R.id.editIDConfig);
        editsenha = (EditText) findViewById(R.id.editSenha);
    }

    public void SalvarID(View view){

        String senha = editsenha.getText().toString();

        switch (senha){
            case "030517" :
                SharedPreferences preferences = getSharedPreferences("LOGVAI_CONFIG", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString("IDMotoboy",editID.getText().toString());
                editor.commit();

                Global.globalID = editID.getText().toString();
                Toast.makeText(getApplicationContext(), "ID Salvo:"+ Global.globalID + ". Reiniciar Aplicativo", Toast.LENGTH_SHORT).show();
                break;
            default :
                Toast.makeText(getApplicationContext(), "SENHA INCORRETA" , Toast.LENGTH_SHORT).show();
        }

    }

}
