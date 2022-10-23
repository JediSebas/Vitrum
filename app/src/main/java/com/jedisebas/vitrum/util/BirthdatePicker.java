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
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), R.style.MySpinnerDatePickerStyle, (DatePickerDialog.OnDateSetListener)
                getActivity(), year, month, dayOfMonth);
    }

    public static String getCurrentDateTime() {
        final Calendar calendar = Calendar.getInstance();
        return getCorrectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    public static String getDate(final int year, int month, final int day) {
        return addZero(day) + "." + addZero(++month) + "." + year;
    }

    public static String getCorrectDate(final int year, int month, final int day) {
        return year + "-" + addZero(++month) + "-" + addZero(day);
    }

    public static String getCorrectDate(final int year, int month, final int day, final int hour, final int minute, final int second) {
        return year + "-" + addZero(++month) + "-" + addZero(day) + " " + addZero(hour) + ":" + addZero(minute) + ":" + addZero(second);
    }

    private static String addZero(final int x) {
        if (x < 10) {
            return "0" + x;
        } else {
            return String.valueOf(x);
        }
    }
}