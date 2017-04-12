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
		//��ʱ����һ��Ҫ�������߳�ִ�У�����UIͨ��handler��messageʵ��
		new Thread(new Runnable() {

			@Override
			public void run() {
				doWork();
			}
		}).start();
		// doWork();���߳�ִ�к�ʱ�����oom
	}

	protected void doWork() {
		//��ȡBinder���ӳص�������ȡ������������Է���Application���棬�Ż���������
		binderPool = BinderPool.getInstance(MainActivity.this);
		//��ȡISecurityCenter��Binder������ȡ����ʵ����
		IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
		mSecurityCenter = SecurityCenterImpl.asInterface(securityBinder);
		Log.d(TAG, "visit ISecurityCenter");
		String msg = "hello world-��׿";
		System.out.println("content:" + msg);
		try {
			//��content����
			String passowrd = mSecurityCenter.encrpt(msg);
			System.out.println("encrpt:" + passowrd);
			//��password����
			System.out.println("decrpt:" + mSecurityCenter.decrpt(passowrd));

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		Log.d(TAG, "visit ICompute");
		//��ȡICompute��Binder������ȡ����ʵ����
		IBinder computerBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
		mcCompute = ComputeImpl.asInterface(computerBinder);
		try {
			//����ICompute��add����
			System.out.println("3+5=" + mcCompute.add(3, 5));
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
