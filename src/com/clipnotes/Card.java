package com.clipnotes;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by dmitchell on 12/14/14.
 */
public class Card extends Fragment {

    private String mFront;
    private String mBack;

    private final String FRONT = "FRONT";
    private final String BACK = "BACK";

    private String mFace;

    private TextView mCardText;
    private LinearLayout mCard;

    private View.OnClickListener mClickCard;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){

        //here is your arguments
        Bundle bundle=getArguments();

        if(bundle != null)
        {
            mFront = bundle.getString("front");
            mBack = bundle.getString("back");
        }

        mCard = (LinearLayout)inflater.inflate(R.layout.card, container, false);
        mCardText = (TextView)mCard.findViewById(R.id.card_text);
        mCardText.setText(mFront);

        mFace = FRONT;

        mClickCard = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flip();
            }
        };

        mCard.setOnClickListener(mClickCard);


        return mCard;
    }

    public void setFront(String front)
    {
        mFront = front;
    }

    public void setBack(String back)
    {
        mBack = back;
    }

    private void flip()
    {
        if(mFace == FRONT) {
            mCardText.setText(mBack);
            mFace = BACK;
        }
        else
        {
            mCardText.setText(mFront);
            mFace = FRONT;
        }

    }





}
