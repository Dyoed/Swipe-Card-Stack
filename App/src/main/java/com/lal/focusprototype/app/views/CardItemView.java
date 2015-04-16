package com.lal.focusprototype.app.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lal.focusprototype.app.CardItem;
import com.lal.focusprototype.app.R;

/**
 * Created by diallo on 21/03/14.
 */
public class CardItemView extends RelativeLayout implements CardStackView.CardStackListener {

    ImageView picture;

    TextView id;

    TextView ok;

    TextView no;

    private CardItem mCardItem;

    public CardItemView(Context context) {
        super(context);
        inflate(getContext(), R.layout.feed_item, this);
        picture = (ImageView) findViewById(R.id.picture);
        id = (TextView) findViewById(R.id.id_textView);
        ok = (TextView) findViewById(R.id.ok);
        no = (TextView) findViewById(R.id.no);
    }

    public void bind(CardItem item) {
        mCardItem = item;

        return;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mCardItem != null) {
            int resource = getResources().getIdentifier(
                    "content_card_x_0" + mCardItem.getId(),
                    "drawable", getContext().getPackageName());

            loadPicture(resource);

            id.setText(mCardItem.toString());
        }
    }

    public CardItem getCardItem() {
        return mCardItem;
    }

    void loadPicture(int id) {
        Drawable drawable = getResources().getDrawable(id);

        setPicture(drawable);
    }

    void setPicture(Drawable drawable) {
        picture.setImageDrawable(drawable);
    }

    @Override
    public void onUpdateProgress(boolean positif, float percent, View view) {
        if (positif) {
            ok.setAlpha(percent);
        } else {
            no.setAlpha(percent);
        }
    }

    @Override
    public void onCancelled(View beingDragged) {
        ok.setAlpha(0);
        no.setAlpha(0);
    }

    @Override
    public void onChoiceMade(boolean choice, View beingDragged) {
        ok.setAlpha(0);
        no.setAlpha(0);
    }
}
