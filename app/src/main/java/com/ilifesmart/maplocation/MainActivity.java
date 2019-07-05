package com.ilifesmart.maplocation;

import android.os.Bundle;
import android.view.View;

import com.ilifesmart.utils.Utils;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
	}

	@OnClick({R.id.location, R.id.map})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.location:
				break;
			case R.id.map:
				Utils.startActivity(this, MapActivity.class);
				break;
		}
	}
}
