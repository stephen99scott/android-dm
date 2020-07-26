package com.example.directmessaging;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import java.util.Date;

import static java.text.DateFormat.getDateInstance;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private Socket socket;

    private static final int SERVER_PORT = 12345;
    private static final String SERVER_IP = "0.0.0.0";

    private static final String CONNECTION_LOST = "connect-lost";

    private SocketAddress sockAddr = new InetSocketAddress(SERVER_IP, SERVER_PORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView status = findViewById(R.id.status);
        String statusStr = "Status: Connecting to server";
        status.setText(statusStr);
        /* Server connection thread */
        new Thread(new ConnectThread()).start();
    }

    class ConnectThread implements Runnable {
        @Override
        public void run() {
            /* Loop until socket is opened */
            while (true) {
                try {
                    socket = new Socket();
                    socket.connect(sockAddr, 3000);
                    break;
                } catch (Exception e) {
                    Log.i(TAG, "Failed to connect");
                    try{
                        socket.close();
                        Log.i(TAG, "Socket closed");
                    } catch(IOException IOe){
                        Log.i(TAG, "Failed to close socket");
                    }
                }
            }
            setText();
            new Thread(new ClientThread()).start();
        }
        
        private TextView status = findViewById(R.id.status);

        private void setText() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String str = "Connected to server at " + socket.getRemoteSocketAddress();
                    status.setText(str);
                }
            });
        }
    }

    class ReconnectThread implements Runnable{

        @Override
        public void run() {
            setText();
            new Thread(new ConnectThread()).start();
        }

        private TextView status = findViewById(R.id.status);
        private EditText msgBox = findViewById(R.id.msgBox);
        private Button sendBtn = findViewById(R.id.sendBtn);

        private void setText() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String statusStr = "Connecting to server";
                    status.setText(statusStr);
                    msgBox.setVisibility(View.GONE);
                    sendBtn.setVisibility(View.GONE);
                }
            });
        }
    }

    class ClientThread implements Runnable{
    /* Thread that deals with anything client-side, ie. visibility of ui, textwatchers, etc. */
        private TextView chatTitle = findViewById(R.id.chatTitle);
        private TextView chatWindow = findViewById(R.id.chatWindow);
        private EditText msgBox = findViewById(R.id.msgBox);
        private Button sendBtn = findViewById(R.id.sendBtn);

        @Override
        public void run() {
            gui_ini();
            new Thread(new SocketInThread()).start();
            Log.d("bruh", "starting client thread");
            /* we display the info in the gui, and start checking for send requests */
            msgBox.setOnEditorActionListener(editorListener);

        }

        /* keyboard action listener */
        private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                /* check for the ime action of next (the enter button on keyboard) */
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    new Thread(new SocketOutThread()).start();
                }
                return true;
            }
        };

        private void gui_ini() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatTitle.setVisibility(View.VISIBLE);
                    chatWindow.setVisibility(View.VISIBLE);
                    chatWindow.setMovementMethod(new ScrollingMovementMethod());
                    msgBox.setVisibility(View.VISIBLE);
                    sendBtn.setVisibility(View.VISIBLE);
                }
            });
        }
    }



    /* Send button listener */
    public void sendMessage(View view) {
        new Thread(new SocketOutThread()).start();
    }


    class SocketOutThread implements Runnable {

        @Override
        public void run() {
            try {
                EditText et = findViewById(R.id.msgBox);
                String str = et.getText().toString();
                if (str.equals("")){
                    return;
                }
                // send the actual message
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(str);
                setText(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private TextView chatWindow = findViewById(R.id.chatWindow);
        private EditText msgBox = findViewById(R.id.msgBox);

        private void setText(final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String timeStamp = getDateInstance().format(new Date());
                    chatWindow.append("You: " + text + "\n");
                    msgBox.setText("");
                }
            });
        }
    }


    class SocketInThread implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input;

                while (true) {
                    try {
                        input = in.readLine();
                        if (input.equals(CONNECTION_LOST)){
                            socket.close();
                            new Thread(new ReconnectThread()).start();
                            Log.i(TAG, "End thread");
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    setText(input);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private TextView chatWindow = findViewById(R.id.chatWindow);

        private void setText(final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String timeStamp = getDateInstance().format(new Date());
                    chatWindow.append("Client: " + text + "\n");
                }
            });
        }
    }
}
