package com.longtraidep.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.UUID;

public class NoteActivity extends SingleFragmentActivity {
    private static final String EXTRA_NOTE_ID = "com.longtraidep.noteapp.note_id";

    // Hàm này dùng để chỉ định Note nào sẽ được hiển thị, bằng cách truyền ID của Note đó đi bằng
    // Intent khi NoteActivity được người dùng bắt đầu bằng cách nhấn chọn 1 Ghi chú bất kỳ trên màn hình
    // (Tức là khi người dùng nhấn chọn 1 ghi chú bất kỳ, NoteActivity vẫn chưa biết đó là Note nào, nên
    // nó sẽ hiển thị một màn hình Ghi chú chi tiết không biết của ai, giờ dùng hàm này để định danh Note đó)
    public static Intent newIntent(Context packageContext, UUID noteId)
    {
        Intent intent = new Intent(packageContext, NoteActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID noteId = (UUID) getIntent().getSerializableExtra(EXTRA_NOTE_ID);
        return NoteFragment.newInstance(noteId);
    }
}