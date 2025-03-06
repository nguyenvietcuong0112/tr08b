package com.calm.tick.tackrelax.minigame.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.calm.tick.tackrelax.minigame.R;
import com.calm.tick.tackrelax.minigame.fragment.UnityViewFragment;
import com.calm.tick.tackrelax.minigame.utils.ConstantsAds;
import com.example.speechtotext.UnityExten;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.mallegan.ads.callback.InterCallback;
import com.mallegan.ads.util.Admob;
import com.unity3d.player.CallbackManager;
import com.unity3d.player.ReciverMessageFromUnity;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends AppCompatActivity {

    boolean sound = true;
    boolean vibra = true;

    private static final String PREF_NAME = "AppPreferences";
    private static final String KEY_SOUND = "sound";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        sound = getSoundPreference();
//        CustomUnityActivity.getInstance().handlePlayAction(this, "play", sound);
        showFragment(new UnityViewFragment());

        loadInterReciverItem();
        setupUnityCallback();
        loadBanner();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.show_unity, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void saveSoundPreference(boolean soundValue) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SOUND, soundValue);
        editor.apply();
    }

    private boolean getSoundPreference() {
        return sharedPreferences.getBoolean(KEY_SOUND, true);
    }


    private void loadInterReciverItem() {
        runOnUiThread(() -> Admob.getInstance().loadInterAds(this, getString(R.string.inter_reciver_item), new InterCallback() {
            @Override
            public void onInterstitialLoad(InterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                ConstantsAds.interReciverItem = interstitialAd;
            }
        }));
    }
    private void setupUnityCallback() {
        ReciverMessageFromUnity.callbackManager = new CallbackManager();

        ReciverMessageFromUnity.callbackManager.setCallback(message -> {
            switch (message) {
                case "back":
                    runOnUiThread(() -> {
                        SplashActivity.isDelayBegin = true;
                        getSoundPreference();
                        UnityPlayer.UnitySendMessage("FlutterAndUnityManager", "OnOFFSound", String.valueOf(sound));
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                    break;
                case "showAds_inter_reciver_item":
                    runOnUiThread(() -> {
                        showInterReciverItem();
                    });
                    break;
                case "HomeScene":
                    runOnUiThread(() -> {
                        loadBanner();
                    });
                    break;
                case "SceneGame":
                    break;
                default:
                    Log.d("MessageHandler", "Unknown message: " + message);
                    break;
            }
        });
    }
    private void showInterReciverItem() {
        loadInterReciverItem();
        if (ConstantsAds.interReciverItem != null) {
            ConstantsAds.interReciverItem.show(MainActivity.this);
//            Admob.getInstance().showInterAds(MainActivity.this, ConstantsAds.interReciverItem, new InterCallback() {
//                @Override
//                public void onNextAction() {
//                    super.onNextAction();
//                    loadInterReciverItem();
//                }
//
//            });
        } else {
            Log.d("Ads", "Interstitial ad not ready yet.");
        }
    }


    private void loadBanner1(Activity ac) {
        try {
            runOnUiThread(() ->
                    Admob.getInstance().loadCollapsibleBanner(ac, getString(R.string.banner_collab), "bottom")

            );
            Log.d("Banner", "Loading banner for HomeScene");
        } catch (Exception e) {
            Log.e("Banner", "Error loading banner", e);
        }
    }

    private void loadBanner() {
        try {
            runOnUiThread(() ->
                    Admob.getInstance().loadCollapsibleBanner(this, getString(R.string.banner_collab), "bottom")

                    );
            Log.d("Banner", "Loading banner for HomeScene");
        } catch (Exception e) {
            Log.e("Banner", "Error loading banner", e);
        }
    }

}