package com.zwir.myjournal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.zwir.myjournal.R;

public class BookMarkArrayAdapter extends ArrayAdapter<Integer> {

    private Integer[] images;
    private LayoutInflater inflater;

    public BookMarkArrayAdapter(Context context, Integer[] images) {
        super(context, R.layout.spinner_item, images);
        this.images = images;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        return getImageForPosition(position, convertView);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        return getImageForPosition(position, convertView);
    }

    private View getImageForPosition(int position, View rootView) {
        ImageView imageView = rootView.findViewById(R.id.ietm_spinner);
        imageView.setImageResource(images[position]);
        return rootView;
    }
}
