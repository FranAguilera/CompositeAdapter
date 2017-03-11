package com.victorious.compositeadapterdemo.TextMessage;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.victorious.compositeadapterdemo.R;

public class TextMessageViewHolder extends RecyclerView.ViewHolder {

    private TextView textMessageView;

    public TextMessageViewHolder(View itemView) {
        super(itemView);
        textMessageView = (TextView) itemView.findViewById(R.id.message);
    }

    public void bind(TextMessage textMessage) {
        textMessageView.setText(textMessage.getBody());
    }
}
