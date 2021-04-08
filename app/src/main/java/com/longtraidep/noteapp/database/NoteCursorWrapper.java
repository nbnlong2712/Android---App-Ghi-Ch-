package com.longtraidep.noteapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.longtraidep.noteapp.Note;
import com.longtraidep.noteapp.database.NoteDbSchema.NoteTable;

import java.util.Date;
import java.util.UUID;

public class NoteCursorWrapper extends CursorWrapper {
    public NoteCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public Note getNote()    //hàm lấy 1 hàng dữ liệu ra từ database và lưu vào 1 biến
    {
        String uuidString = getString(getColumnIndex(NoteTable.Cols.UUID));
        String title =  getString(getColumnIndex(NoteTable.Cols.TITLE));
        String tag = getString(getColumnIndex(NoteTable.Cols.TAG));
        long dateTime = getLong(getColumnIndex(NoteTable.Cols.DATETIME));
        String content = getString(getColumnIndex(NoteTable.Cols.CONTENT));

        Note note = new Note(UUID.fromString(uuidString));
        note.setTitle(title);
        note.setTag(tag);
        note.setDateTime(new Date(dateTime));
        note.setContent(content);

        return note;
    }
}
