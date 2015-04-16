package com.lal.focusprototype.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lal.focusprototype.app.views.CardItemView;

import java.util.List;

/**
 * Created by diallo on 21/03/14.
 */
public class CardAdapter extends BaseAdapter {

    List<CardItem> mItems;

    CardItemImpl mItemsFinder;

    private Context mContext;
    public CardAdapter(Context context){
        mContext = context;
        mItemsFinder = new CardItemImplImpl();
        initAdapter();
    }


    void initAdapter() {
        mItems = mItemsFinder.findAll();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CardItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CardItemView personItemView;
        if (convertView == null) {
            personItemView = new CardItemView(mContext);
        } else {
            personItemView = (CardItemView) convertView;
        }

        personItemView.bind(getItem(position));

        return personItemView;
    }
}
