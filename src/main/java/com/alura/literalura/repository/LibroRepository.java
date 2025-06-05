package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository <Libro,Long>{
    Optional<Libro>findByTituloContainsIgnoreCase(String nombreLibro);
    Optional<Libro>findByTituloAndAutores(String titulo, List<Autor> autores);
    @Query("SELECT l FROM Libro l WHERE :codigo IN ELEMENTS(l.lenguajes)")
    List<Libro>findByCodigoLenguajes(@Param("codigo") String codigo);
}
