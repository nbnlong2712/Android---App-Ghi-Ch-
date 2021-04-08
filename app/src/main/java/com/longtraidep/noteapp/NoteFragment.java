package com.longtraidep.noteapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

//Khi NoteFragment gọi getActivity(), tức là đang gọi đến toàn bộ những thứ thuộc về NoteActivity như View, Data, Intent,...của nó và có thể sử dụng nó

public class NoteFragment extends Fragment {
    private Note mNote;
    private EditText mTitleNote;
    private EditText mTagNote;
    private EditText mContentNote;
    private TextView mDateTimeNote;
    private static final String ARG_NOTE_ID = "note_id";

    public static NoteFragment newInstance(UUID noteId)  //Hàm này dùng để đính kèm một đống dữ liệu cho một Fragment mà nó thuộc về, được
                                                        //lưu trữ và chuyển bằng biến Bundle (Bundle là gì thì cũng tự biết rồi), được
                                                        //dùng để mỗi lần NoteActivity cần gọi một NoteFagment cụ thể nào đó (có thể Note 1,
                                                        // Note 3,...) để hiển thị chi tiết hay làm gì khác thì sẽ gọi newInstance(1 hoặc 3,...), nhanh gọn
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, noteId);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Muốn gọi đến những gì thì cũng phải qua getActivity() hết
        UUID noteId = (UUID) getArguments().getSerializable(ARG_NOTE_ID);
        mNote = NoteLab.get(getActivity()).getNote(noteId);

        //Challenge
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        NoteLab.get(getActivity()).updateNote(mNote);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //biến thứ 2: container - view cha (activity_note)
        View v = inflater.inflate(R.layout.fragment_note, container,false); //biến thứ 3: có add layout fragment (fragment_note)
                                                                                        // vào view parent (activity_note) không : false (không)

        mTitleNote = (EditText) v.findViewById(R.id.note_title);
        mTitleNote.setText(mNote.getTitle());
        mTitleNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mTagNote = (EditText) v.findViewById(R.id.note_tag);
        mTagNote.setText(mNote.getTag());
        mTagNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setTag(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDateTimeNote = (TextView) v.findViewById(R.id.content_last_update);
        Date currentTime = Calendar.getInstance().getTime();
        if(mDateTimeNote == null)
            mNote.setDateTime(currentTime);
        else mDateTimeNote.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(mNote.getDateTime()));

        mContentNote = (EditText) v.findViewById(R.id.note_content);
        mContentNote.setText(mNote.getContent());
        mContentNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setContent(s.toString());

                mNote.setDateTime(currentTime);
                mDateTimeNote.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(mNote.getDateTime()));
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return v;
    }
    // Challenge
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_note:
                getActivity().finish();
                return NoteLab.get(getActivity()).deleteNote(mNote.getId());
            case R.id.align_left:
                mContentNote.setGravity(Gravity.START);
                return true;
            case R.id.align_center:
                mContentNote.setGravity(Gravity.CENTER_HORIZONTAL);
                return true;
            case R.id.align_right:
                mContentNote.setGravity(Gravity.END);
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }
}
