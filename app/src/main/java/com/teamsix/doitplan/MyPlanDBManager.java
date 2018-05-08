package com.teamsix.doitplan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyPlanDBManager extends SQLiteOpenHelper {
    private Context context;

    public MyPlanDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE IF NOT EXISTS PLANS ( ");
        sb.append(" _ID INTEGER PRIMARY KEY, ");
        sb.append(" MSG TEXT, ");
        sb.append(" IF_CODE INTEGER, ");
        sb.append(" IF_VALUE TEXT, ");
        sb.append(" RESULT_CODE INTEGER, ");
        sb.append(" RESULT_VALUE TEXT, ");
        sb.append(" IS_SHARE INTEGER, ");
        sb.append(" IS_WORK INTEGER )");

        // SQLite Database로 쿼리 실행
        sqLiteDatabase.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
