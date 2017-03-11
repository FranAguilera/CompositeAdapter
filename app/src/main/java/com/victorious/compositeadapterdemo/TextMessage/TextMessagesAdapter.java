package com.victorious.compositeadapterdemo.TextMessage;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.victorious.compositeadapterdemo.R;

import java.util.List;

public class TextMessagesAdapter extends RecyclerView.Adapter<TextMessageViewHolder> {

    private static final int TEXT_MESSAGE = 1;

    private List<TextMessage> textMessages;

    public TextMessagesAdapter(List<TextMessage> textMessages) {
        this.textMessages = textMessages;
    }

    @Override
    public TextMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        View view = inflater.inflate(R.layout.message, parent, false);
        return new TextMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TextMessageViewHolder holder, int position) {
        if (holder.getItemViewType() == TEXT_MESSAGE) {
            TextMessage textMessage = textMessages.get(position);
            holder.bind(textMessage);
        }
    }

    @Override
    public int getItemCount() {
        return textMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TEXT_MESSAGE;
    }
}


