package com.example.seradmin.calendario;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.seradmin.InterfazUsuari.Navegador;
import com.example.seradmin.ManejadorFechas;
import com.example.seradmin.ManejadorHoras;
import com.example.seradmin.R;
import com.example.seradmin.Recycler.Cliente;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import yuku.ambilwarna.AmbilWarnaDialog;
//import top.defaults.colorpicker.ColorPickerPopup;

public class EventActivity extends AppCompatActivity implements LocationFragment.OnCallbackReceived {

    private static final int CLAVE_INSERTADO = 90;
    EditText event_title, event_location, event_description;
    AppCompatCheckBox event_all_day;
    TextView event_start_date, event_start_time, event_end_date, event_end_time, event_color;
    ImageView event_color_image, event_show_on_map;
    MaterialToolbar event_toolbar;
    Button crearEvento;
    Cliente cliente = new Cliente();
    FirebaseFirestore db;
    private int mDefaultColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        event_title = (EditText) findViewById(R.id.event_title);
        event_location = (EditText) findViewById(R.id.event_location);
        event_show_on_map = findViewById(R.id.event_show_on_map);
        event_description = (EditText) findViewById(R.id.event_description);
        event_all_day = (AppCompatCheckBox) findViewById(R.id.event_all_day);
        event_start_date = (TextView) findViewById(R.id.event_start_date);
        event_start_time = (TextView) findViewById(R.id.event_start_time);
        event_end_date = (TextView) findViewById(R.id.event_end_date);
        event_end_time = (TextView) findViewById(R.id.event_end_time);
        event_color = (TextView) findViewById(R.id.event_color);
        event_color_image = (ImageView) findViewById(R.id.event_color_image);
        //crearEvento = (Button) findViewById(R.id.crearEvento);
        event_toolbar = (MaterialToolbar) findViewById(R.id.event_toolbar);
        db = FirebaseFirestore.getInstance();

//        MaterialToolbar event_toolbar = findViewById(R.id.event_toolbar);
//        getMenuInflater().inflate(R.menu.menu_event, (Menu) event_toolbar);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("Cliente")) {
                cliente = (Cliente) getIntent().getSerializableExtra("Cliente");
                Log.d("Cliente", cliente.getNombre());
            }
        }

        ManejadorFechas manejadorFechaSalida = new ManejadorFechas(event_start_date, getSupportFragmentManager());
        ManejadorFechas manejadorFechaVuelta = new ManejadorFechas(event_end_date, true, event_start_date, getSupportFragmentManager());

        event_start_date.setOnClickListener(manejadorFechaSalida);
        event_end_date.setOnClickListener(manejadorFechaVuelta);

        ManejadorHoras manejadorHoraSalida = new ManejadorHoras(event_start_time, getSupportFragmentManager());
        ManejadorHoras manejadorHoraVuelta = new ManejadorHoras(event_end_time, getSupportFragmentManager());

        event_start_time.setOnClickListener(manejadorHoraSalida);
        event_end_time.setOnClickListener(manejadorHoraVuelta);

        event_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.save:
                        //Toast.makeText(MainActivity.this,"Search Successful",Toast.LENGTH_LONG).show();
                        crearEvento();
                        break;
//                    case R.id.menus_logout:
//                        Toast.makeText(MainActivity.this,"Logout Successful",Toast.LENGTH_LONG).show();
//                        break;
//                    case R.id.acc_settings:
//                        Toast.makeText(MainActivity.this,"Setting Successful",Toast.LENGTH_LONG).show();
//                        break;
                }
                return true;
            }
        });

//        crearEvento.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                Map<String, Object> evento = prepararEvento();
//
//                if (evento != null) {
//
//                    db.collection("Eventos").add(evento).addOnSuccessListener(documentReference -> {
//                                Log.d(TAG, "Insertado evento con ID: " + documentReference.getId());
//
//                            })
//                            .addOnFailureListener(e -> Log.w(TAG, "Error insertando evento", e));
//
//                    Intent intent = new Intent(EventActivity.this, EventoMain.class);
//                    setResult(CLAVE_INSERTADO, intent);
//                    EventActivity.super.onBackPressed();
//                    finish();
//
//                } else {
//
//                    new AlertDialog.Builder(EventActivity.this)
//                            .setTitle("Faltan datos")
//                            .setMessage("Tienes que rellenar todos los campos")
//                            .setIcon(android.R.drawable.ic_dialog_dialer)
//                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//
//                                }}
//                            ).show();
//
//                }
//
//            }
//
//        });

