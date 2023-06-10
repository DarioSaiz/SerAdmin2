package com.example.seradmin;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class ManejadorFechas implements View.OnClickListener {

    //EditText fecha;
    TextView fecha, fechaIda;
    Boolean fin = false;
    FragmentManager fragmentManager;
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    public ManejadorFechas() {

    }

    public ManejadorFechas(TextView fecha, FragmentManager fragmentManager) {
        this.fecha = fecha;
        this.fragmentManager = fragmentManager;
    }

    public ManejadorFechas(TextView fecha, Boolean fin, TextView fechaIda, FragmentManager fragmentManager) {
        this.fecha = fecha;
        this.fin = fin;
        this.fechaIda = fechaIda;
        this.fragmentManager = fragmentManager;
    }

//    public ManejadorFechas(EditText fecha, FragmentManager fragmentManager) {
//        this.fecha = fecha;
//        this.fragmentManager = fragmentManager;
//    }

    @Override
    public void onClick(View view) {
        if (view.getId() == fecha.getId()) {
            //showDatePickerDialog();
            if (!fin) {
                showDatePickerDialog(fecha, (Date.from(Instant.now())).getTime());
            } else {
                try {
                    showDatePickerDialog(fecha, (df.parse(fechaIda.getText().toString())).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            //showDatePickerDialog(fecha, fragmentManager);
        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "-" + (month+1) + "-" + year;
                fecha.setText(selectedDate);
            }
        });

        newFragment.show(fragmentManager, "datePicker");
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public void showDatePickerDialog(final TextView textView, long min) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(textView.getContext(),
                (datePicker, year1, month1, day1) -> {
                    final String selectedDate = twoDigits(day1) + "-" + twoDigits(month1 + 1) + "-" + year1;
                    textView.setText(selectedDate);
                }, year, month, day);
        dp.getDatePicker().setMinDate(min);
        dp.show();
    }

}