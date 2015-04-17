package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diallo on 21/03/14.
 */
public class CardItemImplImpl implements CardItemImpl {
    @Override
    public List<CardItem> findAll() {
        ArrayList<CardItem> items = new ArrayList<CardItem>();
        for(int i=1; i<= 10000; i++){
            int index = i % 5 != 0 ? i % 5 : 1;
            items.add(new CardItem(i%5, i));
        }
        return items;
    }
}
