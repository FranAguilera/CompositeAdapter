package com.victorious.compositeadapter.Person;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.victorious.compositeadapter.R;

public class PersonViewHolder extends RecyclerView.ViewHolder {

    private TextView firstNameView;
    private TextView lastNameView;

    public PersonViewHolder(View itemView) {
        super(itemView);
        firstNameView = (TextView) itemView.findViewById(R.id.first_name);
        lastNameView = (TextView) itemView.findViewById(R.id.last_name);
    }

    public void bindPerson(Person person) {
        firstNameView.setText(person.getFirst());
        lastNameView.setText(person.getLast());
    }
}
