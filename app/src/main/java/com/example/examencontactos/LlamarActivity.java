package com.example.examencontactos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LlamarActivity extends AppCompatActivity {
    private Contactos contacto;
    private ActivityResultLauncher<String> pedirPermiso;
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_llamar);
        pedirPermiso=registerForActivityResult(new ActivityResultContracts.RequestPermission(),permitido->{
            if(permitido)llamadaDirecta();
            else aviso("Se necesita el permiso de llamadas para llamar directamente.");
        });
        contacto=new DBHelper(this).obtener(getIntent().getLongExtra("contacto_id",-1));
        if(contacto==null){finish();return;}
        ((TextView)findViewById(R.id.tvNombreLlamada)).setText(contacto.getNombre());
        ((TextView)findViewById(R.id.tvNumeroLlamada)).setText(contacto.getNumeroCompleto());
        findViewById(R.id.btnRegresar).setOnClickListener(v->finish());
        findViewById(R.id.btnLlamar).setOnClickListener(v->confirmar());
    }
    private void confirmar(){
        new AlertDialog.Builder(this).setTitle("Acción")
            .setMessage("¿Desea llamar a "+contacto.getNombre()+"?")
            .setNegativeButton("No",null).setPositiveButton("Sí",(d,w)->{
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED)llamadaDirecta();
                else pedirPermiso.launch(Manifest.permission.CALL_PHONE);
            }).show();
    }
    private void llamadaDirecta(){
        try{
            Intent intent=new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+contacto.getNumeroCompleto()));
            startActivity(intent);
        }catch(SecurityException|android.content.ActivityNotFoundException e){
            aviso("No es posible realizar la llamada en este dispositivo.");
        }
    }
    private void aviso(String msg){new AlertDialog.Builder(this).setTitle("Aviso").setMessage(msg).setPositiveButton("Aceptar",null).show();}
}