package com.example.seradmin.calendario;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;
import static androidx.core.widget.TextViewCompat.setTextAppearance;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.seradmin.EventoDetalle;
import com.example.seradmin.EventoMain;
import com.example.seradmin.R;
import com.example.seradmin.Recycler.Cliente;
import com.example.seradmin.database.eventosDatabase.Evento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.joda.time.DateTime;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.callback.Callback;

public class MonthFragment extends Fragment{

    private ImageView right, left;
    private TextView mes;
    RelativeLayout dayTV;
    LinearLayout  linearLayout;
    private final int DAYS_CNT = 42;
    private final String DATE_PATTERN = "ddMMYYYY";
    private final String YEAR_PATTERN = "YYYY";
    private final String today = new DateTime().toString(DATE_PATTERN);
    private DateTime targetDate = new DateTime();
    private Resources res;
    private String packageName;
    private Cliente cliente;
    private ArrayList<Evento> eventos = new ArrayList<Evento>();

    private ArrayList<Event> events = new ArrayList<Event>();

    public MonthFragment(){

    }

    public MonthFragment(Cliente cliente){
        this.cliente = cliente;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month, container, false);
        mes = view.findViewById(R.id.mes);
        right = view.findViewById(R.id.right);
        left = view.findViewById(R.id.left);

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextMonth(view);
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrevMonth(view);
            }
        });

        packageName = getActivity().getPackageName();

        res = view.getResources();
        for (int i = 0; i < DAYS_CNT; i++) {
            linearLayout = view.findViewById(res.getIdentifier("day_" + i, "id", packageName));
        }
        updateCalendar(targetDate, view);

        return view;
    }

    public void updateCalendar(DateTime targetDate, View view) {
        this.targetDate = targetDate;
        getMonthName();
        getDays(targetDate, view);
    }

    public void getPrevMonth(View view) {
        updateCalendar(targetDate.minusMonths(1), view);
    }

    public void getNextMonth(View view) {
        updateCalendar(targetDate.plusMonths(1), view);
    }

    private void getDays(DateTime targetDate, View view) {
        final List<Day> days = new ArrayList<>(DAYS_CNT);

        final int currMonthDays = targetDate.dayOfMonth().getMaximumValue();
        final int firstDayIndex = targetDate.withDayOfMonth(1).getDayOfWeek() - 1;
        final int prevMonthDays = targetDate.minusMonths(1).dayOfMonth().getMaximumValue();

        boolean isThisMonth = false;
        boolean isToday;
        int value = prevMonthDays - firstDayIndex + 1;

        for (int i = 0; i < DAYS_CNT; i++) {
            if (i < firstDayIndex) {
                isThisMonth = false;
            } else if (i == firstDayIndex) {
                value = 1;
                isThisMonth = true;
            } else if (value == currMonthDays + 1) {
                value = 1;
                isThisMonth = false;
            }

            isToday = isThisMonth && isToday(targetDate, value);

            ArrayList<Event> events = new ArrayList<Event>();
            final Day day = new Day(value, isThisMonth, isToday, events);
            if (isToday) {  //Momentaneo hasta que BBDD
                cargarEvento(targetDate, day);
            }
            //poblarCalendario(days, day);
            days.add(day);
            value++;
        }

        updateCalendar(getMonthName(), days, view);
    }

    private void cargarEvento(DateTime targetDate, Day day) {
        eventos = new ArrayList<Evento>();
        events = new ArrayList<Event>();
        DateTime inicio = new DateTime();
        DateTime fin = new DateTime();
        Event uno = new Event(inicio, fin, "Hoy");
        events.add(uno);
        day.setDayEvents(events);
    }

    public void updateCalendar(String month, List<Day> days, View view) {
        updateMonth(month);
        updateDays(days, view);
    }

    private void updateMonth(String month) {
        mes.setText(month);
        mes.setTextColor(Color.WHITE);
    }

    private void updateDays(List<Day> days, View view) {
        final int len = days.size();

        LayoutInflater inflater = LayoutInflater.from(this.getContext());

        for (int i = 0; i < len; i++) {
            final Day day = days.get(i);

            linearLayout = view.findViewById(res.getIdentifier("day_" + i, "id", packageName));
            linearLayout.removeAllViews();
            final View v = inflater.inflate(R.layout.day_monthly_number_view, null);
            dayTV = v.findViewById(R.id.day_monthly_number_holder);
            TextView text = v.findViewById(R.id.day_monthly_number_id);
            ImageView back = v.findViewById(R.id.day_monthly_number_background);

            int curTextColor = Color.GRAY;
            if (day.getIsThisMonth()) {
                curTextColor = Color.WHITE;
            }

            if (day.getIsToday()) {
                setTextAppearance(text, R.style.hoy);
                back.setVisibility(View.VISIBLE);
            }

            text.setText(String.valueOf(day.getValue()));
            text.setTextColor(curTextColor);
            linearLayout.addView(dayTV);

            for (Event event : day.getDayEvents()) {
                final View ev = inflater.inflate(R.layout.day_monthly_event_view_widget, null);
                RelativeLayout rl = ev.findViewById(R.id.day_monthly_event_holder);
                TextView titulo = ev.findViewById(R.id.day_monthly_event_id);
                titulo.setText(event.getTitulo());
                linearLayout.addView(rl);
            }
        }
    }

    private boolean isToday(DateTime targetDate, int curDayInMonth) {
        return targetDate.withDayOfMonth(curDayInMonth).toString(DATE_PATTERN).equals(today);
    }

    private String getMonthName() {
        final String[] meses = getResources().getStringArray(R.array.months);
        String mes = (meses[targetDate.getMonthOfYear() - 1]);
        final String targetYear = targetDate.toString(YEAR_PATTERN);
        if (!targetYear.equals(new DateTime().toString(YEAR_PATTERN))) {
            mes += " " + targetYear;
        }
        return mes;
    }

    public DateTime getTargetDate() {
        return targetDate;
    }

    public void poblarCalendario(List<Day> days, Day day) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Eventos").whereEqualTo("DNI_Cliente", cliente.getDni_cliente()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    eventos = new ArrayList<>();
                    events = new ArrayList<Event>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference ref = document.getReference();
                        Evento evento = new Evento();
                        Timestamp timestampInicio = (Timestamp) document.get("Inicio");
                        Timestamp timestampFin = (Timestamp) document.get("Fin");
                        //evento.setId(document.getId());
                        //evento.setTitulo(document.get("Titulo").toString());
                        //evento.setFechaInicio(simpleDateFormat.format(timestamp.toDate()));
                        DateTime inicio = new DateTime(timestampInicio.getSeconds());
                        DateTime fin = new DateTime(timestampFin.getSeconds());
                        Event uno = new Event(inicio, fin, "Hoy");
                        events.add(uno);
                        day.setDayEvents(events);
                        days.add(day);
                        //eventos.add(evento);
                    }
//                    aE = new AdaptadorEventos(eventos);
//                    RVEventos.setAdapter(aE);
//                    aE.setClickListener(new AdaptadorEventos.ItemClickListener() {
//                        @Override
//                        public void onClick(View view, int position, Evento evento) {
//                            Intent intent = new Intent(EventoMain.this, EventoDetalle.class);
//                            intent.putExtra("Detalle", CLAVE_LISTA);
//                            intent.putExtra("Evento", evento);
//                            controladorEventos.launch(intent);
//                        }
//                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


}