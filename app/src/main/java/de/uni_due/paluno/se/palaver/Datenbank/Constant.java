package de.uni_due.paluno.se.palaver.Datenbank;

public class Constant {
    public static final String DATABASE_NAME="palaver.db";
    public static final int DATABASE_VERSION=1;
    public static String USER_NAME="";

    public static void setUserName(String userName) {
        USER_NAME = userName;
    }

    public static String getUserName() {
        return USER_NAME;
    }
}
