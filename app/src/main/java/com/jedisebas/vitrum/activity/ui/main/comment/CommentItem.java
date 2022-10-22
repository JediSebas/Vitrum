package com.jedisebas.vitrum.activity.ui.main.comment;

import com.jedisebas.vitrum.util.BirthdatePicker;

import java.sql.Timestamp;
import java.util.Calendar;

public class CommentItem {

    private String name;
    private String surname;
    private String datetime;
    private String message;

    public CommentItem(final String name, final String surname, final String datetime, final String message) {
        this.name = name;
        this.surname = surname;
        this.datetime = datetime;
        this.message = message;
    }

    public CommentItem() {
        this(null, null, null, null);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(final String datetime) {
        this.datetime = datetime;
    }

    public void setDateTime(final Timestamp dateTime) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        this.datetime = BirthdatePicker.getCorrectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

}
