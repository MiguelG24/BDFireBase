package com.dmovil.bdfirebase.entidades;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Se crea la entidad Alumno y se utilizan las anotaciones de lombok para definir solo los atributos de la clase
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alumno {

    private String noControl;
    private String nombre;
    private String apellidos;
    private String carrera;
    private String telefono;
    private String email;
    private String direccion;

    //Metodo que devuelve una coleccion de tipo Map con los atributos de la clase
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("nombre", getNombre());
        result.put("apellidos", getApellidos());
        result.put("carrera", getCarrera());
        result.put("telefono", getTelefono());
        result.put("email", getEmail());
        result.put("direccion", getDireccion());

        return result;
    }
}
