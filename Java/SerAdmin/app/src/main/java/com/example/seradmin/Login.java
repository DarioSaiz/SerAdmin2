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
                    /*switch (code) {
                        case RESULT_CANCELED:
                            break;
                        case CLAVE_INGRESAR:
                            Log.d(TAG, "NUEVO INGRESO");
                            PerfilesImagen nuevoPerfil = (PerfilesImagen) result.getData().getSerializableExtra(mensaje);
                            completo.add(nuevoPerfil);
                            contactoDao.insert(nuevoPerfil);
                            AdaptadorListado = new AdaptadorListado(completo, listener);
                            rV.setAdapter(AdaptadorListado);
                            break;

                        case CLAVE_VOLVER:
                            AdaptadorListado = new AdaptadorListado(completo, listener);
                            rV.setAdapter(AdaptadorListado);
                            break;

                        case CLAVE_ELIMINAR:
                            Log.d(TAG, "NUEVO ELIMINADO");
                            //Intent elim = result.getData();
                            String nom = result.getData().getStringExtra(mensaje2);
                            Log.d(TAG, nom);
                            PerfilesImagen elimPerfil = contactoDao.findByName(nom);
                            Log.d(TAG, elimPerfil.getNombre());
                            completo.remove(elimPerfil);
                            contactoDao.delete(elimPerfil);
                            AdaptadorListado = new AdaptadorImagen(completo, listener);
                            rV.setAdapter(AdaptadorListado);
                            break;

                    }*/

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
                alert.startAnimation(animation);
                alert.setVisibility(View.VISIBLE);
                Log.d(TAG, "Hola estoy aqui en gestor creando la alarma");
                AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(4000);
                alert.startAnimation(animation2);
                alert.setVisibility(View.INVISIBLE);
                Log.d(TAG, "Hola estoy aqui en gestor apagando la alarma");

            }

            db = FirebaseFirestore.getInstance();
            CollectionReference gestores = db.collection("Gestores");
            CollectionReference clientes = db.collection("Clientes");

            // SELECT
            Query gestor = gestores.whereEqualTo("DNI", dni).whereEqualTo("Contraseña", pass);
            Query cliente = clientes.whereEqualTo("DNI", dni).whereEqualTo("Contraseña", pass);

            gestor.get().addOnCompleteListener(task -> {
                String g_dni = "", g_pass = "", g_nombre = "", g_apellido = "", g_telefono = "";

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        g_dni = document.get("DNI").toString();
                        g_pass = document.get("Contraseña").toString();
                        g_nombre = document.get("Nombre").toString();
                        g_apellido = document.get("Apellido").toString();
                        g_telefono = document.get("Num_Telf").toString();

                    }

                    Gestor gestorObjeto = new Gestor(g_dni, g_pass, g_nombre, g_apellido, g_telefono);

                    Intent intent = new Intent(getApplicationContext(), GestorMain.class);
                    //Bundle bundle = new Bundle();
                    //bundle.putSerializable("Gestor", (Serializable) gestorObjeto);
                    //intent.putExtras(bundle);
                    //intent.putExtra("DNI_Gestor", gestorObjeto);
                    intent.putExtra("Gestor", gestorObjeto);
                    controladorLogin.launch(intent);
                    finish();


                } else {

                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(4000);
                    alert.startAnimation(animation);
                    alert.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Hola estoy aqui en gestor creando la alarma");
                    AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                    animation2.setDuration(4000);
                    alert.startAnimation(animation2);
                    alert.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "Hola estoy aqui en gestor apagando la alarma");

                }
            });

            cliente.get().addOnCompleteListener(task -> {
                String c_dni = "", c_dni_gestor = "", c_pass = "", c_nombre = "", c_apellido = "", c_telefono = "", c_sociedad = "";

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        c_dni = document.get("DNI").toString();
                        c_pass = document.get("Contraseña").toString();
                        c_nombre = document.get("Nombre").toString();
                        c_apellido = document.get("Apellido").toString();
                        c_telefono = document.get("Num_Telf").toString();
                        c_dni_gestor = document.get("DNI_Gestor").toString();
                        c_sociedad = document.get("Sociedad").toString();

                    }

                    Cliente clienteObjeto = new Cliente(c_nombre, c_apellido, c_dni, c_dni_gestor, c_telefono, c_pass, c_sociedad);


<<<<<<< Updated upstream
                    Intent intent = new Intent(getApplicationContext(), InterfazUsuario.class);
                    intent.putExtra("Cliente", clienteObjeto);
                    intent.putExtra(EXTRA_ID_CLIENTE, clienteObjeto.getDni_cliente());
                    intent.putExtra(EXTRA_SOCIEDAD,clienteObjeto.getSociedad());
                    controladorLogin.launch(intent);
                    finish();
=======
                        AlphaAnimation animation = new AlphaAnimation(0, 1);
                        animation.setDuration(4000);
                        alert.startAnimation(animation);
                        alert.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Hola estoy aqui en cliente creando la alarma");
                        AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                        animation2.setDuration(4000);
                        alert.startAnimation(animation2);
                        alert.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "Hola estoy aqui en cliente apagando la alarma");

                    } else {

                        Intent intent = new Intent(getApplicationContext(), Navegador.class);
                        intent.putExtra("Cliente", clienteObjeto);
                        intent.putExtra(EXTRA_ID_CLIENTE, clienteObjeto.getDni_cliente());
                        intent.putExtra(EXTRA_SOCIEDAD,clienteObjeto.getSociedad());
                        controladorLogin.launch(intent);
                        finish();
>>>>>>> Stashed changes

//                        Intent intent2 = new Intent(getApplicationContext(), MainTree.class);
//                        intent.putExtra("Cliente", clienteObjeto);
//                        intent.putExtra(EXTRA_ID_CLIENTE, clienteObjeto.getDni_cliente());
//                        controladorLogin.launch(intent2);
//                        finish();
//
//                        Intent intent3 = new Intent(getApplicationContext(), FileTreeFragment.class);
//                        intent.putExtra("Cliente", clienteObjeto);
//                        intent.putExtra(EXTRA_ID_CLIENTE, clienteObjeto.getDni_cliente());
//                        controladorLogin.launch(intent3);
//                        finish();


                } else {

                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(4000);
                    alert.startAnimation(animation);
                    alert.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Hola estoy aqui en cliente creando la alarma");
                    AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                    animation2.setDuration(4000);
                    alert.startAnimation(animation2);
                    alert.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "Hola estoy aqui en cliente apagando la alarma");

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
}