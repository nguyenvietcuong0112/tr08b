package com.calm.tick.tackrelax.minigame.activity;

import android.content.Intent;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import com.calm.tick.tackrelax.minigame.R;
import com.calm.tick.tackrelax.minigame.utils.SharePreferenceUtils;
import com.calm.tick.tackrelax.minigame.utils.SystemUtil;
import com.calm.tick.tackrelax.minigame.base.AbsBaseActivity;
import com.calm.tick.tackrelax.minigame.databinding.ActivitySplashBinding;
import com.mallegan.ads.callback.InterCallback;
import com.mallegan.ads.util.Admob;
import com.mallegan.ads.util.ConsentHelper;

import java.util.Map;


public class SplashActivity extends AbsBaseActivity {


    public  static  boolean isDelayBegin = false;
    private SharePreferenceUtils sharePreferenceUtils;

    private ActivitySplashBinding binding;
    private InterCallback interCallback;

    @Override
    public void bind() {

        SystemUtil.setLocale(this);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isDelayBegin = false;
        loadAds();
    }


    private void loadAds() {
        sharePreferenceUtils = new SharePreferenceUtils(this);

        int counterValue = sharePreferenceUtils.getCurrentValue();

        interCallback = new InterCallback() {
            @Override
            public void onNextAction() {
                super.onNextAction();

                if(counterValue == 0) {
                    startActivity(new Intent(SplashActivity.this, LanguageActivity.class));
                }
                else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
//
            }
        };
      if (SharePreferenceUtils.isOrganic(this)) {
            AppsFlyerLib.getInstance().registerConversionListener(this, new AppsFlyerConversionListener() {

                @Override
                public void onConversionDataSuccess(Map<String, Object> conversionData) {
                    String mediaSource = (String) conversionData.get("media_source");
                    SharePreferenceUtils.setOrganicValue(getApplicationContext(), mediaSource == null || mediaSource.isEmpty() || mediaSource.equals("organic"));
                }

                @Override
                public void onConversionDataFail(String s) {
                    // Handle conversion data failure
                }

                @Override
                public void onAppOpenAttribution(Map<String, String> map) {
                    // Handle app open attribution
                }

                @Override
                public void onAttributionFailure(String s) {
                    // Handle attribution failure
                }
            });
        }

        ConsentHelper consentHelper = ConsentHelper.getInstance(this);
        if (!consentHelper.canLoadAndShowAds()) {
            consentHelper.reset();
        }
        consentHelper.obtainConsentAndShow(this, () -> {
            Admob.getInstance().loadSplashInterAds2(SplashActivity.this, getString(R.string.inter_splash), 3000, interCallback);
        });
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}