package com.example.chat2021;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;


public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private MessageList mData;
    private LayoutInflater mInflater;

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolderMessage extends RecyclerView.ViewHolder{
        TextView content;
        TextView author;

        ViewHolderMessage(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.chatContent);
            author = itemView.findViewById(R.id.authorName);
        }

    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolderPersonalMessage extends RecyclerView.ViewHolder  {
        TextView content;

        ViewHolderPersonalMessage(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.chatContentMe);
        }
    }

    // data is passed into the constructor
    ChatRecyclerViewAdapter(Context context, MessageList data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        //si type 0 alors c'est un message de envoyé par nous
        if (viewType == 0) {
            view = mInflater.inflate(R.layout.rvchat_me_row, parent, false);
            return new ViewHolderPersonalMessage(view);
            // Sinon c'est un message envoyé par quelqu'un d'autre
        }

        view = mInflater.inflate(R.layout.rvchat_row, parent, false);
        return new ViewHolderMessage(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String content = mData.messages.get(position).content;

        switch (holder.getItemViewType()){
            case 0:
                ((ViewHolderPersonalMessage) holder).content.setText(content);
                break;
            case 1:
                String authorName = mData.messages.get(position).author;
                ((ViewHolderMessage) holder).content.setText(content);
                ((ViewHolderMessage) holder).author.setText(authorName);
                break;
        };
    }


    @Override
    public int getItemViewType(int position) {
        if(mData.messages.get(position).author.equals("tom")) //TODO : Changer en récuperant notre pseudo dans les sharedPrefs
            return 0;
        else
            return 1;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.messages.size();
    }


    public void addMessage(Message toAdd){
        mData.messages.add(toAdd);

    }
}