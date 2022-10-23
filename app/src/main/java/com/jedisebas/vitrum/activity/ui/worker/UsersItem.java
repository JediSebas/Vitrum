package com.jedisebas.vitrum.activity.ui.worker;

public class UsersItem {

    private int id;
    private String name;
    private String surname;

    public UsersItem(final int id, final String name, final String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public UsersItem() {
        this(0, null, null);
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
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
}
