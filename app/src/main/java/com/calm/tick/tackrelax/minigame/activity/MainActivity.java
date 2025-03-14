package com.calm.tick.tackrelax.minigame.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.calm.tick.tackrelax.minigame.R;
import com.calm.tick.tackrelax.minigame.fragment.UnityViewFragment;
import com.calm.tick.tackrelax.minigame.utils.ConstantsAds;
import com.calm.tick.tackrelax.minigame.utils.SharePreferenceUtils;
import com.example.speechtotext.UnityExten;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.mallegan.ads.callback.InterCallback;
import com.mallegan.ads.callback.NativeCallback;
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
    private SharePreferenceUtils sharePreferenceUtils;

    RelativeLayout rlBanner;
    private View adContainer;
    private FrameLayout.LayoutParams containerParams;
    private int adWidth = 0;
    private int adHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        sound = getSoundPreference();
        CustomUnityActivity.getInstance().handlePlayAction(this, "play", sound);
        // showFragment(new UnityViewFragment());
        sharePreferenceUtils = new SharePreferenceUtils(this);

        sharePreferenceUtils.incrementCounter();


        ReciverMessageFromUnity.callbackManager = new CallbackManager();
        ReciverMessageFromUnity.callbackManager.SetActivity(ac -> {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );


            LayoutInflater inflater = getLayoutInflater();
            View rootView = inflater.inflate(R.layout.activity_main, null);
            rlBanner = rootView.findViewById(R.id.rl_banner);
            rlBanner.setVisibility(View.GONE);

            ac.addContentView(rootView, params);
            setupUnityCallback(ac);
        });

        loadInterReciverItem();
        // loadBanner();
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

    private void setupUnityCallback(Activity ac) {

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
                        rlBanner.setVisibility(View.VISIBLE);
                        loadBanner(ac);
                    });
                    break;
                case "SceneGame":
                    runOnUiThread(() -> {
                        rlBanner.setVisibility(View.GONE);
                    });
                    break;
                case "reciver_item_end_game":
                    runOnUiThread(() -> {
                        rlBanner.setVisibility(View.VISIBLE);
                        loadBanner(ac);
                    });
                    break;
                case "nativePos: x,y":
                    break;
                default:
                    if (message.startsWith("nativePos: ")) {
                        runOnUiThread(() -> {
                            String[] parts = message.replace("nativePos: ", "").split(",");
                            if (parts.length == 2) {
                                try {
                                    int x = Integer.parseInt(parts[0].trim());
                                    int y = Integer.parseInt(parts[1].trim());
                                    loadAds(ac, x, y);
                                } catch (NumberFormatException e) {
                                    Log.e("UnityCallback", "Lỗi chuyển đổi tọa độ x,y: " + message, e);
                                }
                            } else {
                                Log.e("UnityCallback", "Sai định dạng nativePos: " + message);
                            }
                        });
                    } else if (message.startsWith("nativePos_Set: ")) {
                        runOnUiThread(() -> {
                            String[] parts = message.replace("nativePos_Set: ", "").split(",");
                            if (parts.length == 2) {
                                try {
                                    int x = Integer.parseInt(parts[0].trim());
                                    int y = Integer.parseInt(parts[1].trim());
                                    updateAdsPosition(x, y);
                                } catch (NumberFormatException e) {
                                    Log.e("UnityCallback", "Lỗi chuyển đổi tọa độ x,y cho nativePos_Set: " + message, e);
                                }
                            } else {
                                Log.e("UnityCallback", "Sai định dạng nativePos_Set: " + message);
                            }
                        });
                        Log.d("MessageHandler", "Ntivepostset: " + message);
                    }
                    break;
            }
        });
    }

    private void updateAdsPosition(int x, int y) {
        if (adContainer != null && containerParams != null) {
            containerParams.leftMargin = x - (adWidth / 2);
            containerParams.topMargin = y - (adHeight / 2);

            containerParams.leftMargin = Math.max(0, containerParams.leftMargin);
            containerParams.topMargin = Math.max(0, containerParams.topMargin);

            adContainer.setLayoutParams(containerParams);
            Log.d("AdsPosition", "Ad position updated to center at: x=" + x + ", y=" + y);
            Log.d("AdsPosition", "Actual top-left position: x=" + containerParams.leftMargin + ", y=" + containerParams.topMargin);
        } else {
            Log.e("AdsPosition", "Cannot update ad position, container or params is null");
        }
    }

    private void loadAds(Activity ac, int x, int y) {
        Admob.getInstance().loadNativeAd(ac, getString(R.string.native_language), new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                super.onNativeAdLoaded(nativeAd);

                if (adContainer != null) {
                    ViewGroup rootView = ac.findViewById(android.R.id.content);
                    rootView.removeView(adContainer);
                }

                adContainer = LayoutInflater.from(ac)
                        .inflate(R.layout.ads_native, null);

                FrameLayout frAds = adContainer.findViewById(R.id.fr_ads);
                RelativeLayout shimmer = adContainer.findViewById(R.id.shimmer_native_language);

                shimmer.setVisibility(View.GONE);

                NativeAdView adView;
                adView = (NativeAdView) LayoutInflater.from(ac)
                        .inflate(R.layout.layout_native_index, null);

                frAds.removeAllViews();
                frAds.addView(adView);

                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);

                frAds.setVisibility(View.VISIBLE);

                ViewGroup rootView = ac.findViewById(android.R.id.content);

                containerParams = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                rootView.addView(adContainer, containerParams);

                adContainer.post(() -> {
                    adWidth = adContainer.getWidth();
                    adHeight = adContainer.getHeight();

                    Log.d("AdsPosition", "Ad size measured: width=" + adWidth + ", height=" + adHeight);

                    containerParams.leftMargin = x - (adWidth / 2);
                    containerParams.topMargin = y - (adHeight / 2);

                    containerParams.leftMargin = Math.max(0, containerParams.leftMargin);
                    containerParams.topMargin = Math.max(0, containerParams.topMargin);

                    adContainer.setLayoutParams(containerParams);

                    Log.d("AdsPosition", "Ad displayed centered at: x=" + x + ", y=" + y);
                    Log.d("AdsPosition", "Actual top-left position: x=" + containerParams.leftMargin + ", y=" + containerParams.topMargin);
                });
            }

            @Override
            public void onAdFailedToLoad() {
                super.onAdFailedToLoad();
                if (adContainer != null) {
                    ViewGroup rootView = ac.findViewById(android.R.id.content);
                    rootView.removeView(adContainer);
                    adContainer = null;
                    containerParams = null;
                    adWidth = 0;
                    adHeight = 0;
                }
                View failedAdContainer = LayoutInflater.from(ac)
                        .inflate(R.layout.ads_native, null);
                FrameLayout frAds = failedAdContainer.findViewById(R.id.fr_ads);
                frAds.setVisibility(View.GONE);
            }
        });
    }


    private void showInterReciverItem() {
        loadInterReciverItem();
        if (ConstantsAds.interReciverItem != null) {
//            ConstantsAds.interReciverItem.show(MainActivity.this);
            Admob.getInstance().showInterAds(MainActivity.this, ConstantsAds.interReciverItem, new InterCallback() {
                @Override
                public void onNextAction() {
                    super.onNextAction();
                    loadInterReciverItem();
                }

            });
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


    private void loadBanner(Activity ac) {
        try {
            runOnUiThread(() ->
                    Admob.getInstance().loadCollapsibleBanner(ac, getString(R.string.banner_collab), "bottom")

            );
            Log.d("Banner", "Loading banner for HomeScene");
        } catch (Exception e) {
            Log.e("Banner", "Error loading banner", e);
        }
    }
}