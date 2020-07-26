package com.example.directmessaging;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
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
import java.util.Date;

import static java.text.DateFormat.getDateInstance;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private Socket socket;

    private static final int SERVER_PORT = 12345;
    private static final String SERVER_IP = "0.0.0.0";

    private SocketAddress sockAddr = new InetSocketAddress(SERVER_IP, SERVER_PORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView status = findViewById(R.id.status);
        String statusText = "Status: Connecting to server";
        status.setText(statusText);
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
                    break;
                } catch (Exception e) {}
            }
            /* Try to connect to server with infinite timeout */
            try {
                socket.connect(sockAddr, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setText();
            new Thread(new ClientThread()).start();
        }

        private TextView status = findViewById(R.id.status);

        private void setText() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String str = "";
                    if (socket.getRemoteSocketAddress() != null){
                        str = "Status: Connected to server at " + socket.getRemoteSocketAddress();
                    }
                    else{
                        str = "Status: Connection failed";
                    }
                    status.setText(str);
                }
            });
        }
    }


    class ClientThread implements Runnable{

        @Override
        public void run() {
            setText();
            enter_check();
            new Thread(new SocketInThread()).start();
        }

        private TextView chatTitle = findViewById(R.id.chatTitle);
        private TextView chatWindow = findViewById(R.id.chatWindow);
        private EditText msgBox = findViewById(R.id.msgBox);
        private Button sendBtn = findViewById(R.id.sendBtn);

        private void setText(){
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

        // checks if the enter key was pressed, if it was then send the message
        private void enter_check(){
            // needs to be on the UI thread since it's dealing with the UI
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final EditText msgBox = findViewById(R.id.msgBox);
                    // adds a text listener for the message box, everything that's entered goes through here
                    msgBox.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable input) {
                            // as soon as text in the message box changes, this function is run
                            String str = String.valueOf(input);
                            if (!str.equals("")) {
                                String lastChar = str.substring(str.length() - 1);
                                // check to see if client just hit enter
                                if (lastChar.equals("\n")){
                                    // they hit enter, so grab their input excluding the enter key
                                    String message = str.substring(0, str.length() - 1);
                                    // replace their message with their message minus enter key
                                    msgBox.setText(message);
                                    // send the message
                                    new Thread(new SocketOutThread()).start();
                                }
                            }
                        }
                        // functions we don't use but TextWatcher needs to run
                        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    });
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