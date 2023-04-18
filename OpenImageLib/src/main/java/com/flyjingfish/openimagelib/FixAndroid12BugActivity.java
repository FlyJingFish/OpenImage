package com.flyjingfish.openimagelib;

import androidx.appcompat.app.AppCompatActivity;

public class FixAndroid12BugActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        finishAfterTransition();
    }
}
