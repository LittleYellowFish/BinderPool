package com.example.aidlpro.aidl;

import java.util.concurrent.CountDownLatch;

import com.example.aidlpro.BinderPoolService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.util.Log;
import android.os.RemoteException;

/**
 * BInder���ӳ�
 * ��Ҫ��ʵ��������
 * @author yl
 *
 */
public class BinderPool {

	private static final String TAG = "BinderPool";

	public static final int BINDER_NONE = -1;
	//ICompute��ISecurityCenter��binderCode,��������������Aidl�ӿڣ��ڴ˴���Ӷ�Ӧ��binderCode
	public static final int BINDER_COMPUTE = 0;
	public static final int BINDER_SECURITY_CENTER = 1;

	private Context mContext;
	private IBinderPool mBinderPool;
	private static volatile BinderPool sInstance;
	private CountDownLatch mcCountDownLatch;

	private BinderPool(Context mcContext) {
		super();
		this.mContext = mcContext.getApplicationContext();
		//��ʼ����ʱ�����ӷ����Service
		connectBinderPoolService();
	}

	/**
	 * ����ʵ��
	 * @param context
	 * @return
	 */
	public static BinderPool getInstance(Context context) {
		if (sInstance == null) {
			synchronized (BinderPool.class) {
				if (sInstance == null) {
					sInstance = new BinderPool(context);
				}
			}
		}
		return sInstance;
	}

	//�����������ͬ�����⣬���Ի����ͬ�������
	private synchronized void connectBinderPoolService() {
		//CountDownLatch������ܹ�ʹһ���̵߳ȴ������߳���ɸ��ԵĹ�������ִ��
		mcCountDownLatch = new CountDownLatch(1);
		//��service
		Intent service = new Intent(mContext, BinderPoolService.class);
		mContext.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE);

		try {
			//�ȴ�
			mcCountDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private ServiceConnection mBinderPoolConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.d(TAG, "onServiceDisconnected");
			//�ر�����֮���������ͷ���Դ��Ҳ���Խ����������ݲ�����
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			//��ȡIBinderPool�ӿڶ���
			mBinderPool = IBinderPool.Stub.asInterface(arg1);
			try {
				//�������������������
				mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			//��ǰ�߳��������
			mcCountDownLatch.countDown();
		}
	};

	private IBinder.DeathRecipient mBinderPoolDeathRecipient = new DeathRecipient() {

		@Override
		public void binderDied() {
			Log.w(TAG, "binder died");
			//binder ����֮����Binder���ӳضϿ�����
			mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
			mBinderPool = null;
			//��������
			connectBinderPoolService();
		}
	};

	/**
	 * ͨ��IBinderPool�ӿڸ���binderCodeȥ��ȡ��Ӧ��AIDL�ӿڶ���
	 * @param binderCode
	 * @return
	 */
	public IBinder queryBinder(int binderCode) {
		IBinder binder = null;
		if (mBinderPool != null) {
			try {
				binder = mBinderPool.queryBinder(binderCode);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return binder;
	}

	public static class BinderPoolImpl extends IBinderPool.Stub {

		public BinderPoolImpl() {
			super();
		}

		@Override
		public IBinder queryBinder(int binderCode) throws RemoteException {
			IBinder binder = null;
			switch (binderCode) {
			case BINDER_SECURITY_CENTER:
				binder = new SecurityCenterImpl();
				break;

			case BINDER_COMPUTE:
				binder = new ComputeImpl();
				break;
			default:
				break;
			}
			return binder;
		}

	}

}
