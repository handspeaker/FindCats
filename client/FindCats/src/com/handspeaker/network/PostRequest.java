package com.handspeaker.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * 利用http协议，和后台进行数据交互 一共有三种类型：查询，更新，添加
 * 
 * @author cheersong
 */
public class PostRequest implements Runnable {
	// 服务的网址
	public static final String tag = "enter";
	public static final String URL = "http://handspeaker.xicp.net";
	// 4种请求，搜索，添加，更新,ping
	public final static String QUERY = "/query";
	public final static String ADD = "/add";
	public final static String UPDATE = "/update";
	public final static String PING = "/ping";

	private int connectionTimeout = 60000;
	private int socketTimeout = 60000;
	private String strResult;
//	private JSONObject sendObj;
//	private JSONObject rootObj;
	private HttpClient httpClient;
	private HttpPost httpPost;
	private HttpResponse httpResponse;
//	private ArrayList<NameValuePair> nameValuePairs;
	//线程相关
	private ExecutorService executorService;
	private Handler handler;
	//单例对象
	private static PostRequest postRequest=null;
	
	// private String requestType;
	// public JSONArray queryFaceArray=null;
	// public String md5=null;
	// private StringBuffer paramContent;
	/**
	 * 单例模式创建PostRequest对象
	 * @return
	 */
	public static PostRequest getPostRequest()
	{
		if(postRequest==null)
		{
			postRequest=new PostRequest();
		}
		return postRequest;
	}
	
