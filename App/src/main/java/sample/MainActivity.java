package sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.jecs.swipecardstack.R;

import core.CardStackView;

public class MainActivity extends Activity {
    CardStackView mCardStack;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    public void initialize(){
        mCardStack = (CardStackView) findViewById(R.id.mCardStack);
        handler = new Handler();
        doInitialize();

        mCardStack.setCardStackListener(new CardStackView.CardStackListener<CardItem>() {
            @Override
            public void onCancelled() {

            }

            @Override
            public void onChoiceMade(int choice, CardItem item) {
                Toast.makeText(getApplicationContext(), "Choice:"+choice, Toast.LENGTH_SHORT).show();
                Log.d("", item.toString()+" Choice:"+choice);
            }

            @Override
            public void onClick(CardItem item) {
                Log.d("", "Clicked "+item.toString());
            }

            @Override
            public void onDOubleClick(CardItem item) {

            }
        });

        return;
    }

    private void doInitialize() {
        mCardStack.setAdapter(new UserCardAdapter(getApplicationContext()));
    }



}
