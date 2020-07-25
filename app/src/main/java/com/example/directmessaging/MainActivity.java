package com.example.directmessaging;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.TestLooperManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private Socket socket;

    private static final int SERVER_PORT = 12345;
    private static final String SERVER_IP = "0.0.0.0";

    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView chatWindow = (TextView) findViewById(R.id.chatWindow);
        chatWindow.setMovementMethod(new ScrollingMovementMethod());

        new Thread(new ClientThread()).start();
    }

    public void clearInputBox(View view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            TextView inputBox = (TextView) findViewById(R.id.EditText01);
                inputBox.setText("");
            }
        });
    }

    public void sendMessage(View view){
        new Thread(new SocketOutThread()).start();
    }

    class SocketOutThread implements Runnable{
        private TextView chatWindow = (TextView) findViewById(R.id.chatWindow);
        private TextView sendMsg = (TextView) findViewById(R.id.EditText01);

        @Override
        public void run() {
            try{
                EditText et = (EditText) findViewById(R.id.EditText01);
                String str = et.getText().toString();
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(str);
                setText(str);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        private void setText(final String text){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    chatWindow.append(timeStamp + ": You: " + text + "\n");
                    sendMsg.setText("");
                }
            });
        }
    }

    class SocketInThread implements Runnable{
        private TextView chatWindow = (TextView) findViewById(R.id.chatWindow);

        @Override
        public void run() {
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input;

                while(true) {
                    try {
                        input = in.readLine();
                    } catch(IOException e){
                        e.printStackTrace();
                        break;
                    }
                    setText(input);
                }
            } catch(Exception e){
                    e.printStackTrace();
            }
        }

        private void setText(final String text){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    chatWindow.append(timeStamp + ": Client: " + text + "\n");
                }
            });
        }
    }

    class ClientThread implements Runnable{
        private TextView status = (TextView) findViewById(R.id.status);

        @Override
        public void run() {
            try{
                socket = new Socket(SERVER_IP, SERVER_PORT);
                setText();
                new Thread(new SocketInThread()).start();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        private void setText(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String str = "Connected to server at " + socket.getRemoteSocketAddress();
                    status.setText(str);
                }
            });
        }
    }
}