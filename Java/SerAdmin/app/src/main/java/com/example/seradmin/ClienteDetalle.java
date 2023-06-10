package com.example.seradmin;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayoutStates;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seradmin.InterfazUsuari.*;
import com.example.seradmin.Recycler.Cliente;
import com.example.seradmin.database.eventosDatabase.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.C;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClienteDetalle extends AppCompatActivity {

    private EditText nombreCliente, apellidoCliente, dniCliente, dniGestor, telefonoCliente, passCliente;
    private Cliente perfil;
    private TextView alertDniClienteDetalle;
    private Button eliminarCliente, modificarCliente;
    ImageView editarNombre, editarApellido, editarDniCliente, editarDniGestor, editarTelefono, editarPass;
    FirebaseFirestore db;
    Cliente cliente = new Cliente();
    Gestor gestor = new Gestor();

    public static final int CLAVE_CLIENTE_MODIFICADO = 80;
    public static final int CLAVE_CLIENTE_ELIMINADO = 81;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_detalle);

        //inicializa las vistas,recupera los elementos de la interfaz
        initViews();
        // inicializa los listeners, agrega el comportamiento a los elementos de la interfaz de usuario (botones) cuando se hacen clic
        initListeners();

        // inicializa los valores, establece los valores en los elementos de la interfaz de usuario
        //initValues();

        db = FirebaseFirestore.getInstance();
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("Gestor")) {
                gestor = (Gestor) getIntent().getSerializableExtra("Gestor");
                if (gestor != null) {
                    Log.d(TAG, gestor.toString());
                    Log.d(TAG, "Hola " + "No debería estar aqui si vengo de cliente directo");
                } else {
                    dniCliente.setFocusable(false);
                    dniCliente.setFocusableInTouchMode(false);
                    dniCliente.setKeyListener(null);
                    dniCliente.setClickable(false);
                    Log.d(TAG , "Hola " + "Estoy aquí en el else de gestor cliente detalle");
                }
            }

            if (getIntent().getExtras().containsKey("Cliente")) {
                cliente = (Cliente) getIntent().getSerializableExtra("Cliente");
                dniCliente.setEnabled(false);
                Log.d(TAG , "ID: " + cliente.getId());
            }
        }
        selectCliente(db , cliente.getId());
        //rellenarEditTexts2();

    }

    private void initViews() {
        nombreCliente = findViewById(R.id.nombreEditableClienteDetalle);
        apellidoCliente = findViewById(R.id.apellidoEditableClienteDetalle);
        dniCliente = findViewById(R.id.dniClienteEditableClienteDetalle);
        //dniGestor = findViewById(R.id.dniGestorEditableClienteDetalle);
        telefonoCliente = findViewById(R.id.telefonoEditableClienteDetalle);
        passCliente = findViewById(R.id.passwordEditableClienteDetalle);
        alertDniClienteDetalle = findViewById(R.id.alertdniGestorClienteDetalle);

        eliminarCliente = findViewById(R.id.eliminarCliente);
        modificarCliente = findViewById(R.id.modificarCliente);

        editarNombre = findViewById(R.id.editNombreCliente);
        editarApellido = findViewById(R.id.editApellidoCliente);
        editarDniCliente = findViewById(R.id.editDniCliente);
        //editarDniGestor = findViewById(R.id.editDniGestorCliente);
        editarTelefono = findViewById(R.id.editTelefonoCliente);
        editarPass = findViewById(R.id.editPasswordCliente);

    }

    private void initValues() {
        perfil = (Cliente) getIntent().getSerializableExtra("Cliente");

        nombreCliente.setText(perfil.getNombre());
        apellidoCliente.setText(perfil.getApellidos());
        dniCliente.setText(perfil.getDni_cliente());
        //dniGestor.setText(perfil.getDni_gestor());
        telefonoCliente.setText(perfil.getNum_tel());
        passCliente.setText(perfil.getPass());
    }

    private void initListeners() {

        ManejadorClickEdit manejadorClickEditNombre = new ManejadorClickEdit(
                nombreCliente, editarNombre, getDecorador(nombreCliente.getText().toString())
        );

        ManejadorClickEdit manejadorClickEditApellido = new ManejadorClickEdit(
                apellidoCliente, editarApellido, getDecorador(apellidoCliente.getText().toString())
        );
        ManejadorClickEdit manejadorClickEditDniCliente = new ManejadorClickEdit(
                dniCliente, editarDniCliente, getDecorador(dniCliente.getText().toString())
        );
        ManejadorClickEdit manejadorClickEditTelefono = new ManejadorClickEdit(
                telefonoCliente, editarTelefono, getDecorador(telefonoCliente.getText().toString())
        );
        ManejadorClickEdit manejadorClickEditPassword = new ManejadorClickEdit(
                passCliente, editarPass, getDecorador(passCliente.getText().toString())
        );

        editarNombre.setOnClickListener(manejadorClickEditNombre);
        editarApellido.setOnClickListener(manejadorClickEditApellido);
        editarDniCliente.setOnClickListener(manejadorClickEditDniCliente);
        editarTelefono.setOnClickListener(manejadorClickEditTelefono);
        editarPass.setOnClickListener(manejadorClickEditPassword);

        eliminarCliente.setOnClickListener(v -> {

            new AlertDialog.Builder(ClienteDetalle.this)
                    .setTitle("¿Estás seguro de que deseas eliminar el cliente?")
                    .setMessage("El cliente desaparecerá de la base de datos y no podrás recuperarlo")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            DocumentReference ref = db.collection("Clientes").document(cliente.getId());
                            ref.delete()
                                    .addOnSuccessListener((v) -> {
                                        Log.d(TAG, "Cliente eliminado!");
                                        volverGestorMain(CLAVE_CLIENTE_ELIMINADO);
                                    })
                                    .addOnFailureListener((v) -> {
                                        Log.d(TAG, "Error eliminando cliente");
                                    });
                        }})
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "Eliminación de cliente cancelada");
                        }
                    }).show();

        });

        modificarCliente.setOnClickListener((v) -> {

            // UPDATE
            DocumentReference ref = db.collection("Clientes").document(cliente.getId());
            ref.update("Nombre", getEditTextText(nombreCliente.getText().toString()));
            ref.update("Apellido", getEditTextText(apellidoCliente.getText().toString()));
            ref.update("DNI", getEditTextText(dniCliente.getText().toString()));
            //ref.update("DNI_Gestor", getEditTextText(dniGestor.getText().toString()));
            ref.update("Telefono", getEditTextText(telefonoCliente.getText().toString()));
            ref.update("Contraseña", getEditTextText(passCliente.getText().toString()));

            Toast.makeText(this, "Cliente con Id " + cliente.getId() + " modificado", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Cliente con Id " + cliente.getId() + " modificado");

            volverGestorMain(CLAVE_CLIENTE_MODIFICADO);

        });

        dniCliente.setOnClickListener(v -> {
            AlphaAnimation animation = new AlphaAnimation(0, 1);
            animation.setDuration(4000);
            alertDniClienteDetalle.startAnimation(animation);
            alertDniClienteDetalle.setVisibility(View.VISIBLE);
            AlphaAnimation animation2 = new AlphaAnimation(1, 0);
            animation2.setDuration(4000);
            alertDniClienteDetalle.startAnimation(animation2);
            alertDniClienteDetalle.setVisibility(View.INVISIBLE);
        });

    }

    public String getDecorador (String texto) {

        return texto.substring(0, texto.indexOf(": ") + 2);

    }

    public String getEditTextText (String texto) {

        return texto.substring(texto.indexOf(": ") + 2);

    }

    public void volverGestorMain (int clave) {

        Intent intent = new Intent(ClienteDetalle.this, GestorMain.class);
        setResult(clave , intent);
        ClienteDetalle.super.onBackPressed();
        finish();

    }

    public void selectCliente (FirebaseFirestore db, String id) {

        db.collection("Clientes").whereEqualTo("__name__", id).limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            rellenarEditTexts(task);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void rellenarEditTexts(Task<QuerySnapshot> task) {

        for (QueryDocumentSnapshot document : task.getResult()) {
            Log.d(TAG, document.getId() + " => " + document.getData());
            nombreCliente.setText(nombreCliente.getText() + document.get("Nombre").toString());
            apellidoCliente.setText(apellidoCliente.getText() + document.get("Apellido").toString());
            dniCliente.setText(dniCliente.getText() + document.get("DNI").toString());
            //dniGestor.setText(dniGestor.getText() + document.get("DNI_Gestor").toString());
            telefonoCliente.setText(telefonoCliente.getText() + document.get("Num_Telf").toString());
            passCliente.setText(passCliente.getText() + document.get("Contraseña").toString());
        }

    }

    public void rellenarEditTexts2() {

        nombreCliente.setText(nombreCliente.getText() + cliente.getNombre());
        apellidoCliente.setText(apellidoCliente.getText() + cliente.getNombre());
        dniCliente.setText(dniCliente.getText() + cliente.getDni_cliente());
        //dniGestor.setText(dniGestor.getText() + cliente.getDni_gestor());
        telefonoCliente.setText(telefonoCliente.getText() + cliente.getNum_tel());
        passCliente.setText(passCliente.getText() + cliente.getPass());

    }

}
