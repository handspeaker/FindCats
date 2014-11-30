package com.handspeaker.findcats;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.handspeaker.network.PostRequest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewLocationActivity extends ActionBarActivity {
	
	private TextView lbUpdatetime;
	private EditText etAddrInfo;
	private EditText etCatNum;
	private EditText etBabyNum;
	private EditText etFood;
	private EditText etCondition;
	private EditText etMoreInfo;
	
	private PostRequest postRequest;
	
	private double latitude;
	private double longitude;
	
	private ProgressDialog progressDialog;
	
	private Handler handler=new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			Toast.makeText(NewLocationActivity.this, "信息上传成功！", Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_for_findcats);
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Intent intent = this.getIntent();
		String addr=intent.getStringExtra("address");
		latitude=intent.getDoubleExtra("latitude", -1.0);
		longitude=intent.getDoubleExtra("longitude",-1.0);
		
		lbUpdatetime=(TextView) findViewById(R.id.label_updatetime);
		etAddrInfo=(EditText) findViewById(R.id.edit_addrinfo);
		etCatNum=(EditText) findViewById(R.id.edit_catnum);
		etBabyNum=(EditText) findViewById(R.id.edit_babynum);
		etFood=(EditText) findViewById(R.id.edit_food);
		etCondition=(EditText) findViewById(R.id.edit_condition);
		etMoreInfo=(EditText) findViewById(R.id.edit_moreinfo);
		
		lbUpdatetime.setVisibility(TextView.GONE);
		etAddrInfo.setVisibility(EditText.VISIBLE);
		etCatNum.setVisibility(EditText.VISIBLE);
		etBabyNum.setVisibility(EditText.VISIBLE);
		etFood.setVisibility(EditText.VISIBLE);
		etCondition.setVisibility(EditText.VISIBLE);
		etMoreInfo.setVisibility(EditText.VISIBLE);
		
		etAddrInfo.setText(addr);
		postRequest=PostRequest.getPostRequest();
	}
	/**
	 * 上传流浪猫喂食点的数据
	 */
	private void uploadInfo()
	{
		if(!checkInfo())
		{
			Toast.makeText(this, "信息填写不正确", Toast.LENGTH_SHORT).show();
			return;
		}
		showDialog();
		JSONObject jsonObject=new JSONObject();
		try {
			jsonObject.put("type","location");
			jsonObject.put("latitude",latitude);
			jsonObject.put("longitude",longitude);
			jsonObject.put("address", etAddrInfo.getText().toString());
			jsonObject.put("catnum", etCatNum.getText().toString());
			jsonObject.put("babynum", etBabyNum.getText().toString());
			jsonObject.put("food", etFood.getText().toString());
			jsonObject.put( "catcondition", etCondition.getText().toString());
			jsonObject.put( "moreinfo", etMoreInfo.getText().toString());
			jsonObject.put( "updatetime",System.currentTimeMillis());
			postRequest.iniRequest(PostRequest.ADD,handler,jsonObject);
			postRequest.execute();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return;
	}
	private void showDialog()
	{
		progressDialog=new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setTitle("正在上传");
		progressDialog.setMessage("请稍等");
		progressDialog.show();		
	}
	/**
	 * 检查当前填写的数据是否有效
	 * @return
	 */
	private boolean checkInfo()
	{
		if(latitude<0||longitude<0||etAddrInfo.length()==0||etCatNum.length()==0||etFood.length()==0)
		{
			return false;
		}
		return true;
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
		case R.id.action_accept:
			uploadInfo();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
