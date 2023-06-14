package com.example.seradmin;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayoutStates;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seradmin.InterfazUsuari.*;
import com.example.seradmin.Recycler.Cliente;
import com.example.seradmin.database.eventosDatabase.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.C;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ClienteDetalle extends AppCompatActivity {

    private EditText nombreCliente, apellidoCliente, dniCliente, dniGestor, telefonoCliente, passCliente;
    private Cliente perfil;
    private TextView alertDniClienteDetalle, alertDniGestorClienteDetalle;
    private Button eliminarCliente, modificarCliente;
    ImageView editarNombre, editarApellido, editarDniCliente, editarDniGestor, editarTelefono, editarPass;
    TextView alertDNI, alertTel, alertCon, alertNom, alertApe, alertTS, alertVerifyDNI, alertVerifyTel;
    FirebaseFirestore db;
    Cliente cliente = new Cliente();
    Gestor gestor = new Gestor();
    boolean soyCliente = false, soyGestor = false;
    Spinner spinner;

    public static final int CLAVE_CLIENTE_MODIFICADO = 80;
    public static final int CLAVE_CLIENTE_ELIMINADO = 81;

    Pattern dniPattern = Pattern.compile("^\\d{8}[A-Z]");
    Pattern niePattern = Pattern.compile("^[XYZ]\\d{7}[A-Z]");
    Pattern telPattern = Pattern.compile("^[76]{1}[0-9]{8}$");
    Pattern loQueSeaPattern = Pattern.compile("^(?!\\s*$).+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_detalle);

        spinner = (Spinner) findViewById(R.id.sociedad3);
        Resources res = getResources();
        String[] sociedades = res.getStringArray(R.array.sociedades);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_texto, sociedades) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (tv.getText().toString().equals("Tipo de Sociedad")) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(25);
                    tv.setPadding(0, 15, 20, 25);

                } else {
                    tv.setTextSize(18);
                    tv.setTextColor(Color.BLACK);
                    tv.setGravity(Gravity.NO_GRAVITY);
                    tv.setGravity(Gravity.FILL_VERTICAL);
                    tv.setPadding(55, 25, 0, 25);
                }
                return view;
            }

        };

        spinner.setAdapter(adapter);

        //inicializa las vistas,recupera los elementos de la interfaz
        initViews();
        // inicializa los listeners, agrega el comportamiento a los elementos de la interfaz de usuario (botones) cuando se hacen clic
        initListeners();

        db = FirebaseFirestore.getInstance();
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("Gestor")) {
                gestor = (Gestor) getIntent().getSerializableExtra("Gestor");
                if (gestor.getNombre() != null) {
                    Log.d(TAG, gestor.toString());
                    Log.d(TAG, "Hola " + "No debería estar aqui si vengo de cliente directo");
                    editarDniCliente.setVisibility(View.VISIBLE);
                    soyGestor = true;
                } else {

                    dniCliente.setFocusable(false);
                    dniCliente.setFocusableInTouchMode(false);
                    dniCliente.setKeyListener(null);
                    //dniCliente.setClickable(false);
                    dniCliente.setEnabled(false);
                    dniCliente.setTextColor(Color.GRAY);
                    dniGestor.setVisibility(View.INVISIBLE);
                    editarDniGestor.setVisibility(View.INVISIBLE);
                    Log.d(TAG , "Hola " + "Estoy aquí en el else de gestor cliente detalle");
                    soyCliente = true;
                }
                if (getIntent().getExtras().containsKey("Cliente")) {
                    cliente = (Cliente) getIntent().getSerializableExtra("Cliente");
                    Log.d(TAG , "ID: " + cliente.getId());
                }
            }


        }

        if (soyCliente) {

            editarDniCliente.setOnClickListener(v -> {
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

        selectCliente(db , cliente.getId());

    }

    private void initViews() {
        nombreCliente = findViewById(R.id.nombreEditableClienteDetalle);
        apellidoCliente = findViewById(R.id.apellidoEditableClienteDetalle);
        dniCliente = findViewById(R.id.dniClienteEditableClienteDetalle);
        dniGestor = findViewById(R.id.dniGestorClienteDetalle);
        telefonoCliente = findViewById(R.id.telefonoEditableClienteDetalle);
        passCliente = findViewById(R.id.passwordEditableClienteDetalle);
        alertDniClienteDetalle = findViewById(R.id.alertdniClienteDetalle);
        alertDniGestorClienteDetalle = findViewById(R.id.alertdniGestorClienteDetalle);

        eliminarCliente = findViewById(R.id.eliminarCliente);
        modificarCliente = findViewById(R.id.modificarCliente);

        editarNombre = findViewById(R.id.editNombreCliente);
        editarApellido = findViewById(R.id.editApellidoCliente);
        editarDniCliente = findViewById(R.id.editDniCliente);
        editarDniGestor = findViewById(R.id.editDniGestorCliente);
        editarTelefono = findViewById(R.id.editTelefonoCliente);
        editarPass = findViewById(R.id.editPasswordCliente);

        alertDNI = findViewById(R.id.alert);
        alertTel = findViewById(R.id.alertT2);
        alertCon = findViewById(R.id.alertP2);
        alertNom = findViewById(R.id.alertN2);
        alertApe = findViewById(R.id.alertA2);
        alertVerifyDNI = findViewById(R.id.alertVerifyDNI2);
        alertVerifyTel = findViewById(R.id.alertVerifyTel3);
        alertTS = findViewById(R.id.alertTS2);

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

//        ManejadorClickEdit manejadorClickEditDniGestor = new ManejadorClickEdit(
//                dniGestor, editarDniGestor, getDecorador(dniGestor.getText().toString())
//        );

        editarNombre.setOnClickListener(manejadorClickEditNombre);
        editarApellido.setOnClickListener(manejadorClickEditApellido);
        editarDniCliente.setOnClickListener(manejadorClickEditDniCliente);
        editarTelefono.setOnClickListener(manejadorClickEditTelefono);
        editarPass.setOnClickListener(manejadorClickEditPassword);
        //editarDniGestor.setOnClickListener(manejadorClickEditDniGestor);

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
                                        volverBorrar(CLAVE_CLIENTE_ELIMINADO);
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

//            // UPDATE
//            DocumentReference ref = db.collection("Clientes").document(cliente.getId());
//            ref.update("Nombre", getEditTextText(nombreCliente.getText().toString()));
//            ref.update("Apellido", getEditTextText(apellidoCliente.getText().toString()));
//            ref.update("DNI", getEditTextText(dniCliente.getText().toString()));
//            //ref.update("DNI_Gestor", getEditTextText(dniGestor.getText().toString()));
//            ref.update("Telefono", getEditTextText(telefonoCliente.getText().toString()));
//            ref.update("Contraseña", getEditTextText(passCliente.getText().toString()));
//
//            Toast.makeText(this, "Cliente con Id " + cliente.getId() + " modificado", Toast.LENGTH_LONG).show();
//            Log.d(TAG, "Cliente con Id " + cliente.getId() + " modificado");
//
//            volverActualizar();
            verificarTodo();

        });

        editarDniGestor.setOnClickListener(v -> {
            AlphaAnimation animation = new AlphaAnimation(0, 1);
            animation.setDuration(4000);
            alertDniGestorClienteDetalle.startAnimation(animation);
            alertDniGestorClienteDetalle.setVisibility(View.VISIBLE);
            AlphaAnimation animation2 = new AlphaAnimation(1, 0);
            animation2.setDuration(4000);
            alertDniGestorClienteDetalle.startAnimation(animation2);
            alertDniGestorClienteDetalle.setVisibility(View.INVISIBLE);
        });


    }

    public String getDecorador (String texto) {

        return texto.substring(0, texto.indexOf(": ") + 2);

    }

    public String getEditTextText (String texto) {

        if (texto.length() != 0) {
            return texto.substring(texto.indexOf(": ") + 2);
        }
        return "";

    }

    public void volverBorrar (int clave) {

        Intent intentGestorMain = new Intent(ClienteDetalle.this, GestorMain.class);
        Intent intentCliente = new Intent(ClienteDetalle.this, Login.class);
//        setResult(clave , intentGestorMain);
//        setResult(clave , intentCliente);
        if (soyCliente) {
            controladorCliente.launch(intentCliente);
        }
        if (soyGestor) {
            intentGestorMain.putExtra("Gestor", gestor);
            controladorCliente.launch(intentGestorMain);
        }
        //ClienteDetalle.super.onBackPressed();
        finish();

    }

    public void volverActualizar () {

        Intent intent = new Intent(ClienteDetalle.this, Navegador.class);
//        setResult(clave , intentGestorMain);
//        setResult(clave , intentCliente);
        if (soyCliente) {
            intent.putExtra("Cliente", cliente);
        }
        if (soyGestor) {
            intent.putExtra("Gestor", gestor);
        }
        controladorCliente.launch(intent);
        //ClienteDetalle.super.onBackPressed();
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
            dniGestor.setText(dniGestor.getText() + document.get("DNI_Gestor").toString());
            telefonoCliente.setText(telefonoCliente.getText() + document.get("Num_Telf").toString());
            passCliente.setText(passCliente.getText() + document.get("Contraseña").toString());
        }

    }

    ActivityResultLauncher controladorCliente = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                //Log.d(TAG, "Vuelve cancelado");
                int code = result.getResultCode();
                switch (code) {
                    case RESULT_CANCELED:
                        break;
//                    case NuevoCliente.CLAVE_ADD_CLIENTE:
//                        Log.d(TAG, "NUEVO CLIENTE");
//                        poblarRecyclerView();
//                        break;
//                    case InterfazUsuario.CLAVE_MODIFICAR_CLIENTE:
//                        Log.d(TAG, "CLIENTE MODIFICADO");
//                        poblarRecyclerView();
//                        break;
//                    case InterfazUsuario.CLAVE_ELIMINAR_CLIENTE:
//                        Log.d(TAG, "CLIENTE ELIMINADO");
//                        poblarRecyclerView();
//                        break;

                }

            });

    public void verificarTodo() {

        String s_dni = getEditTextText(dniCliente.getText().toString());
        String nom = getEditTextText(nombreCliente.getText().toString());
        String ape = getEditTextText(apellidoCliente.getText().toString());
        String con = getEditTextText(passCliente.getText().toString());
        String tel = getEditTextText(telefonoCliente.getText().toString());
        String sociedad = spinner.getSelectedItem().toString();

        if ((dniPattern.matcher(s_dni).matches() || niePattern.matcher(s_dni).matches()) && telPattern.matcher(tel).matches() && loQueSeaPattern.matcher(con).matches() && loQueSeaPattern.matcher(nom).matches() && loQueSeaPattern.matcher(ape).matches() && !sociedad.equals("Tipo de Sociedad")) {

        //INSTANCIA BBDD
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference clientes = db.collection("Clientes");

        //VERIFICAR TEL
        Query gestorVerify = clientes.whereEqualTo("DNI", s_dni);
        String tel_respaldo = "";
            gestorVerify.get().addOnCompleteListener(task -> {
                String lv_dni = "";

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        lv_dni = document.get("DNI").toString();
                    }

                    if (lv_dni.equals("")) {

                        // SELECT X TELF
                        Query gestorVerifyTel = clientes.whereEqualTo("Num_Telf", tel);
                        gestorVerifyTel.get().addOnCompleteListener(taskTel -> {
                            String lv_num = "";
                            if (taskTel.isSuccessful()) {
                                for (QueryDocumentSnapshot document : taskTel.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    lv_num = document.get("Num_Telf").toString();
                                }

                                if (lv_num.equals("")) {

                                    //UPDATE
                                    DocumentReference ref = db.collection("Clientes").document(cliente.getId());
                                    ref.update("Nombre", getEditTextText(nombreCliente.getText().toString()));
                                    ref.update("Apellido", getEditTextText(apellidoCliente.getText().toString()));
                                    ref.update("DNI", getEditTextText(dniCliente.getText().toString()));
                                    //ref.update("DNI_Gestor", getEditTextText(dniGestor.getText().toString()));
                                    ref.update("Telefono", getEditTextText(telefonoCliente.getText().toString()));
                                    ref.update("Contraseña", getEditTextText(passCliente.getText().toString()));

                                    Toast.makeText(this, "Cliente con Id " + cliente.getId() + " modificado", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "Cliente con Id " + cliente.getId() + " modificado");

                                } else {

                                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                                    animation.setDuration(4000);
                                    alertVerifyTel.startAnimation(animation);
                                    alertVerifyTel.setVisibility(View.VISIBLE);
                                    AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                                    animation2.setDuration(4000);
                                    alertVerifyTel.startAnimation(animation2);
                                    alertVerifyTel.setVisibility(View.INVISIBLE);

                                }

                            } else Log.w(TAG, "Error select gestor.", taskTel.getException());

                        });

                    } else {

                        AlphaAnimation animation = new AlphaAnimation(0, 1);
                        animation.setDuration(4000);
                        alertVerifyDNI.startAnimation(animation);
                        alertVerifyDNI.setVisibility(View.VISIBLE);
                        AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                        animation2.setDuration(4000);
                        alertVerifyDNI.startAnimation(animation2);
                        alertVerifyDNI.setVisibility(View.INVISIBLE);

                    }

                } else Log.w(TAG, "Error select gestor.", task.getException());

            });

        } else {

            if (!dniPattern.matcher(s_dni).matches() && !niePattern.matcher(s_dni).matches()) {

                AlphaAnimation animation = new AlphaAnimation(0, 1);
                animation.setDuration(4000);
                alertDNI.startAnimation(animation);
                alertDNI.setVisibility(View.VISIBLE);
                AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(4000);
                alertDNI.startAnimation(animation2);
                alertDNI.setVisibility(View.INVISIBLE);
            }
            if (!telPattern.matcher(tel).matches()) {

                AlphaAnimation animation = new AlphaAnimation(0, 1);
                animation.setDuration(4000);
                alertTel.startAnimation(animation);
                alertTel.setVisibility(View.VISIBLE);
                AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(4000);
                alertTel.startAnimation(animation2);
                alertTel.setVisibility(View.INVISIBLE);

            }

            if (!loQueSeaPattern.matcher(con).matches()) {

                AlphaAnimation animation = new AlphaAnimation(0, 1);
                animation.setDuration(4000);
                alertCon.startAnimation(animation);
                alertCon.setVisibility(View.VISIBLE);
                AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(4000);
                alertCon.startAnimation(animation2);
                alertCon.setVisibility(View.INVISIBLE);

            }

            if (!loQueSeaPattern.matcher(nom).matches()) {

                AlphaAnimation animation = new AlphaAnimation(0, 1);
                animation.setDuration(4000);
                alertNom.startAnimation(animation);
                alertNom.setVisibility(View.VISIBLE);
                AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(4000);
                alertNom.startAnimation(animation2);
                alertNom.setVisibility(View.INVISIBLE);

            }

            if (!loQueSeaPattern.matcher(ape).matches()) {

                AlphaAnimation animation = new AlphaAnimation(0, 1);
                animation.setDuration(4000);
                alertApe.startAnimation(animation);
                alertApe.setVisibility(View.VISIBLE);
                AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(4000);
                alertApe.startAnimation(animation2);
                alertApe.setVisibility(View.INVISIBLE);

            }

            if (sociedad.equals("Tipo de Sociedad")) {
                AlphaAnimation animation = new AlphaAnimation(0, 1);
                animation.setDuration(4000);
                alertTS.startAnimation(animation);
                alertTS.setVisibility(View.VISIBLE);
                AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(4000);
                alertTS.startAnimation(animation2);
                alertTS.setVisibility(View.INVISIBLE);
            }
        }
    }

}
