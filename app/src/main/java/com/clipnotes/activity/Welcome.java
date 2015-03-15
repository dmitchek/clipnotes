package com.clipnotes.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.clipnotes.R;
import com.clipnotes.flashcard.activity.CardCreator;
//import com.googlecode.tesseract.android.TessBaseAPI;


public class Welcome extends Activity {

    private View.OnClickListener _doOcrClick;
    private String _path = "";
    private TextView _errorTxt;
    private TextView _resultTxt;
    private ProgressBar _spinner;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        Button capture = (Button)findViewById(R.id.capture_new);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Capture.class));
            }
        });

        Button flashCards = (Button)findViewById(R.id.flash_cards);
        flashCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FlashCardActivity.class));
            }
        });

        Button cardCreator = (Button)findViewById(R.id.card_creator);
        cardCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CardCreator.class));
            }
        });

    }



}
