package com.victorious.compositeadapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.victorious.compositeadapter.Person.Person;
import com.victorious.compositeadapter.Person.PersonViewHolder;
import com.victorious.compositeadapter.TextMessage.TextMessage;
import com.victorious.compositeadapter.TextMessage.TextMessageViewHolder;

import java.util.List;

public class HeterogenousAdapter extends RecyclerView.Adapter {

    private static final int PERSON = 0;
    private static final int TEXT_MESSAGE = 1;

    private List<Object> items;

    public HeterogenousAdapter(List<Object> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        switch (viewType) {
            case PERSON:
                View personView = inflater.inflate(R.layout.person, parent, false);
                return new PersonViewHolder(personView);
            case TEXT_MESSAGE:
            default:
                View textMessageView = inflater.inflate(R.layout.message, parent, false);
                return new TextMessageViewHolder(textMessageView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case PERSON:
                Person person = (Person) items.get(position);
                ((PersonViewHolder)holder).bindPerson(person);
                break;
            case TEXT_MESSAGE:
                TextMessage textMessage = (TextMessage) items.get(position);
                ((TextMessageViewHolder)holder).bind(textMessage);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Person) {
            return PERSON;
        } else {
            return TEXT_MESSAGE;
        }
    }
}