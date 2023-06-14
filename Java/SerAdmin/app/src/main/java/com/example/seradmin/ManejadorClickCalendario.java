package com.example.seradmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.seradmin.InterfazUsuari.Navegador;
import com.example.seradmin.calendario.Event;

import java.util.ArrayList;

public class ManejadorClickCalendario extends Activity implements View.OnClickListener {

    RelativeLayout relativeLayout;
    boolean hasEvent = true;
    Context context;
    Event evento;

    public ManejadorClickCalendario(){

    }

    public ManejadorClickCalendario(RelativeLayout relativeLayout, Event evento){
        this.relativeLayout = relativeLayout;
        this.evento = evento;
    }

    public ManejadorClickCalendario(RelativeLayout relativeLayout, Event evento, Context context){
        this.relativeLayout = relativeLayout;
        this.evento = evento;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (evento != null) {
            intent = new Intent(context, EventoDetalle.class);
            intent.putExtra("Evento", evento);
            //finish();
        } else {
            intent = new Intent(context, EventoMain.class);
            //finish();
        }
        startActivity(intent);
    }

}
