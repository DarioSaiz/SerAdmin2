package com.example.seradmin;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seradmin.InterfazUsuari.InterfazUsuario;
import com.example.seradmin.InterfazUsuari.Navegador;
import com.example.seradmin.Recycler.Cliente;
import com.example.seradmin.Tree.FileTreeFragment;
import com.example.seradmin.Tree.MainTree;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;

public class Login extends AppCompatActivity {
    public static final String EXTRA_ID_CLIENTE = "ID_CLIENTE";
    public static final String EXTRA_SOCIEDAD = "TIPO_SOCIEDAD";

    private static final int CLAVE_GESTOR = 55;
    private static final int CLAVE_CLIENTE = 56;
    private static final int CLAVE_OLVIDADO = 57;
    private static final int CLAVE_CREAR_CUENTA = 58;

    ImageView logo;
    Button login;
    EditText usuario, contraseña;
    TextView crearCuenta, olvidar, alert;
    Long countCliente, countGestor;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//        logo = findViewById(R.id.logo);
//        logo.setOnClickListener(v -> {
//            //Registra evento de click en foto (no funciona)
//            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, logo.getContentDescription().toString());
//            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//            Toast.makeText(getApplicationContext(), "CLICK", Toast.LENGTH_LONG).show();
//        });

        login = findViewById(R.id.log);
        usuario = findViewById(R.id.user);
        contraseña = findViewById(R.id.password);
        crearCuenta = findViewById(R.id.createAccount);
        olvidar = findViewById(R.id.forget);
        alert = findViewById(R.id.alert);

        logo = findViewById(R.id.logo);

