package com.calm.tick.tackrelax.minigame.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.calm.tick.tackrelax.minigame.R;
import com.calm.tick.tackrelax.minigame.base.BaseActivity;
import com.calm.tick.tackrelax.minigame.databinding.ActivityLanguageBinding;
import com.calm.tick.tackrelax.minigame.utils.SharePreferenceUtils;
import com.calm.tick.tackrelax.minigame.utils.SystemConfiguration;
import com.calm.tick.tackrelax.minigame.utils.SystemUtil;
import com.calm.tick.tackrelax.minigame.utils.language.ConstantLangage;
import com.calm.tick.tackrelax.minigame.utils.language.UILanguageCustom;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.mallegan.ads.callback.NativeCallback;
import com.mallegan.ads.util.Admob;


import java.util.Map;


public class LanguageActivity extends BaseActivity implements UILanguageCustom.OnItemClickListener {

    String codeLang = "";
    ActivityLanguageBinding binding;


    private boolean itemSelected = false;

    @Override
    public void bind() {
        SystemConfiguration.setStatusBarColor(this, R.color.transparent, SystemConfiguration.IconColor.ICON_DARK);
        SystemUtil.setLocale(this);
        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        boolean fromSettings = getIntent().getBooleanExtra("from_settings", false);


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

        if (fromSettings) {
//            binding.ivBack.setVisibility(View.VISIBLE);
            binding.frAds.setVisibility(View.GONE);

        }
//        binding.ivBack.setOnClickListener(v -> {
//            finish();
//        });

        setUpLayoutLanguage();

        binding.ivSelect.setOnClickListener(v -> {
            if (itemSelected) {
                SystemUtil.saveLocale(this, codeLang);
                if (fromSettings) {
                    finish();
                } else {
                    startActivity(new Intent(LanguageActivity.this, IntroActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(this, "Please choose a language to continue", Toast.LENGTH_LONG).show();

            }
        });
        binding.ivSelect.setVisibility(View.GONE);
        loadAds();
    }


    private void setUpLayoutLanguage() {
        binding.uiLanguage.upDateData(ConstantLangage.getLanguage1(), ConstantLangage.getLanguage2(), ConstantLangage.getLanguage3());
        binding.uiLanguage.setOnItemClickListener(this);
    }

    private void loadAds() {
        checkNextButtonStatus(false);
        Admob.getInstance().loadNativeAd(LanguageActivity.this, getString(R.string.native_language), new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                super.onNativeAdLoaded(nativeAd);
                NativeAdView adView = new NativeAdView(LanguageActivity.this);
                if (!SharePreferenceUtils.isOrganic(LanguageActivity.this)) {
                    adView = (NativeAdView) LayoutInflater.from(LanguageActivity.this).inflate(R.layout.layout_native_language_non_organic, null);
                } else {
                    adView = (NativeAdView) LayoutInflater.from(LanguageActivity.this).inflate(R.layout.layout_native_language, null);
                }
                binding.frAds.removeAllViews();
                binding.frAds.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
                checkNextButtonStatus(true);
            }

            @Override
            public void onAdFailedToLoad() {
                super.onAdFailedToLoad();
                binding.frAds.removeAllViews();
                checkNextButtonStatus(true);
            }

        });
    }

    public void loadAdsNativeLanguageSelect() {
        NativeAdView adView;
        if (SharePreferenceUtils.isOrganic(this)) {
            adView = (NativeAdView) LayoutInflater.from(this)
                    .inflate(R.layout.layout_native_language, null);
        } else {
            adView = (NativeAdView) LayoutInflater.from(this)
                    .inflate(R.layout.layout_native_language_non_organic, null);
        }
        checkNextButtonStatus(false);

        Admob.getInstance().loadNativeAd(LanguageActivity.this, getString(R.string.native_language_select), new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                binding.frAds.removeAllViews();
                binding.frAds.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);

                checkNextButtonStatus(true);
            }

            @Override
            public void onAdFailedToLoad() {
                binding.frAds.removeAllViews();
                checkNextButtonStatus(true);
            }
        });

    }

    private void checkNextButtonStatus(boolean isReady) {
        if (isReady) {
            binding.ivSelect.setVisibility(View.VISIBLE);
            binding.btnNextLoading.setVisibility(View.GONE);
        } else {
            binding.ivSelect.setVisibility(View.GONE);
            binding.btnNextLoading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClickListener(int position, boolean itemseleted) {
        this.itemSelected = itemseleted;
        if (itemseleted) {
            binding.ivSelect.setAlpha(1.0f);
        }
        loadAdsNativeLanguageSelect();
    }

    @Override
    public void onPreviousPosition(int pos) {

    }


}