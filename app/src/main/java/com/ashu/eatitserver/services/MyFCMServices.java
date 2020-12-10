package com.ashu.eatitserver.services;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFCMServices extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> dataRecv =remoteMessage.getData();
        if (dataRecv != null) {
            if (dataRecv.get(Common.NOT1_TITLE).equals("New Order")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER, true);
                Common.showNotification(this, new Random().nextInt(),
                        dataRecv.get(Common.NOT1_TITLE),
                        dataRecv.get(Common.NOT1_CONTENT),
                        intent);
            } else {
                Common.showNotification(this, new Random().nextInt(),
                        dataRecv.get(Common.NOT1_TITLE),
                        dataRecv.get(Common.NOT1_CONTENT),
                        null);
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            Common.updateToken(this, s, true, false);
    }

}
