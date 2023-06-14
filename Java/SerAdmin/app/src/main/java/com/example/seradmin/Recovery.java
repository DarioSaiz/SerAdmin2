package com.example.seradmin;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seradmin.InterfazUsuari.Navegador;
import com.example.seradmin.Recycler.Cliente;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;

public class Recovery extends AppCompatActivity {

    private static final int CLAVE_SMS_CORRECTO = 55;
    EditText telefono, dni, escribirSMS;
    TextView alertRecovery;
    Button mandarSMS;
    ActivityResultLauncher<String> requestPermissionLauncherSMS;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        telefono = findViewById(R.id.Phone);
        dni = findViewById(R.id.dniRecovery);
        escribirSMS = findViewById(R.id.escribirSMS);
        mandarSMS = findViewById(R.id.SMS);
        alertRecovery = findViewById(R.id.alertRecovery);

        db = FirebaseFirestore.getInstance();

//        requestPermissionLauncherSMS = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//            if (isGranted) {
//                // Permission is granted. Continue the action or workflow in your
//                // app.
//                //mandarSMS();
//            } else {
//                // Explain to the user that the feature is unavailable because the
//                // feature requires a permission that the user has denied. At the
//                // same time, respect the user's decision. Don't link to system
//                // settings in an effort to convince the user to change their
//                // decision.
//                Toast.makeText(Recovery.this, "Necesitamos permiso para mandar SMSs", Toast.LENGTH_SHORT).show();
//            }
//        });

        mandarSMS.setOnClickListener(v -> {
            //checkSMSStatePermission();
            comprobarPermisosSMS();
        });

    }

    public void comprobarPermisosSMS () {

        if (ContextCompat.checkSelfPermission(
                Recovery.this, Manifest.permission.RECEIVE_SMS) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                Recovery.this, Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            validarDniTelefono();
        } else if (false) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.

            // Mostrar UI Dialog para explicar al usuarios la necesidad del permiso
            // Vamos a usar la de por defecto de Android. Se ejecuta en el else

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncherSMS.launch(Manifest.permission.RECEIVE_SMS);
            requestPermissionLauncherSMS.launch(Manifest.permission.SEND_SMS);
        }
    }

//    private void checkSMSStatePermission() {
//        int permissionCheck = ContextCompat.checkSelfPermission(
//                this, android.Manifest.permission.SEND_SMS);
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            Log.i("Mensaje", "No se tiene permiso para enviar SMS.");
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 225);
//        } else {
//            Log.i("Mensaje", "Se tiene permiso para enviar SMS!");
//            String phone = telefono.getText().toString();
//            String text = Integer.toString(generarNumeroAletorio());
//            SmsManager sms = SmsManager.getDefault();
//            sms.sendTextMessage(phone, null, text , null, null);
//            escribirSMS.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void requestSmsPermission() {
//        String permission = android.Manifest.permission.RECEIVE_SMS;
//        int grant = ContextCompat.checkSelfPermission(this, permission);
//        if ( grant != PackageManager.PERMISSION_GRANTED) {
//            String[] permission_list = new String[1];
//            permission_list[0] = permission;
//            ActivityCompat.requestPermissions(this, permission_list, 1);
//        }
//    }

    public int generarNumeroAletorio() {
        int num = (int) (Math.random() * 100000) + 100000;
        return num;
    }

    public void mandarSMS(String s_tel, String id) {

        String text = Integer.toString(generarNumeroAletorio());
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(s_tel, null, text , null, null);
        escribirSMS.setVisibility(View.VISIBLE);
        escribirSMS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().equals(text)){

                    lanzarNuevaContraseña(id);

                }
            }
        });

    }

    public void lanzarNuevaContraseña(String id) {
        Intent intent = new Intent(getApplicationContext(), NuevaContraseña.class);
        //Bundle bundle = new Bundle();
        //bundle.putSerializable("Gestor", (Serializable) gestorObjeto);
        //intent.putExtras(bundle);
        intent.putExtra("ID", id);
        startActivity(intent);
        finish();
    }

    public void validarDniTelefono() {
        String s_dni = dni.getText().toString();
        String s_tel = telefono.getText().toString();
        Query cliente = db.collection("Clientes").whereEqualTo("DNI", s_dni).whereEqualTo("Num_Telf", s_tel);
        Query gestor = db.collection("Gestores").whereEqualTo("DNI", s_dni).whereEqualTo("Num_Telf", s_tel);
        AggregateQuery countGestor = gestor.count();
        AggregateQuery countCliente = cliente.count();

        countGestor.get(AggregateSource.SERVER).addOnCompleteListener(taskCountGestor -> {
            if (taskCountGestor.isSuccessful()) {
                // Count fetched successfully
                AggregateQuerySnapshot snapshot = taskCountGestor.getResult();
                Log.d(TAG, "Count: " + snapshot.getCount());
                if (snapshot.getCount() != 0) {
                    gestor.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mandarSMS(s_tel, document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
                } else {

                    countCliente.get(AggregateSource.SERVER).addOnCompleteListener(taskCountCliente -> {
                        if (taskCountCliente.isSuccessful()) {
                            // Count fetched successfully
                            AggregateQuerySnapshot snapshotCliente = taskCountCliente.getResult();
                            Log.d(TAG, "Count: " + snapshotCliente.getCount());
                            if (snapshotCliente.getCount() != 0) {
                                cliente.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            mandarSMS(s_tel, document.getId());
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                });
                            } else {

                                if (!dni.equals("") || !telefono.equals("")) {

                                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                                    animation.setDuration(4000);
                                    alertRecovery.startAnimation(animation);
                                    alertRecovery.setVisibility(View.VISIBLE);
                                    Log.d(TAG, "Hola estoy aqui en recovery creando la alarma");
                                    AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                                    animation2.setDuration(4000);
                                    alertRecovery.startAnimation(animation2);
                                    alertRecovery.setVisibility(View.INVISIBLE);
                                    Log.d(TAG, "Hola estoy aqui en recovery apagando la alarma");
                                }
                            }
                        } else {
                            Log.d(TAG, "Count failed: ", taskCountCliente.getException());
                        }
                    });

                }
            }
        });
    }
}