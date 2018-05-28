package com.hamiti.florim.memorizegame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hamiti.florim.memorizegame.R;
import com.hamiti.florim.memorizegame.utils.DataModelForListView;

import java.util.ArrayList;

/**
 * Created by Florim on 5/27/2018.
 */

public class CustomAdapterListView extends ArrayAdapter<DataModelForListView> implements View.OnClickListener{

    private ArrayList<DataModelForListView> dataSet;
    Context context;

    // View lookup cache
    private static class ViewHolder {
        TextView txtDate;
        TextView txtScore;
    }

    public CustomAdapterListView(ArrayList<DataModelForListView> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.context =context;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModelForListView dataModel=(DataModelForListView)object;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataModelForListView dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date);
            viewHolder.txtScore = (TextView) convertView.findViewById(R.id.score);

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtDate.setText(dataModel.getDate());
        viewHolder.txtScore.setText(dataModel.getScore());
        // Return the completed view to render on screen
        return convertView;
    }
}