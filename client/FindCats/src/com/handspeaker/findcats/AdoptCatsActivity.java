package com.handspeaker.findcats;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class AdoptCatsActivity extends ActionBarActivity {

	public static final int ADD_ADOPTION = 0;
	public static final int DETAIL_ADOPTION = 1;
	
	private Spinner spinCity;
	private List<String> listCity;
	private ArrayAdapter<String> adapterCity;

	private ListView listViewTitle;
	private ArrayAdapter<String> adapterTitle;
	private List<String> listTitle;

	private boolean isLoading;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adoptcats);
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		isLoading = true;
		listCity = new ArrayList<String>();
		listCity.add("北京");
		listCity.add("上海");
		listCity.add("深圳");
		listCity.add("福州");
		listCity.add("厦门");
		spinCity = (Spinner) findViewById(R.id.spinner_city);
		listViewTitle = (ListView) findViewById(R.id.listview_adopt);

		adapterCity = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listCity);
		adapterCity
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinCity.setAdapter(adapterCity);
		
		listTitle = new ArrayList<String>();
		listTitle.add("test1");
		listTitle.add("test2");
		listTitle.add("test3");
		adapterTitle = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, listTitle);
		listViewTitle.setAdapter(adapterTitle);
		listViewTitle.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("pos", position);
				intent.setClass(AdoptCatsActivity.this, DetailAdoptActivity.class);
				startActivityForResult(intent, DETAIL_ADOPTION);
			}
		});
		
		
		loadData();
	}

	/**
	 * 添加新的收养信息
	 */
	public void addNewPost() {
		Intent intent = new Intent();
		intent.setClass(AdoptCatsActivity.this, NewAdoptActivity.class);
		startActivityForResult(intent, ADD_ADOPTION);
	}

	public void loadData() {
		isLoading = false;
		Toast.makeText(this, "load data", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.findcats_actionbar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.action_add:
			addNewPost();
			return true;
		case R.id.action_refresh:
			// call loadData to get data from server
			if (!isLoading) {
				loadData();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
