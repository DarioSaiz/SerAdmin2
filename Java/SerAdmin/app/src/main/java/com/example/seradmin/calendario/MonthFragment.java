package com.example.seradmin.calendario;

import static androidx.core.widget.TextViewCompat.setTextAppearance;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.example.seradmin.ManejadorClickCalendario;
import com.example.seradmin.R;
import com.example.seradmin.Recycler.Cliente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class MonthFragment extends Fragment {

    private ImageView right, left;
    private TextView mes;
    RelativeLayout dayTV;
    LinearLayout linearLayout;
    private final int DAYS_CNT = 42;
    private final String DATE_PATTERN = "ddMMYYYY";
    private final String YEAR_PATTERN = "YYYY";
    private final String today = new DateTime().toString(DATE_PATTERN);
    private DateTime targetDate = new DateTime();
    private Resources res;
    private String packageName;
    private Cliente cliente;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MonthFragment(){

    }

    public MonthFragment(Cliente cliente){
        this.cliente = cliente;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month, container, false);
        mes = view.findViewById(R.id.mes);
        right = view.findViewById(R.id.right);
        left = view.findViewById(R.id.left);

        if (getActivity().getIntent().getExtras().containsKey("Cliente")) {
            cliente = (Cliente) getActivity().getIntent().getSerializableExtra("Cliente");
        }

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
            //linearLayout.setOnClickListener();
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
        DateTime currentDate = getCurrDate(targetDate, firstDayIndex);

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
            // Crar el día
            final Day day = new Day(value, isThisMonth, isToday, events);
            // Cargarle los evento
            cargarEvento(currentDate, day, new OnEventsLoadedListener() {
                @Override
                public void onEventsLoaded(ArrayList<Event> loadedEvents) {
                    day.setDayEvents(loadedEvents);
                    updateDays(days, view);
                }
            });

            days.add(day);
            value++;

            currentDate = currentDate.plusDays(1); // No mover

        }

        updateCalendar(getMonthName(), days, view);
    }

    ArrayList<Event> events = new ArrayList<Event>();

    private void cargarEvento(DateTime currentDate, Day day, OnEventsLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference events_firebase = db.collection("Eventos");

        // DateTime de inicio del día que se procesa
        DateTime inicioDia = currentDate.withTimeAtStartOfDay().minusHours(2);;
        // DateTime del fin del día que se procesa
        DateTime finDia = currentDate.withTimeAtStartOfDay().plusDays(1).minusMillis(1).minusHours(2);;

        Query queryInicio = events_firebase.whereEqualTo("DNI_Cliente", cliente.getDni_cliente()).whereLessThanOrEqualTo("Inicio", finDia.toDate())
                .orderBy("Inicio", Query.Direction.ASCENDING);
        Query queryFin = events_firebase.whereEqualTo("DNI_Cliente", cliente.getDni_cliente()).whereGreaterThanOrEqualTo("Fin", inicioDia.toDate())
                .orderBy("Fin", Query.Direction.ASCENDING);

        queryInicio.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Event> events = new ArrayList<>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    DateTime fin = new DateTime(document.getDate("Fin"));
                    String title = document.getString("Titulo");
                    String color = document.getString("Color");
                    String id = document.getId();

                    if (fin.isAfter(inicioDia)) {
                        events.add(new Event(inicioDia, fin, title, color, id));
                    }
                }

                // Notificar al listener que los eventos se han cargado
                listener.onEventsLoaded(events);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MonthFragment.this.getActivity(), "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                Log.e("EVENTOS", e.toString());
            }
        });

        queryFin.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Event> events = new ArrayList<>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    DateTime inicio = new DateTime(document.getDate("Inicio"));
                    String title = document.getString("Titulo");
                    String color = document.getString("Color");
                    String id = document.getId();

                    if (inicio.isBefore(finDia)) {
                        events.add(new Event(inicio, finDia, title, color, id));
                    }
                }

                // Notificar al listener que los eventos se han cargado
                listener.onEventsLoaded(events);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MonthFragment.this.getActivity(), "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                Log.e("EVENTOS", e.toString());
            }
        });
    }

    public interface OnEventsLoadedListener {
        void onEventsLoaded(ArrayList<Event> loadedEvents);
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
                rl.setBackgroundColor(Integer.valueOf(event.getColor()));
                rl.setOnClickListener(new ManejadorClickCalendario(rl, event, getContext()));
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

    public DateTime getCurrDate(DateTime targetDate, int firstDayIndex) {
        DateTime currentDate = targetDate.withDayOfMonth(1); // Establecer el día como el primer día del mes
        if (firstDayIndex > 0) {
            int daysToSubtract = firstDayIndex + currentDate.getDayOfMonth() - 1; // Calcular el número de días a restar para llegar al primer día
            currentDate = currentDate.minusDays(daysToSubtract);
        }
        return currentDate;
    }


}
