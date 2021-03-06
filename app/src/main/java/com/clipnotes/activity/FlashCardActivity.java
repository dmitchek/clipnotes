package com.clipnotes.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.clipnotes.BuildConfig;
import com.clipnotes.R;
import com.clipnotes.Card;
import com.clipnotes.flashcard.FlashCardsUtil;

import java.util.ArrayList;

/**
 * Created by dmitchell on 12/14/14.
 */
public class FlashCardActivity extends Activity {

    private int mCurCard = 0;
    TextView mCardText;
    TextView mCardNum;
    TextView mCardTitle;
    private FlashCardsUtil mCardUtil;

    private Card mCardObject;

    public FlashCardActivity()
    {
        /*mCards[0] = new CardData("This is the front of 1", "This is the back of 1");
        mCards[1] = new CardData("This is the front of 2", "This is the back of 2");
        mCards[2] = new CardData("This is the front of 3", "This is the back of 3");
        mCards[3] = new CardData("This is the front of 4", "This is the back of 4");*/

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.flash_cards);

        Button mPrev = (Button)findViewById(R.id.prev_card);
        Button mNext = (Button)findViewById(R.id.next_card);

        View.OnClickListener navButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.prev_card)
                    loadCard(mCurCard-1);
                else if(v.getId() == R.id.next_card)
                    loadCard(mCurCard+1);
            }
        };

        mPrev.setOnClickListener(navButtonListener);
        mNext.setOnClickListener(navButtonListener);

        mCardText = (TextView)findViewById(R.id.card_text);
        mCardTitle = (TextView)findViewById(R.id.card_title);
        mCardNum = (TextView)findViewById(R.id.card_num);

        mCardUtil = new FlashCardsUtil(getBaseContext());
        mCardUtil.createCard("test front", "text back", 0, 1, null, null);

        ArrayList<FlashCardsUtil.CardData> cards = mCardUtil.getCardsByCategory(0);
    }

    private void loadCard(int cardIndex)
    {
        /*if(cardIndex >= 0 && cardIndex < mCards.length)
        {
            Bundle bundle = new Bundle();

            bundle.putString("front", mCards[cardIndex].getFront());
            bundle.putString("back", mCards[cardIndex].getBack());

            Fragment newCard = new Card();
            newCard.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.card, newCard);
            transaction.addToBackStack(null);

            transaction.commit();

            String cardNum = Integer.toString(cardIndex+1);
            cardNum += " / ";
            cardNum += Integer.toString(mCards.length);
            mCardNum.setText(cardNum);

            mCurCard = cardIndex;
        }*/
    }


}
