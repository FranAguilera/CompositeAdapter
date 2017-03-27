/*
 * Copyright 2017 Victorious,Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.victorious.compositeadapterdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.victorious.compositeadapter.CompositeAdapter;
import com.victorious.compositeadapterdemo.Person.PeopleAdapter;
import com.victorious.compositeadapterdemo.Person.Person;
import com.victorious.compositeadapterdemo.TextMessage.TextMessage;
import com.victorious.compositeadapterdemo.TextMessage.TextMessagesAdapter;

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

        CompositeAdapter<RecyclerView.Adapter> compositeAdapter = new CompositeAdapter<>();
        compositeAdapter.addAdapter(peopleAdapter);
        compositeAdapter.addAdapter(textAdapter);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.composite_list);
        recyclerView.setAdapter(compositeAdapter);
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
