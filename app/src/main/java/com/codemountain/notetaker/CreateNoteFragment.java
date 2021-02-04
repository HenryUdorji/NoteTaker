package com.codemountain.notetaker;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codemountain.notetaker.database.NoteRepository;
import com.codemountain.notetaker.databinding.BottomSheetLayoutBinding;
import com.codemountain.notetaker.databinding.FragmentCreateNoteBinding;
import com.codemountain.notetaker.entity.Note;
import com.codemountain.notetaker.util.Constants;
import com.codemountain.notetaker.util.NoteUtil;
import com.codemountain.notetaker.util.RecorderImpl;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.observers.DisposableCompletableObserver;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


public class CreateNoteFragment extends Fragment
        implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    private static final String TAG = "CreateNoteFragment";
    private FragmentCreateNoteBinding binding;
    private final int PICK_IMAGE_CODE = 101;
    private String imagePath;
    private String audioPath;
    private Bitmap bitmapGlobal;
    //Bundle data
    private int bundleId;
    private String bundleImagePath;
    private String bundleAudioPath;
    private String bundleContent;
    private String bundleDate;
    private String bundleTitle;
    private boolean bundleUpdate;
    private RecorderImpl recorder;

    public CreateNoteFragment() {
        // Required empty public constructor
    }


    public static CreateNoteFragment newInstance() {
        CreateNoteFragment fragment = new CreateNoteFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState = getArguments();
        if (savedInstanceState != null) {
            bundleId = savedInstanceState.getInt(Constants.ID);
            bundleAudioPath = savedInstanceState.getString(Constants.AUDIO_PATH);
            bundleImagePath = savedInstanceState.getString(Constants.IMAGE_PATH);
            bundleContent = savedInstanceState.getString(Constants.CONTENT);
            bundleTitle = savedInstanceState.getString(Constants.TITLE);
            bundleDate = savedInstanceState.getString(Constants.DATE);
            bundleUpdate = savedInstanceState.getBoolean(Constants.UPDATE);
        }

        recorder = new RecorderImpl(getActivity());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateNoteBinding.inflate(inflater, container, false);

        init();
        return binding.getRoot();
    }

    private void init() {
        if (!bundleUpdate) {
            binding.dateTv.setText(NoteUtil.getInstance().generateTimeStamp());
        }else {
            getNote(bundleContent, bundleTitle, bundleDate, bundleAudioPath, bundleImagePath);
        }


        binding.done.setOnClickListener(v -> {
            String timeStamp = binding.dateTv.getText().toString();
            String title = binding.title.getText().toString();
            String body = binding.body.getText().toString();

            if (getActivity() != null && bitmapGlobal != null) {
                imagePath = NoteUtil.getInstance().saveImageToFile(getActivity(), bitmapGlobal);

                if (bundleUpdate) {
                    //TODO -> work on deleting files
                   /* String deletePath = bundleImagePath;
                    NoteUtil.getInstance().deleteFileFromPath(deletePath);*/

                    bundleImagePath = NoteUtil.getInstance().saveImageToFile(getActivity(), bitmapGlobal);
                }
            }

            if (body.equals("")) {
                Toast.makeText(getActivity(), "Note body cannot be empty", Toast.LENGTH_LONG).show();
            }else if (!bundleUpdate){
                saveNote(timeStamp, title, body, imagePath, audioPath);
            }else {
                updateNote(bundleId, timeStamp, title, body, bundleImagePath, bundleAudioPath);
            }
        });

        binding.back.setOnClickListener(v -> {
            //load fragment
            HomeFragment newInstance = HomeFragment.newInstance();
            FragmentTransaction beginTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            beginTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            beginTransaction.replace(R.id.container, newInstance);
            beginTransaction.commit() ;
        });

        binding.pickImage.setOnClickListener(v -> {
            readStorageTask();
        });

        binding.recordAudio.setOnClickListener(v -> {
            recordAudioTask();
        });

        //play record layout
        ImageView playBtn = binding.playBtn;
        ImageView pauseBtn = binding.pauseBtn;
        //Chronometer counter = binding.counter;
        WaveVisualizer audioPlayerWaveVisualizer = binding.audioPlayerWaveVisualizer;

        binding.closeBtn.setOnClickListener(v -> {
            binding.constraintLayout.setVisibility(View.GONE);
        });
        playBtn.setOnClickListener(v -> {
            playBtn.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.VISIBLE);
            recorder.onPlay(true);

            //TODO -> work on the countdown for playing audio
            /*counter.setBase(recorder.getDuration());
            counter.isCountDown();*/
            audioPlayerWaveVisualizer.setAudioSessionId(recorder.getAudioSessionId());
        });
        pauseBtn.setOnClickListener(v -> {
            playBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.GONE);

            recorder.onPause();
        });

        int randomNoteColor = NoteUtil.getInstance().getRandomNoteColor(requireContext());
        binding.colorView.setBackgroundColor(randomNoteColor);
    }


    private void updateNote(int bundleId, String timeStamp, String title, String body,
                            String bundleImagePath, String bundleAudioPath) {
        Note note = new Note(title, body, timeStamp, bundleImagePath, bundleAudioPath);
        note.setId(bundleId);

        NoteRepository noteRepository = new NoteRepository(getActivity());
        Completable updateNote = noteRepository.updateNote(note);

        updateNote.subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {

                if (getActivity() != null) {
                    HomeFragment newInstance = HomeFragment.newInstance();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, newInstance);
                    transaction.commit();
                    Toast.makeText(getActivity(), "Note Updated successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getNote(String bundleContent, String bundleTitle, String bundleDate,
                         String bundleAudioPath, String bundleImagePath) {

        binding.body.setText(bundleContent);
        binding.title.setText(bundleTitle);
        binding.dateTv.setText(bundleDate);

        if (bundleImagePath != null) {
            binding.noteImage.setImageBitmap(BitmapFactory.decodeFile(bundleImagePath));
            binding.noteImage.setVisibility(View.VISIBLE);
        }else {
            binding.noteImage.setVisibility(View.GONE);
        }

        if (bundleAudioPath != null) {
            binding.constraintLayout.setVisibility(View.VISIBLE);
        }else {
            binding.constraintLayout.setVisibility(View.GONE);
        }
    }


    private void saveNote(String timeStamp, String title, String body, String imagePath, String audioPath) {
        Note note = new Note(title, body, timeStamp, imagePath, audioPath);
        NoteRepository noteRepository = new NoteRepository(getActivity());
        Completable insert = noteRepository.insertNote(note);

        insert.subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {

                if (getActivity() != null) {
                    HomeFragment newInstance = HomeFragment.newInstance();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, newInstance);
                    transaction.commit();
                    Toast.makeText(getActivity(), "Note Saved", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean hasReadStoragePermission() {
        if (getActivity() != null) {
            return EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return false;
    }

    private void readStorageTask() {
        if (hasReadStoragePermission()) {
            pickImageFromGallery();
        }else {
            if (getActivity() == null) {
                return;
            }
            int READ_STORAGE_CODE = 100;
            EasyPermissions.requestPermissions(
                    getActivity(),
                    getString(R.string.storage_permission_text),
                    READ_STORAGE_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            );
        }
    }

    public boolean hasRecordAudioPermission() {
        if (getActivity() != null) {
            return EasyPermissions.hasPermissions(getActivity(), Manifest.permission.RECORD_AUDIO);
        }
        return false;
    }

    private void recordAudioTask() {
        if (hasRecordAudioPermission()) {
            showRecordDialog();
        }else {
            if (getActivity() == null) {
                return;
            }
            int RECORD_AUDIO_CODE = 102;
            EasyPermissions.requestPermissions(
                    getActivity(),
                    getString(R.string.record_audio_permission),
                    RECORD_AUDIO_CODE,
                    Manifest.permission.RECORD_AUDIO
            );
        }
    }

    private void showRecordDialog() {
        if (getActivity() != null) {
            BottomSheetLayoutBinding binding;
            final BottomSheetDialog recordDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
            binding = BottomSheetLayoutBinding.inflate(getLayoutInflater());

            WaveVisualizer audioPlayerWaveVisualizer = binding.audioPlayerWaveVisualizer;
            Chronometer counter = binding.counter;
            FloatingActionButton pauseBtn = binding.pauseBtn;
            FloatingActionButton recordBtn = binding.recordBtn;
            ImageView done = binding.done;

            binding.cancel.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.cancel_recording_message)
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setPositiveButton("Yes", (dialog, which) -> {
                            recordDialog.dismiss();
                            recorder.stopRecording();
                            dialog.dismiss();
                        });
                builder.show();

            });
            done.setOnClickListener(v -> {
                audioPath = recorder.getFilePath();
                recorder.stopRecording();
                recordDialog.dismiss();
                counter.stop();
                this.binding.constraintLayout.setVisibility(View.VISIBLE);
            });
            //TODO -> Work on pause mechanism
            pauseBtn.setOnClickListener(v -> {
                //recordBtn.setVisibility(View.VISIBLE);
                //pauseBtn.setVisibility(View.GONE);

            });
            recordBtn.setOnClickListener(v -> {
                //recordBtn.setVisibility(View.GONE);
                //pauseBtn.setVisibility(View.VISIBLE);

                recorder.onRecord(true);
                counter.start();
                binding.recordingText.setVisibility(View.VISIBLE);
                //audioPlayerWaveVisualizer.;
            });


            recordDialog.setContentView(binding.getRoot());
            recordDialog.show();
        }
    }

    private void pickImageFromGallery() {
        if (getActivity() != null) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, PICK_IMAGE_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null && getActivity() != null) {
                    try {
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.noteImage.setImageBitmap(bitmap);
                        binding.noteImage.setVisibility(View.VISIBLE);
                        bitmapGlobal = bitmap;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) { }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (getActivity() ==  null) {
            return;
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(getActivity(), perms)) {
            AppSettingsDialog dialog = new AppSettingsDialog.Builder(getActivity()).build();
            dialog.show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        recorder.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}