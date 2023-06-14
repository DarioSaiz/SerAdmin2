package com.example.seradmin;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.seradmin.Recycler.Cliente;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class NuevoCliente extends AppCompatActivity {

    public static final int CLAVE_ADD_CLIENTE = 70;
    EditText nombre, apellido, contraseña, num_tel, dni, dni_gestor;
    Button crearCliente;
    ImageView imagen;
    TextView alertDNI, alertTel, alertCon, alertNom, alertApe, alertVerifyDNI, alertVerifyTel, alertTS;
    Gestor gestor = new Gestor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_gestor2);

        Spinner spinner = (Spinner) findViewById(R.id.sociedad2);
        Resources res = getResources();
        String[] sociedades = res.getStringArray(R.array.sociedades);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //imagen = findViewById(R.id.imagen);
        //imagen.setImageResource(R.drawable.logoseradmin);
        dni = findViewById(R.id.dni);
        nombre = findViewById(R.id.nombre);
        apellido = findViewById(R.id.apellido);
        contraseña = findViewById(R.id.contraseña);
        num_tel = findViewById(R.id.telefono);
        crearCliente = findViewById(R.id.crear);
        alertDNI = findViewById(R.id.alert);
        alertTel = findViewById(R.id.alert2);
        alertCon = findViewById(R.id.alertC);
        alertNom = findViewById(R.id.alertN);
        alertApe = findViewById(R.id.alertA);
        alertVerifyDNI = findViewById(R.id.alertVerifyDNI);
        alertVerifyTel = findViewById(R.id.alertVerifyTel);
        alertTS = findViewById(R.id.alertTS);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("Gestor")) {
                gestor = (Gestor) getIntent().getSerializableExtra("Gestor");
            }
        }

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


        crearCliente.setOnClickListener(v -> {
            String s_dni = dni.getText().toString(),
                    s_cont = contraseña.getText().toString(),
                    s_nom = nombre.getText().toString(),
                    s_ape = apellido.getText().toString(),
                    s_num = num_tel.getText().toString(),
                    //s_dni_gestor = getIntent().getStringExtra("DNI_Gestor"),
                    s_dni_gestor = gestor.getDNI(),
                    s_sociedad = spinner.getSelectedItem().toString();



            Pattern dniPattern = Pattern.compile("^\\d{8}[A-Z]");
            Pattern niePattern = Pattern.compile("^[XYZ]\\d{7}[A-Z]");
            Pattern telPattern = Pattern.compile("^[76]{1}[0-9]{8}$");
            Pattern loQueSeaPattern = Pattern.compile("^(?!\\s*$).+");

            if ((dniPattern.matcher(s_dni).matches() || niePattern.matcher(s_dni).matches()) && telPattern.matcher(s_num).matches() && loQueSeaPattern.matcher(s_cont).matches() && loQueSeaPattern.matcher(s_nom).matches() && loQueSeaPattern.matcher(s_ape).matches() && !s_sociedad.equals("Tipo de Sociedad")) {

                //VERIFICAR DNI Y TEL

                // INSTANCIACIÓN BBDD
                FirebaseFirestore dbVerify = FirebaseFirestore.getInstance();
                CollectionReference clientes = dbVerify.collection("Clientes");

                // SELECT X DNI
                Query clienteVerify = clientes.whereEqualTo("DNI", s_dni);
                clienteVerify.get().addOnCompleteListener(task -> {
                    String lv_dni = "";

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            lv_dni = document.get("DNI").toString();
                        }

                        if (lv_dni.equals("")) {

                            // SELECT X TELF
                            Query gestorVerifyTel = clientes.whereEqualTo("Num_Telf", s_num);
                            gestorVerifyTel.get().addOnCompleteListener(taskTel -> {
                                String lv_num = "";
                                if (taskTel.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : taskTel.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        lv_num = document.get("Num_Telf").toString();
                                    }

                                    if (lv_num.equals("")) {

                                        //CREAR CUENTA
                                        Map<String, Object> cliente = new HashMap<>();
                                        cliente.put("DNI", s_dni);
                                        cliente.put("DNI_Gestor", s_dni_gestor);
                                        cliente.put("Contraseña", s_cont);
                                        cliente.put("Nombre", s_nom);
                                        cliente.put("Apellido", s_ape);
                                        cliente.put("Num_Telf", s_num);
                                        cliente.put("Sociedad", s_sociedad);
                                        cliente.put("Archivos", new ArrayList<String>());


                                        db.collection("Clientes").add(cliente).addOnSuccessListener(documentReference -> {
                                            Log.d(TAG, "Insert cliente con ID: " + documentReference.getId());

                                            Cliente clienteObject = new Cliente(s_nom, s_ape, s_dni, s_dni_gestor, s_num, s_cont, s_sociedad);

                                            Intent intent = new Intent(NuevoCliente.this, GestorMain.class);
//                                                Bundle bundle = new Bundle();
//                                                bundle.putSerializable("Cliente", clienteObject);
//                                                intent.putExtras(bundle);
                                            intent.putExtra("Gestor", gestor);
                                            //intent.putExtra("DNI_Gestor", s_dni_gestor);
                                            //setResult(CLAVE_ADD_CLIENTE , intent);
                                            Log.d(TAG, "Dni Gestor: " + s_dni_gestor);
                                            startActivity(intent);
                                            finish();
                                        }).addOnFailureListener(e -> Log.w(TAG, "Error insert cliente", e));

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

                                } else Log.w(TAG, "Error select cliente.", taskTel.getException());

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

                    } else Log.w(TAG, "Error select cliente.", task.getException());

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

                if (s_sociedad.equals("Tipo de Sociedad")) {
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

        });


//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedItemText = (String) parent.getItemAtPosition(position);
//                // If user change the default selection
//                // First item is disable and it is used for hint
//                if (position > 0) {
//                    // Notify the selected item text
//                    Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        //spinner.setPrompt("Sociedades");
    }

    public void volverGestorMain(int clave) {

        Intent intent = new Intent(NuevoCliente.this, GestorMain.class);
        setResult(clave, intent);
        NuevoCliente.super.onBackPressed();
        finish();

    }
}