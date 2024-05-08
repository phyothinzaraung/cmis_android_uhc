package com.koekoetech.clinic.adapter;

import android.view.ViewGroup;

import com.koekoetech.clinic.adapter.viewholders.SuggestionHolder;
import com.koekoetech.clinic.interfaces.ManageSuggestionsCallback;
import com.koekoetech.clinic.model.SuggestionWordModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.realm.OrderedRealmCollection;

/**
 * Created by ZMN on 12/15/17.
 **/

public class ManageSuggestionsAdapter extends RealmRecyclerViewAdapter<SuggestionWordModel, SuggestionHolder> {

    private final ManageSuggestionsCallback manageSuggestionsCallback;

    public ManageSuggestionsAdapter(@Nullable OrderedRealmCollection<SuggestionWordModel> data, ManageSuggestionsCallback manageSuggestionsCallback) {
        super(data, true);
        this.manageSuggestionsCallback = manageSuggestionsCallback;
    }

    @NonNull
    @Override
    public SuggestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SuggestionHolder.create(parent, manageSuggestionsCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionHolder holder, int position) {
        @Nullable final SuggestionWordModel suggestionWordModel = getItem(position);
        if (suggestionWordModel != null) {
            holder.bind(suggestionWordModel);
        }
    }

}
