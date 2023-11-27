package com.sunil.sms_autodetect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    WeakReference<SmsAutodetectPlugin> plugin;

    SmsBroadcastReceiver(WeakReference<SmsAutodetectPlugin> plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // intent.setPackage(context.getPackageName());
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            if (plugin.get() == null) {
                return;
            } else {
                plugin.get().unregister();
            }

            Bundle extras = intent.getExtras();
            Status status;
            if (extras != null) {
                status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                if (status != null) {
                    if (status.getStatusCode() == CommonStatusCodes.SUCCESS) {
                        // Get SMS message contents
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        Pattern pattern = Pattern.compile("\\d{4,6}");
                        if (message != null) {
                            HashMap<String, String> map = new HashMap<>();
                            Matcher matcher = pattern.matcher(message);
                            if (matcher.find()) {
                                map.put("code", matcher.group(0));
                                map.put("msg", message);
                                plugin.get().setCode(map);
                            } else {
                                map.put("code", "");
                                map.put("msg", message);
                                plugin.get().setCode(map);
                            }
                        }
                    }
                }
            }
        }
    }
}
