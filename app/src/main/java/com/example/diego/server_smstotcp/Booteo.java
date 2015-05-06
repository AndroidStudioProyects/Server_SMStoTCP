package com.example.diego.server_smstotcp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Diego on 30/04/2015.
 */
public class Booteo extends BroadcastReceiver {
Context contexto;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.contexto=context;
        Intent intento= new Intent(context,MainActivity.class);
        intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intento);

    }
}
