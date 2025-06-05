package com.alura.literalura.service;

public interface IConversorDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
