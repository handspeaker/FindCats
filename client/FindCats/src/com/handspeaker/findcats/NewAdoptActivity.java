package com.handspeaker.findcats;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class NewAdoptActivity extends ActionBarActivity{

	private Spinner spCity;
	private EditText etAge;
	private EditText etSex;
	private EditText etHealth;
	private EditText etAdoptCondition;
	private EditText etHost;
	private EditText etContact;
	private EditText etAdoptMoreInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_for_adoptcats);
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		spCity=(Spinner) findViewById(R.id.spin_city);
//		spCity.setAdapter(adapter);
		etAge=(EditText) findViewById(R.id.edit_age);
		etSex=(EditText) findViewById(R.id.edit_sex);
		etHealth=(EditText) findViewById(R.id.edit_health);
		etAdoptCondition=(EditText) findViewById(R.id.edit_adopt_condition);
		etHost=(EditText) findViewById(R.id.edit_host);
		etContact=(EditText) findViewById(R.id.edit_contact);
		etAdoptMoreInfo=(EditText) findViewById(R.id.edit_adopt_moreinfo);
		
		spCity.setVisibility(Spinner.VISIBLE);
		etAge.setVisibility(EditText.VISIBLE);
		etSex.setVisibility(EditText.VISIBLE);
		etHealth.setVisibility(EditText.VISIBLE);
		etAdoptCondition.setVisibility(EditText.VISIBLE);
		etHost.setVisibility(EditText.VISIBLE);
		etContact.setVisibility(EditText.VISIBLE);
		etAdoptMoreInfo.setVisibility(EditText.VISIBLE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detail_actionbar, menu);
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