	/**
	 * 构造函数，初始化一些可以重复使用的变量
	 */
	private PostRequest() {
		strResult = null;
//		sendObj=null;
//		rootObj = null;
		httpResponse = null;
		handler=null;
		httpPost = new HttpPost();
		httpClient = new DefaultHttpClient();
//		nameValuePairs=new ArrayList<NameValuePair>(15);
		executorService=Executors.newFixedThreadPool(1);
	}

//	/**
//	 * 添加需要发送的数据
//	 * @param pair 需要发送的键值对
//	 */
//	public void addData(BasicNameValuePair pair)
//	{
//		try {
//			sendObj.put(pair.getName(), pair.getValue());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
////		nameValuePairs.add(pair);
//	}
	/**
	 * 根据不同的请求类型来初始化httppost
	 * 
	 * @param requestType
	 *            请求类型
	 * @param nameValuePairs
	 *            需要传递的参数
	 */
	public void iniRequest(String requestType,Handler handler,JSONObject jsonObject) {
		this.handler=handler;
//		httpPost.setHeader("Content-Type", "text/plain");
//		httpPost.addHeader("Content-Type", "text/html");
		httpPost.addHeader("Content-Type", "text/json");
		httpPost.addHeader("charset", "UTF-8");
		
		httpPost.addHeader("Cache-Control", "no-cache");
		HttpParams httpParameters = httpPost.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				connectionTimeout);
//		httpParameters.setParameter("charset", "UTF-8");
		HttpConnectionParams.setSoTimeout(httpParameters, socketTimeout);
		httpPost.setParams(httpParameters);
		try {
			httpPost.setURI(new URI(URL + requestType));
			httpPost.setEntity(new StringEntity(jsonObject.toString(),HTTP.UTF_8));
//			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			Log.v(tag, "URI failed");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.v(tag, "setEntity failed");
		}
	}

	public void execute()
	{
		executorService.execute(this);
	}
	
	@Override
	public void run() {
		Message message=new Message();
		message.arg1=1;
		try {
			long start_time = System.currentTimeMillis();
			httpResponse = httpClient.execute(httpPost);
			long current_time = System.currentTimeMillis();
			strResult = EntityUtils.toString(httpResponse.getEntity());
			Log.v(tag, "time consume:" + (current_time - start_time));
			Log.v(tag, "result code:"
					+ httpResponse.getStatusLine().getStatusCode() + "");
		} catch (ClientProtocolException e1) {
			message.arg1=-1;
			e1.printStackTrace();
			
		} catch (IOException e1) {
			message.arg1=-2;
			e1.printStackTrace();
		}
		finally{
//			nameValuePairs.clear();
			if(handler!=null)
			{
				handler.sendMessage(message);
			}
		}
	}
	// /**
	// * 添加新喂食点/领养消息
	// * @param uID 当前用户id
	// * @param fID 人脸标注用户id
	// * @param jpgImg 剪切过的局部人脸图片
	// */
	// public void pingRequest(int uID,int fID,byte[] jpgImg)
	// {
	// // iniRequest(ADD);
	// String str64=Base64.encodeToString(jpgImg, Base64.DEFAULT);
	// ArrayList<NameValuePair> nameValuePairs = new
	// ArrayList<NameValuePair>(2);
	// nameValuePairs.add(new BasicNameValuePair("uID",uID+""));
	// nameValuePairs.add(new BasicNameValuePair("fID",fID+""));//有可能是新fID
	// //添加一个string类型的faceName
	// nameValuePairs.add(new BasicNameValuePair("pic",str64));
	// try {
	// mHttpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// Log.v("TAG", "send failed");
	// }
	// sendRequest();
	// }

	/**
	 * 人脸检索请求
	 * 
	 * @param uID
	 *            当前用户id
	 * @param jpgImg
	 *            人脸图片
	 */
	// public void queryRequest(int uID,ByteArrayOutputStream jpgImg)
	// {
	// iniRequest(QUERY);
	// String str64=Base64.encodeToString(jpgImg.toByteArray(), Base64.DEFAULT);
	// ArrayList<NameValuePair> nameValuePairs = new
	// ArrayList<NameValuePair>(2);
	// nameValuePairs.add(new BasicNameValuePair("uID",uID+""));
	// nameValuePairs.add(new BasicNameValuePair("pic",str64));
	// try {
	// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// Log.v("TAG", "send failed");
	// }
	// // paramContent.delete(0, paramContent.length());
	// // paramContent.append("v=1.0&uID="+uID+"&pic="+str64);
	// // Log.v("TAG", paramContent.toString());
	// sendRequest();
	// }
	// /**
	// * 添加新喂食点/领养消息
	// * @param uID 当前用户id
	// * @param fID 人脸标注用户id
	// * @param jpgImg 剪切过的局部人脸图片
	// */
	// public void addRequest(int uID,int fID,byte[] jpgImg)
	// {
	// // iniRequest(ADD);
	// String str64=Base64.encodeToString(jpgImg, Base64.DEFAULT);
	// ArrayList<NameValuePair> nameValuePairs = new
	// ArrayList<NameValuePair>(2);
	// nameValuePairs.add(new BasicNameValuePair("uID",uID+""));
	// nameValuePairs.add(new BasicNameValuePair("fID",fID+""));//有可能是新fID
	// //添加一个string类型的faceName
	// nameValuePairs.add(new BasicNameValuePair("pic",str64));
	// try {
	// mHttpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// Log.v("TAG", "send failed");
	// }
	// sendRequest();
	// }
	// /**
	// * 更新/删除当前人脸标注结果
	// * @param uID 当前用户id
	// * @param fID 原有人脸id
	// * @param nfID 新的人脸id
	// * @param md5 验证码
	// */
	// public void updateRequest(int uID,int fID,int nfID,String md5)
	// {
	// iniRequest(UPDATE);
	// ArrayList<NameValuePair> nameValuePairs = new
	// ArrayList<NameValuePair>(2);
	// nameValuePairs.add(new BasicNameValuePair("uID",uID+""));
	// nameValuePairs.add(new BasicNameValuePair("fID",fID+""));
	// nameValuePairs.add(new BasicNameValuePair("nfID",nfID+""));
	// nameValuePairs.add(new BasicNameValuePair("md5",md5+""));
	// try {
	// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// Log.v("TAG", "send failed");
	// }
	// sendRequest();
	// }
	// /**
	// * 封装实体，发送请求
	// */
	// private void sendRequest()
	// {
	// try {
	// long start_time= System.currentTimeMillis();
	// mHttpResponse=mHttpClient.execute(mHttpPost);
	// long current_time = System.currentTimeMillis();
	// strResult=EntityUtils.toString(mHttpResponse.getEntity());
	// Log.v("test", "time:"+(current_time-start_time));
	// Log.v("test", mHttpResponse.getStatusLine().getStatusCode()+"");
	// } catch (ClientProtocolException e1) {
	// e1.printStackTrace();
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }
	// }
	// /**
	// * 解析服务器返回的结果
	// * @param requestType 不同的请求类型
	// * @return 成功:1，失败：<0
	// */
	// public int analyzeResult(String requestType)
	// {
	// if(strResult==null)
	// {return -1;}
	// else
	// {
	//
	// // if(requestType.equals(QUERY))
	// // {
	// // try
	// // {
	// // rootObj = new JSONObject(strResult);
	// // int ret=rootObj.getInt("ret");
	// // if(ret!=0)
	// // return 0;
	// // queryFaceArray=root.getJSONArray("res");
	// // }
	// // catch (JSONException e1)
	// // {
	// // return 0;
	// // }
	// // return 1;
	// // }
	// // else if(requestType.equals(ADD))
	// // {
	// // try {
	// // root = new JSONObject(regResult);
	// // int ret=root.getInt("ret");
	// // md5=root.getString("md5");
	// // return ret;
	// // } catch (JSONException e) {
	// // e.printStackTrace();
	// // return 0;
	// // }
	// // }
	// // else if(requestType.equals(UPDATE))
	// // {
	// // try {
	// // root = new JSONObject(regResult);
	// // int ret=root.getInt("ret");
	// // return ret;
	// // } catch (JSONException e) {
	// // e.printStackTrace();
	// // return 0;
	// // }
	// // }
	// }
	// return 1;
	// }
}
