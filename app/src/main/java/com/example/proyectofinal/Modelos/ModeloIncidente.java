package com.example.proyectofinal.Modelos;

public class ModeloIncidente {

    String titulo, descripcion, path, estado, nombre, fechaCreacion, fechaSolucion;



    public ModeloIncidente() {}

    public ModeloIncidente(String titulo, String descripcion, String path, String estado, String nombre, String fechaCreacion, String fechaSolucion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.path = path;
        this.estado = estado;
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion;
        this.fechaSolucion = fechaSolucion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getnombre() {
        return nombre;
    }

    public void setnombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaSolucion() {
        return fechaSolucion;
    }

    public void setFechaSolucion(String fechaSolucion) {
        this.fechaSolucion = fechaSolucion;
    }
}
