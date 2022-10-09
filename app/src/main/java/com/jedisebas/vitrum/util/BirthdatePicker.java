package com.jedisebas.vitrum.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jedisebas.vitrum.R;

import java.util.Calendar;

public class BirthdatePicker extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable final Bundle savedInstanceState) {
        final Calendar mCalendar = Calendar.getInstance();
        final int year = mCalendar.get(Calendar.YEAR);
        final int month = mCalendar.get(Calendar.MONTH);
        final int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), R.style.MySpinnerDatePickerStyle, (DatePickerDialog.OnDateSetListener)
                getActivity(), year, month, dayOfMonth);
    }
}