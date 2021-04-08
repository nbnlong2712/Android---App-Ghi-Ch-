package com.longtraidep.noteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.longtraidep.noteapp.database.NoteBaseHelper;
import com.longtraidep.noteapp.database.NoteCursorWrapper;
import com.longtraidep.noteapp.database.NoteDbSchema;
import com.longtraidep.noteapp.database.NoteDbSchema.NoteTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteLab {
    private static NoteLab sNoteLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static NoteLab get(Context context)
    {
        if(sNoteLab == null)
        {
            sNoteLab = new NoteLab(context);
        }
        return sNoteLab;
    }

    private NoteLab (Context context)
    {
        mContext = context.getApplicationContext();  //lấy context (với trường hợp này là lấy của NoteLab, dùng
                                                     // để cho DB biết rằng sẽ xử lý DB ở NoteLab, nơi có list
                                                     // các Note, để lưu vào DB)
        mDatabase = new NoteBaseHelper(mContext).getWritableDatabase();
    }

    public void addNote(Note note)
    {
        ContentValues values = getContentValues(note);
        mDatabase.insert(NoteTable.NAME,null, values);
    }
    //Challenge
    public Boolean deleteNote(UUID id) {
        String uuidString = id.toString();
        mDatabase.delete(NoteTable.NAME, NoteTable.Cols.UUID + " = ?", new String[] {uuidString});
        return true;
    }

    public List<Note> getNotes()     //hàm lấy đanh sách note từ db
    {
        List<Note> notes = new ArrayList<>();
        NoteCursorWrapper cursor = queryNotes(null, null);
        try{
            cursor.moveToFirst();   //bắt đầu duyệt và lấy từ liệu từ hàng đầu tiên
            while (!cursor.isAfterLast()){  //nếu vẫn chưa phải hàng dữ liệu cuối dùng thì tiếp tục lấy và move đến hàng tiếp theo
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return notes;
    }

    public Note getNote(UUID id)
    {
        NoteCursorWrapper cursor = queryNotes(NoteTable.Cols.UUID + " = ?", new String[]{id.toString()});
        try {
            if(cursor.getCount() == 0)
                return null;
            cursor.moveToFirst();
            return cursor.getNote();
        }
        finally {
            cursor.close();
        }
    }

    public void updateNote(Note note) //update lại một ghi chú khi ta chỉnh sửa ghi chú đó
    {
        String uuidString = note.getId().toString();    //lấy ID của hàng
        ContentValues values = getContentValues(note);   //Giá trị sẽ được lưu vào ContentValues cả khi có hoặc không chỉnh sửa

        mDatabase.update(NoteTable.NAME, values, NoteTable.Cols.UUID + " = ?", new String[]{uuidString});//update dữ liệu trong hàng có id là uuidString
    }

    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(
                NoteTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new NoteCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Note note) //note lưu các thuộc tính giá trị muốn thêm vào bảng
    {
        ContentValues values = new ContentValues();
        values.put(NoteTable.Cols.UUID, note.getId().toString());   //arg1: chọn thuộc tính để thêm vào bảng, arg2: giá trị thêm vào
        values.put(NoteTable.Cols.TITLE, note.getTitle());
        values.put(NoteTable.Cols.TAG, note.getTag());
        values.put(NoteTable.Cols.DATETIME, note.getDateTime().getTime());
        values.put(NoteTable.Cols.CONTENT, note.getContent());

        return values;
    }
}
