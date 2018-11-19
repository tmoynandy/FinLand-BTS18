/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.finland.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkManager {

    public static boolean isUserOnline(Context context) {
        @SuppressLint("ServiceCast") ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            } else {
                Log.i("NetworkManger", "Connectivity Manager is null");
            }
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        } catch (NullPointerException exception) {
            Log.i("NetworkManger", "Exception: " + exception.getMessage());
            return false;
        }
    }
}
