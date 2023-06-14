package com.example.seradmin;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.seradmin.calendario.EventActivity;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class NuevoGestor extends AppCompatActivity {

    EditText nombre, apellido, contraseña, num_tel, dni;
    Button crearGestor;
    ImageView imagen;
    TextView alertDNI, alertTel, alertCon, alertNom, alertApe, alertVerifyDNI, alertVerifyTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_gestor);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        imagen = findViewById(R.id.imagen);
        imagen.setImageResource(R.drawable.logoseradmin);
        dni = findViewById(R.id.dni);
        nombre = findViewById(R.id.nombre);
        apellido = findViewById(R.id.apellido);
        contraseña = findViewById(R.id.contraseña);
        num_tel = findViewById(R.id.telefono);
        crearGestor = findViewById(R.id.crear);
        alertDNI = findViewById(R.id.alert);
        alertTel = findViewById(R.id.alert2);
        alertCon = findViewById(R.id.alertC);
        alertNom = findViewById(R.id.alertN);
        alertApe = findViewById(R.id.alertA);
        alertVerifyDNI = findViewById(R.id.alertVerifyDNI);
        alertVerifyTel = findViewById(R.id.alertVerifyTel);

        crearGestor.setOnClickListener(v -> {
            String s_dni = dni.getText().toString(),
                    s_cont = contraseña.getText().toString(),
                    s_nom = nombre.getText().toString(),
                    s_ape = apellido.getText().toString(),
                    s_num = num_tel.getText().toString();

            Pattern dniPattern = Pattern.compile("^\\d{8}[A-Z]");
            Pattern niePattern = Pattern.compile("^[XYZ]\\d{7}[A-Z]");
            Pattern telPattern = Pattern.compile("^[76]{1}[0-9]{8}$");
            Pattern loQueSeaPattern = Pattern.compile("^(?!\\s*$).+");

            if ((dniPattern.matcher(s_dni).matches() || niePattern.matcher(s_dni).matches()) && telPattern.matcher(s_num).matches() && loQueSeaPattern.matcher(s_cont).matches() && loQueSeaPattern.matcher(s_nom).matches() && loQueSeaPattern.matcher(s_ape).matches()) {

                //VERIFICAR DNI Y TEL

                // INSTANCIACIÓN BBDD
                FirebaseFirestore dbVerify = FirebaseFirestore.getInstance();
                CollectionReference gestores = dbVerify.collection("Gestores");

                // SELECT X DNI
                Query gestorVerify = gestores.whereEqualTo("DNI", s_dni);
                AggregateQuery countDniGestor = gestores.whereEqualTo("DNI", s_dni).count();
                AggregateQuery countTelefono = gestores.whereEqualTo("Num_Telf", s_num).count();

                countDniGestor.get(AggregateSource.SERVER).addOnCompleteListener(taskCountGestor -> {
                    if (taskCountGestor.isSuccessful()) {
                        // Count fetched successfully
                        AggregateQuerySnapshot snapshot = taskCountGestor.getResult();
                        Log.d(TAG, "Count: " + snapshot.getCount());
                        //setCountGestor(snapshot.getCount());
                        if (snapshot.getCount() != 0) {
                            new AlertDialog.Builder(NuevoGestor.this)
                                    .setTitle("Error")
                                    .setMessage("Ya existe un gestor con ese DNI")
                                    .setIcon(android.R.drawable.ic_dialog_dialer)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }}
                                    ).show();
                        }
                    }
                });

                countTelefono.get(AggregateSource.SERVER).addOnCompleteListener(taskCountGestor -> {
                    if (taskCountGestor.isSuccessful()) {
                        // Count fetched successfully
                        AggregateQuerySnapshot snapshot = taskCountGestor.getResult();
                        Log.d(TAG, "Count: " + snapshot.getCount());
                        //setCountGestor(snapshot.getCount());
                        if (snapshot.getCount() != 0) {
                            new AlertDialog.Builder(NuevoGestor.this)
                                    .setTitle("Error")
                                    .setMessage("Ya existe un gestor con ese teléfono")
                                    .setIcon(android.R.drawable.ic_dialog_dialer)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }}
                                    ).show();
                        }
                    }
                });

                gestorVerify.get().addOnCompleteListener(task -> {
                    String lv_dni = "";

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            lv_dni = document.get("DNI").toString();
                        }

                        if (lv_dni.equals("")) {

                            // SELECT X TELF
                            Query gestorVerifyTel = gestores.whereEqualTo("Num_Telf", s_num);
                            gestorVerifyTel.get().addOnCompleteListener(taskTel -> {
                                String lv_num = "";
                                if (taskTel.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : taskTel.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        lv_num = document.get("Num_Telf").toString();
                                    }

                                    if (lv_num.equals("")) {

                                        //CREAR CUENTA
                                        Map<String, Object> gestor = new HashMap<>();
                                        gestor.put("DNI", s_dni);
                                        gestor.put("Contraseña", s_cont);
                                        gestor.put("Nombre", s_nom);
                                        gestor.put("Apellido", s_ape);
                                        gestor.put("Num_Telf", s_num);

                                        db.collection("Gestores").add(gestor).addOnSuccessListener(documentReference -> {
                                            Log.d(TAG, "Insert gestor con ID: " + documentReference.getId());

                                            Gestor gestorObject = new Gestor(s_dni, s_cont, s_nom, s_ape, s_num);

                                            Intent intent = new Intent(getApplicationContext(), GestorMain.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("Gestor", gestorObject);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            finish();
                                        }).addOnFailureListener(e -> Log.w(TAG, "Error insert gestor", e));

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
                if (!telPattern.matcher(s_num).matches()) {

                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(4000);
                    alertTel.startAnimation(animation);
                    alertTel.setVisibility(View.VISIBLE);
                    AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                    animation2.setDuration(4000);
                    alertTel.startAnimation(animation2);
                    alertTel.setVisibility(View.INVISIBLE);

                }

                if (!loQueSeaPattern.matcher(s_cont).matches()) {

                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(4000);
                    alertCon.startAnimation(animation);
                    alertCon.setVisibility(View.VISIBLE);
                    AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                    animation2.setDuration(4000);
                    alertCon.startAnimation(animation2);
                    alertCon.setVisibility(View.INVISIBLE);

                }

                if (!loQueSeaPattern.matcher(s_nom).matches()) {

                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(4000);
                    alertNom.startAnimation(animation);
                    alertNom.setVisibility(View.VISIBLE);
                    AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                    animation2.setDuration(4000);
                    alertNom.startAnimation(animation2);
                    alertNom.setVisibility(View.INVISIBLE);

                }

                if (!loQueSeaPattern.matcher(s_ape).matches()) {

                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(4000);
                    alertApe.startAnimation(animation);
                    alertApe.setVisibility(View.VISIBLE);
                    AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                    animation2.setDuration(4000);
                    alertApe.startAnimation(animation2);
                    alertApe.setVisibility(View.INVISIBLE);

                }
            }

        });
    }
}