        ActivityResultLauncher controladorLogin = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    //Log.d(TAG, "Vuelve cancelado");
                    int code = result.getResultCode();


                });

        login.setOnClickListener(v -> {
            //Registra evento de click en foto (no funciona)
//            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, login.getContentDescription().toString());
//            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//            Toast.makeText(getApplicationContext(), "CLICK", Toast.LENGTH_LONG).show();

            String dni = usuario.getText().toString();
            String pass = contraseña.getText().toString();

            if (dni.equals("") || pass.equals("")) {

                AlphaAnimation animation = new AlphaAnimation(0, 1);
                animation.setDuration(4000);
                alert.setText("DNI o Contraseña vacíos");
                alert.startAnimation(animation);
                alert.setVisibility(View.VISIBLE);
                Log.d(TAG, "Hola estoy creando la alarma de campos vacios");
                AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(4000);
                alert.startAnimation(animation2);
                alert.setVisibility(View.INVISIBLE);
                Log.d(TAG, "Hola estoy apagando la alarma de campos vacios");

            }

            db = FirebaseFirestore.getInstance();
            CollectionReference gestores = db.collection("Gestores");
            CollectionReference clientes = db.collection("Clientes");

            // SELECT
            Query gestor = gestores.whereEqualTo("DNI", dni).whereEqualTo("Contraseña", pass);
            Query cliente = clientes.whereEqualTo("DNI", dni).whereEqualTo("Contraseña", pass);
            AggregateQuery countGestor = gestor.count();
            AggregateQuery countCliente = cliente.count();

            countGestor.get(AggregateSource.SERVER).addOnCompleteListener(taskCountGestor -> {
                if (taskCountGestor.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = taskCountGestor.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    setCountGestor(snapshot.getCount());
                    if (snapshot.getCount() != 0) {
                        gestor.get().addOnCompleteListener(task -> {
                            String g_id = "", g_dni = "", g_pass = "", g_nombre = "", g_apellido = "", g_telefono = "";

                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    g_id = document.getId();
                                    g_dni = document.get("DNI").toString();
                                    g_pass = document.get("Contraseña").toString();
                                    g_nombre = document.get("Nombre").toString();
                                    g_apellido = document.get("Apellido").toString();
                                    g_telefono = document.get("Num_Telf").toString();

                                }

                                Gestor gestorObjeto = new Gestor(g_id, g_dni, g_pass, g_nombre, g_apellido, g_telefono);

                                Intent intent = new Intent(Login.this, GestorMain.class);
                                //Bundle bundle = new Bundle();
                                //bundle.putSerializable("Gestor", (Serializable) gestorObjeto);
                                //intent.putExtras(bundle);
                                //intent.putExtra("DNI_Gestor", gestorObjeto);
                                intent.putExtra("Gestor", gestorObjeto);
                                controladorLogin.launch(intent);
                                finish();

                            } else {
                                Log.d(TAG, "Count failed: ", task.getException());
                            }
                        });
                    } else {

                        countCliente.get(AggregateSource.SERVER).addOnCompleteListener(taskCountCliente -> {
                            if (taskCountCliente.isSuccessful()) {
                                // Count fetched successfully
                                AggregateQuerySnapshot snapshotCliente = taskCountCliente.getResult();
                                setCountCliente(snapshotCliente.getCount());
                                Log.d(TAG, "Count: " + snapshotCliente.getCount());
                                if (snapshotCliente.getCount() != 0) {
                                    cliente.get().addOnCompleteListener(task -> {
                                        String c_id = "", c_dni = "", c_dni_gestor = "", c_pass = "", c_nombre = "", c_apellido = "", c_telefono = "", c_sociedad = "";

                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());

                                                c_id = document.getId();
                                                c_dni = document.get("DNI").toString();
                                                c_pass = document.get("Contraseña").toString();
                                                c_nombre = document.get("Nombre").toString();
                                                c_apellido = document.get("Apellido").toString();
                                                c_telefono = document.get("Num_Telf").toString();
                                                c_dni_gestor = document.get("DNI_Gestor").toString();
                                                c_sociedad = document.get("Sociedad").toString();

                                            }

                                            Cliente clienteObjeto = new Cliente(c_id, c_nombre, c_apellido, c_dni, c_dni_gestor, c_telefono, c_pass, c_sociedad);

                                            Intent intent = new Intent(getApplicationContext(), Navegador.class);
                                            intent.putExtra("Cliente", clienteObjeto);
                                            intent.putExtra(EXTRA_ID_CLIENTE, clienteObjeto.getDni_cliente());
                                            intent.putExtra(EXTRA_SOCIEDAD, clienteObjeto.getSociedad());
                                            controladorLogin.launch(intent);
                                            finish();

                                        } else {
                                            Log.d(TAG, "Count failed: ", task.getException());
                                        }
                                    });
                                } else {

                                    if (!dni.equals("") || !pass.equals("")) {

                                        AlphaAnimation animation = new AlphaAnimation(0, 1);
                                        animation.setDuration(4000);
                                        alert.setText("DNI o Contraseña incorrectos");
                                        alert.startAnimation(animation);
                                        alert.setVisibility(View.VISIBLE);
                                        Log.d(TAG, "Hola estoy aqui en cliente creando la alarma");
                                        AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                                        animation2.setDuration(4000);
                                        alert.startAnimation(animation2);
                                        alert.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "Hola estoy aqui en cliente apagando la alarma");
                                    }
                                }
                            } else {
                                Log.d(TAG, "Count failed: ", taskCountCliente.getException());
                            }
                        });

//                        AlphaAnimation animation = new AlphaAnimation(0, 1);
//                        animation.setDuration(4000);
//                        alert.startAnimation(animation);
//                        alert.setVisibility(View.VISIBLE);
//                        Log.d(TAG, "Hola estoy aqui en gestor creando la alarma");
//                        AlphaAnimation animation2 = new AlphaAnimation(1, 0);
//                        animation2.setDuration(4000);
//                        alert.startAnimation(animation2);
//                        alert.setVisibility(View.INVISIBLE);
//                        Log.d(TAG, "Hola estoy aqui en gestor apagando la alarma");
                    }
                } else {
                    Log.d(TAG, "Count failed: ", taskCountGestor.getException());
                }
            });



        });

        olvidar.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Recovery.class);
            intent.putExtra("OLVIDADO", CLAVE_OLVIDADO);
            controladorLogin.launch(intent);
        });

        crearCuenta.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, NuevoGestor.class);
            intent.putExtra("OLVIDADO", CLAVE_CREAR_CUENTA);
            controladorLogin.launch(intent);
        });

    }

    public void getCountGestor() {

    }

    public void getCountCliente() {

    }

    public void setCountGestor(Long countGestor) {
        this.countGestor = countGestor;
    }

    public void setCountCliente(Long countCliente) {
        this.countCliente = countCliente;
    }

//    public void selectGestor() {

//    }
}