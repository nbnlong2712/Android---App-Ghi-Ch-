package com.longtraidep.noteapp;

import androidx.fragment.app.Fragment;

public class NoteListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new NoteListFragment();
    }
}
