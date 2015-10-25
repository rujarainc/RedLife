package com.rujara.health.redlife.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.classes.CardObject;

import java.util.ArrayList;

/**
 * Created by deep.patel on 9/23/15.
 */
public class RowAdapterCardWithIcon extends RecyclerView
        .Adapter<RowAdapterCardWithIcon.RequestObjectHolder> {
    private static MyClickListener myClickListener;
    private ArrayList<CardObject> mDataset;

    public RowAdapterCardWithIcon(ArrayList<CardObject> myDataset) {
        mDataset = myDataset;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public RequestObjectHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row_icon, parent, false);

        RequestObjectHolder requestObjectHolder = new RequestObjectHolder(view);
        return requestObjectHolder;
    }

    @Override
    public void onBindViewHolder(RequestObjectHolder holder, int position) {
        holder.label.setText(mDataset.get(position).getmText1());
        holder.label2.setText(mDataset.get(position).getmText2());
        holder.label3.setText(mDataset.get(position).getmText3());
        holder.icon.setImageDrawable(mDataset.get(position).getCardIcon());
    }

    public void addItem(CardObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static class RequestObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView label2;
        TextView label3;
        ImageView icon;

        public RequestObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            label2 = (TextView) itemView.findViewById(R.id.textView2);
            label3 = (TextView) itemView.findViewById(R.id.textView3);
            icon = (ImageView) itemView.findViewById(R.id.cardIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}
