package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.util.AttributeSet;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagefulllib.OpenImageCoverVideoPlayer;
import com.flyjingfish.openimagefulllib.ScaleOpenImageVideoPlayer;

public class FriendsVideoPlayer extends ScaleOpenImageVideoPlayer {

    public FriendsVideoPlayer(Context context) {
        super(context);
    }

    public FriendsVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        changeUiToNormal();
        mTextureViewContainer.setOnClickListener(null);
        mTextureViewContainer.setOnTouchListener(null);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_friends_player;
    }

}
