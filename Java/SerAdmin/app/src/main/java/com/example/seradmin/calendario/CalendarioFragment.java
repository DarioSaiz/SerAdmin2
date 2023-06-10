package com.example.seradmin.calendario;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.seradmin.InterfazUsuari.InterfazUsuario;
import com.example.seradmin.NuevoEvento;
import com.example.seradmin.R;
import com.example.seradmin.Recycler.Cliente;
import com.example.seradmin.Tree.MainTree;
import com.example.seradmin.database.eventosDatabase.Evento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class CalendarioFragment extends Fragment {

    FloatingActionButton add;
    Cliente cliente = new Cliente();
    ImageView home,files,calendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calendario, container, false);
        add = view.findViewById(R.id.add);
        add.setVisibility(View.VISIBLE);

//        home = findViewById(R.id.home);
//        files = findViewById(R.id.files);
//        calendar = findViewById(R.id.calendar);

        if (getActivity().getIntent().getExtras() != null) {
            if (getActivity().getIntent().getExtras().containsKey("Cliente")) {
                cliente = (Cliente) getActivity().getIntent().getSerializableExtra("Cliente");
                Log.d("Cliente", cliente.getNombre());
            }
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                intent.putExtra("Cliente", cliente);
                startActivity(intent);
                //finish();
            }
        });

        getActivity().getSupportFragmentManager().
                beginTransaction().add(R.id.fragments_holder, new MonthFragment(cliente)).
                commit();

        return view;

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
