package com.handspeaker.network;

public interface OnReceiveDataListener {
	//接收到返回信息的回调函数
	public abstract void onReceiveData(String strResult,int StatusCode);
}