//        event_location.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                abrirMapa();
//            }
//        });

        event_show_on_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirMapa();
            }
        });

        event_color_image.setOnClickListener(v -> {
//            new ColorPickerPopup.Builder(EventActivity.this).initialColor(
//                            Color.RED) // set initial color
//                    // of the color
//                    // picker dialog
//                    .enableBrightness(
//                            true) // enable color brightness
//                    // slider or not
//                    .enableAlpha(
//                            true) // enable color alpha
//                    // changer on slider or
//                    // not
//                    .okTitle(
//                            "Choose") // this is top right
//                    // Choose button
//                    .cancelTitle(
//                            "Cancel") // this is top left
//                    // Cancel button which
//                    // closes the
//                    .showIndicator(
//                            true) // this is the small box
//                    // which shows the chosen
//                    // color by user at the
//                    // bottom of the cancel
//                    // button
//                    .showValue(
//                            true) // this is the value which
//                    // shows the selected
//                    // color hex code
//                    // the above all values can be made
//                    // false to disable them on the
//                    // color picker dialog.
//                    .build()
//                    .show(
//                            v,
//                            new ColorPickerPopup.ColorPickerObserver() {
//                                @Override
//                                public void
//                                onColorPicked(int color) {
//                                    // set the color
//                                    // which is returned
//                                    // by the color
//                                    // picker
//                                    mDefaultColor = color;
//
//                                    // now as soon as
//                                    // the dialog closes
//                                    // set the preview
//                                    // box to returned
//                                    // color
//                                    //mColorPreview.setBackgroundColor(mDefaultColor);
//                                }
//                            });
            openColorPickerDialogue();
        });

    }

    public Map<String, Object> prepararEvento () {

        String s_titulo = event_title.getText().toString(), s_descripcion = event_description.getText().toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String s_fechaInicio = event_start_date.getText().toString();
        String s_horaInicio = event_start_time.getText().toString();
        String stringDateInicio = s_fechaInicio + " " + s_horaInicio;
        String s_fechaFin = event_end_date.getText().toString();
        String s_horaFin = event_end_time.getText().toString();
        String stringDateFin = s_fechaFin + " " + s_horaFin;
        String s_location = event_location.getText().toString();
        String s_color = event_color.getText().toString();
        Date dateInicio = null;
        Date dateFin = null;
        Map<String, Object> evento = new HashMap<>();

        if (s_titulo.isEmpty() || s_fechaInicio.isEmpty() || s_horaInicio.isEmpty()
                || s_fechaFin.isEmpty() || s_horaFin.isEmpty() || s_location.isEmpty() ||
                //latitud.getText().toString().isEmpty() || longitud.getText().toString().isEmpty() ||
                s_descripcion.isEmpty() || s_color.isEmpty()) {

            Log.d(TAG, "Tienes que rellenar todos los campos");

        } else {

            try {
                dateInicio = simpleDateFormat.parse(stringDateInicio);
                dateFin = simpleDateFormat.parse(stringDateFin);
            } catch (ParseException e) {
                Log.d(TAG, e.toString());
            }

            Timestamp timeStampInicio = new Timestamp(dateInicio);
            Timestamp timeStampFin = new Timestamp(dateFin);
            Float s_latitud = Float.valueOf(event_location.getText().toString().substring(0, event_location.getText().toString().indexOf(":")));
            Float s_longitud = Float.valueOf(event_location.getText().toString().substring(event_location.getText().toString().indexOf(":") + 2));
            GeoPoint geoPoint = new GeoPoint(s_latitud, s_longitud);

            evento.put("Titulo", s_titulo);
            evento.put("Inicio", timeStampInicio);
            evento.put("Fin", timeStampFin);
            evento.put("DNI_Cliente", cliente.getDni_cliente());
            evento.put("Ubicacion", geoPoint);
            evento.put("Descripcion", s_descripcion);
            evento.put("Color", s_color);
            return evento;

        }

        return null;

    }

    private void abrirMapa(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LocationFragment locationFragment = new LocationFragment();
        //MapaFragment mapaFragment = new MapaFragment();
        //fragmentTransaction.add(locationFragment, "LocationFragment");
        //fragmentTransaction.replace(R.id.event_coordinator, locationFragment);
        fragmentTransaction.add(R.id.event_holder, locationFragment);
        //fragmentTransaction.replace(R.id.event_holder, mapaFragment);
        fragmentTransaction.commit();
    }

    public void crearEvento() {
        Map<String, Object> evento = prepararEvento();

        if (evento != null) {

            db.collection("Eventos").add(evento).addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Insertado evento con ID: " + documentReference.getId());

                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error insertando evento", e));

            Intent intent = new Intent(EventActivity.this, Navegador.class);
            setResult(CLAVE_INSERTADO, intent);
            intent.putExtra("Cliente", cliente);
            EventActivity.super.onBackPressed();
            controladorEventos.launch(intent);
            finish();

        } else {

            new AlertDialog.Builder(EventActivity.this)
                    .setTitle("Faltan datos")
                    .setMessage("Tienes que rellenar todos los campos")
                    .setIcon(android.R.drawable.ic_dialog_dialer)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }}
                    ).show();

        }
    }

    ActivityResultLauncher controladorEventos = registerForActivityResult(
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

    @Override
    public void Update(MarkerOptions markerOptions) {
        event_location.setText(markerOptions.getTitle());
    }

    public void openColorPickerDialogue() {

        // the AmbilWarnaDialog callback needs 3 parameters
        // one is the context, second is default color,
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, mDefaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // leave this function body as
                        // blank, as the dialog
                        // automatically closes when
                        // clicked on cancel button
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // change the mDefaultColor to
                        // change the GFG text color as
                        // it is returned when the OK
                        // button is clicked from the
                        // color picker dialog
                        mDefaultColor = color;
                        Log.d(TAG, color + " - " + mDefaultColor);

                        // now change the picked color
                        // preview box to mDefaultColor
                        //mColorPreview.setBackgroundColor(mDefaultColor);
                        event_color.setText(String.valueOf(mDefaultColor));
                    }
                });
        colorPickerDialogue.show();
    }
}