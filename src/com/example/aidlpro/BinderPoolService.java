package com.example.aidlpro;

import com.example.aidlpro.aidl.BinderPool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
/**
 * ����˵�Serviceʵ��
 * �ǵ�Ҫ��AndroidManifest����ע��
 * @author yl
 *
 */
public class BinderPoolService extends Service{

	private static final String TAG="BinderPoolService";
	
	//��ȡBinder���ӳ�ʵ���࣬�������ڻ�ȡ��Ӧ��Binder��
	private Binder mBinderPool=new BinderPool.BinderPoolImpl();
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onBind"+mBinderPool);
		return mBinderPool;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
