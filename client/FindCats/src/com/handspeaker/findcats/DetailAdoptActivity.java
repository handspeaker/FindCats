package com.handspeaker.findcats;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailAdoptActivity extends ActionBarActivity {
	
	private TextView tvCity;
	private TextView tvPostTime;
	private TextView tvAge;
	private TextView tvSex;
	private TextView tvHealth;
	private TextView tvAdoptCondition;
	private TextView tvHost;
	private TextView tvContact;
	private TextView tvMoreInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_for_adoptcats);
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);	
		
		tvCity=(TextView) findViewById(R.id.text_city);
		tvPostTime=(TextView) findViewById(R.id.text_posttime);
		tvAge=(TextView) findViewById(R.id.text_age);
		tvSex=(TextView) findViewById(R.id.text_sex);
		tvHealth=(TextView) findViewById(R.id.text_health);
		tvAdoptCondition=(TextView) findViewById(R.id.text_adopt_condition);
		tvHost=(TextView) findViewById(R.id.text_host);
		tvContact=(TextView) findViewById(R.id.text_contact);
		tvMoreInfo=(TextView) findViewById(R.id.text_adopt_moreinfo);
		
		tvCity.setVisibility(TextView.VISIBLE);
		tvPostTime.setVisibility(TextView.VISIBLE);
		tvAge.setVisibility(TextView.VISIBLE);
		tvSex.setVisibility(TextView.VISIBLE);
		tvHealth.setVisibility(TextView.VISIBLE);
		tvAdoptCondition.setVisibility(TextView.VISIBLE);
		tvHost.setVisibility(TextView.VISIBLE);
		tvContact.setVisibility(TextView.VISIBLE);
		tvMoreInfo.setVisibility(TextView.VISIBLE);		
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
