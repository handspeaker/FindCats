package com.handspeaker.findcats;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
/**
 *  用于显示流浪猫喂食点的详细内容
 * @author SongQi
 * 
 */
public class DetailLocationActivity extends ActionBarActivity {
	
	private TextView tvAddrInfo;
	private TextView tvUpdateTime;
	private TextView tvCatNum;
	private TextView tvBabyNum;
	private TextView tvFood;
	private TextView tvCondition;
	private TextView tvAddrMoreInfo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_for_findcats);
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		tvAddrInfo=(TextView) findViewById(R.id.text_addrinfo);
		tvUpdateTime=(TextView) findViewById(R.id.text_updatetime);
		tvCatNum=(TextView) findViewById(R.id.text_catnum);
		tvBabyNum=(TextView) findViewById(R.id.text_babynum);
		tvFood=(TextView) findViewById(R.id.text_food);
		tvCondition=(TextView) findViewById(R.id.text_condition);
		tvAddrMoreInfo=(TextView) findViewById(R.id.text_moreinfo);
		
		tvAddrInfo.setVisibility(TextView.VISIBLE);
		tvUpdateTime.setVisibility(TextView.VISIBLE);
		tvCatNum.setVisibility(TextView.VISIBLE);
		tvBabyNum.setVisibility(TextView.VISIBLE);
		tvFood.setVisibility(TextView.VISIBLE);
		tvCondition.setVisibility(TextView.VISIBLE);
		tvAddrMoreInfo.setVisibility(TextView.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.simple_actionbar, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
