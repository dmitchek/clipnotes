package com.clipnotes.activity;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clipnotes.R;

/**
 * Created by dave on 12/13/14.
 */
public class Results extends Activity {

    private SpellCheckerSession mScs;
    private String[] mResults;
    private ViewGroup mResultsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        Intent intent = getIntent();

        if (intent.hasExtra("results"))
            mResults = intent.getStringArrayExtra("results");

        Button retakeBtn = (Button) findViewById(R.id.retake);
        retakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Capture.class));
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        mResultsView = (LinearLayout) findViewById(R.id.results);
        for (int i = 0; i < mResults.length; i++) {
            EditText result = (EditText) LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.result_text, null);

            String resultStr = mResults[i];

            String[] resultsArray = resultStr.split(" ");

            TextInfo[] strings = new TextInfo[1];

            for (int str = 0; str < resultsArray.length; str++) {
                strings[0] = new TextInfo(resultStr);
            }

            result.setText(mResults[i]);
            Log.v("Results", "is suggestion enabled? " + result.isSuggestionsEnabled());
            mResultsView.addView(result);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScs != null) {
            mScs.close();
        }
    }
}
