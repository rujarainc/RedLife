package com.rujara.health.redlife.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.classes.Donors;

import java.util.ArrayList;

/**
 * Created by deep.patel on 9/23/15.
 */
public class DonorListAdapter extends RecyclerView
        .Adapter<DonorListAdapter.DonorHolder> {
    private static MyClickListener myClickListener;
    private ArrayList<Donors> mDataset;

    public DonorListAdapter(ArrayList<Donors> myDataset) {
        mDataset = myDataset;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DonorHolder onCreateViewHolder(ViewGroup parent,
                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_vew_row, parent, false);

        DonorHolder dataObjectHolder = new DonorHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DonorHolder holder, int position) {
        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText(mDataset.get(position).getmText2());
    }

    public void addItem(Donors dataObj, int index) {
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

    public static class DonorHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;

        public DonorHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}
