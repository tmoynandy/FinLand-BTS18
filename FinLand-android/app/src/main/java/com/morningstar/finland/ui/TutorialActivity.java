/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.finland.ui;

import android.os.Bundle;

import com.morningstar.finland.R;
import com.morningstar.finland.adapter.TutorialPagerAdapter;
import com.morningstar.finland.utility.DrawerUtils;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class TutorialActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;

    private TutorialPagerAdapter tutorialPagerAdapter;
    private SpringDotsIndicator springDotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        toolbar = findViewById(R.id.tutorialToolbar);
        toolbar.setTitle("Walkthrough");
        setSupportActionBar(toolbar);

        DrawerUtils.getDrawer(TutorialActivity.this, toolbar);

        viewPager = findViewById(R.id.viewPager);
        springDotsIndicator = findViewById(R.id.dotsIndicator);
        tutorialPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tutorialPagerAdapter);
        springDotsIndicator.setViewPager(viewPager);
    }
}
