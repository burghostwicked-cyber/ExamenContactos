package com.example.examencontactos;

public class Contactos {
    private long id;
    private String pais, nombre, telefono, nota, imagen;
    public Contactos(long id, String pais, String nombre, String telefono, String nota, String imagen) {
        this.id=id; this.pais=pais; this.nombre=nombre; this.telefono=telefono; this.nota=nota; this.imagen=imagen;
    }
    public long getId(){return id;}
    public void setId(long id){this.id=id;}
    public String getPais(){return pais;}
    public void setPais(String pais){this.pais=pais;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public String getTelefono(){return telefono;}
    public void setTelefono(String telefono){this.telefono=telefono;}
    public String getNota(){return nota;}
    public void setNota(String nota){this.nota=nota;}
    public String getImagen(){return imagen;}
    public void setImagen(String imagen){this.imagen=imagen;}
    public String getPrefijo() {
        if(pais.startsWith("Honduras"))return "+504";
        if(pais.startsWith("Costa Rica"))return "+506";
        if(pais.startsWith("Guatemala"))return "+502";
        if(pais.startsWith("El Salvador"))return "+503";
        return "";
    }
    public String getNumeroCompleto(){return getPrefijo()+telefono;}
}