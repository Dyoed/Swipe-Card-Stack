package sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.jecs.swipecardstack.R;

import java.util.ArrayList;

import core.CardStackView;

public class MainActivity extends Activity {
    CardStackView mCardStack;

    private Handler handler;
    private UserCardAdapter mCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    public void initialize(){
        mCardStack = (CardStackView) findViewById(R.id.mCardStack);
        handler = new Handler(Looper.getMainLooper());
        doInitialize();

        mCardStack.setCardStackListener(new CardStackView.CardStackListener<CardItem>() {
            @Override
            public void onCancelled() {

            }

            @Override
            public void onChoiceMade(int choice, CardItem item) {
                Toast.makeText(getApplicationContext(), "Choice:" + choice, Toast.LENGTH_SHORT).show();
                Log.d("", item.toString() + " Choice:" + choice);
            }

            @Override
            public void onClick(CardItem item) {
                Log.d("", "Clicked " + item.toString());
            }

            @Override
            public void onDoubleClick(CardItem item) {
                Log.d("", item.getId()+"");
            }

            @Override
            public void onGetNewCards(int current, int total, int skippedItemCount) {
                ArrayList<CardItem> items = new ArrayList<CardItem>();
                for (int i = total; i <= total+25; i++) {
                    int index = i % 5 != 0 ? i % 5 : 1;
                    items.add(new CardItem(i % 5, i));
                }
                Log.d("","Get new cards"+items.size());
                mCardAdapter.addAll(items);
            }
        });


        return;
    }

    private void doInitialize() {
        mCardAdapter = new UserCardAdapter(getApplicationContext());
        mCardStack.setAdapter(mCardAdapter);
    }



}
