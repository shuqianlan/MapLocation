package com.ilifesmart.utils;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public final class Utils {

	public static void startActivity(Context context, Class<? extends AppCompatActivity> clazz) {
		Intent i = new Intent(context, clazz);
		context.startActivity(i);
	}
}
