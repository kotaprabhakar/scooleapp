package com.android_perspective.scoole;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

public class ChatListeningService extends Service {

    String server_addr = "192.168.0.104";
    private final int PORT_NUM = 4156;
    int clientIdentifier;
    String message;
    Socket clientSocket;
    LocalBroadcastManager broadcaster;
    Intent intent;

    public static final String broadcastMe = "com.android_perspective.scoole.BROADCAST_MESSAGE";

    public static final String messageBroadcast = "com.android_perspective.scoole.MESSAGE_INTENT_NAME";

    public ChatListeningService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        clientIdentifier = (int)(intent.getExtras().get("ClientIdentifier"));
        this.intent = intent;
        message = (String)(intent.getExtras().get("Message"));
        Toast.makeText(this,"Message"+message,Toast.LENGTH_LONG).show();
        if(clientIdentifier != -1) {
            new ChatListenTask().execute();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    void sendBackMessageToUIThread(String incomingMessage)
    {
        Intent broadcastIntent = new Intent(this,ChatActivity.class);
        broadcastIntent.putExtra("Message",incomingMessage);
        broadcaster.sendBroadcast(broadcastIntent);
    }

    private class ChatListenTask extends AsyncTask<String, Void, String> {
        int x = 0;
        @Override
        protected String doInBackground(String... params) {
            try {
                clientSocket = new Socket(server_addr, PORT_NUM);
                String incomingMessage = null;
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                //BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
                    while (true) {
                        while ((incomingMessage = (String)ois.readObject()) != null) {
                            sendBackMessageToUIThread(incomingMessage);
                        }
                        publishProgress();
                        if (message != null || message != "NULL") {
                            JSONObject obj = new JSONObject().put("message", new JSONObject().put("id", intent.getExtras().get("sendToIdentifier")).put("text", message));
                            oos.writeObject(obj);
                            message = null;
                        }
                    }

            }catch(IOException io)
            {
                x = 1;
                publishProgress();
                Log.e("ListeningService","Cannot Connect to Server");
            }catch (JSONException je)
            {
                Log.e("ListeningService","Cannot Connect to Server");
            }catch(ClassNotFoundException cnfe)
            {
                Log.e("ListeningService","CNFE");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... value) {
            if(x == 1)
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(),"message: "+message,Toast.LENGTH_LONG).show();
            super.onProgressUpdate(value);
        }
    }
    /*
    private class SendStatusToServer extends AsyncTask<Socket, Void, String>{
        @Override
        protected String doInBackground(Socket... params) {
                boolean sent = false;
                    try {
                        Toast.makeText(getApplicationContext(),"Trying to Send message/status",Toast.LENGTH_LONG).show();
                        PrintWriter out = new PrintWriter(params[0].getOutputStream(),true);
                        if(message == "null")
                            out.write(clientIdentifier);
                        else if(message != null){
                            try {
                                JSONObject obj = new JSONObject().put("message", new JSONObject().put("id", intent.getExtras().get("sendToIdentifier")).put("text", message));
                                out.println(obj);
                                message = null;
                            }catch (JSONException je)
                            {
                                Toast.makeText(getApplicationContext(),"JSONException",Toast.LENGTH_LONG).show();
                                Log.e("ChatListeningService","JSON exception");
                            }
                        }
                        Thread.sleep(10000);
                        out.close();
                    }catch (IOException io)
                    {
                        Toast.makeText(getApplicationContext(),"SendMessage:IOException",Toast.LENGTH_LONG).show();
                        Log.e("ChatListeningService","IOException");
                    }catch (InterruptedException ie)
                    {
                        Toast.makeText(getApplicationContext(),"InterruptedException",Toast.LENGTH_LONG).show();
                        Log.e("ChatListeningService","InterruptedException");
                    }

            return null;
        }
    }*/
}