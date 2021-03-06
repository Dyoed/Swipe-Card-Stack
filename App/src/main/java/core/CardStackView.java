package core;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jecs.swipecardstack.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Updated by Dyoed on 04/17/2015
 *
 */
public class CardStackView extends FrameLayout {



    public interface CardStackListener<T> {
        void onCancelled();

        void onChoiceMade(int choice, T item);

        void onClick(T item);

        void onDoubleClick(T item);

        void onGetNewCards(int current, int total, int skippedItemCount);
    }

    public static final int NO = 0;
    public static final int YES = 1;
    public static final int SKIP = 2;

    private static final String TRANSLATIONX = "translationX";
    private static final String TRANSLATIONY = "translationY";
    public int gestureCount = 3;

    private static int STACK_SIZE = 3;
    private static int MAX_ANGLE_DEGREE = 20;
    private static final int BOUNCE_SPEED = 300;
    public static final int SKIP_THRESHHOLD = 80;
    public static final int YES_NO_THRESHHOLD = 30;
    private boolean isGoingBack;
    private CardAdapter mAdapter;
    private int lastCardIndex;
    private int mMinDragDistance;
    private int mMinAcceptDistance;
    private int mYMinAcceptDistance;

    private int mXDelta;
    private int mYDelta;

    protected LinkedList<View> mCards = new LinkedList<>();
    protected LinkedList<View> mRecycledCards = new LinkedList<>();


    private CardStackListener mCardStackListener;

    protected LinkedList<Object> mCardStack = new LinkedList<>();
    private int mXStart;
    private int mYStart;
    private View mBeingDragged;
    private CardTouchListener mCardTouchListener;
    Resources r = getContext().getResources();
    float translateStep = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

    private int mSkipCount;
    private boolean isGettingNewCards = false;
    private static int PAGINATION_THRESHOLD = 1;//Cards left before pagination is fired

    public static void setPaginationThreshhold(int PAGINATION_THRESHOLD) {
        CardStackView.PAGINATION_THRESHOLD = PAGINATION_THRESHOLD;
    }

    public int getSkipCount() {
        return mSkipCount;
    }


    public CardStackView(Context context) {
        super(context);
        setup();
    }

