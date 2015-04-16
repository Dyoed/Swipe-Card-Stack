package com.lal.focusprototype.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lal.focusprototype.app.views.FeedItemView;

import java.util.List;

/**
 * Created by diallo on 21/03/14.
 */
public class FeedListAdapter extends BaseAdapter {

    List<FeedItem> mItems;

    FeedItemFinder mItemsFinder;

    private Context mContext;
    public FeedListAdapter(Context context){
        mContext = context;
        mItemsFinder = new FeedItemFinderImpl();
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
    public FeedItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FeedItemView personItemView;
        if (convertView == null) {
            personItemView = new FeedItemView(mContext);
        } else {
            personItemView = (FeedItemView) convertView;
        }

        personItemView.bind(getItem(position));

        return personItemView;
    }
}
