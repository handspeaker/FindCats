package com.handspeaker.findcats;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.handspeaker.network.PostRequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EnterActivity extends Activity implements OnClickListener {
	
	public final static int VERSION=1;
	public static String IMEI;
	
	private static final long QUIT_PERIOD=2000;
	
	private TextView tvLogo;
	private Button btnFindCats;
	private Button btnAdoptCats;
	private Button btnHelp;
	private SoundPool soundPool;
	private int meowId;
	private Intent intent;
	private long lastPressBackTime;
	private PostRequest mPostRequest;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter);
		tvLogo = (TextView) findViewById(R.id.textview_logo);
		btnFindCats = (Button) findViewById(R.id.button_findcats);
		btnAdoptCats = (Button) findViewById(R.id.button_adoptcats);
		btnHelp = (Button) findViewById(R.id.button_help);
		tvLogo.setOnClickListener(this);
		btnFindCats.setOnClickListener(this);
		btnAdoptCats.setOnClickListener(this);
		btnHelp.setOnClickListener(this);

		mPostRequest=PostRequest.getPostRequest();
		TelephonyManager tManager = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
		IMEI = tManager.getDeviceId();
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		meowId=soundPool.load(getApplicationContext(),R.raw.cat_call, 1);
		lastPressBackTime=-1;
		
		if(!checkNetState())
		{
			btnFindCats.setEnabled(false);
			btnAdoptCats.setEnabled(false);
			Toast.makeText(getApplication(),"当前网络不可用，请退出程序后重新连接网络", Toast.LENGTH_SHORT)
			.show();
		}
		else
		{
			//ping服务器，上传当前客户端状态，并返回服务器状态
			//ping通信需要的参数
			JSONObject jsonObject=new JSONObject();
			try {
				jsonObject.put("version", VERSION);
				jsonObject.put("imei", IMEI);
				mPostRequest.iniRequest(PostRequest.PING,null,jsonObject);
				mPostRequest.execute();
			} catch (JSONException e) {
				e.printStackTrace();
			}
//			mPostRequest.addData(new BasicNameValuePair("version",Integer.toString(VERSION)));
//			mPostRequest.addData(new BasicNameValuePair("imei",IMEI));
		}
	}

	public boolean checkNetState()
	{
		ConnectivityManager connManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		soundPool.stop(meowId);
		switch (v.getId()) {
		case R.id.textview_logo:
			//tricky,play a call of cat
			soundPool.play(meowId, 0.5f, 0.5f, 1, 0, 1.0f);
			break;
		case R.id.button_findcats:
			intent=new Intent();
			intent.setClass(EnterActivity.this, FindCatsActivity.class);
			startActivity(intent);
			break;
		case R.id.button_adoptcats:
			intent=new Intent();
			intent.setClass(EnterActivity.this, AdoptCatsActivity.class);
			startActivity(intent);			
			break;
		case R.id.button_help:
			intent=new Intent();
			intent.setClass(EnterActivity.this, HelpActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			long currentTime=System.currentTimeMillis();
			if(lastPressBackTime==-1||(currentTime-lastPressBackTime)>QUIT_PERIOD)
			{
				lastPressBackTime=currentTime;
				Toast toast=Toast.makeText(getApplicationContext(),R.string.str_quit, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10);
				toast.show();
				return true;
			}
			else 
			{
				return super.onKeyUp(keyCode, event);
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		soundPool.release();
		soundPool=null;
		super.onDestroy();
	}
	
}
