package com.clipnotes.flashcard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.clipnotes.R;
import com.clipnotes.activity.Capture;

/**
 * Created by dave on 3/1/15.
 */
public class NewCard extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.creator_create_card);

        Button captureNew = (Button)findViewById(R.id.creator_new);
        Button.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startCapture = new Intent(NewCard.this, Capture.class);
                startActivityForResult(startCapture, 0);
            }
        };
        captureNew.setOnClickListener(clickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }
}
