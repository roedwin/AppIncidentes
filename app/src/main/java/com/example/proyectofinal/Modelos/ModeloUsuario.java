package com.example.proyectofinal.Modelos;

public class ModeloUsuario {
    String usuario, nombre, rol, pass, fechaCreacion;

    public ModeloUsuario() {}

    public ModeloUsuario(String usuario, String nombre, String rol, String pass, String fechaCreacion) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.rol = rol;
        this.pass = pass;
        this.fechaCreacion = fechaCreacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
