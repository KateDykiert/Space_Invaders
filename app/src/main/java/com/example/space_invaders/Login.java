package com.example.space_invaders;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.graphics.Typeface;

public class Login extends AppCompatActivity {

    TextView myView;
    Typeface myFont;
    TextView B;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myView = (TextView) findViewById(R.id.SignIn);
        myFont = Typeface.createFromAsset(this.getAssets(), "Fonts/ca.ttf");
        myView.setTypeface(myFont);

        B = (TextView) findViewById(R.id.buttonLogin);
        //B.setTypeface(myFont);

    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }
}
