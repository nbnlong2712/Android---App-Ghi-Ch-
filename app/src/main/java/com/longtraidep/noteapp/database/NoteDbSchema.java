package com.longtraidep.noteapp.database;

public class NoteDbSchema {
    public static final class NoteTable{      //tạo bảng
        public static final String NAME = "notes";
        public static final class Cols{       //tạo các cột thuộc tính
            public static final String UUID = "uuid";      //nếu muốn truy cập đến cột title, có thể gọi một cách safely NoteDbChema.Cols.TITLE
            public static final String TITLE = "title";
            public static final String TAG = "tag";
            public static final String CONTENT = "content";
            public static final String DATETIME = "dateTime";
        }
    }
}
