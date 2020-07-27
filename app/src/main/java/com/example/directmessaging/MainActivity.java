package com.example.directmessaging;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
        String statusStr = "Connecting to server";
        status.setText(statusStr);
        /* Server connection thread */
        new Thread(new ConnectThread()).start();
    }

    class ConnectThread implements Runnable {

        @Override
        public void run() {
            /* Create socket */
            socket = new Socket();
            /* Loop until a connection is made */
            while (true) {
                try {
                    /* Try to connect for a second */
                    socket.connect(sockAddr, 2000);
                    break;
                } catch (SocketTimeoutException | SocketException Se) {
                    /* Exception causes socket to close - open new */
                    try {
                        socket.close();
                    } catch (IOException IOe) {
                        IOe.printStackTrace();
                    }
                    try{
                        /* Wait before attempting to connect again */
                        Thread.sleep(1000);
                    } catch(InterruptedException Ie){
                        Ie.printStackTrace();
                    }
                    socket = new Socket();
                } catch(IOException e){
                    /* A real error */
                    e.printStackTrace();
                }
            }
            setStatus();
            new Thread(new ClientThread()).start();
        }

        private TextView status = findViewById(R.id.status);

        private void setStatus() {
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
            updateUI();
            new Thread(new ConnectThread()).start();
        }

        private TextView status = findViewById(R.id.status);
        private EditText msgBox = findViewById(R.id.msgBox);

        private void updateUI() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String statusStr = "Reconnecting to server";
                    status.setText(statusStr);
                    msgBox.setVisibility(View.GONE);
                }
            });
        }
    }

    class ClientThread implements Runnable{

        private TextView chatTitle = findViewById(R.id.chatTitle);
        private TextView chatWindow = findViewById(R.id.chatWindow);
        private EditText msgBox = findViewById(R.id.msgBox);

        @Override
        public void run() {
            initUI();
            new Thread(new SocketInThread()).start();
            /* Listen to keyboard for enter (send) */
            msgBox.setOnEditorActionListener(editorListener);
        }

        private void initUI(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatTitle.setVisibility(View.VISIBLE);
                    chatWindow.setVisibility(View.VISIBLE);
                    chatWindow.setMovementMethod(new ScrollingMovementMethod());
                    msgBox.setVisibility(View.VISIBLE);
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
                    chatWindow.append(timeStamp + ": You: " + text + "\n");
                    if (msgBox.length() > 0) {
                        msgBox.getText().clear();
                    }
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
                    chatWindow.append(timeStamp + ": Client: " + text + "\n");
                }
            });
        }
    }
}