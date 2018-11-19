/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.finland.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.morningstar.finland.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SlideFragment extends Fragment {

    private View view;
    private ArrayList<Integer> images;
    private int pos;
    private ImageView imageView;

    public SlideFragment() {
        images = new ArrayList<>();
        images.add(R.drawable.diptangsu);
        images.add(R.drawable.sujoy);
        images.add(R.drawable.tanumoy);
        images.add(R.drawable.koustav);
    }

    public static SlideFragment newInstance(int position) {
        SlideFragment slideFragment = new SlideFragment();
        slideFragment.pos = position;
        return slideFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_slide, container, false);


        imageView = view.findViewById(R.id.slideImage);

        switch (pos) {
            case 0:
                imageView.setImageDrawable(getResources().getDrawable(images.get(pos)));
                break;
            case 1:
                imageView.setImageDrawable(getResources().getDrawable(images.get(pos)));
                break;
            case 2:
                imageView.setImageDrawable(getResources().getDrawable(images.get(pos)));
                break;
            case 3:
                imageView.setImageDrawable(getResources().getDrawable(images.get(pos)));
                break;
            default:
                break;
        }
        return view;
    }
}
