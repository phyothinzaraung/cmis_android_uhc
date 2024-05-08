package com.koekoetech.clinic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.Pageable;
import com.koekoetech.clinic.interfaces.PressNoteCallback;
import com.koekoetech.clinic.model.PressNoteModel;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PressNoteAdapter extends BaseRecyclerViewAdapter {

    private PressNoteCallback pressNoteCallback;
    private int lastSelectedPressNote = -1;

    public void registerCallback(PressNoteCallback callbackClass) {
        pressNoteCallback = callbackClass;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_press_note, parent, false);
        return new PressNoteAdapter.PressNoteHolder(view);
    }

    @Override
    protected void onBindCustomViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PressNoteAdapter.PressNoteHolder) holder).bindPost((PressNoteModel) getItemsList().get(position));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateCustomHeaderViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected void onBindCustomHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    public void select(String pressNoteType) {

        List<Pageable> itemsList = getItemsList();

        for (int i = 0; i < itemsList.size(); i++) {
            Pageable item = itemsList.get(i);
            if (item instanceof PressNoteModel) {
                PressNoteModel pnm = (PressNoteModel) item;
                if (pnm.getTitle().equals(pressNoteType)) {
                    lastSelectedPressNote = i;
                    notifyItemChanged(lastSelectedPressNote);
                }
            }
        }
    }

    public void resetSelection() {
        lastSelectedPressNote = -1;
    }

    class PressNoteHolder extends RecyclerView.ViewHolder {


        private final LinearLayout linear_press_note;
        private final TextView tv_title;
        private final Context mContext;

        PressNoteHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            linear_press_note = itemView.findViewById(R.id.linear_press_note);
            tv_title = itemView.findViewById(R.id.tv_title);
        }


        void bindPost(final PressNoteModel model) {

            linear_press_note.setBackgroundResource(model.getBackground());
            tv_title.setText(model.getTitle());

            manageSelection(lastSelectedPressNote == getAdapterPosition(), model);

            linear_press_note.setOnClickListener(v -> {
                int lastSelectedPos = lastSelectedPressNote;
                lastSelectedPressNote = getAdapterPosition();
                notifyItemChanged(lastSelectedPos);
                notifyItemChanged(lastSelectedPressNote);
                pressNoteCallback.onPressNoteSelected(model.getTitle());
//                    tv_title.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.mark, 0, 0, 0);
            });
        }

        private void manageSelection(boolean isSelected, PressNoteModel pressNoteModel) {
            if (isSelected) {
                linear_press_note.setBackgroundResource(pressNoteModel.getBackground());
                tv_title.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
            } else {
                linear_press_note.setBackgroundResource(R.drawable.textviewborder);
                Drawable backgroundDrawable = linear_press_note.getBackground();
                if (backgroundDrawable instanceof GradientDrawable) {
                    GradientDrawable myGrad = (GradientDrawable) backgroundDrawable;
                    myGrad.setStroke(3, Color.parseColor(pressNoteModel.getPreFixTextColor()));
                }
                tv_title.setTextColor(Color.parseColor(pressNoteModel.getPreFixTextColor()));
            }
        }


    }

}