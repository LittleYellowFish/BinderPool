package com.example.aidlpro;

import com.example.aidlpro.aidl.BinderPool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
/**
 * 服务端的Service实现
 * 记得要在AndroidManifest里面注册
 * @author yl
 *
 */
public class BinderPoolService extends Service{

	private static final String TAG="BinderPoolService";
	
	//获取Binder连接池实现类，此类用于获取相应的Binder类
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
