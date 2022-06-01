package com.dmovil.bdfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dmovil.bdfirebase.entidades.Alumno;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Se declaran las variables de instancia para la base de datos y los EditText
    private DatabaseReference mdatabase;
    EditText control, nombre, apellidos, carrera, telefono, email, direccion;

    //Variables para definir el tipo de operacion (Regstrar o Actualizar)
    private final int OPERACION_REG = 0;
    private final int OPERACION_EDIT = 1;
    private int tipoOperacion = OPERACION_REG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se castean las variables con los EditText
        control = findViewById(R.id.edt_noControl);
        nombre = findViewById(R.id.edt_nombre);
        apellidos = findViewById(R.id.edt_apellidos);
        carrera = findViewById(R.id.edt_carrera);
        telefono = findViewById(R.id.edt_telefono);
        email = findViewById(R.id.edt_email);
        direccion = findViewById(R.id.edt_direccion);
        control.requestFocus();

        //Se hace la conexión con la base de datos
        mdatabase = FirebaseDatabase.getInstance().getReference("Alumno");
    }

    //Metodo para registrar en la base de datos, primero se validan los campos,
    // se establece la operacion registrar y se manda a llamar al metodo existAlumno que evaluará si existe
    // el número de control que se pasa como parametro.
    public void registrar (View view){
        if (validaciones()) {

            tipoOperacion = OPERACION_REG;
            existAlumno(control.getText().toString(), tipoOperacion);

        }
    }
    //Se busca un registro en la base de datos,
    // primero se valida que el campo de busqueda no se encuentre vacio
    //se hace la busqueda en la base de datos y el objeto devuelto se pasa a los EditText
    public void buscar (View view){

        if (validarConsulta()){

            Query findByNoControl = mdatabase.orderByChild("noControl").equalTo(control.getText().toString()).limitToFirst(1);

            findByNoControl.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        for (DataSnapshot child :
                                snapshot.getChildren()) {
                            Alumno alumno = child.getValue(Alumno.class);

                            control.setText(alumno.getNoControl());
                            nombre.setText(alumno.getNombre());
                            apellidos.setText(alumno.getApellidos());
                            carrera.setText(alumno.getCarrera());
                            telefono.setText(alumno.getTelefono());
                            email.setText(alumno.getEmail());
                            direccion.setText(alumno.getDireccion());
                            Toast.makeText(MainActivity.this, "Consulta realizada", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No se encontro resultados", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //Se edita uno de los registros existentes, primero se validan los campos
    // se establece como la operación de actualización y se manda a llamar al metodo existAlumno
    // y se le pasa como parametro el numero de control y el tipo de operacion.
    public void editar (View view) {
        if (validaciones()) {

            tipoOperacion = OPERACION_EDIT;
            existAlumno(control.getText().toString(), tipoOperacion);

        }
    }

    //Elimina un registro
    //Primero valida el campo de busqueda y despues crea la instancia y la elimina.
    public void eliminar (View view) {
        if (validarConsulta()){
            Alumno alumno = new Alumno();
            alumno.setNoControl(control.getText().toString());

            mdatabase.child(alumno.getNoControl()).removeValue();
            Toast.makeText(this, "Registro eliminado...!", Toast.LENGTH_SHORT).show();
            limpiar();
            control.requestFocus();
        }
    }

    //Limpia los EditText y enfoca al EditText de control
    private void limpiar (){
        control.setText("");
        nombre.setText("");
        apellidos.setText("");
        carrera.setText("");
        telefono.setText("");
        email.setText("");
        direccion.setText("");
        control.requestFocus();
    }

    //Método para validar los editText, devuelve un valor booleano,
    // verdadero si ningun campo esta vacío y con la estructura correcta o falso si alguna
    // condicion no se cumple y muestra un mensaje de error con instrucciones.
    public boolean validaciones(){
        boolean bandera = true;
        if(control.getText().toString().isEmpty()){
            control.setError("Dato requerido");
            bandera = false;
        }
        if(nombre.getText().toString().isEmpty()){
            nombre.setError("Dato requerido");
            bandera = false;
        }
        if(apellidos.getText().toString().isEmpty()){
            apellidos.setError("Dato requerido");
            bandera = false;
        }
        if(carrera.getText().toString().isEmpty()){
            carrera.setError("Dato requerido");
            bandera = false;
        }
        if (email.getText().toString().isEmpty()){
            email.setError("Dato requerido");
            bandera = false;
        } else if(!email.getText().toString().matches("[a-zA-Z0-9]+[-_.]*[a-zA-Z0-9]+\\@[a-zA-Z]+\\.[a-zA-Z]+")){
            email.setError("Introduzca una dirección de correo electrónico válida");
            bandera = false;
        }
        if (telefono.getText().toString().isEmpty()){
            telefono.setError("Dato requerido");
            bandera = false;
        } else if(!telefono.getText().toString().matches("(\\+?[0-9]{2,3}\\-)?([0-9]{10})")){
            telefono.setError("El telefono debe contener 10 digitos");
            bandera = false;
        }
        if(direccion.getText().toString().isEmpty()){
            direccion.setError("Este campo es obligatorio");
            bandera = false;
        }
        return bandera;
    }

    //Valida que el campo control se encuentre lleno y manda un mensaje de advertencia si no es así.
    public boolean validarConsulta() {
        boolean bandera = true;
        if (control.getText().toString().isEmpty()) {
            control.setError("Se requiere un criterio de busqueda");
            bandera = false;
        }
        return bandera;
    }

    //Antes de hacer un registro o una actualización se busca en la base de datos si existe el numero de control
    // que se le pasa y dependiendo de los resultados y del tipo de operacion que se planea realizar llama
    // al metodo registrarBD o editarBD o avisa que existe o no existe el alumno que se planea registrar o actualizar.
    public void existAlumno(String control, int tipoOperacion) {

        Query findByNoControl = mdatabase.orderByChild("noControl").equalTo(control).limitToFirst(1);

        findByNoControl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (tipoOperacion == OPERACION_REG) {
                        Toast.makeText(MainActivity.this, "El Alumno ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        editarBD();
                    }
                } else {
                    if (tipoOperacion == OPERACION_REG) {
                        registrarBD();
                    } else {
                        Toast.makeText(MainActivity.this, "El alumno no existe", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Metodo para registrar, se crea el objeto alumno y se inserta en la base de datos.
    public void registrarBD(){
        // Creamos la variable key asignandole el numero de control que servirá como nodo principal
        String key = control.getText().toString();

        Alumno alumno = new Alumno(control.getText().toString(), nombre.getText().toString(),
                apellidos.getText().toString(), carrera.getText().toString(), telefono.getText().toString(),
                email.getText().toString(), direccion.getText().toString());

        //Metemos los valores que llevan las variables
        // del constructor, finalmente con un Toast manda mensaje de registro con exito
        mdatabase.child(key).setValue(alumno);
        Toast.makeText(this, "Registro exitoso...!", Toast.LENGTH_SHORT).show();
        limpiar();
    }

    //Metodo para actualizar un registro en la base de datos, crea el objeto y lo actualiza
    public void editarBD(){
        Alumno alumno = new Alumno(control.getText().toString(), nombre.getText().toString(),
                apellidos.getText().toString(), carrera.getText().toString(), telefono.getText().toString(),
                email.getText().toString(), direccion.getText().toString());

        //Se llama al metodo toMap para crear una coleccion con los atributos del alumno que se va a actualizar
        Map<String, Object> alumnoValues = alumno.toMap();

        //Se actualiza el nodo indicado con el numero de control y se le pasa el objeto Map.
        mdatabase.child(control.getText().toString()).updateChildren(alumnoValues);
        Toast.makeText(this, "Registro actualizado...!", Toast.LENGTH_SHORT).show();
        limpiar();
    }
}