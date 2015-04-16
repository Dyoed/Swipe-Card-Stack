package com.lal.focusprototype.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.lal.focusprototype.app.views.CardStackView;
import com.lal.focusprototype.app.views.FeedItemView;

public class MainActivity extends Activity {
    CardStackView mCardStack;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    public void initialize(){
        mCardStack = (CardStackView) findViewById(R.id.mCardStack);
        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void run() {
                final View splash = findViewById(R.id.splash);

                splash.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        splash.setVisibility(View.GONE);
                        doInitialize();
                    }
                }).setDuration(2000).start();

            }
        }, 0);

        mCardStack.setCardStackListener(new CardStackView.CardStackListener() {
            @Override
            public void onUpdateProgress(boolean choice, float percent, View view) {
                FeedItemView item = (FeedItemView)view;
                item.onUpdateProgress(choice, percent, view);
            }

            @Override
            public void onCancelled(View beingDragged) {
                FeedItemView item = (FeedItemView)beingDragged;
                item.onCancelled(beingDragged);
            }

            @Override
            public void onChoiceMade(boolean choice, View beingDragged) {
                FeedItemView item = (FeedItemView)beingDragged;
                item.onChoiceMade(choice, beingDragged);
            }
        });

        return;
    }

    private void doInitialize() {
        mCardStack.setAdapter(new FeedListAdapter(getApplicationContext()));
    }

    public Rect locateView(View view) {
        Rect loc = new Rect();
        int[] location = new int[2];
        if (view == null) {
            return loc;
        }
        view.getLocationOnScreen(location);

        loc.left = location[0];
        loc.top = location[1];
        loc.right = loc.left + view.getWidth();
        loc.bottom = loc.top + view.getHeight();
        return loc;
    }

}
