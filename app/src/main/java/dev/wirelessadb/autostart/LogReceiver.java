package dev.wirelessadb.autostart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class LogReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        String line = intent.getStringExtra("line");
        if (line == null || line.isEmpty()) return;
        EventLog.append(context, line);
    }
}
