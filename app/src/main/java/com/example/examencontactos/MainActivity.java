package com.example.examencontactos;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Spinner pais;
    private EditText nombre,telefono,nota;
    private ImageView foto;
    private DBHelper db;
    private String rutaImagen="";
    private long editandoId=-1;
    private static final Pattern NOMBRE=Pattern.compile("^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$");
    private static final Pattern TELEFONO=Pattern.compile("^[0-9]{7,15}$");
    private ActivityResultLauncher<String> galeria;
    private ActivityResultLauncher<Void> camara;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=new DBHelper(this);
        pais=findViewById(R.id.spPais);
        nombre=findViewById(R.id.etNombre);
        telefono=findViewById(R.id.etTelefono);
        nota=findViewById(R.id.etNota);
        foto=findViewById(R.id.ivFoto);
        String[] paises={"Honduras (504)","Costa Rica (506)","Guatemala (502)","El Salvador (503)"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,paises);
        pais.setAdapter(adapter);
        galeria=registerForActivityResult(new ActivityResultContracts.GetContent(), uri->{
            if(uri!=null)copiarImagen(uri);
        });
        camara=registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bmp->{
            if(bmp!=null)guardarBitmap(bmp);
        });
        // El filtro aplica la expresión regular a la entrada, además de comprobarla al guardar.
        nombre.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            public void onTextChanged(CharSequence s,int start,int before,int count){
                if(s.toString().matches(".*[^\\p{L} '\\-].*")){
                    String limpio=s.toString().replaceAll("[^\\p{L} '\\-]","");
                    nombre.removeTextChangedListener(this);
                    nombre.setText(limpio);nombre.setSelection(limpio.length());
                    nombre.addTextChangedListener(this);
                }
            }
            public void afterTextChanged(Editable e){}
        });
        telefono.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            public void onTextChanged(CharSequence s,int start,int before,int count){
                if(!s.toString().matches("[0-9]*")){
                    String limpio=s.toString().replaceAll("[^0-9]","");
                    telefono.removeTextChangedListener(this);
                    telefono.setText(limpio);telefono.setSelection(limpio.length());
                    telefono.addTextChangedListener(this);
                }
            }
            public void afterTextChanged(Editable e){}
        });
        findViewById(R.id.btnFoto).setOnClickListener(v->new AlertDialog.Builder(this)
            .setTitle("Imagen del contacto")
            .setItems(new String[]{"Tomar fotografía","Seleccionar de galería"},(dialog,op)->{
                if(op==0)camara.launch(null);else galeria.launch("image/*");
            }).show());
        findViewById(R.id.btnGuardar).setOnClickListener(v->guardar());
        findViewById(R.id.btnLista).setOnClickListener(v->startActivity(new Intent(this,ListaContactosActivity.class)));
        editandoId=getIntent().getLongExtra("editar_id",-1);
        if(editandoId!=-1)cargar(editandoId);
    }
    private void cargar(long id){
        Contactos c=db.obtener(id);
        if(c==null)return;
        for(int i=0;i<pais.getCount();i++)if(pais.getItemAtPosition(i).toString().equals(c.getPais()))pais.setSelection(i);
        nombre.setText(c.getNombre());telefono.setText(c.getTelefono());nota.setText(c.getNota());
        rutaImagen=c.getImagen();mostrarImagen();
        ((Button)findViewById(R.id.btnGuardar)).setText("Actualizar contacto");
    }
    private void guardar(){
        String n=nombre.getText().toString().trim();
        String t=telefono.getText().toString().trim();
        String no=nota.getText().toString().trim();
        if(n.isEmpty()){alerta("Debe escribir un nombre");nombre.requestFocus();return;}
        if(t.isEmpty()){alerta("Debe escribir un teléfono");telefono.requestFocus();return;}
        if(no.isEmpty()){alerta("Debe escribir una nota");nota.requestFocus();return;}
        if(!NOMBRE.matcher(n).matches()){alerta("Nombre inválido: escriba solo letras y espacios");return;}
        if(!TELEFONO.matcher(t).matches()){alerta("Teléfono inválido: use de 7 a 15 dígitos");return;}
        Contactos c=new Contactos(editandoId,pais.getSelectedItem().toString(),n,t,no,rutaImagen);
        boolean ok=editandoId==-1?db.guardar(c)!=-1:db.actualizar(c);
        if(ok){
            Toast.makeText(this,"Contacto guardado correctamente",Toast.LENGTH_SHORT).show();
            if(editandoId!=-1){finish();return;}
            nombre.setText("");telefono.setText("");nota.setText("");rutaImagen="";mostrarImagen();
        }else alerta("No se pudo guardar el contacto");
    }
    private void alerta(String mensaje){new AlertDialog.Builder(this).setTitle("Alerta").setMessage(mensaje).setPositiveButton("Aceptar",null).show();}
    private void copiarImagen(Uri uri){
        try(InputStream in=getContentResolver().openInputStream(uri)){
            Bitmap bmp=BitmapFactory.decodeStream(in);
            if(bmp==null)throw new Exception("Imagen no válida");
            guardarBitmap(bmp);
        }catch(Exception e){alerta("No se pudo obtener la imagen");}
    }
    private void guardarBitmap(Bitmap bmp){
        try{
            File dir=new File(getFilesDir(),"photos");if(!dir.exists())dir.mkdirs();
            File archivo=new File(dir,"foto_"+System.currentTimeMillis()+".jpg");
            try(FileOutputStream out=new FileOutputStream(archivo)){bmp.compress(Bitmap.CompressFormat.JPEG,85,out);}
            rutaImagen=archivo.getAbsolutePath();
            mostrarImagen();
        }catch(Exception e){alerta("No se pudo guardar la imagen");}
    }
    private void mostrarImagen(){
        if(!rutaImagen.isEmpty()&&new File(rutaImagen).exists())foto.setImageBitmap(BitmapFactory.decodeFile(rutaImagen));
        else foto.setImageResource(android.R.drawable.ic_menu_camera);
    }
}