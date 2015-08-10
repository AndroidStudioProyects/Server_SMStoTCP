package com.example.diego.server_smstotcp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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


public class MainActivity extends ActionBarActivity {

    Switch switch_Server;
    TextView textMensajes;
    EditText edit_IP,edit_Puert;
    Button button;
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
                    edit_Puert.setEnabled(false);
                    edit_IP.setEnabled(false);


                } else {
                    Toast.makeText(getApplicationContext(), "Servidor OffLine", Toast.LENGTH_SHORT).show();
                    switch_Server.setText("Server Off");
                    unregisterReceiver(Broad);// de apaga el broadcastReceiver
                    textMensajes.setEnabled(false);
                    textMensajes.append("Servidor Desactivado \n");
                    edit_Puert.setEnabled(true);
                    edit_IP.setEnabled(true);
                 //   button.setEnabled(true);

                }
            }
        });
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
                            Toast.makeText(context, "llego Radiobase: " + senderNum + " Mensaje: " + message, Toast.LENGTH_SHORT).show();
                          String ip=edit_IP.getText().toString();
                            int puerto=Integer.parseInt(edit_Puert.getText().toString());
                            ConexionIP cliente=new ConexionIP(ip,puerto,message);
                            cliente.start();
                            textMensajes.append("Radio:" + senderNum + " msg: " + message+"\n");
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
