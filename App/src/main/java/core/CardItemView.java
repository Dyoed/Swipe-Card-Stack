package core;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class CardItemView<T> extends RelativeLayout {


    protected TextView ok;
    protected TextView no;
    protected TextView skip;
    private T mCardItem;
    private int lastChoice;

    public T getCardItem() {
        return mCardItem;
    }

    public void setCardItem(T mCardItem) {
        this.mCardItem = mCardItem;
    }

    public CardItemView(Context context) {
        super(context);
        initView();
    }

    /**
     * Inflate the CardView with layout
     */
    public abstract void initView();

    public void bind(T item) {
        mCardItem = item;
        return;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mCardItem != null) {
            displayCard();
        }
    }

    /**
     * Inflate layout and inject to class
     */
    public abstract void displayCard();

    public void onUpdateProgress(boolean positif, float percent) {
        if (positif) {
            ok.setAlpha(percent);
        } else {
            no.setAlpha(percent);
        }
    }

    public void onUpdateProgress(int action, float percent) {
        switch (action){
            case CardStackView.NO:
                no.setAlpha(percent);
                skip.setAlpha(Math.abs(percent-1));
                break;
            case CardStackView.YES:
                ok.setAlpha(percent);
                skip.setAlpha(Math.abs(percent-1));
                break;
            case CardStackView.SKIP:
                skip.setAlpha(percent);
//                no.setAlpha(percent);
//                ok.setAlpha(percent);
                break;
            default:
                break;
        }

    }

    private void updateSkip(int yDelta, float percent, int gestureCount){
        if(yDelta >= -CardStackView.SKIP_THRESHHOLD){
            skip.setAlpha(0);
        }
        else if(gestureCount > 2){
            skip.setAlpha(Math.abs(percent-1)- 0.50f);
        }
    }

    public void onUpdateProgress(int action, float percent, int xDelta, int yDelta, int gestureCount) {
        switch (action){
            case CardStackView.NO:
                no.setAlpha(percent);
                ok.setAlpha(0);
                updateSkip(yDelta, percent, gestureCount);
                if(lastChoice == CardStackView.SKIP){
                    no.setAlpha(0);
                }
                break;
            case CardStackView.YES:
                ok.setAlpha(percent);
                updateSkip(yDelta, percent, gestureCount);
                if(lastChoice == CardStackView.SKIP){
                    ok.setAlpha(0);
                }
                break;
            case CardStackView.SKIP:
                if(gestureCount > 2){

                }
                skip.setAlpha(percent);
                if(yDelta <= -CardStackView.SKIP_THRESHHOLD){
                    if(xDelta > CardStackView.YES_NO_THRESHHOLD){
                        ok.setAlpha(Math.abs(percent-1) - 0.30f);
                    }
                    else if(xDelta < -CardStackView.YES_NO_THRESHHOLD){
                        no.setAlpha(Math.abs(percent-1) - 0.30f);
                    }
//                    no.setAlpha(0);
//                    ok.setAlpha(0);
                }
//                else{
//                    if(xDelta > CardStackView.YES_NO_THRESHHOLD){
//                        ok.setAlpha(Math.abs(percent-1) - 0.30f);
//                    }
//                    else if(xDelta < -CardStackView.YES_NO_THRESHHOLD){
//                        no.setAlpha(Math.abs(percent-1) - 0.30f);
//                    }
//                }
                break;
            default:
                break;
        }
        lastChoice = action;
    }

    public void onCancelled() {
        ok.setAlpha(0);
        no.setAlpha(0);
        skip.setAlpha(0);
    }

    public void onChoiceMade(boolean choice) {
        ok.setAlpha(0);
        no.setAlpha(0);
        skip.setAlpha(0);
    }
}
