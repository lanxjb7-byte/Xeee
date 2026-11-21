package com.lixxzy.security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class PackageAddedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) return;
        Uri data = intent.getData();
        if (data == null) return;

        String packageName = data.getSchemeSpecificPart();
        if (packageName == null || packageName.equals(context.getPackageName())) return;

        SharedPreferences prefs = context.getSharedPreferences("lixxzy_security", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("pending_package", packageName)
                .putLong("pending_time", System.currentTimeMillis())
                .apply();
    }
}
