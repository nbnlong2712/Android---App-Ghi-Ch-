package com.longtraidep.noteapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class NoteListFragment extends Fragment {
    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_TWO = 2;
    private RecyclerView mNoteRecyclerView;
    private NoteAdapter mAdapter;
    private GridLayoutManager gridLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        mNoteRecyclerView = (RecyclerView) view.findViewById(R.id.note_recycler_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    //- NoteActivity chứa NoteFragment (NoteFragment là nơi chứa thông tin chi tiết ghi chú)
    //- NoteListActivity chứa NoteListFragment (NoteListFragment là nơi chứa list các NoteFragment, tức là màn hình hiển thị các Ghi chú)
    //- Khi chạy chương trình, NoteListActivity sẽ được gọi, màn hình sẽ hiển thị list các ghi chú cho chúng ta chọn, khi chúng ta chọn
    //  1 ghi chú bất kỳ, instance của NoteActivity sẽ được hiển thị, khi đó NoteActivity sẽ được đưa lên đầu, NoteListActivity sẽ được
    //  Pause, sau khi xem chi tiết ghi chú xong, chúng ta trở lại màn hình danh sách ghi chú, khi đó NoteActivity vừa xem sẽ bị hủy,
    //  NoteListActivity sẽ được tiếp tục chạy, nó sẽ nhận được một call onResume() từ hệ điều hành, nếu chúng ta có thay đổi trong chi
    //  tiết ghi chú vừa xem, những sự thay đổi đó sẽ không được cập nhật, để cập nhật và lưu trữ sự thay đổi đó, chúng ta gọi hàm cập
    //  nhật updateUI() trong onResume() để khi NoteListActivity vừa nhận được lệnh tiếp tục từ hệ điều hành, chúng ta sẽ cập nhật và
    //  lưu trữ dữ liệu mới, sau đó hiển thị trên màn hình.
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI()
    {
        gridLayoutManager = new GridLayoutManager(this.getActivity(), SPAN_COUNT_ONE);
        NoteLab noteLab = NoteLab.get(getActivity());
        List<Note> notes = noteLab.getNotes();
        if(mAdapter == null) {
            mAdapter = new NoteAdapter(notes);
            mNoteRecyclerView.setAdapter(mAdapter);  //Hàm này được RecyclerView dùng để bind dữ liệu từ Adapter lên các Item
        }
        else{
            mAdapter.setNotes(notes);
            mAdapter.notifyDataSetChanged();
        }
        mNoteRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note_list, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

/////////////////////////////////////////---GRID VIEW---/////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add_note:
                Note note = new Note();
                NoteLab.get(getActivity()).addNote(note);
                Intent intent = NoteActivity.newIntent(getActivity(), note.getId());
                startActivity(intent);
                return true;
            case R.id.switch_layout:
                switchLayout();
                switchIcon(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void switchLayout() {
        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_ONE) {
            gridLayoutManager.setSpanCount(SPAN_COUNT_TWO);
        } else {
            gridLayoutManager.setSpanCount(SPAN_COUNT_ONE);
        }
        NoteLab noteLab = NoteLab.get(getActivity());
        List<Note> notes = noteLab.getNotes();
        mAdapter = new NoteAdapter(notes);
        mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
    }

    private void switchIcon(MenuItem item) {
        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_TWO) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_baseline_menu_24));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_baseline_grid_view_24));
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////
    //-------------Khai bao ViewHolder--------------
    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mTitleTextView;
        private TextView mTagTextView;
        private TextView mDateTimeTextView;
        private Note mNote;

        public NoteHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.list_item_note, parent, false)); // (1)
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.note_title);      // note_title này là của list_item_note, vì dòng (1) ta inflate nó
            mTagTextView = (TextView) itemView.findViewById(R.id.note_tag);                        // này cũng vậy
            mDateTimeTextView = (TextView) itemView.findViewById(R.id.note_datetime);

        }

        public void bind(Note note){                             //Hàm kết nối dữ liệu, set dữ liệu cho một object Note
                mNote = note;
                mTitleTextView.setText(mNote.getTitle());
                mTagTextView.setText(mNote.getTag());
                mDateTimeTextView.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(mNote.getDateTime()));
        }

        @Override
        public void onClick(View v) {
            Intent intent = NoteActivity.newIntent(getActivity(), mNote.getId()); //khi user kích vào 1 Ghi chú nào, thì Ghi chú đó sẽ được hiển
                                                                                   // thị đúng theo ID của Note đó
            startActivity(intent);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //-------------Khai bao Adapter-------------
    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> implements Filterable//Adapter chứa các Noteholder, để bind view trong Noteholder
                                                                       //(tức item) và dữ liệu (data) trong Adapter
    {
        private List<Note> mNotes; //dữ liệu (data) sẽ được lấy từ các thuộc tính của Note
        private List<Note> mNotesOld;

        public NoteAdapter(List<Note> notes)
        {
            mNotes = notes;
            mNotesOld = new ArrayList<>(notes);
        }

        @NonNull
        @Override
        //Khi RecyclerView cần 1 ViewHolder để hiển thị 1 item, nó sẽ gọi hàm này
        public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new NoteHolder(layoutInflater, parent);
        }

        @Override

        public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
            Note note = mNotes.get(position);     //lấy ra note ở vị trí position
            holder.bind(note);                  //truyền dữ liệu của note vào holder
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }

        public void setNotes(List<Note> notes)
        {
            mNotes = notes;
        }

        @Override
        public Filter getFilter() {
            return exampleFilter;
        }

        private Filter exampleFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<Note> filterNote = new ArrayList<>();
                if (constraint == null || constraint.length() == 0){

                    filterNote.addAll(mNotesOld);
                }
                else
                {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Note note : mNotesOld)
                    {
                        if ((note.getContent().toLowerCase().contains(filterPattern)) || (note.getTitle().toLowerCase().contains(filterPattern)) || (note.getTag().toLowerCase().contains(filterPattern)))
                        {
                            filterNote.add(note);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filterNote;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mNotes.clear();
                mNotes.addAll((List) results.values);

                notifyDataSetChanged();
            }
        };
    }
}
