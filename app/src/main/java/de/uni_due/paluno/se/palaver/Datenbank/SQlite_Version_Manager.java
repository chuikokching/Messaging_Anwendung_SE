package de.uni_due.paluno.se.palaver.Datenbank;

public class SQlite_Version_Manager {
    public static final String DATABASE_NAME="palaver.db";
    public static final int DATABASE_VERSION=1;
    public static String table_name;


    public static void setTable_name(String table_name) {
        SQlite_Version_Manager.table_name = table_name;
    }


    public static String getTable_name() {
        return table_name;
    }

}
