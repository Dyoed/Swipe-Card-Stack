package sample;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.jecs.swipecardstack.R;

import core.CardItemView;

public class UserCardItemView extends CardItemView<CardItem>  {

    ImageView picture;
    TextView id;

    public UserCardItemView(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        inflate(getContext(), R.layout.feed_item, this);
        picture = (ImageView) findViewById(R.id.picture);
        id = (TextView) findViewById(R.id.id_textView);
        ok = (TextView) findViewById(R.id.ok);
        no = (TextView) findViewById(R.id.no);
        skip = (TextView) findViewById(R.id.skip);
    }

    @Override
    public void displayCard() {
        int resource = getResources().getIdentifier(
                "content_card_x_0" + getCardItem().getId(),
                "drawable", getContext().getPackageName());

        loadPicture(resource);
        id.setText(getCardItem().toString());
    }


    void loadPicture(int id) {
        Drawable drawable = getResources().getDrawable(id);
        setPicture(drawable);
    }

    void setPicture(Drawable drawable) {
        picture.setImageDrawable(drawable);
    }

}
