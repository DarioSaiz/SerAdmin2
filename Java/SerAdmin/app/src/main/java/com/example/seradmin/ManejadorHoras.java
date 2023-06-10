package com.example.seradmin;

import android.app.TimePickerDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.FragmentManager;

import java.util.Calendar;

public class ManejadorHoras implements View.OnClickListener {

    //EditText hora;
    TextView hora;
    FragmentManager fragmentManager;

    public ManejadorHoras() {

    }

    public ManejadorHoras(EditText hora, FragmentManager fragmentManager) {
        this.hora = hora;
        this.fragmentManager = fragmentManager;
    }

    public ManejadorHoras(TextView hora, FragmentManager fragmentManager) {
        this.hora = hora;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == hora.getId()) {
            //showTimePickerDialog();
            //showTimePickerDialog(hora, fragmentManager);
            showTimePickerDialog(hora);
        }
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public void showTimePickerDialog(final TextView textView) {

        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog tp = new TimePickerDialog(textView.getContext(),
                (view, hourOfDay, minute) -> textView.setText(twoDigits(hourOfDay) + ":" + twoDigits(minute)), mHour, mMinute, true);

        tp.show();
    }

    private void showTimePickerDialog() {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                // +1 because January is zero
                final String selectedHour = hourOfDay + ":" + minute;
                hora.setText(selectedHour);
            }
        });

        newFragment.show(fragmentManager, "timePicker");
    }

}


