package com.rujara.health.redlife.adapter;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.classes.CardObject;

import java.util.ArrayList;

/**
 * Created by deep.patel on 9/23/15.
 */
public class RowAdapterCardWithIcon extends RecyclerView
        .Adapter<RowAdapterCardWithIcon.RequestObjectHolder> {
    private static MyClickListener myClickListener;
    private static MyMenuItenClickListener myMenuItenClickListener;
    private ArrayList<CardObject> mDataset;
    private boolean showContextMenu;
    public RowAdapterCardWithIcon(ArrayList<CardObject> myDataset, boolean showContextMenu) {
        mDataset = myDataset;
        this.showContextMenu = showContextMenu;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }
    public void setOnMenuItemClickListener(MyMenuItenClickListener myMenuItenClickListener) {
        this.myMenuItenClickListener = myMenuItenClickListener;
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
        holder.status.setImageDrawable(mDataset.get(position).getStatusIcon());
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
    public interface MyMenuItenClickListener {
        public boolean onItemClick(MenuItem item, View v, int position);
    }

    public class RequestObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener{
        TextView label;
        TextView label2;
        TextView label3;
        ImageView icon;
        ImageView status;
        public RequestObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            label2 = (TextView) itemView.findViewById(R.id.textView2);
            label3 = (TextView) itemView.findViewById(R.id.textView3);
            icon = (ImageView) itemView.findViewById(R.id.cardIcon);
            status = (ImageView) itemView.findViewById(R.id.cardStatusIcon);
            itemView.setOnClickListener(this);
            if(showContextMenu)
                itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, final View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            menu.add(0, 0, 0, "Rate and Complete");//groupId, itemId, order, title
            menu.add(0, 1, 0, "Complete");
            for (int i=0;i<menu.size();i++){
                menu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
//                        int menuindex = item.getItemId();
//                        switch (menuindex) {
//                            case 0:
//                                System.out.println("0");
//                                break;
//                            case 1:
//                                System.out.println("1");
//                                break;
//
//                            default:
//                                System.out.println("-1");
//                                break;
//                        }
//                        return false;
                        return myMenuItenClickListener.onItemClick(item, v, getAdapterPosition());
                    }

                });
            }
        }


        @Override
        public boolean onLongClick(View v) {

            return false;
        }
    }
}
