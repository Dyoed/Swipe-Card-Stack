package core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CardAdapter<T> extends BaseAdapter {

    private List<T> mItems;
    private Object mLock = new Object();

    public void setItems(List<T> mItems) {
        this.mItems = mItems;
    }

    private Context mContext;
    public CardAdapter(Context context){
        mContext = context;
        initAdapter();
    }

    public Context getContext(){
        return mContext;
    }

    protected abstract void initAdapter();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardItemView cardItemView;

        if (convertView == null) {
            cardItemView = createView();
        } else {
            cardItemView = (CardItemView) convertView;
        }

        cardItemView.bind(getItem(position));
        return cardItemView;
    }

    public void addAll(List<T> items){
        synchronized (mLock){
            mItems.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void replaceAll(List<T> items){
        synchronized (mLock){
            mItems = items;
            notifyDataSetInvalidated();
            notifyDataSetChanged();
        }
    }


    public void add(T item){
        synchronized (mLock){
            mItems.add(item);
            notifyDataSetChanged();
        }
    }

    public abstract CardItemView createView();
}
