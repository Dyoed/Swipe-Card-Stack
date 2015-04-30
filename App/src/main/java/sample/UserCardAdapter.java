package sample;

import android.content.Context;

import core.CardAdapter;
import core.CardItemView;

public class UserCardAdapter extends CardAdapter<CardItem> {

    CardItemImpl mItemsFinder;

    public UserCardAdapter(Context context){
        super(context);
    }

    @Override
    public void initAdapter() {
        mItemsFinder = new CardItemImplImpl();
        setItems(mItemsFinder.findAll());
    }

    @Override
    public CardItemView createView() {
        return new UserCardItemView(getContext());
    }
}
