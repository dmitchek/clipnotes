package com.example.highlighter.activity;

import android.app.Activity;
import android.os.Bundle;
import com.example.highlighter.R;
import com.example.highlighter.Mask;


/**
 * Created by dave on 12/13/14.
 */
public class Capture extends Activity {

    Mask _mask;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);

        _mask = (Mask)findViewById(R.id.mask);

    }
}
