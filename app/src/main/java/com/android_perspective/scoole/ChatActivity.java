package com.android_perspective.scoole;

import android.animation.AnimatorSet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class ChatActivity extends AppCompatActivity {

    String serverIP = "172.16.0.5";
    private String PORT_NUM = "4156";
    TextView dynamicChatItemView;
    LinearLayout chatLinearLayout;
    ViewGroup.LayoutParams layoutParams;
    EditText textToSend;
    Button sendButton;
    String sendToEmailID;
    Handler sender;
    HttpURLConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.setTitle(getIntent().getExtras().getString("Title"));
        sendToEmailID = getIntent().getExtras().getString("Email");
        sendButton = (Button) findViewById(R.id.button);
        textToSend = (EditText) findViewById(R.id.editText);
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chatLinearLayout = (LinearLayout)findViewById(R.id.chat_LinearLayout);
        new ThreadToSendMessage().start();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageToSend = textToSend.getText().toString();
                sendMessage(messageToSend);
            }
        });
        new ThreadToProbeNewMessages().execute("receive", serverIP, PORT_NUM, "checkForMessage");

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    void sendMessage(final String messageToSend) {
        if (messageToSend == null) {
            return;
        } else {
            updateUI(messageToSend);
            while(sender == null);
            sender.post(new Runnable() {
                @Override
                public void run() {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http").encodedAuthority(serverIP + ":" + PORT_NUM).path("/");
                    String stringUrl = builder.build().toString();
                    try {
                        URL url = new URL(stringUrl);
                        synchronized (conn) {
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("from", "kotaprabhakar@scoole.com");
                            conn.setRequestProperty("content-type", "text/plain");
                            conn.setReadTimeout(2000);
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                            conn.connect();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                            JSONObject jObject = new JSONObject().put("to", "kvrkarthik@scoole.com").put("msg", messageToSend);
                            writer.write(jObject.toString());
                            writer.newLine();
                            writer.flush();
                            writer.close();
                            conn.disconnect();
                        }
                    } catch (Exception e) {
                        Log.d("Exception", e.getMessage());
                    }
                }
            });
            textToSend.setText("");
        }
    }

    void updateUI(String message){
        dynamicChatItemView = new TextView(this);
        dynamicChatItemView.setLayoutParams(layoutParams);
        dynamicChatItemView.setText(message);
        dynamicChatItemView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        this.chatLinearLayout.addView(dynamicChatItemView);
    }

    class ThreadToProbeNewMessages extends AsyncTask<String,String,Void>{
        @Override
        protected Void doInBackground(String... params) {
            String server = params[1],port = params[2],message = params[3], readData;
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").encodedAuthority(server + ":" + port).path("/");
            String stringUrl = builder.build().toString();
            Log.d("Debug",params[0]);
            try {
                URL url = new URL(stringUrl);
                synchronized (conn){
                    while(params[0].equals("receive")) {
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("from", "kotaprabhakar@scoole.com");
                        conn.setRequestProperty("content-type", "text/plain");
                        conn.setReadTimeout(2000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.connect();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                        JSONObject jObject = new JSONObject().put("to", "server").put("msg", message);
                        writer.write(jObject.toString());
                        writer.flush();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((readData = reader.readLine()) != null) {
                            if (readData.equals("NaNNULLNaN"))
                                Log.d("ReceivedMessages", "No new Messages");
                            else {
                                Log.d("ReceivedMessages", readData);
                                publishProgress(readData);
                            }
                        }
                        writer.close();
                        reader.close();
                        conn.disconnect();
                        Thread.sleep(2000);
                    }
                }
            }catch(MalformedURLException mue){
                Log.d("ThreadToProbeNewMUE",mue.getMessage());
            }catch(IOException ioe){
                Log.d("ThreadToProbeNewIOE",ioe.getMessage());
            }catch(InterruptedException ie){
                Log.d("ThreadToProbeNewIE",ie.getMessage());
            }catch(JSONException je){
                Log.d("ThreadToProbeNewJE",je.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            updateUI(values[0]);
        }

    }

    private class ThreadToSendMessage extends Thread{
        @Override
        public void run() {
            if(Looper.myLooper() == null)
                Looper.prepare();
            sender = new Handler();
            Looper.loop();
            super.run();
        }
    }

}
