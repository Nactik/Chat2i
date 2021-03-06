package com.example.chat2021;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class ConvRecyclerViewAdapter extends RecyclerView.Adapter<ConvRecyclerViewAdapter.ViewHolder> {

    private ListConversation mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    ConvRecyclerViewAdapter(Context context, ListConversation data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rvconv_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String convName = mData.conversations.get(position).theme;

        holder.myTextView.setText(convName);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.conversations.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView delConvImageView;

        ViewHolder(View itemView) {
            super(itemView);

            myTextView = itemView.findViewById(R.id.convName);
            this.delConvImageView = itemView.findViewById(R.id.convDel);

            this.delConvImageView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void addConv(Conversation toAdd){
        mData.conversations.add(toAdd);
    }

    // convenience method for getting data at click position
    String getId(int position) {
        return mData.conversations.get(position).id;
    }

    String getTheme(int position){
        return mData.conversations.get(position).theme;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}