package com.example.aidlpro;

import com.example.aidlpro.aidl.BinderPool;
import com.example.aidlpro.aidl.ComputeImpl;
import com.example.aidlpro.aidl.ICompute;
import com.example.aidlpro.aidl.ISecurityCenter;
import com.example.aidlpro.aidl.SecurityCenterImpl;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

	private ISecurityCenter mSecurityCenter;
	private ICompute mcCompute;
	private BinderPool binderPool;

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	public void onClick(View v) {
		Log.d(TAG, "onclick");
		//耗时任务一定要放在子线程执行，更新UI通过handler、message实现
		new Thread(new Runnable() {

			@Override
			public void run() {
				doWork();
			}
		}).start();
		// doWork();主线程执行耗时任务会oom
	}

	protected void doWork() {
		//获取Binder连接池单例，获取单例的任务可以放在Application里面，优化程序体验
		binderPool = BinderPool.getInstance(MainActivity.this);
		//获取ISecurityCenter的Binder，并获取他的实现类
		IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
		mSecurityCenter = SecurityCenterImpl.asInterface(securityBinder);
		Log.d(TAG, "visit ISecurityCenter");
		String msg = "hello world-安卓";
		System.out.println("content:" + msg);
		try {
			//对content加密
			String passowrd = mSecurityCenter.encrpt(msg);
			System.out.println("encrpt:" + passowrd);
			//对password解密
			System.out.println("decrpt:" + mSecurityCenter.decrpt(passowrd));

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		Log.d(TAG, "visit ICompute");
		//获取ICompute的Binder，并获取他的实现类
		IBinder computerBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
		mcCompute = ComputeImpl.asInterface(computerBinder);
		try {
			//调用ICompute的add方法
			System.out.println("3+5=" + mcCompute.add(3, 5));
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
