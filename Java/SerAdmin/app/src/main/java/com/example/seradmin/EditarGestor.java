package com.example.seradmin;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.appcompat.app.AlertDialog;
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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.regex.Pattern;

public class EditarGestor extends AppCompatActivity {

        EditText dni, contrasena, nombre, apellido, telefono;
        ImageView listo;
        TextView alert, alertVerifyTel, alertTel, alertCon, alertNom, alertApe;
        Button delete, save;
        Gestor gestorObject;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle bundle = this.getIntent().getExtras();
            if (bundle != null) {
                gestorObject = (Gestor) bundle.getSerializable("Gestor");
                Log.d( "GESTOR EDIT",  gestorObject.toString());
                setGestorObject(gestorObject);
            }

            setContentView(R.layout.activity_editar_gestor);
            dni = findViewById(R.id.dni);
            contrasena = findViewById(R.id.contrasena);
            nombre = findViewById(R.id.nombre);
            apellido = findViewById(R.id.apellido);
            telefono = findViewById(R.id.telefono);
            delete = findViewById(R.id.delete);
            alert = findViewById(R.id.alert);
            listo = findViewById(R.id.listo);
            save = findViewById(R.id.save);
            alertTel = findViewById(R.id.alert2);
            alertCon = findViewById(R.id.alertC);
            alertNom = findViewById(R.id.alertN);
            alertApe = findViewById(R.id.alertA);
            alertVerifyTel = findViewById(R.id.alertVerifyTel);

            dni.setText(gestorObject.getDNI());
            contrasena.setText(gestorObject.getContraseña());
            nombre.setText(gestorObject.getNombre());
            apellido.setText(gestorObject.getApellido());
            telefono.setText(gestorObject.getNum_Telf());
            String tel_respaldo = telefono.getText().toString();

            dni.setOnClickListener(v -> {
                AlphaAnimation animation = new AlphaAnimation(0, 1);
                animation.setDuration(4000);
                alert.startAnimation(animation);
                alert.setVisibility(View.VISIBLE);
                AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                animation2.setDuration(4000);
                alert.startAnimation(animation2);
                alert.setVisibility(View.INVISIBLE);
            });

            save.setOnClickListener(v -> {
                String s_dni = dni.getText().toString();
                String nom = nombre.getText().toString();
                String ape = apellido.getText().toString();
                String con = contrasena.getText().toString();
                String tel = telefono.getText().toString();

                //INSTANCIA BBDD
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference gestores = db.collection("Gestores");

                //VERIFICAR TEL
                Query gestorVerifyTel = gestores.whereEqualTo("Num_Telf", tel);
                gestorVerifyTel.get().addOnCompleteListener(taskTel -> {
                    String lv_num = "";
                    if (taskTel.isSuccessful()) {
                        for (QueryDocumentSnapshot document : taskTel.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            lv_num = document.get("Num_Telf").toString();
                        }

                        if (lv_num.equals("") || tel_respaldo.equals(lv_num)) {

                            //VERIFICAR OTROS CAMPOS
                            Pattern telPattern = Pattern.compile("^[76]{1}[0-9]{8}$");
                            Pattern loQueSeaPattern = Pattern.compile("^(?!\\s*$).+");

                            if (telPattern.matcher(tel).matches() && loQueSeaPattern.matcher(con).matches() && loQueSeaPattern.matcher(nom).matches() && loQueSeaPattern.matcher(ape).matches()) {

                                //SELECT DOCUMENTO / REGISTRO
                                gestores.whereEqualTo("DNI", s_dni).limit(1).get().addOnCompleteListener(task -> {

                                    if (task.isSuccessful()) {
                                        DocumentReference docRef = null;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());

                                            // UPDATE
                                            DocumentReference ref = document.getReference();
                                            ref.update("Nombre", nom);
                                            ref.update("Apellido", ape);
                                            ref.update("Contraseña", con);
                                            ref.update("Num_Telf", tel);
                                            docRef = ref;
                                        }

                                        Gestor gestorObjeto = new Gestor(s_dni, con, nom, ape, tel);
                                        Log.d("GESTOR EDIT ACTUALIZADO", gestorObjeto.toString());
                                        setGestorObject(gestorObjeto);

                                        Intent intent = new Intent(getApplicationContext(), GestorMain.class);
                                        Bundle bundle1 = new Bundle();
                                        bundle1.putSerializable("Gestor", gestorObjeto);
                                        Log.d("GESTOR EDIT ACTUALIZADO", getGestorObject().toString());
                                        intent.putExtras(bundle1);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Log.w(TAG, "Error select documento.", task.getException());
                                    }
                                });

                                finish();

                            } else {

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
                            }

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

            });

            delete.setOnClickListener(v -> {

                new AlertDialog.Builder(EditarGestor.this)
                        .setTitle("¿Estás seguro de que deseas eliminar tu cuenta?")
                        .setMessage("Tu cuenta desaparecerá de la base de datos y no podrás recuperarla")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Gestores").whereEqualTo("DNI", dni.getText().toString()).limit(1).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentReference ref = null;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                        ref = document.getReference();
                                    }

                                    // DELETE
                                    assert ref != null;
                                    ref.delete().addOnSuccessListener(aVoid -> Log.d(TAG, "Docuemnto eliminado!"))
                                            .addOnFailureListener(e -> Log.w(TAG, "Error eliminando documento", e));

                                } else {
                                    Log.w(TAG, "Error select documento.", task.getException());
                                }
                            });

                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();

                        })
                        .setNegativeButton(android.R.string.no, null).show();

            });

            listo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), GestorMain.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("Gestor", getGestorObject());
                    Log.d( "GESTOR EDIT LISTO",  getGestorObject().toString());
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    finish();
                }
            });

        }

        private Gestor getGestorObject() {
            return gestorObject;
        }

        private void setGestorObject(Gestor gestorObject) {
            this.gestorObject = gestorObject;
        }
}