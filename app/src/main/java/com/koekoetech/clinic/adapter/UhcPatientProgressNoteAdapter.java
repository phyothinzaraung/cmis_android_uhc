package com.koekoetech.clinic.adapter;

import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.AppUtils;
import com.koekoetech.clinic.helper.PressNoteTypeHelper;
import com.koekoetech.clinic.model.UhcPatientProgressNote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.OrderedRealmCollection;

/**
 * Created by ZMN on 8/27/17.
 **/

class UhcPatientProgressNoteAdapter extends RealmRecyclerViewAdapter<UhcPatientProgressNote, UhcPatientProgressNoteAdapter.UhcPatientProgressNoteHolder> {

    private static final String TAG = UhcPatientProgressNoteAdapter.class.getSimpleName();


    UhcPatientProgressNoteAdapter(@Nullable OrderedRealmCollection<UhcPatientProgressNote> data) {
        super(data, true);
    }

    @NonNull
    @Override
    public UhcPatientProgressNoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medical_records_progress, parent, false);
        return new UhcPatientProgressNoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UhcPatientProgressNoteHolder holder, int position) {
        if (getData() != null) {
            holder.bindProgressNote(getData().get(position));
        }
    }

    static class UhcPatientProgressNoteHolder extends RecyclerView.ViewHolder {

        private TextView progressNoteContent;
        private TextView txt_progressnote_type;
        private View progressNoteIndicatorView;
        private LinearLayout layout_progress_note;

        private void allFindViewbyId(View itemView){
            progressNoteContent = itemView.findViewById(R.id.tv_progress_note_content);
            txt_progressnote_type = itemView.findViewById(R.id.txt_progressnote_type);
            progressNoteIndicatorView = itemView.findViewById(R.id.indicator_view);
            layout_progress_note = itemView.findViewById(R.id.layout_progress_note);
        }

        UhcPatientProgressNoteHolder(View itemView) {
            super(itemView);
            allFindViewbyId(itemView);
        }

        void bindProgressNote(UhcPatientProgressNote progressNote) {
            int indicatorColor = PressNoteTypeHelper.getTypeHelper().getTypeBackground(progressNote.getType());
            Log.i(TAG, "bindNote: Type " + progressNote.getType());
            Log.i(TAG, "bindNote: " + indicatorColor);

            if (!TextUtils.isEmpty(progressNote.getType())) {
                txt_progressnote_type.setText(progressNote.getType());
                txt_progressnote_type.setBackground(ContextCompat.getDrawable(itemView.getContext(), indicatorColor));
            }

            if (indicatorColor != 0) {
                progressNoteIndicatorView.setBackground(ContextCompat.getDrawable(itemView.getContext(), indicatorColor));
                String note = progressNote.getNote();
                Spannable spannable = AppUtils.getProgressNoteKeyValueSpannable(note);
//                Spannable spannable = new SpannableString(note);
//                int boldStart = 0;
//                int boldEnd;
//
//                char[] characters = note.toCharArray();
//                for (int i = 0; i < characters.length; i++) {
//                    if (characters[i] == ':') {
//                        boldEnd = i;
//                        spannable.setSpan(new StyleSpan(Typeface.BOLD), boldStart, boldEnd, 0);
//                    } else if (characters[i] == ';') {
//                        boldStart = i + 1;
//                    }
//                }
                if (TextUtils.isEmpty(progressNote.getFreeNote())) {
                    progressNoteContent.setText(spannable);
                } else {
                    progressNoteContent.setText(spannable + "\n\n" + progressNote.getFreeNote());
                }

                if (!note.isEmpty()) {
                    layout_progress_note.setVisibility(View.VISIBLE);
                } else {
                    layout_progress_note.setVisibility(View.GONE);
                }


//                String[] splitedNote = note.split("\\s+");
//
//                for (int i = 0; i < splitedNote.length; i++){
//                    if(splitedNote[i].contains("#")){
//                        SpannableString spannableString = new SpannableString(splitedNote[i]);
//                        if(progressNote.getType().equals("CC/Dx")){
//                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#d87272")), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        }else if(progressNote.getType().equals("INV/IMG")){
//                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#027F17")), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        }else if(progressNote.getType().equals("Care")){
//                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#98A605")), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        }else if(progressNote.getType().equals("Request")){
//                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0FA89E")), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        }else if(progressNote.getType().equals("Referral")){
//                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#DE16B2")), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        }else if(progressNote.getType().equals("Service")){
//                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#4964a0")), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        }else {
//                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#d87272")), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        }
//                        progressNoteContentTitle.setText(spannableString);
//                    }
//                    StringBuilder builder = new StringBuilder();
//                    for(int j = 1; j < splitedNote.length; j++){
//                        builder.append(splitedNote[j] + " ");
//                    }
//                    progressNoteContent.setText(" " + builder.toString());
//                }
            }
        }
    }
}
