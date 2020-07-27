package com.example.directmessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        ArrayList<ChatCard> cardList = new ArrayList<>();
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
        cardList.add(new ChatCard("name text 2", "prev message 2", "July 28th"));

        RecyclerView myRecyclerView = findViewById(R.id.chat_list_view);
        /* ensures each element in the recycler view is the same size */
        myRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter myAdapter = new ChatCardAdapter(cardList);

        myRecyclerView.setLayoutManager(myLayoutManager);
        myRecyclerView.setAdapter(myAdapter);

        /*
        Button button = (Button) findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openChatRoom();
            }
        });
        */
    }

    private void openChatRoom() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}