package com.longtraidep.noteapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.longtraidep.noteapp.database.NoteDbSchema.NoteTable;

public class NoteBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "noteBase.db";

    public NoteBaseHelper(Context context) //lấy context của file .java mà muốn tạo DB, ở đây NoteLab là nơi lưu trữ các Note, sẽ lấy context của NoteLab
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng
        db.execSQL("create table " + NoteTable.NAME + "("
                + " _id integer primary key autoincrement,"
                + NoteTable.Cols.UUID + ", "
                + NoteTable.Cols.TITLE + ", "
                + NoteTable.Cols.TAG + ", "
                + NoteTable.Cols.DATETIME + ", "
                + NoteTable.Cols.CONTENT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
