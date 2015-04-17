package sample;

/**
 * Created by diallo on 21/03/14.
 */
public class CardItem {
    private int mId;
    private int mIndex;

    public CardItem(int i, int index) {
        this.mId = i;
        this.mIndex = index;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public String toString() {
        return "Card #"+mIndex;
    }
}
