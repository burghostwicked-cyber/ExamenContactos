package com.example.examencontactos;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListaContactosActivity extends AppCompatActivity {
    private DBHelper db;
    private ListView lista;
    private EditText buscar;
    private Contactos seleccionado;
    private List<Contactos> contactos=new ArrayList<>();
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_lista_contactos);
        db=new DBHelper(this);lista=findViewById(R.id.lvContactos);buscar=findViewById(R.id.etBuscar);
        findViewById(R.id.btnAtras).setOnClickListener(v->finish());
        buscar.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            public void onTextChanged(CharSequence s,int start,int before,int count){cargar();}
            public void afterTextChanged(Editable e){}
        });
        lista.setOnItemClickListener((parent,view,pos,id)->{
            seleccionado=contactos.get(pos);
            new AlertDialog.Builder(this).setTitle("Acción")
                .setMessage("¿Desea llamar a "+seleccionado.getNombre()+"?")
                .setNegativeButton("No",(d,w)->{})
                .setPositiveButton("Sí",(d,w)->irALlamar()).show();
        });
        findViewById(R.id.btnEliminar).setOnClickListener(v->{
            if(!comprobar())return;
            new AlertDialog.Builder(this).setTitle("Eliminar contacto")
                .setMessage("¿Eliminar a "+seleccionado.getNombre()+"?")
                .setNegativeButton("No",null).setPositiveButton("Eliminar",(d,w)->{
                    db.eliminar(seleccionado.getId());seleccionado=null;cargar();
                    Toast.makeText(this,"Contacto eliminado",Toast.LENGTH_SHORT).show();
                }).show();
        });
        findViewById(R.id.btnActualizar).setOnClickListener(v->{
            if(!comprobar())return;
            Intent i=new Intent(this,MainActivity.class);i.putExtra("editar_id",seleccionado.getId());startActivity(i);
        });
        findViewById(R.id.btnCompartir).setOnClickListener(v->{
            if(!comprobar())return;
            Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT,seleccionado.getNombre()+"\n"+seleccionado.getNumeroCompleto());
            startActivity(Intent.createChooser(i,"Compartir contacto"));
        });
        findViewById(R.id.btnVerImagen).setOnClickListener(v->{
            if(!comprobar())return;
            String ruta=seleccionado.getImagen();
            if(ruta==null||ruta.isEmpty()||!new File(ruta).exists()){alerta("Este contacto no tiene imagen");return;}
            ImageView imagen=new ImageView(this);
            imagen.setImageBitmap(BitmapFactory.decodeFile(ruta));
            imagen.setAdjustViewBounds(true);imagen.setPadding(20,20,20,20);
            new AlertDialog.Builder(this).setTitle(seleccionado.getNombre())
                .setView(imagen).setPositiveButton("Cerrar",null).show();
        });
    }
    @Override protected void onResume(){super.onResume();cargar();}
    private void cargar(){
        if(db==null||buscar==null)return;
        long idAnterior=seleccionado==null?-1:seleccionado.getId();
        contactos=db.listar(buscar.getText().toString().trim());
        ArrayAdapter<Contactos> a=new ArrayAdapter<Contactos>(this,R.layout.item_contacto,contactos){
            @Override public View getView(int position,View convertView,ViewGroup parent){
                View v=convertView;
                if(v==null)v=LayoutInflater.from(getContext()).inflate(R.layout.item_contacto,parent,false);
                Contactos c=getItem(position);
                ((TextView)v.findViewById(R.id.tvNombreItem)).setText(c.getNombre());
                ((TextView)v.findViewById(R.id.tvTelefonoItem)).setText(c.getNumeroCompleto());
                ImageView foto=v.findViewById(R.id.ivMiniatura);
                String ruta=c.getImagen();
                if(ruta!=null&&!ruta.isEmpty()&&new File(ruta).exists())foto.setImageBitmap(BitmapFactory.decodeFile(ruta));
                else foto.setImageResource(android.R.drawable.ic_menu_myplaces);
                v.setBackgroundColor(c.getId()==(seleccionado==null?-1:seleccionado.getId())?0xFFB5D4FF:(position%2==0?0xFFFFFFFF:0xFFE4F0FF));
                return v;
            }
        };
        lista.setAdapter(a);
        seleccionado=null;
        for(Contactos c:contactos)if(c.getId()==idAnterior){seleccionado=c;break;}
        a.notifyDataSetChanged();
    }
    private boolean comprobar(){
        if(seleccionado!=null)return true;
        alerta("Primero seleccione un contacto de la lista");
        return false;
    }
    private void irALlamar(){
        if(!comprobar())return;
        Intent i=new Intent(this,LlamarActivity.class);
        i.putExtra("contacto_id",seleccionado.getId());startActivity(i);
    }
    private void alerta(String msg){new AlertDialog.Builder(this).setTitle("Alerta").setMessage(msg).setPositiveButton("Aceptar",null).show();}
}