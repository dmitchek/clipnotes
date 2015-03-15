package com.clipnotes.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.clipnotes.R;
import com.clipnotes.flashcard.data.Card;
import com.clipnotes.flashcard.FlashCardsUtil;
import com.clipnotes.flashcard.data.Stack;
import com.clipnotes.flashcard.fragment.CardFragment;

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

    private Card mCard;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.flash_cards);

        Button mPrev = (Button)findViewById(R.id.prev_card);
        Button mNext = (Button)findViewById(R.id.next_card);

        /*View.OnClickListener navButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.prev_card)
                    loadCard(mCurCard-1);
                else if(v.getId() == R.id.next_card)
                    loadCard(mCurCard+1);
            }
        };*/

        //mPrev.setOnClickListener(navButtonListener);
        //mNext.setOnClickListener(navButtonListener);

        mCardText = (TextView)findViewById(R.id.card_text);
        mCardTitle = (TextView)findViewById(R.id.card_title);
        mCardNum = (TextView)findViewById(R.id.card_num);

        mCard = Card.createCard("test front", "text back", null, null, "", "");

        Card.insertCard(getApplicationContext(), mCard);

        Card cardFromDB = Card.getCardByUUID(getApplicationContext(), mCard.getUUID());

        //ArrayList<FlashCardsUtil.CardData> cards = mCardUtil.getCardsByCategory(0);
        loadCard(cardFromDB);

        Stack stack = Stack.createStack("The Title of My Stack", "1234", "");

        Stack.insertStack(getApplicationContext(), stack);

        Stack stackFromDB = Stack.getStackByUUID(getApplicationContext(), stack.getUUID());
    }

    private void loadCard(Card card)
    {

            Bundle bundle = new Bundle();

            bundle.putString("front", card.getFront());
            bundle.putString("back", card.getBack());

            Fragment newCard = new CardFragment();
            newCard.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.card, newCard);
            transaction.addToBackStack(null);

            transaction.commit();

            /*String cardNum = Integer.toString(cardIndex+1);
            cardNum += " / ";
            cardNum += Integer.toString(mCards.length);
            mCardNum.setText(cardNum);

            mCurCard = cardIndex;*/

    }
}
