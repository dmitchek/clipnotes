package com.clipnotes.activity;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.clipnotes.R;

/**
 * Created by dave on 12/13/14.
 */
public class Results extends Activity implements SpellCheckerSession.SpellCheckerSessionListener {

    private SpellCheckerSession mScs;
    private String[] mResults;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        Intent intent = getIntent();

        if(intent.hasExtra("results"))
            mResults = intent.getStringArrayExtra("results");

        Button retakeBtn = (Button)findViewById(R.id.retake);
        retakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Capture.class));
            }
        });


    }

    @Override
    public void onResume()
    {
        super.onResume();

        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        mScs = tsm.newSpellCheckerSession(null, null, this, true);

        LinearLayout resultsView = (LinearLayout)findViewById(R.id.results);
        for (int i = 0; i < mResults.length; i++)
        {
            TextView result = (TextView)LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.result_text, null);

            String resultStr = mResults[i];

            String[] resultsArray = resultStr.split(" ");

            TextInfo[] strings = new TextInfo[resultsArray.length];
            for(int str = 0; str < resultsArray.length; str++)
            {
                strings[str] = new TextInfo(resultsArray[str]);
            }
            mScs.getSentenceSuggestions(strings, 3);

            result.setText(mResults[i]);
            resultsView.addView(result);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScs != null) {
            mScs.close();
        }
    }

    @Override
    public void onGetSuggestions(final SuggestionsInfo[] arg0) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arg0.length; ++i) {
            // Returned suggestions are contained in SuggestionsInfo
            final int len = arg0[i].getSuggestionsCount();
            sb.append('\n');
            for (int j = 0; j < len; ++j) {
                sb.append("," + arg0[i].getSuggestionAt(j));
            }
            sb.append(" (" + len + ")");
        }
        /*runOnUiThread(new Runnable() {

            public void run() {
                mMainView.append(sb.toString());
            }
        });*/
    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] arg0) {
        // TODO Auto-generated method stub
    }
}
