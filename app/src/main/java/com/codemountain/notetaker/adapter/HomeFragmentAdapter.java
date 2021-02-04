package com.codemountain.notetaker.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codemountain.notetaker.R;
import com.codemountain.notetaker.databinding.NoteLayoutBinding;
import com.codemountain.notetaker.entity.Note;
import com.codemountain.notetaker.interfaces.OnItemClickListener;
import com.codemountain.notetaker.util.NoteUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

//
// Created by  on 1/20/2021.
//
public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder> {

    private static final String TAG = "HomeFragmentAdapter";
    private Context context;
    private List<Note> notes;
    private OnItemClickListener listener;

    public HomeFragmentAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteLayoutBinding binding = NoteLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.bodyText.setText(note.getContent());
        holder.timeText.setText(note.getDate());
        if (note.getTitle() == null) {
            holder.titleText.setVisibility(View.GONE);
        }else {
            holder.titleText.setVisibility(View.VISIBLE);
            holder.titleText.setText(note.getTitle());
        }
        if (note.getImagePath() != null) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
        }else {
            holder.imageView.setVisibility(View.GONE);
        }
        if (note.getAudioPath() != null) {
            holder.recordImage.setVisibility(View.VISIBLE);
        }else {
            holder.recordImage.setVisibility(View.GONE);
        }

        int randomColor = NoteUtil.getInstance().getRandomNoteColor(context);
        holder.cardView.setCardBackgroundColor(randomColor);
    }

    public Note getNoteAt(int position) {
       return notes.get(position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView bodyText, timeText, titleText;
        private CardView cardView;
        private RoundedImageView imageView;
        private ImageView recordImage;

        public ViewHolder(@NonNull NoteLayoutBinding binding) {
            super(binding.getRoot());
            cardView = binding.cardContainer;
            imageView = binding.noteImage;
            bodyText = binding.bodyText;
            timeText = binding.dateTv;
            titleText = binding.titleText;
            recordImage = binding.recordOn;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, v);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
