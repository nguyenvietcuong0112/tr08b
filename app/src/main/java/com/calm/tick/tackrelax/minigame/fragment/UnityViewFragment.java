package com.calm.tick.tackrelax.minigame.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.fragment.app.Fragment;
import com.unity3d.player.UnityPlayer;

public class UnityViewFragment extends Fragment {
    private UnityPlayer unityPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout layout = new FrameLayout(requireContext());
        unityPlayer = new UnityPlayer(requireActivity());
        layout.addView(unityPlayer.getView(),
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        unityPlayer.requestFocus();
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (unityPlayer != null) {
            unityPlayer.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (unityPlayer != null) {
            unityPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unityPlayer != null) {
            unityPlayer.quit();
        }
    }
}
