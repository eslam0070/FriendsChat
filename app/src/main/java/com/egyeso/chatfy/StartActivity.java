package com.egyeso.chatfy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.egyeso.chatfy.Activity.LoginActivity;
import com.egyeso.chatfy.Activity.RegisterActivity;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout l1;
    Animation uptodown;
    Button button,button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        button = findViewById(R.id.intent_reg);
        button2 = findViewById(R.id.intent_log);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        l1 = findViewById(R.id.l1);
        uptodown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.uptodown);
        l1.setAnimation(uptodown);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.intent_reg:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.intent_log:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
