package com.clipnotes.flashcard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.clipnotes.R;
import com.clipnotes.flashcard.data.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dave on 2/14/15.
 */
public class CardCreator extends Activity {

    ArrayList<Card> mCards;

    private class CardAdapter extends ArrayAdapter<Card>
    {
        CardAdapter(Context context, int resource, List<Card> cards)
        {
            super(context, resource, cards);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Card card = getItem(position);
            convertView = View.inflate(getContext(), R.layout.creator_card_item, null);

            TextView cardTitle = (TextView) convertView.findViewById(R.id.card_title);
            cardTitle.setText(card.getFront());

            TextView itemNum = (TextView) convertView.findViewById(R.id.card_num);
            itemNum.setText(Integer.toString(position+1) + ".");


            return convertView;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.card_creator);

        ListView cardList = (ListView)findViewById(R.id.cards_list);

        mCards = new ArrayList<>();
        mCards.add(Card.createCard("My Front Yo", "My back jack", null, null, "", "dave"));
        mCards.add(Card.createCard("My Front You", "My back jack 1", null, null, "", "dave"));
        mCards.add(Card.createCard("My Front Your", "My back jack 2", null, null, "", "dave"));

        CardAdapter cardsAdapter = new CardAdapter(getBaseContext(), R.id.card_num, mCards);
        //adapter.add(card);

        cardList.setAdapter(cardsAdapter);

        cardList.addFooterView(View.inflate(getBaseContext(), R.layout.creator_new_item, null));

        final Button createNewBtn = (Button)findViewById(R.id.create_new_card);

        final DialogInterface.OnClickListener createPopupOkBtn = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: something
            }
        };

        Button.OnClickListener createNewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CardCreator.this, NewCard.class);
                startActivity(intent);
            }
        };

        createNewBtn.setOnClickListener(createNewListener);
    }

}