    public CardStackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public CardStackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {

        Resources r = getContext().getResources();
        mMinDragDistance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
        mMinAcceptDistance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, r.getDisplayMetrics());
        mYMinAcceptDistance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, r.getDisplayMetrics());

        if (isInEditMode()) {
//            mAdapter = new MockListAdapter(getContext());
        }

        lastCardIndex = 0;
    }

    public void setAdapter(CardAdapter adapter) {
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mSkipCount = 0;
                isGettingNewCards = false;
                super.onChanged();
            }

            @Override
            public void onInvalidated() {
                mSkipCount = 0;
                mRecycledCards.clear();
                mCards.clear();
                removeAllViews();
                lastCardIndex = 0;
                initializeStack();
                super.onInvalidated();
            }
        });
        mRecycledCards.clear();
        mCards.clear();
        removeAllViews();
        lastCardIndex = 0;

        initializeStack();
    }

    private void initializeStack() {
        int position = 0;
        for (; position < lastCardIndex + STACK_SIZE; position++) {

            if (position >= mAdapter.getCount()) {
                break;
            }

            Object item = mAdapter.getItem(position);
            mCardStack.offer(item);
            View card = mAdapter.getView(position, null, null);

            mCards.offer(card);

//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


            addView(card, 0);

        }
        mCardTouchListener = new CardTouchListener();
        lastCardIndex += position;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mBeingDragged != null) {
            mXDelta = (int) mBeingDragged.getTranslationX();
            mYDelta = (int) mBeingDragged.getTranslationY();
        }

        animateCards();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void animateCards() {
        int index = 0;
        Iterator<View> it = mCards.descendingIterator();
        while (it.hasNext() && index < 3) {
            View card = it.next();
            if (card == null) {
                break;
            }

            if (isTopCard(card)) {
                card.setOnTouchListener(mCardTouchListener);
            } else {
                card.setOnTouchListener(null);
            }

            if (index == 0 && adapterHasMoreItems()) {
                if (mBeingDragged != null) {
                    index++;
                    continue;
                }

                scaleAndTranslate(1, card);
            } else {
                scaleAndTranslate(index, card);
            }

            index++;
        }
    }

    private boolean adapterHasMoreItems() {
        return lastCardIndex < mAdapter.getCount();
    }

    private boolean isTopCard(View card) {
        return card == mCards.peek();
    }


    private boolean canAcceptChoice() {
        return Math.abs(mXDelta) > mMinAcceptDistance || (gestureCount > 2 && Math.abs(mYDelta) > mYMinAcceptDistance &&
                mYDelta < -mYMinAcceptDistance && getChoice() == SKIP);
    }

    private void scaleAndTranslate(int cardIndex, View view) {

        LinearInterpolator interpolator = new LinearInterpolator();

        if (view == mBeingDragged) {

            int sign = 1;
            if (mXDelta > 0) {
                sign = -1;
            }
            float progress = Math.min(Math.abs(mXDelta) / ((float) mMinAcceptDistance * 5), 1);
            float angleDegree = MAX_ANGLE_DEGREE * interpolator.getInterpolation(progress);

//            view.setRotation(sign * angleDegree);

            return;
        }

        float zoomFactor = 0;
        if (mBeingDragged != null) {
            float interpolation = 0;
            float distance = (float) Math.sqrt(mXDelta * mXDelta + mYDelta * mYDelta);
            float progress = Math.min(distance / mMinDragDistance, 1);
            interpolation = interpolator.getInterpolation(progress);
            interpolation = Math.min(interpolation, 1);
            zoomFactor = interpolation;
        }

        int position = STACK_SIZE - cardIndex - 1;

        float step = 0.025f;



        float scale = step * (position - zoomFactor);
        Log.d("","Scale:"+scale);
        float translate = translateStep * (position - zoomFactor);
        view.setTranslationY(0);
        view.setTranslationX(translate);
        view.setRotation(0);
        view.setScaleX(1f-scale);
        view.setScaleY(1f-scale);


        return;
    }

    public CardStackListener getCardStackListener() {
        return mCardStackListener;
    }

    public void setCardStackListener(CardStackListener cardStackListener) {
        mCardStackListener = cardStackListener;
    }


    private static class MockListAdapter extends BaseAdapter {

        List<String> mItems;

        Context mContext;

        public MockListAdapter(Context context) {
            mContext = context;
            mItems = new ArrayList<String>();
            for (int i = 1; i < 15; i++) {
                mItems.add(i + "");
            }
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView view = new ImageView(mContext);
            view.setImageResource(R.drawable.content_card_x_00);

            Resources r = mContext.getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());

            LayoutParams params = new LayoutParams(px, px);
            //view.setLayoutParams(params);

            return view;
        }
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx);
    }

    private float pxToDp(float px) {
        return px / getContext().getResources().getDisplayMetrics().density;
    }

    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public boolean onDoubleTap(MotionEvent e) {
            mCardStackListener.onDoubleClick(((CardItemView)mCards.peek()).getCardItem());
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            mCardStackListener.onClick(((CardItemView) mCards.peek()).getCardItem());
            return true;
        }
    });


    private class CardTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(final View view, MotionEvent event) {

            gestureDetector.onTouchEvent(event);

            if (!isTopCard(view) || isGoingBack) {
                return false;
            }

            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();

            final int action = event.getAction();
            switch (action) {

                case MotionEvent.ACTION_DOWN: {
                    mXStart = X;
                    mYStart = Y;
                    break;
                }
                case MotionEvent.ACTION_UP:
                    if (mBeingDragged == null) {
                        return false;
                    }


                    if (!canAcceptChoice()) {

                        animateCards();
                        AnimatorSet set = new AnimatorSet();
                        ObjectAnimator yTranslation = ObjectAnimator.ofFloat(mBeingDragged, TRANSLATIONY, 0);
                        ObjectAnimator xTranslation = ObjectAnimator.ofFloat(mBeingDragged, TRANSLATIONX, 0);
                        set.playTogether(
                                xTranslation,
                                yTranslation
                        );

                        set.setDuration(BOUNCE_SPEED).start();
                        set.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                isGoingBack = false;
                                View finalView = mBeingDragged;
                                mBeingDragged = null;
                                mXDelta = 0;
                                mYDelta = 0;
                                mXStart = 0;
                                mYStart = 0;
                                animateCards();

                                CardItemView item = (CardItemView) view;
                                item.onCancelled();
                                if (mCardStackListener != null && finalView != null) {
                                    mCardStackListener.onCancelled();
                                }

                            }
                        });

                        ValueAnimator.AnimatorUpdateListener onUpdate = new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mXDelta = (int) view.getTranslationX();
                                mYDelta = (int) view.getTranslationY();
                                animateCards();
                            }
                        };

                        yTranslation.addUpdateListener(onUpdate);
                        xTranslation.addUpdateListener(onUpdate);

                        set.start();
                        isGoingBack = true;

                    } else {
                        final View last = mCards.poll();



                        int sign;
                        final int choice = getChoice();
                        String translation = choice == SKIP ? TRANSLATIONY : TRANSLATIONX;
                        if (translation.equals(TRANSLATIONY)) {
                            sign = -1;
                        } else {
                            sign = mXDelta > 0 ? +1 : -1;
                        }

                        mBeingDragged = null;
                        mXDelta = 0;
                        mYDelta = 0;
                        mXStart = 0;
                        mYStart = 0;

                        ObjectAnimator animation = ObjectAnimator.ofFloat(last, translation, sign * 1000)
                                .setDuration(150);
                        animation.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                CardItemView item = (CardItemView) view;
                                item.onChoiceMade(getStackChoice());

                                if (mCardStackListener != null) {
                                    mCardStackListener.onChoiceMade(choice, item.getCardItem());
                                }

                                if(choice == SKIP){
                                   mSkipCount++;
                                }

                                recycleView(last);

//                                final ViewGroup parent = (ViewGroup) view.getParent();
//                                if (null != parent) {
//                                    parent.removeView(view);
//                                    parent.addView(view, 0);
//                                }

                                View recycled = getRecycledOrNew();
                                if (recycled != null) {
//                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//                            params.addRule(RelativeLayout.CENTER_IN_PARENT);

                                    mCards.offer(recycled);
                                    addView(recycled, 0);
                                }

                                last.setScaleX(1);
                                last.setScaleY(1);
                                setTranslationY(0);
                                setTranslationX(0);
//                                animateCards();

                                if(mAdapter.getCount() - lastCardIndex <= PAGINATION_THRESHOLD && !isGettingNewCards){
                                    Log.d("","Total:"+mAdapter.getCount()+" Current Position:"+ (lastCardIndex - STACK_SIZE) +
                                            " " + "isLoading: "+isGettingNewCards);
                                    isGettingNewCards = true;
                                    mCardStackListener.onGetNewCards(lastCardIndex, mAdapter.getCount(), getSkipCount());
                                }
                            }
                        });
                        animation.start();
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    int choice = getChoice();
                    float progress = getXStackProgress();
                    view.setTranslationX(X - mXStart);
                    view.setTranslationY(Y - mYStart);
                    mXDelta = isGoingBack ? 0 : (X - mXStart);
                    mYDelta = Y - mYStart;
                    mBeingDragged = view;
                    animateCards();
                    CardItemView item = (CardItemView) view;
                    if (choice == SKIP) {
                        item.onUpdateProgress(choice, getYStackProgress(), mXDelta, mYDelta, gestureCount);
                    } else {
                        item.onUpdateProgress(choice, progress, mXDelta, mYDelta, gestureCount);
                    }
                    break;
            }
            return true;
        }

    }

    private void recycleView(View last) {
        ((ViewGroup) last.getParent()).removeView(last);
        mRecycledCards.offer(last);
    }

    private View getRecycledOrNew() {
        if (adapterHasMoreItems()) {
            View view = mRecycledCards.poll();
            view = mAdapter.getView(lastCardIndex++, view, null);

            return view;
        } else {
            return null;
        }
    }

    private boolean getStackChoice() {
        boolean choiceBoolean = false;
        if (mXDelta > 0) {
            choiceBoolean = true;
        }
        return choiceBoolean;
    }

    private int getChoice() {
        int stackChoice;
        if (mYDelta <= -120 && gestureCount > 2) {
            stackChoice = SKIP;
        } else {
            stackChoice = mXDelta > 0 ? YES : NO;
        }
        return stackChoice;
    }

    private float getXStackProgress() {
        LinearInterpolator interpolator = new LinearInterpolator();
        float progress = Math.min(Math.abs(mXDelta) / ((float) mMinAcceptDistance), 1);
        progress = interpolator.getInterpolation(progress);
        return progress;
    }

    private float getYStackProgress() {
        LinearInterpolator interpolator = new LinearInterpolator();
        float progress = Math.min(Math.abs(mYDelta) / ((float) mYMinAcceptDistance), 1);
        progress = interpolator.getInterpolation(progress);
        return progress;
    }

}
