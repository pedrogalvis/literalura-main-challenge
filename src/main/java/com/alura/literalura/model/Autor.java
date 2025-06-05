package com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer nacimiento;
    private Integer muerte;

    @ManyToMany(mappedBy = "autores", fetch = FetchType.EAGER)
    private List<Libro> libros=new ArrayList<>();

    public Autor(){}

    public Autor(DatosAutor datosAutor){
        this.nombre= datosAutor.nombre();
        this.nacimiento=datosAutor.nacimiento();
        this.muerte=datosAutor.muerte();
    }

    public void agregaLibro(Libro libro){
        if (libro==null){
            libros=new ArrayList<>();
        }

        if(!libros.contains(libro)){
            libros.add(libro);
        }
        if (!libro.getAutores().contains(this)){
            libro.getAutores().add(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(Integer nacimiento) {
        this.nacimiento = nacimiento;
    }

    public Integer getMuerte() {
        return muerte;
    }

    public void setMuerte(Integer muerte) {
        this.muerte = muerte;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }
}

