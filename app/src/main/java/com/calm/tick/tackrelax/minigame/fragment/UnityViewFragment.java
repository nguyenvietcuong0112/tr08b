package com.calm.tick.tackrelax.minigame.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.calm.tick.tackrelax.minigame.R;
import com.calm.tick.tackrelax.minigame.activity.CustomUnityActivity;
import com.unity3d.player.UnityPlayer;

public class UnityViewFragment extends Fragment {
    private UnityPlayer unityPlayer;
    private FrameLayout frameContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout fragment
        View view = inflater.inflate(R.layout.fragment_unity, container, false);

        // Tìm FrameLayout để add UnityPlayer
        frameContainer = view.findViewById(R.id.frameContainer);

        // Tạo UnityPlayer theo chương trình
        unityPlayer = new UnityPlayer(requireActivity());

        // Đặt layout params cho UnityPlayer
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                300 // Chiều cao 300dp, điều chỉnh theo nhu cầu
        );
        unityPlayer.setLayoutParams(params);

        // Add UnityPlayer vào container
        frameContainer.addView(unityPlayer);

        // Gọi phương thức load màn Unity của bạn
        CustomUnityActivity.getInstance().handlePlayAction(getContext(), "play", true);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unityPlayer != null) {
            unityPlayer.quit();
        }
    }
}