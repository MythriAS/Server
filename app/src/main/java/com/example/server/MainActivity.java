package com.example.server;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView tvServerName, tvServerPort, tvStatus;
    private String serverIP = "192.168.29.94"; //check your own IP Addrress
    private int serverPort = 1234;//choose your own port number >1023 , avoid resreved ones like 8080
    private Button btnStart,btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvServerName = findViewById(R.id.tv_servername);
        tvServerPort = findViewById(R.id.tv_server_port);
        tvStatus = findViewById(R.id.tv_status);
        btnStart=findViewById(R.id.btn_start);
        btnStop=findViewById(R.id.btn_stop);

        tvServerName.setText(serverIP);
        tvServerPort.setText(String.valueOf(serverPort));

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickStartServer(view);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickStopServer(view);
            }
        });
    }
    private ServerThread serverThread;

    public void onClickStartServer(View view){
        serverThread=new ServerThread();
        serverThread.startServer();
    }
    public void onClickStopServer(View view){
        serverThread.stopServer();
    }



    class ServerThread extends Thread implements Runnable {

        private boolean serverRunning;
        private ServerSocket serverSocket;
        private int count = 0;

        public void startServer() {
            serverRunning = true;
            start();
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(serverPort);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvStatus.setText("Waiting for clients");
                    }
                });
                while (serverRunning) {
                    Socket socket = serverSocket.accept();
                    count++;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvStatus.setText("Connected to:" + socket.getInetAddress() + ":" + socket.getLocalPort());
                            Log.d(MainActivity.class.getSimpleName(),"my1"+socket.getInetAddress());
                            Log.d(MainActivity.class.getSimpleName(),"my1"+socket.getLocalPort());
                        }
                    });

                    PrintWriter output_server = new PrintWriter(socket.getOutputStream());
                    output_server.write("welcome to server : " + count);
                    output_server.flush();//DISPLAY IN CLIENT SCREEN//SENDING RESPONSE TO CLEINT
                    socket.close();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stopServer() {
            serverRunning = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (serverSocket != null) {
                        try {
                            serverSocket.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvStatus.setText("Server Stpped");
                                }//DISPLAY IN SERVER SCREEN
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();
        }
    }
}