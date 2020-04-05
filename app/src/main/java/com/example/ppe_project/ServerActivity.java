package com.example.ppe_project;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;


public class ServerActivity extends AsyncTask<Void, Void, String> {

    private String message,location;
    private SimpleDateFormat dateOfMessage;
    private Boolean isUrgent;

    ServerActivity(String message, SimpleDateFormat dateOfMessage, String location, Boolean isUrgent){
        this.message=message;
        this.location=location;
        this.dateOfMessage=dateOfMessage;
        this.isUrgent=isUrgent;

    }

    @Override
    protected String doInBackground(Void... voids) {
        String hash="default";
        String temp;
        synchronized (this){

            try {
                Socket clientSocket = new Socket("10.0.2.2", 9876);
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                /*
                System.out.println(this.isUrgent);
                System.out.println(this.message);
                System.out.println(this.dateOfMessage);
                System.out.println(this.location);
                 */
                temp=this.isUrgent.toString()+"#"+this.message+'#'+this.dateOfMessage.toString()+'#'+this.location;

                //System.out.println(temp);

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash2 = digest.digest(temp.getBytes("UTF-8"));

                //System.out.println(hash2.toString());

                dos.writeBytes(hash2.toString());




                dis.close();
                dos.close();
                clientSocket.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


        }

        return hash;
    }



    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);

    }

    @Override
    protected void onProgressUpdate(Void... avoid) {
        super.onProgressUpdate(avoid);
    }


}
