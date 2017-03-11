package com.victorious.compositeadapterdemo.Person;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.victorious.compositeadapterdemo.R;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PersonViewHolder> {

    private static final int PERSON = 0;

    private List<Person> people;

    public PeopleAdapter(List<Person> people) {
        this.people = people;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        View view = inflater.inflate(R.layout.person, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        if (holder.getItemViewType() == PERSON) {
            Person person = people.get(position);
            holder.bindPerson(person);
        }
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    @Override
    public int getItemViewType(int position) {
        return PERSON;
    }
}
