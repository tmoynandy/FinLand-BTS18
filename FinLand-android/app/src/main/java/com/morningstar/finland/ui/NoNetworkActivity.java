/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.finland.ui;

import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.morningstar.finland.R;

import androidx.appcompat.app.AppCompatActivity;

public class NoNetworkActivity extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network);

        lottieAnimationView = findViewById(R.id.networkErrorAnim);
    }
}
