package com.project.sdl.tripplanner.HomePackage.AutoSuggestPackage;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.here.android.mpa.search.AutoSuggest;
import com.here.android.mpa.search.AutoSuggestPlace;
import com.here.android.mpa.search.AutoSuggestQuery;
import com.project.sdl.tripplanner.R;

import java.util.List;

public class AutoSuggestAdapter extends ArrayAdapter<AutoSuggest> {
    private List<AutoSuggest> m_resultsList;

    public AutoSuggestAdapter(Context context, int resource, List<AutoSuggest> objects) {
        super(context, resource, objects);
        m_resultsList = objects;
    }

    @Override
    public int getCount() {
        return m_resultsList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AutoSuggest autoSuggest = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_autosuggest_list_item,
                    parent, false);
        }

        TextView tv = null;
        tv = convertView.findViewById(R.id.header);
        tv.setBackgroundColor(Color.WHITE);

        // set highlightedTitle
        tv = convertView.findViewById(R.id.highlightedTitle);
        tv.setText(Html.fromHtml(autoSuggest.getHighlightedTitle()));

        switch (autoSuggest.getType()) {
            case PLACE:
                AutoSuggestPlace autoSuggestPlace = (AutoSuggestPlace) autoSuggest;
                // set vicinity
                tv = convertView.findViewById(R.id.vicinity);
                tv.setVisibility(View.VISIBLE);
                if (autoSuggestPlace.getVicinity() != null) {
                    String[] vicinity = autoSuggestPlace.getVicinity().split("<br/>");
                    String updatedVicinity = "";
                    for(int i=0;i<vicinity.length;i++){

                        updatedVicinity+=autoSuggestPlace.getVicinity().split("<br/>")[i];
                        if(vicinity.length > 1 && i != vicinity.length-1) {
                            updatedVicinity += ",";
                        }
                    }

                    tv.setText(updatedVicinity);


                } else {
                    tv.setText("");
                }

                break;
            case QUERY:
                AutoSuggestQuery autoSuggestQuery = (AutoSuggestQuery) autoSuggest;
                // set completion
                tv = convertView.findViewById(R.id.vicinity);
                tv.setText(autoSuggestQuery.getQueryCompletion());

                break;
            case SEARCH:
                // set vicinity
                tv = convertView.findViewById(R.id.vicinity);
                tv.setVisibility(View.GONE);

                break;
            default:
        }
        return convertView;
    }

    @Override
    public AutoSuggest getItem(int position) {
        return m_resultsList.get(position);
    }
}
