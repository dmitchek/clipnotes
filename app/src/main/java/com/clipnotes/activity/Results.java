package com.clipnotes.activity;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.clipnotes.R;

/**
 * Created by dave on 12/13/14.
 */
public class Results extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        Intent intent = getIntent();
        String[] results;

        if(intent.hasExtra("results")) {
            results = intent.getStringArrayExtra("results");

            LinearLayout resultsView = (LinearLayout)findViewById(R.id.results);
            for (int i = 0; i < results.length; i++)
            {
                TextView result = (TextView)LayoutInflater.from(getApplicationContext())
                                                          .inflate(R.layout.result_text, null);

                result.setText(results[i]);
                resultsView.addView(result);
            }
        }

        Button retakeBtn = (Button)findViewById(R.id.retake);
        retakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Capture.class));
            }
        });
    }
}
