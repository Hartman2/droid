package com.bluetext.nextapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class ServerListener extends AsyncTask<Integer, Void, Socket>
{
	Socket sock;
	int port;
	//ObjectInputStream fromServer;
	//ObjectOutputStream toServer;
	/**
	 * To Server
	 */
	PrintStream ps;
	/**
	 * From server
	 */
	BufferedReader br;
	private final String TAG = "AGG";
	
	//Constructor
	protected Socket doInBackground(Integer... params)
	{
		Log.d(TAG, "Creating socket on port: " + params[0]);
		this.port = params[0];
		
		try{
			sock = new Socket("206.127.186.13", 1300); // andy's PC
		}catch(Exception e){
			Log.d(TAG, "Error in serverListener ctor: " + e.getMessage());
		}
		
		try{
			//fromServer = new ObjectInputStream(sock.getInputStream());
			//toServer = new ObjectOutputStream(sock.getOutputStream());
			ps =  new PrintStream(sock.getOutputStream());
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}catch(Exception e){
			Log.d(TAG, "Error in serverListener ctor: " + e.getMessage());
		}
		SmsListener.setServListener(this);
		Log.d(TAG, "Listening for messages from server forever...");
		String msg = null;
		int sendCode = 0;
		while(br != null || sendCode != 0)
		{
			try {
				msg = br.readLine();
				if(msg == null){
					return sock;
				}
				sendCode = sendTextToAndy(msg);
			} catch (IOException e) {		
				return sock;
			}
		}
		return sock;
	}
	
	public void sendMsgToServer(TextMessage msg)
	{
		Log.d(TAG, "Sending message: " + msg.getContent());
		//toServer.writeObject(msg);		
		ps.println(msg.getContent());
	}
	
	private int sendTextToAndy(String msg)
    {
    	try{
    		SmsManager man = SmsManager.getDefault();
    		man.sendTextMessage("+1 5072542815", null, msg, null, null);
    		Log.d(TAG, "SMS sent!");
    		return 0;
    	} catch (Exception e){
    		Log.d(TAG, "SMS failed.");
    		return -1;
    	}
    }
}
