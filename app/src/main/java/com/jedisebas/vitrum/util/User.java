package com.jedisebas.vitrum.util;

public class User {

    private User() {
        throw new IllegalStateException("Utility class");
    }

    public static int id;
    public static String unit;
    public static boolean worker;
    public static int unitId;
}
