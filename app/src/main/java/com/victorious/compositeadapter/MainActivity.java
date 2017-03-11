package com.victorious.compositeadapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.victorious.compositeadapter.Person.PeopleAdapter;
import com.victorious.compositeadapter.Person.Person;
import com.victorious.compositeadapter.TextMessage.TextMessage;
import com.victorious.compositeadapter.TextMessage.TextMessagesAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LIST_SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PeopleAdapter peopleAdapter = new PeopleAdapter(generatePeopleList());
        TextMessagesAdapter textAdapter = new TextMessagesAdapter(generateTextMessages());

//        CompositeAdapter<RecyclerView.Adapter> compositeAdapter = new CompositeAdapter<>();
//        compositeAdapter.addAdapter(peopleAdapter);
//        compositeAdapter.addAdapter(textAdapter);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.composite_list);
        recyclerView.setAdapter(peopleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<Person> generatePeopleList() {
        List<Person> people = new ArrayList<>();

        for (int i=0, size=LIST_SIZE; i<size; i++) {
            Person person = new Person("Bob", "Loblaw #" + i);
            people.add(person);
        }

        return people;
    }

    private List<TextMessage> generateTextMessages() {
        List<TextMessage> textMessages = new ArrayList<>();

        for (int i=0, size=LIST_SIZE; i<size; i++) {
            TextMessage textMessage = new TextMessage("#" + i
                    + " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam porta tempus erat, quis dictum sem mollis eget. Vivamus vel felis ac est tristique molestie.");
            textMessages.add(textMessage);
        }

        return textMessages;
    }
}
