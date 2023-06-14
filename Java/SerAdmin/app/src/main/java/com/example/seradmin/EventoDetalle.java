package com.example.seradmin;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.seradmin.InterfazUsuari.InterfazUsuario;
import com.example.seradmin.InterfazUsuari.Navegador;
import com.example.seradmin.calendario.EventActivity;
import com.example.seradmin.calendario.LocationFragment;
import com.example.seradmin.database.eventosDatabase.Evento;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class EventoDetalle extends AppCompatActivity implements LocationFragment.OnCallbackReceived {

    public static final int CLAVE_MODIFICADO = 56;
    public static final int CLAVE_ELIMINADO = 57;
    EditText tituloEventoDetalle, fechaInicioEventoDetalle, fechaFinEventoDetalle, colorEventoDetalle;
    EditText horaInicioEventoDetalle, horaFinEventoDetalle, ubicacionEventoDetalle, descripcionEventoDetalle;
    Button modificarEvento, eliminarEvento;
    ImageView editarTitulo, editarFechaInicio, editarFechaFin, editarHoraInicio, editarHoraFin, editarUbicacion, editarDescripcion, editarColor;

    String patternFecha = "dd-MM-yyyy";
    String patternHora = "HH:mm";
    SimpleDateFormat simpleDateFormatFecha = new SimpleDateFormat(patternFecha);
    SimpleDateFormat simpleDateFormatHora = new SimpleDateFormat(patternHora);
    Evento evento;
    FirebaseFirestore db;
    private int mDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detalle);

        tituloEventoDetalle = findViewById(R.id.tituloEditableEventoDetalle);
        fechaInicioEventoDetalle = findViewById(R.id.fechaInicioEditableEventoDetalle);
        horaInicioEventoDetalle = findViewById(R.id.horaInicioEditableEventoDetalle);
        fechaFinEventoDetalle = findViewById(R.id.fechaFinEditableEventoDetalle);
        horaFinEventoDetalle = findViewById(R.id.horaFinEditableEventoDetalle);
        ubicacionEventoDetalle = findViewById(R.id.ubicacionEditableEventoDetalle);
        descripcionEventoDetalle = findViewById(R.id.descripcionEditableEventoDetalle);
        colorEventoDetalle = findViewById(R.id.colorEditableEventoDetalle);

        editarTitulo = findViewById(R.id.editTitulo);
        editarFechaInicio = findViewById(R.id.editFechaInicio);
        editarFechaFin = findViewById(R.id.editFechaFin);
        editarHoraInicio = findViewById(R.id.editHoraInicio);
        editarHoraFin = findViewById(R.id.editHoraFin);
        editarUbicacion = findViewById(R.id.editUbicacion);
        editarDescripcion = findViewById(R.id.editDescripcion);
        editarColor = findViewById(R.id.editColor);

        modificarEvento = findViewById(R.id.modificarEvento);
        eliminarEvento = findViewById(R.id.eliminarEvento);

        ManejadorFechas manejadorFechaInicio = new ManejadorFechas(fechaInicioEventoDetalle, getSupportFragmentManager());
        ManejadorFechas manejadorFechaFin = new ManejadorFechas(fechaFinEventoDetalle, true, fechaInicioEventoDetalle, getSupportFragmentManager());

        fechaInicioEventoDetalle.setOnClickListener(manejadorFechaInicio);
        fechaFinEventoDetalle.setOnClickListener(manejadorFechaFin);

        ManejadorHoras manejadorHoraInicio = new ManejadorHoras(horaInicioEventoDetalle, getSupportFragmentManager());
        ManejadorHoras manejadorHoraFin = new ManejadorHoras(horaFinEventoDetalle, getSupportFragmentManager());

        horaInicioEventoDetalle.setOnClickListener(manejadorHoraInicio);
        horaFinEventoDetalle.setOnClickListener(manejadorHoraFin);

        evento = (Evento) getIntent().getSerializableExtra("Evento");

        db = FirebaseFirestore.getInstance();

        selectEvento(evento.getId());

        eliminarEvento.setOnClickListener(v -> {

            new AlertDialog.Builder(EventoDetalle.this)
                    .setTitle("¿Estás seguro de que deseas eliminar el evento?")
                    .setMessage("El evento desaparecerá de la base de datos y no podrás recuperarlo")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            DocumentReference ref = db.collection("Eventos").document(evento.getId());
                            ref.delete()
                                .addOnSuccessListener((v) -> {
                                    Log.d(TAG, "Evento eliminado!");
                                    volverEventoMain(CLAVE_ELIMINADO);
                                })
                                .addOnFailureListener((v) -> {
                                    Log.d(TAG, "Error eliminando evento");
                                });
                        }})
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "Eliminación de evento cancelada");
                        }
                    }).show();

        });

        modificarEvento.setOnClickListener((v) -> {

            actualizarEvento();

        });

        ubicacionEventoDetalle.setOnClickListener(v -> {
            if (ubicacionEventoDetalle.isEnabled()) {
                abrirMapa();
            }
        });

        ManejadorClickEdit manejadorClickEditTitulo = new ManejadorClickEdit(
                tituloEventoDetalle, editarTitulo, getDecorador(tituloEventoDetalle.getText().toString())
        );

        ManejadorClickEdit manejadorClickEditUbicacion = new ManejadorClickEdit(
                ubicacionEventoDetalle, editarUbicacion, getDecorador(ubicacionEventoDetalle.getText().toString())
        );
        ManejadorClickEdit manejadorClickEditDescripcion = new ManejadorClickEdit(
                descripcionEventoDetalle, editarDescripcion, getDecorador(descripcionEventoDetalle.getText().toString())
        );
        ManejadorClickEdit manejadorClickEditFechaInicio = new ManejadorClickEdit(
                fechaInicioEventoDetalle, editarFechaInicio, getDecorador(fechaInicioEventoDetalle.getText().toString())
        );
        ManejadorClickEdit manejadorClickEditFechaFin = new ManejadorClickEdit(
                fechaFinEventoDetalle, editarFechaFin, getDecorador(fechaFinEventoDetalle.getText().toString())
        );
        ManejadorClickEdit manejadorClickEditHoraInicio = new ManejadorClickEdit(
                horaInicioEventoDetalle, editarHoraInicio, getDecorador(horaInicioEventoDetalle.getText().toString())
        );
        ManejadorClickEdit manejadorClickEditHoraFin = new ManejadorClickEdit(
                horaFinEventoDetalle, editarHoraFin, getDecorador(horaFinEventoDetalle.getText().toString())
        );
        ManejadorClickEdit manejadorClickEditColor = new ManejadorClickEdit(
                colorEventoDetalle, editarColor, getDecorador(colorEventoDetalle.getText().toString())
        );

        editarTitulo.setOnClickListener(manejadorClickEditTitulo);
        editarUbicacion.setOnClickListener(manejadorClickEditUbicacion);
        editarDescripcion.setOnClickListener(manejadorClickEditDescripcion);
        editarFechaInicio.setOnClickListener(manejadorClickEditFechaInicio);
        editarFechaFin.setOnClickListener(manejadorClickEditFechaFin);
        editarHoraInicio.setOnClickListener(manejadorClickEditHoraInicio);
        editarHoraFin.setOnClickListener(manejadorClickEditHoraFin);
        editarColor.setOnClickListener(manejadorClickEditColor);

        colorEventoDetalle.setOnClickListener(view -> {
            openColorPickerDialogue();
        });

    }

    public String getEditTextText (String texto) {

        if (texto.length() != 0) {
            return texto.substring(texto.indexOf(": ") + 2);
        }
        return "";

    }

    public String getDecorador (String texto) {

        return texto.substring(0, texto.indexOf(": ") + 2);

    }

    public void selectEvento (String id) {

        db.collection("Eventos").whereEqualTo("__name__", id).limit(1).get()
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

    public void volverEventoMain (int clave) {

        Intent intent = new Intent(EventoDetalle.this, Navegador.class);
        setResult(clave , intent);
        //intent.putExtra("Cliente", cliente);
        EventoDetalle.super.onBackPressed();
        finish();

    }

    public void rellenarEditTexts(Task<QuerySnapshot> task) {

        for (QueryDocumentSnapshot document : task.getResult()) {
            Log.d(TAG, document.getId() + " => " + document.getData());
            Timestamp timestampInicio = (Timestamp) document.get("Inicio");
            Timestamp timestampFin = (Timestamp) document.get("Fin");
            GeoPoint geoPoint = (GeoPoint) document.get("Ubicacion");
            tituloEventoDetalle.setText(tituloEventoDetalle.getText() + document.get("Titulo").toString());
            fechaInicioEventoDetalle.setText(fechaInicioEventoDetalle.getText() + " " + simpleDateFormatFecha.format(timestampInicio.toDate()));
            horaInicioEventoDetalle.setText(horaInicioEventoDetalle.getText() + " " + simpleDateFormatHora.format(timestampInicio.toDate()));
            fechaFinEventoDetalle.setText(fechaFinEventoDetalle.getText() + " " + simpleDateFormatFecha.format(timestampFin.toDate()));
            horaFinEventoDetalle.setText(horaFinEventoDetalle.getText() + " " + simpleDateFormatHora.format(timestampFin.toDate()));
            ubicacionEventoDetalle.setText(ubicacionEventoDetalle.getText() + " " + geoPoint.getLatitude() + ":" + geoPoint.getLongitude());
            descripcionEventoDetalle.setText(descripcionEventoDetalle.getText() + document.get("Descripcion").toString());
            colorEventoDetalle.setText(colorEventoDetalle.getText() + document.get("Color").toString());
        }

    }

    public void actualizarEvento() {

        String s_titulo = getEditTextText(tituloEventoDetalle.getText().toString());
        String s_descripcion = getEditTextText(descripcionEventoDetalle.getText().toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Log.d(TAG, getEditTextText(fechaInicioEventoDetalle.getText().toString()));
        Log.d(TAG, getEditTextText(horaInicioEventoDetalle.getText().toString()));
        String s_fechaInicio = getEditTextText(fechaInicioEventoDetalle.getText().toString());
        String s_horaInicio = getEditTextText(horaInicioEventoDetalle.getText().toString());
        String stringDateInicio = s_fechaInicio + " " + s_horaInicio;
        String s_fechaFin = getEditTextText(fechaFinEventoDetalle.getText().toString());
        String s_horaFin = getEditTextText(horaFinEventoDetalle.getText().toString());
        String stringDateFin = s_fechaFin + " " + s_horaFin;
        String s_location = getEditTextText(ubicacionEventoDetalle.getText().toString());
        String s_color = getEditTextText(colorEventoDetalle.getText().toString());
        Log.d(TAG, getEditTextText(ubicacionEventoDetalle.getText().toString()));
        Date dateInicio = null;
        Date dateFin = null;
        Map<String, Object> event = new HashMap<>();

        if (s_titulo.isEmpty() || s_fechaInicio.isBlank() || s_horaInicio.isBlank()
                || s_fechaFin.isBlank() || s_horaFin.isBlank() ||s_location.isEmpty() ||
                //latitud.getText().toString().isEmpty() || longitud.getText().toString().isEmpty() ||
                s_descripcion.isEmpty() || s_color.isEmpty()) {

            Log.d(TAG, "Tienes que rellenar todos los campos");

            new AlertDialog.Builder(EventoDetalle.this)
                    .setTitle("Faltan datos")
                    .setMessage("Tienes que rellenar todos los campos")
                    .setIcon(android.R.drawable.ic_dialog_dialer)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }}
                    ).show();

        } else {

            try {
                dateInicio = simpleDateFormat.parse(stringDateInicio);
                dateFin = simpleDateFormat.parse(stringDateFin);
            } catch (ParseException e) {
                Log.d(TAG, e.toString());
            }

            Timestamp timeStampInicio = new Timestamp(dateInicio);
            Timestamp timeStampFin = new Timestamp(dateFin);
            Float s_latitud = Float.valueOf(s_location.substring(0, s_location.indexOf(":")));
            Float s_longitud = Float.valueOf(s_location.substring(s_location.indexOf(":") + 2));
            GeoPoint geoPoint = new GeoPoint(s_latitud, s_longitud);

            // UPDATE
            DocumentReference ref = db.collection("Eventos").document(evento.getId());
            ref.update("Titulo", getEditTextText(tituloEventoDetalle.getText().toString()));
            ref.update("Inicio", timeStampInicio);
            ref.update("Fin", timeStampFin);
            ref.update("Ubicacion", geoPoint);
            ref.update("Descripcion", getEditTextText(descripcionEventoDetalle.getText().toString()));
            ref.update("Color", getEditTextText(colorEventoDetalle.getText().toString()));

            Toast.makeText(this, "Evento con Id " + evento.getId() + " modificado", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Evento con Id " + evento.getId() + " modificado");

            volverEventoMain(CLAVE_MODIFICADO);
        }

    }

    private void abrirMapa(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LocationFragment locationFragment = new LocationFragment();
        //MapaFragment mapaFragment = new MapaFragment();
        //fragmentTransaction.add(locationFragment, "LocationFragment");
        //fragmentTransaction.replace(R.id.event_coordinator, locationFragment);
        fragmentTransaction.add(R.id.eventoDetallado, locationFragment);
        //fragmentTransaction.replace(R.id.event_holder, mapaFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void Update(MarkerOptions markerOptions) {
        ubicacionEventoDetalle.setText(markerOptions.getTitle());
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
                        colorEventoDetalle.setText(String.valueOf(mDefaultColor));
                    }
                });
        colorPickerDialogue.show();
    }
}