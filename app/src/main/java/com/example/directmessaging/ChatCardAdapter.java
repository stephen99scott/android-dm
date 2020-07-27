package com.example.directmessaging;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatCardAdapter extends RecyclerView.Adapter<ChatCardAdapter.ChatCardViewHolder> {
    private ArrayList<ChatCard> myChatCard;

    public static class ChatCardViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public TextView mesPrevText;
        public TextView date;

        public ChatCardViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.name_text);
            mesPrevText = itemView.findViewById(R.id.mes_prev_text);
            date = itemView.findViewById(R.id.date);
        }
    }

    public ChatCardAdapter(ArrayList<ChatCard> ChatCard) {
        myChatCard = ChatCard;
    }

    @NonNull
    @Override
    public ChatCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card, parent, false);
        ChatCardViewHolder ccvh = new ChatCardViewHolder(v);
        return ccvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatCardViewHolder holder, int position) {
        ChatCard current = myChatCard.get(position);
        holder.nameText.setText(current.getNameText());
        holder.mesPrevText.setText(current.getMesPrevText());
        holder.date.setText(current.getDate());
    }

    @Override
    public int getItemCount() {
        return myChatCard.size();
    }
}
