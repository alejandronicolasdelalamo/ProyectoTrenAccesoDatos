package com.example.tren;

import net.datafaker.Faker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class GeneradorTrenesSql {

    public static void main(String[] args) {
        int cantidadAGenerar = 20000;
        String rutaArchivo = "src/main/resources/data.sql";

        Faker faker = new Faker(Locale.of("es"));

        String[] fabricantes = {
            "Baldwin Locomotive Works", "ALCO", "CAF", "TALGO", 
            "Siemens", "Alstom", "Bombardier", "Swindon Works"
        };

        String[] modelos = {
            "Mallard 4468", "Flying Scotsman 4472", "Big Boy 4014", 
            "Polar Express 1225", "Orient Express", "Civia 465", "Oaris 350"
        };

        String[] propulsiones = {"ELECTRICA", "DIESEL", "HIBRIDA"};
        String[] servicios = {"ALTA_VELOCIDAD", "CERCANIAS", "REGIONAL", "MERCANCIAS"};
        String[] colores = {"Rojo", "Blanco", "Negro", "Azul", "Verde", "Gris"};

        System.out.println("Generando " + cantidadAGenerar + " registros SQL en " + rutaArchivo + "...");

        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo, StandardCharsets.UTF_8))) {

            writer.println("-- Script de datos generado automáticamente");
            writer.println("DELETE FROM trenes;");
            writer.println();

            for (long i = 1; i <= cantidadAGenerar; i++) {
                int anio = faker.number().numberBetween(1850, 2025);
                int velocidadMaxima = (anio < 1900) ? faker.number().numberBetween(30, 85) : faker.number().numberBetween(80, 330);

                String fabricante = faker.options().option(fabricantes).replace("'", "''");
                String modelo = (faker.options().option(modelos) + " " + faker.regexify("[A-Z]{1,2}-[0-9]{2}")).replace("'", "''");
                String matricula = "TR-" + String.format("%05d", i);
                double precio = faker.number().randomDouble(2, 150000, 4500000);
                String propulsion = faker.options().option(propulsiones);
                String servicio = faker.options().option(servicios);
                String color = faker.options().option(colores);

                String sqlInsert = String.format(Locale.US,
                    "INSERT INTO trenes (fabricante, modelo, matricula, anio, precio, velocidad_maxima, propulsion, tipo_servicio, color) " +
                    "VALUES ('%s', '%s', '%s', %d, %.2f, %d, '%s', '%s', '%s');",
                    fabricante, modelo, matricula, anio, precio, velocidadMaxima, propulsion, servicio, color);

                writer.println(sqlInsert);
            }

            System.out.println("¡Proceso finalizado! Archivo data.sql generado exitosamente con " + cantidadAGenerar + " registros.");

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo SQL: " + e.getMessage());
        }
    }
}