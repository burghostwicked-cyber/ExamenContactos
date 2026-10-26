package com.example.examencontactos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="contactos.db";
    private static final int VERSION=1;
    public DBHelper(Context context){super(context,DB_NAME,null,VERSION);}
    @Override public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE contactos (id INTEGER PRIMARY KEY AUTOINCREMENT, pais TEXT NOT NULL, nombre TEXT NOT NULL, telefono TEXT NOT NULL, nota TEXT NOT NULL, imagen TEXT)");
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS contactos"); onCreate(db);
    }
    public long guardar(Contactos c){
        ContentValues v=valores(c);
        return getWritableDatabase().insert("contactos",null,v);
    }
    public boolean actualizar(Contactos c){
        return getWritableDatabase().update("contactos",valores(c),"id=?",new String[]{String.valueOf(c.getId())})>0;
    }
    public boolean eliminar(long id){
        return getWritableDatabase().delete("contactos","id=?",new String[]{String.valueOf(id)})>0;
    }
    public Contactos obtener(long id){
        try(Cursor cursor=getReadableDatabase().query("contactos",null,"id=?",new String[]{String.valueOf(id)},null,null,null)){
            return cursor.moveToFirst()?leer(cursor):null;
        }
    }
    public List<Contactos> listar(String filtro){
        ArrayList<Contactos> lista=new ArrayList<>();
        String[] args={"%"+filtro+"%","%"+filtro+"%"};
        try(Cursor cursor=getReadableDatabase().query("contactos",null,"nombre LIKE ? OR telefono LIKE ?",args,null,null,"nombre COLLATE NOCASE ASC")){
            while(cursor.moveToNext())lista.add(leer(cursor));
        }
        return lista;
    }
    private ContentValues valores(Contactos c){
        ContentValues v=new ContentValues();
        v.put("pais",c.getPais());v.put("nombre",c.getNombre());
        v.put("telefono",c.getTelefono());v.put("nota",c.getNota());v.put("imagen",c.getImagen());
        return v;
    }
    private Contactos leer(Cursor cur){
        return new Contactos(cur.getLong(cur.getColumnIndexOrThrow("id")),
            cur.getString(cur.getColumnIndexOrThrow("pais")),cur.getString(cur.getColumnIndexOrThrow("nombre")),
            cur.getString(cur.getColumnIndexOrThrow("telefono")),cur.getString(cur.getColumnIndexOrThrow("nota")),
            cur.getString(cur.getColumnIndexOrThrow("imagen")));
    }
}