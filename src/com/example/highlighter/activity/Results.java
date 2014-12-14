package com.example.highlighter.activity;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.highlighter.R;

/**
 * Created by dave on 12/13/14.
 */
public class Results extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        TextView resultTxt = (TextView)findViewById(R.id.result_text);

        resultTxt.setText(R.string.result_demo_text);

        Button retakeBtn = (Button)findViewById(R.id.retake);
        retakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Capture.class));
            }
        });
    }
}
