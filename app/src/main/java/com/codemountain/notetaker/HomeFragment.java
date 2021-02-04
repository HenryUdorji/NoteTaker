package com.codemountain.notetaker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codemountain.notetaker.adapter.HomeFragmentAdapter;
import com.codemountain.notetaker.database.NoteRepository;
import com.codemountain.notetaker.databinding.FragmentCreateNoteBinding;
import com.codemountain.notetaker.databinding.FragmentHomeBinding;
import com.codemountain.notetaker.entity.Note;
import com.codemountain.notetaker.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private HomeFragmentAdapter adapter;
    RecyclerView recyclerView;
    TextView noNote;

    public HomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        init();
        showNotes();
        return binding.getRoot();
    }


    private void init() {
        noNote = binding.noNote;

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(layoutManager);

        binding.createNoteFab.setOnClickListener(v -> {
            //load fragment
            CreateNoteFragment newInstance = CreateNoteFragment.newInstance();
            FragmentTransaction beginTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            beginTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            beginTransaction.replace(R.id.container, newInstance);
            beginTransaction.commit();
        });

        binding.clearAll.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.delete_all_notes);
            builder.setMessage(R.string.delete_all_notes_message);
            builder.setPositiveButton("Delete", (dialog, which) -> {
                deleteAllNotes();
                dialog.dismiss();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
               dialog.dismiss();
            });
            builder.show();
        });
    }


    private void showNotes() {
        if (getActivity() == null) {
            return;
        }
        NoteRepository noteRepository = new NoteRepository(getActivity());
        Observable<List<Note>> allNotes = noteRepository.getAllNotes();

        allNotes.subscribe(new DisposableObserver<List<Note>>() {
            @Override
            public void onNext(List<Note> notes) {
                if (notes.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    noNote.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noNote.setVisibility(View.GONE);

                    adapter = new HomeFragmentAdapter(getActivity(), notes);
                    adapter.setOnItemClickListener((position, view) -> {
                        CreateNoteFragment newInstance = CreateNoteFragment.newInstance();
                        Bundle bundle = saveBundle(position);
                        newInstance.setArguments(bundle);
                        FragmentTransaction beginTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        beginTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        beginTransaction.replace(R.id.container, newInstance);
                        beginTransaction.commit();
                    });
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //Callback handles swipe to left and right of the screen to delete single note item
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note note = adapter.getNoteAt(viewHolder.getAdapterPosition());
                deleteNote(note);
            }
        }).attachToRecyclerView(recyclerView);

    }

    private Bundle saveBundle(int position) {
        Bundle bundle = new Bundle();
        int id = adapter.getNoteAt(position).getId();
        String audioPath = adapter.getNoteAt(position).getAudioPath();
        String imagePath = adapter.getNoteAt(position).getImagePath();
        String title = adapter.getNoteAt(position).getTitle();
        String content = adapter.getNoteAt(position).getContent();
        String date = adapter.getNoteAt(position).getDate();

        bundle.putInt(Constants.ID, id);
        bundle.putString(Constants.AUDIO_PATH, audioPath);
        bundle.putString(Constants.IMAGE_PATH, imagePath);
        bundle.putString(Constants.CONTENT, content);
        bundle.putString(Constants.TITLE, title);
        bundle.putString(Constants.DATE, date);
        bundle.putBoolean(Constants.UPDATE, true);

        return bundle;
    }

    private void deleteNote(Note note) {
        if (getActivity() == null) {
            return;
        }
        NoteRepository noteRepository = new NoteRepository(getActivity());
        Completable deleteNote = noteRepository.deleteNote(note);

        deleteNote.subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                Toast.makeText(getActivity(), "Note deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }

    private void deleteAllNotes() {
        if (getActivity() == null) {
            return;
        }
        NoteRepository noteRepository = new NoteRepository(getActivity());
        Completable deleteAllNote = noteRepository.deleteAllNote();

        deleteAllNote.subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                Toast.makeText(getActivity(), "All notes deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    //TODO -> when i delete an item the corresponding file should also be deleted


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}