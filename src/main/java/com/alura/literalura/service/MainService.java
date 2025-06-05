package com.alura.literalura.service;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MainService {
    private Scanner teclado=new Scanner(System.in);
    private BuscadorGutendex buscador=new BuscadorGutendex();
    public  ConversorDatos conversor=new ConversorDatos();
    @Autowired
    private LibroRepository repositoryLibro;
    @Autowired
    private AutorRepository repositoryAutor;

    public void muestraMenu(){
        var opcion=-1;
        while (opcion!=0){
            System.out.println("Bienvenido a Literalura!");
            System.out.println("Por favor teclea una de estas opciones: ");
            var menu= """
                    1- Buscar y Registrar el Libro Más Popular Por Título o Autor
                    2- Mostrar Libros Registrados
                    3- Mostrar Autores Registrados
                    4- Mostrar Autores Vivos en el Año Seleccionado
                    5- Mostrar Libros Registrados por Idioma
                    0- Salir de la Aplicación
                    """;
            System.out.println(menu);
            String opcionInput=teclado.nextLine();
            try {
                opcion=Integer.parseInt(opcionInput);
            }catch (NumberFormatException e){
                System.out.println("Opción inválida, por favor intente de nuevo (solo ingrese el número).\n");
                continue;
            }
            switch (opcion){
                case 0:
                    System.out.println("Gracias por usar Literalura.");
                    System.exit(0);
                    break;
                case 1:
                    System.out.println("Por favor escriba el título o autor del libro que desea buscar: ");
                    var tituloLibro=teclado.nextLine();
                    var json=buscador.obtenBusqueda(tituloLibro.replace(" ","%20").toLowerCase());
                    List<DatosLibro> resultados=conversor.obtenerDatos(json,DatosListaLibros.class).resultados();
                    if (!resultados.isEmpty()) {
                        var primerLibro = resultados.get(0);
                        String autores=primerLibro.autores().stream()
                                .map(DatosAutor::nombre)
                                .collect(Collectors.joining("\n"));
                        String lenguajes=String.join(", ",primerLibro.lenguajes());
                        System.out.println("""
                            ****LIBRO****
                            
                            Título: %s
                            Autores: 
                            %s
                            Lenguajes: %s
                            Número de descargas: %d
                            
                            *************
                            """.formatted(primerLibro.titulo(),autores,lenguajes,primerLibro.descargas()));
                        Optional<Libro> libroExistente=repositoryLibro.findByTituloAndAutores(primerLibro.titulo(), primerLibro.autores().stream()
                                .map(a-> {
                                    Optional<Autor> autorExistente = repositoryAutor.findByNombreIgnoreCase(a.nombre());
                                    return autorExistente.orElse(null);
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                        if (libroExistente.isPresent()){
                            return;
                        }
                        Libro nuevoLibro=new Libro(primerLibro);
                        for (DatosAutor datosAutor:primerLibro.autores()){
                            Optional<Autor>autorOptional=repositoryAutor.findByNombreIgnoreCase(datosAutor.nombre());
                            Autor autor;
                            if (autorOptional.isPresent()){
                                autor=autorOptional.get();
                            }else {
                                autor=new Autor(datosAutor);
                                repositoryAutor.save(autor);
                            }
                            autor.agregaLibro(nuevoLibro);
                        }
                        repositoryLibro.save(nuevoLibro);
                    }else {
                        System.out.println("No se encontró el libro.");
                    }
                    break;
                case 2:
                    List<Libro>libros=repositoryLibro.findAll();
                    if (libros.isEmpty()){
                        System.out.println("No hay libros registrados.");
                        return;
                    }
                    for (Libro libro:libros){
                        String autoresString=libro.getAutores().stream()
                                .map(a->a.getNombre())
                                .collect(Collectors.joining("\n"));
                        System.out.println("""
                            ****LIBRO****
                            
                            Título: %s
                            Autores: 
                            %s
                            Lenguajes: %s
                            Número de descargas: %d
                            
                            *************
                            """.formatted(libro.getTitulo(),autoresString,libro.getLenguajes(),libro.getDescargas()));
                    }
                    break;
                case 3:
                    List<Autor> autores=repositoryAutor.findAll();
                    for (Autor autor:autores){
                        System.out.println("**** AUTOR ****");
                        System.out.println("Nombre: "+autor.getNombre());
                        System.out.println("Año de Nacimiento: "+formatoFechas(autor.getNacimiento()));
                        System.out.println("Año de Muerte: "+formatoFechas(autor.getMuerte()));
                        String librosString=autor.getLibros().stream()
                                .map(Libro::getTitulo)
                                .collect(Collectors.joining("\n"));
                        System.out.println("Libros:\n"+librosString);
                        System.out.println("***************");
                    }
                    break;
                case 4:
                    System.out.println("Por favor ingrese el año que desea investigar: ");
                    Integer fecha;
                    String fechaInput=teclado.nextLine();
                    try {
                        fecha=Integer.parseInt(fechaInput);
                    }catch (NumberFormatException e){
                        System.out.println("Opción inválida.\n");
                        continue;
                    }
                    List<Autor>autoresPorFecha=repositoryAutor.findAll().stream()
                            .filter(a->a.getNacimiento()!=null&&
                                    a.getNacimiento()<=fecha&&
                                    (a.getMuerte()==null||a.getMuerte()>=fecha))
                            .collect(Collectors.toList());
                    for (Autor autor:autoresPorFecha){
                        System.out.println("**** AUTOR ****");
                        System.out.println("Nombre: "+autor.getNombre());
                        System.out.println("Año de Nacimiento: "+formatoFechas(autor.getNacimiento()));
                        System.out.println("Año de Muerte: "+formatoFechas(autor.getMuerte()));
                        String librosString=autor.getLibros().stream()
                                .map(Libro::getTitulo)
                                .collect(Collectors.joining("\n"));
                        System.out.println("Libros:\n"+librosString);
                        System.out.println("***************");
                    }
                    break;
                case 5:
                    System.out.println("Por favor ingrese el código de idioma que desee buscar (Ej. es-Español, en-Inglés)");
                    String codigo=teclado.nextLine().toLowerCase();
                    if (codigo.length()!=2||!codigo.matches("[a-zA-Z]{2}")){
                        System.out.println("Código inválido.\n");
                        continue;
                    }
                    List<Libro>librosPorIdioma=repositoryLibro.findByCodigoLenguajes(codigo);
                    if (librosPorIdioma.isEmpty()){
                        System.out.println("No se encontraron libros en el idioma especificado: "+codigo);
                    }else {
                        librosPorIdioma.forEach(l-> System.out.println("""
                            ****LIBRO****
                            
                            Título: %s
                            Autores: 
                            %s
                            Lenguajes: %s
                            Número de descargas: %d
                            
                            *************
                            """.formatted(l.getTitulo(),l.getAutores().stream().map(Autor::getNombre).collect(Collectors.joining("\n")),
                                String.join(", ",l.getLenguajes()),
                                l.getDescargas())));
                    }
                    break;
                default:
                    System.out.println("Opción inválida, por favor intente de nuevo.\n");
                    break;
            }
        }
    }
    private String formatoFechas(Integer fecha){
        if(fecha==null){
            return "No disponible";
        } else if (fecha<0) {
            return Math.abs(fecha)+" BC";
        }else {
            return fecha.toString();
        }
    }

}
