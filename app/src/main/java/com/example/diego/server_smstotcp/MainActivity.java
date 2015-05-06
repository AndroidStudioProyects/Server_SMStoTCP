package com.example.diego.server_smstotcp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends ActionBarActivity {

    Switch switch_Server;
    TextView textMensajes;
    EditText edit_IP,edit_Puert;
    Button button;
    ClienteTask hilo;
    Socket socket;
    String IP;
    int Port;
    BroadcastReceiver Broad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XML();
        BOTONES();
        BROADCAST();


        
    }

    private void ClienteTCP() {




    }


    private void XML() {
        switch_Server= (Switch) findViewById(R.id.switch_Server);
        textMensajes= (TextView) findViewById(R.id.textMensajes);
        edit_IP= (EditText) findViewById(R.id.edit_IP);
        IP=edit_IP.getText().toString();
        edit_Puert= (EditText) findViewById(R.id.edit_Puerto);
        Port=Integer.parseInt(edit_Puert.getText().toString());
        button= (Button) findViewById(R.id.button);
        Toast.makeText(getApplicationContext(),"IP:"+IP+" Puerto:"+Port,Toast.LENGTH_SHORT).show();



    }


    private void BOTONES() {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hilo=new ClienteTask();
                hilo.execute();
                button.setEnabled(true);
            }
        });

        switch_Server.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Servidor OnLine", Toast.LENGTH_SHORT).show();
                    switch_Server.setText("Server On");
                    registerReceiver(Broad, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));//POWER_CONNECTED
                    textMensajes.setEnabled(true);
                    textMensajes.append("Servidor Activado \n");


                } else {
                    Toast.makeText(getApplicationContext(), "Servidor OffLine", Toast.LENGTH_SHORT).show();
                    switch_Server.setText("Server Off");
                    unregisterReceiver(Broad);// de apaga el broadcastReceiver
                    textMensajes.setEnabled(false);
                    textMensajes.append("Servidor Desactivado \n");

                    hilo.cancel(true);
                 //   button.setEnabled(true);

                }
            }
        });
    }

    public class ClienteTask extends AsyncTask<String,Void,String>{

        Boolean control=true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            control=true;
            Toast.makeText(getApplicationContext(), "onPreExecute", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                //Create a client socket and define internet address and the port of the server
                socket = new Socket("192.168.0.103",9001);
                //Get the input stream of the client socket
                InputStream is = socket.getInputStream();
                //Get the output stream of the client socket
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                //Write data to the output stream of the client socket
                out.println(params[0]);
                //Buffer the data coming from the input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read data in the input buffer
                result = br.readLine();
                //Close the client socket
                socket.close();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }


        protected void onProgressUpdate(String values) {
            Toast.makeText(getApplicationContext(), "onProgressUpdate", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPostExecute(String o) {
            Toast.makeText(getApplicationContext(), "onPostExecute", Toast.LENGTH_SHORT).show();
            textMensajes.append("\n" + o);
        }

        @Override
        protected void onCancelled(String o) {
            Toast.makeText(getApplicationContext(), "onCancelled", Toast.LENGTH_SHORT).show();
            control=false;
        }
    }

    private void BROADCAST() {


        Broad=new BroadcastReceiver()   {
            @Override
            public void onReceive(Context context, Intent intent) {

                //           Toast.makeText(context,"BroadcasrReceiver",Toast.LENGTH_SHORT).show();
                final Bundle bundle = intent.getExtras();

                try {
                    if(bundle != null){

                        final Object[] pdusObj = (Object[]) bundle.get("pdus");

                        for(int i = 0; i < pdusObj.length; i++){
                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                            String senderNum = phoneNumber;
                            String message = currentMessage.getDisplayMessageBody();
                            Toast.makeText(context, "Radiobase: " + senderNum + " Mensaje: " + message, Toast.LENGTH_SHORT).show();
                            hilo=new ClienteTask();
                            hilo.execute("Radiobase: " + senderNum + " Alarma: " + message);
                            textMensajes.append("Radiobase:" + senderNum + " Mensaje: " + message+"\n");
                        }
                    }
                } catch (Exception e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" + e);
                }

            }
        };

        registerReceiver(Broad, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));//POWER_CONNECTED


}

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "onResume", Toast.LENGTH_SHORT).show();

    }
    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "onPause", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();
        unregisterReceiver(Broad);// de apaga el broadcastReceiver

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
