package org.bitren.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mobclick.android.MobclickAgent;
import com.mobclick.android.ReportPolicy;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.layout_main);
		super.onCreate(savedInstanceState);
		
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.setDefaultReportPolicy(this, ReportPolicy.BATCH_AT_LAUNCH);
		MobclickAgent.setSessionContinueMillis(60000);
		
		Intent intent = new Intent();
		intent.setClass(this, ContactActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
}
