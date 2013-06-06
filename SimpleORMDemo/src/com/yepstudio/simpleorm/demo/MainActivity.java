package com.yepstudio.simpleorm.demo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * 
 * @author zzljob@gmail.com
 * @date 2013-6-6
 *
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
