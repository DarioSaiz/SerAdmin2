package com.example.seradmin;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ManejadorFechas implements View.OnClickListener {

    //EditText fecha;
    TextView fecha;
    FragmentManager fragmentManager;

    public ManejadorFechas() {

    }

    public ManejadorFechas(TextView fecha, FragmentManager fragmentManager) {
        this.fecha = fecha;
        this.fragmentManager = fragmentManager;
    }

//    public ManejadorFechas(EditText fecha, FragmentManager fragmentManager) {
//        this.fecha = fecha;
//        this.fragmentManager = fragmentManager;
//    }

    @Override
    public void onClick(View view) {
        if (view.getId() == fecha.getId()) {
            showDatePickerDialog();
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

}