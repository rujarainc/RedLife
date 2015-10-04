package com.rujara.health.redlife.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.classes.RequestObject;

import java.util.ArrayList;

/**
 * Created by deep.patel on 9/23/15.
 */
public class RowAdapterListWithIcon extends ArrayAdapter<RequestObject> {

    private final Context context;
    private final ArrayList<RequestObject> modelsArrayList;

    public RowAdapterListWithIcon(Context context, ArrayList<RequestObject> modelsArrayList) {

        super(context, R.layout.list_withicon_row, modelsArrayList);
        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;
        rowView = inflater.inflate(R.layout.list_withicon_row, parent, false);
        // 3. Get icon,title & counter views from the rowView
        ImageView imgView = (ImageView) rowView.findViewById(R.id.listIcon);
        TextView titleView = (TextView) rowView.findViewById(R.id.textView);
        TextView counterView = (TextView) rowView.findViewById(R.id.textView2);

        // 4. Set the text for textView
        imgView.setImageDrawable(modelsArrayList.get(position).getBloodGroupIcon());
        titleView.setText(modelsArrayList.get(position).getmText1());
        counterView.setText(modelsArrayList.get(position).getmText2());
        // 5. retrn rowView
        return rowView;
    }
}
