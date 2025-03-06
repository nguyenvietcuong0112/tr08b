package com.calm.tick.tackrelax.minigame.activity;

import android.content.Context;
import android.content.Intent;

import com.example.speechtotext.UnityExten;
import com.unity3d.player.UnityPlayer;


public class CustomUnityActivity {

    private static CustomUnityActivity instance;
    private boolean isDelayBegin = false;

    public static synchronized CustomUnityActivity getInstance() {
        if (instance == null) {
            instance = new CustomUnityActivity();
        }
        return instance;
    }

    public void openUnityActivity(Context context) {
        Intent intent = new Intent(context, UnityExten.class);
        context.startActivity(intent);
    }

    public void handlePlayAction(Context context, String action, boolean sound) {
        openUnityActivity(context);

        if (!isDelayBegin) {
            isDelayBegin = true;
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                sendUnityMessage("FlutterAndUnityManager", "OnPlayGame", action);
                sendUnityMessage("FlutterAndUnityManager", "OnOFFSound", String.valueOf(sound));
            }, 2000);
        } else {
            sendUnityMessage("FlutterAndUnityManager", "OnPlayGame", action);
            sendUnityMessage("FlutterAndUnityManager", "OnOFFSound", String.valueOf(sound));
        }
    }

    public void sendUnityMessage(String objectName, String methodName, String message) {
        UnityPlayer.UnitySendMessage(objectName, methodName, message);
    }
}
