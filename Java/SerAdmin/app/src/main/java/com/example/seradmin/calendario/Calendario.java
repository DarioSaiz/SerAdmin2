package com.example.seradmin.calendario;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.seradmin.InterfazUsuari.InterfazUsuario;
import com.example.seradmin.NuevoEvento;
import com.example.seradmin.R;
import com.example.seradmin.Recycler.Cliente;
import com.example.seradmin.Tree.MainTree;
import com.example.seradmin.database.eventosDatabase.Evento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Calendario extends AppCompatActivity {

    FloatingActionButton add;
    Cliente cliente = new Cliente();
    ImageButton home,files,calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        add = findViewById(R.id.add);
        add.setVisibility(View.VISIBLE);

        home = findViewById(R.id.home);
        files = findViewById(R.id.files);
        calendar = findViewById(R.id.calendar);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("Cliente")) {
                cliente = (Cliente) getIntent().getSerializableExtra("Cliente");
                Log.d("Cliente", cliente.getNombre());
            }
        }

        home.setOnClickListener(view -> {
            Intent intent = new Intent(Calendario.this, InterfazUsuario.class);
            //intent.putExtra("Home", CLAVE_HOME);
            intent.putExtra("Cliente", cliente);
            controladorCalendario.launch(intent);
            finish();
        });

        files.setOnClickListener(view -> {
            Intent intent = new Intent(Calendario.this, MainTree.class);
            intent.putExtra("Cliente", cliente);
            //intent.putExtra("Files", CLAVE_FILES);
            controladorCalendario.launch(intent);
            finish();
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendario.this, EventActivity.class);
                intent.putExtra("Cliente", cliente);
                startActivity(intent);
                finish();
            }
        });

        getSupportFragmentManager().
                beginTransaction().add(R.id.fragments_holder, new MonthFragment()).
                commit();

    }

    ActivityResultLauncher controladorCalendario = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                int code = result.getResultCode();
//                switch (code) {
//                    case RESULT_CANCELED:
//                        Log.d(TAG, "Vuelve cancelado");
//                        break;
//                    case NuevoEvento.CLAVE_INSERTADO:
//                        Log.d(TAG, "EVENTO INSERTADO");
//                        poblarRecyclerView();
//                        break;
//
//                    case EventoDetalle.CLAVE_MODIFICADO:
//                        Log.d(TAG, "EVENTO MODIFICADO");
//                        poblarRecyclerView();
//                        break;
//
//                    case EventoDetalle.CLAVE_ELIMINADO:
//                        Log.d(TAG, "EVENTO ELIMINADO");
//                        poblarRecyclerView();
//                        break;
//
//                }

            }
    );

}