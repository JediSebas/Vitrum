package com.jedisebas.vitrum.util;

public class User {

    private User() {
        throw new IllegalStateException("Utility class");
    }

    public static long id;
    public static String unit;
    public static boolean worker;
    public static int unitId;
}
