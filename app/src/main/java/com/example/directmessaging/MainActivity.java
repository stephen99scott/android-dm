package com.example.directmessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ChatCard> cardList;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        createChatList();
        buildRecycleView();

        /* button listener to add a new chat -----------------------------------------------------*/
        button = (Button) findViewById(R.id.add_chat);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinNewRoom();
            }
        });
        /* end button listener to add a new chat -------------------------------------------------*/

    }

    public void createChatList() {
        cardList = new ArrayList<>();
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
        cardList.add(new ChatCard("name text 2", "prev message 2", "July 28th"));
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
        cardList.add(new ChatCard("name text 2", "prev message 2", "July 28th"));
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
        cardList.add(new ChatCard("name text 2", "prev message 2", "July 28th"));
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
        cardList.add(new ChatCard("name text 2", "prev message 2", "July 28th"));
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
        cardList.add(new ChatCard("name text 2", "prev message 2", "July 28th"));
        cardList.add(new ChatCard("name text", "prev message", "July 27th"));
    }

    public void buildRecycleView() {
        RecyclerView myRecyclerView = findViewById(R.id.chat_list_view);
        /* ensures each element in the recycler view is the same size */
        myRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(this);
        final ChatCardAdapter myAdapter = new ChatCardAdapter(cardList);

        myRecyclerView.setLayoutManager(myLayoutManager);
        myRecyclerView.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(new ChatCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //cardList.get(position).changeText("clicked");
                //myAdapter.notifyItemChanged(position);
                openChatRoom();
            }
        });
    }
    private void openChatRoom() {
        Intent intent = new Intent(this, ChatRoom.class);
        startActivity(intent);
    }

    private void joinNewRoom() {
        Intent intent = new Intent(this, AddChatRoom.class);
        startActivity(intent);
    }
}