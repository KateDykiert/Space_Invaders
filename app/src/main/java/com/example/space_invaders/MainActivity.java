package com.example.space_invaders;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    TextView myView;
    Typeface myFont;
    TextView B1;
    TextView B2;
    TextView B3;
    private Button login;
    private Button Register;
    private Button Guest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fonts
        myView = (TextView) findViewById(R.id.GameName);
        myFont = Typeface.createFromAsset(this.getAssets(), "Fonts/ca.ttf");
        myView.setTypeface(myFont);
        B1 = (TextView) findViewById(R.id.b_signin);
        B2 = (TextView) findViewById(R.id.b_signup);
        B3 = (TextView) findViewById(R.id.b_guestmode);
        B1.setTypeface(myFont);
        B2.setTypeface(myFont);
        B3.setTypeface(myFont);

        //actions
        login = (Button) findViewById(R.id.b_signin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });

        Register = (Button) findViewById(R.id.b_signup);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });

        Guest = (Button) findViewById(R.id.b_guestmode);
        Guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGuest();
            }
        });
    }

    public void openLogin()
    {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void openRegister()
    {
        Intent intent = new Intent(this, SingUp.class);
        startActivity(intent);
    }

    public void openGuest()
    {
        //Intent intent = new Intent(this, SingUp.class);
        //startActivity(intent);
    }
}