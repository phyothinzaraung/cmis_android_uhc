package com.koekoetech.clinic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.interfaces.PressNoteTextCallback;

import java.util.ArrayList;

/**
 * Created by Phyo Thinzar Aung on 10/16/2017.
 */

public class PressNoteSubjectWordAdapter extends RecyclerView.Adapter<PressNoteSubjectWordAdapter.SubjectHolder> {

    private PressNoteTextCallback pressNoteTextCallback;
    private ArrayList<String> wordList;

    public PressNoteSubjectWordAdapter() {
        this.wordList = new ArrayList<>();
    }

    public void registerCallback(PressNoteTextCallback callbackClass) {
        pressNoteTextCallback = callbackClass;
    }

    @NonNull
    @Override
    public PressNoteSubjectWordAdapter.SubjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_press_note_suggestion, parent, false);
        return new PressNoteSubjectWordAdapter.SubjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PressNoteSubjectWordAdapter.SubjectHolder holder, int position) {
        holder.bindSuggestion(wordList.get(position));
    }

    public void clear() {
        wordList.clear();
        notifyDataSetChanged();
    }

    public void addSubjects(ArrayList<String> newSubjects) {
        wordList.addAll(newSubjects);
        notifyItemRangeInserted(wordList.size() - newSubjects.size(), newSubjects.size());
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    class SubjectHolder extends RecyclerView.ViewHolder {

        private TextView tv_suggestion;
        private LinearLayout linear_press_note_suggestion;

        SubjectHolder(View itemView) {
            super(itemView);
            tv_suggestion = itemView.findViewById(R.id.tv_suggestion);
            linear_press_note_suggestion = itemView.findViewById(R.id.linear_press_note_suggestion);
        }

        void bindSuggestion(final String word) {

            tv_suggestion.setText(word);

            linear_press_note_suggestion.setOnClickListener(view -> pressNoteTextCallback.sendSuggestionText(word));

        }

    }
}
