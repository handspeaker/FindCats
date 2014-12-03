package com.handspeaker.findcats;

import org.json.JSONException;
import org.json.JSONObject;

import com.handspeaker.network.OnReceiveDataListener;
import com.handspeaker.network.PostRequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class EntranceActivity extends Activity implements OnClickListener,
		OnReceiveDataListener {

	public final static int VERSION = 1;
	public static String IMEI;

	private final static long QUIT_PERIOD = 2000;

	public final static int FINDCATS = 0;
	public final static int ADOPTION = 1;
	public final static int GETHELP = 2;

	public final static int RESULT_OK = 100;
	public final static int RESULT_ERR =0;
	//天安门
	public final static String DEFALUT_LATITUDE ="39.90960456049752";
	public final static String DEFALUT_LONGITUDE ="116.3972282409668";
	
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
		SharedPreferences sharedPreferences = getPreferences(Activity.MODE_PRIVATE);
		if (!sharedPreferences.contains("latitude")) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("latitude", DEFALUT_LATITUDE);
			editor.putString("longitude",DEFALUT_LONGITUDE);
			editor.commit();
		}
		setContentView(R.layout.activity_enter);
		tvLogo = (TextView) findViewById(R.id.textview_logo);
		btnFindCats = (Button) findViewById(R.id.button_findcats);
		btnAdoptCats = (Button) findViewById(R.id.button_adoptcats);
		btnHelp = (Button) findViewById(R.id.button_help);
		tvLogo.setOnClickListener(this);
		btnFindCats.setOnClickListener(this);
		btnAdoptCats.setOnClickListener(this);
		btnHelp.setOnClickListener(this);

		mPostRequest = PostRequest.getPostRequest();
		TelephonyManager tManager = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		IMEI = tManager.getDeviceId();
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		meowId = soundPool.load(getApplicationContext(), R.raw.cat_call, 1);
		lastPressBackTime = -1;

		if (!checkNetState()) {
			btnFindCats.setEnabled(false);
			btnAdoptCats.setEnabled(false);
			Toast.makeText(getApplication(), "当前网络不可用，请退出程序后重新连接网络",
					Toast.LENGTH_SHORT).show();
		} else {
			// ping服务器，上传当前客户端状态，并返回服务器状态
			// ping通信需要的参数
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("version", VERSION);
				jsonObject.put("imei", IMEI);
				mPostRequest.iniRequest(PostRequest.PING, jsonObject);
				mPostRequest.setOnReceiveDataListener(this);
				mPostRequest.execute();
			} catch (JSONException e) {
				e.printStackTrace();
				btnFindCats.setEnabled(false);
				btnAdoptCats.setEnabled(false);
				Toast.makeText(getApplication(), "程序出错，请重新安装",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public boolean checkNetState() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		soundPool.stop(meowId);
		switch (v.getId()) {
		case R.id.textview_logo:
			// tricky,play a call of cat
			soundPool.play(meowId, 0.5f, 0.5f, 1, 0, 1.0f);
			break;
		case R.id.button_findcats:
			intent = new Intent();
			intent.setClass(EntranceActivity.this, FindCatsActivity.class);
			startActivityForResult(intent, FINDCATS);
			break;
		case R.id.button_adoptcats:
			intent = new Intent();
			intent.setClass(EntranceActivity.this, AdoptCatsActivity.class);
			startActivityForResult(intent, ADOPTION);
			break;
		case R.id.button_help:
			intent = new Intent();
			intent.setClass(EntranceActivity.this, HelpActivity.class);
			startActivityForResult(intent, GETHELP);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long currentTime = System.currentTimeMillis();
			if (lastPressBackTime == -1
					|| (currentTime - lastPressBackTime) > QUIT_PERIOD) {
				lastPressBackTime = currentTime;
				Toast toast = Toast.makeText(getApplicationContext(),
						R.string.str_quit, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0,
						10);
				toast.show();
				return true;
			} else {
				return super.onKeyUp(keyCode, event);
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		soundPool.release();
		soundPool = null;
		super.onDestroy();
	}

	@Override
	public void onReceiveData(String strResult, int StatusCode) {
		if (strResult == null) {
			btnFindCats.setEnabled(false);
			btnAdoptCats.setEnabled(false);
			Toast.makeText(getApplication(), "服务器出错，请稍后再试", Toast.LENGTH_SHORT)
					.show();
		} else {
			try {
				JSONObject rootObj = new JSONObject(strResult);
				int state = rootObj.getInt("state");
				if (state < 0) {
					// btnFindCats.setEnabled(false);
					// btnAdoptCats.setEnabled(false);
					Toast.makeText(getApplication(), "有新版程序存在",
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// int state=mPostRequest.analyzeResult(PostRequest.PING);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FINDCATS:
			//如果在寻猫activity中定位成功，则在返回时保存最后的定位，以便下次进入程序时使用
			if(resultCode==RESULT_OK)
			{
				String latitude=data.getStringExtra("latitude");
				String longitude=data.getStringExtra("longitude");
				if(latitude!=null&&longitude!=null)
				{
					SharedPreferences sharedPreferences = getPreferences(Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("latitude", latitude);
					editor.putString("longitude",longitude);
					editor.commit();
				}
			}
			break;
		case ADOPTION:
			break;
		case GETHELP:
			break;
		}
	}
}
