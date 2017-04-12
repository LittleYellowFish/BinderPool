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
 * BInder连接池
 * 主要的实现在这里
 * @author yl
 *
 */
public class BinderPool {

	private static final String TAG = "BinderPool";

	public static final int BINDER_NONE = -1;
	//ICompute和ISecurityCenter的binderCode,如果有添加其它的Aidl接口，在此处添加对应的binderCode
	public static final int BINDER_COMPUTE = 0;
	public static final int BINDER_SECURITY_CENTER = 1;

	private Context mContext;
	private IBinderPool mBinderPool;
	private static volatile BinderPool sInstance;
	private CountDownLatch mcCountDownLatch;

	private BinderPool(Context mcContext) {
		super();
		this.mContext = mcContext.getApplicationContext();
		//初始化的时候连接服务端Service
		connectBinderPoolService();
	}

	/**
	 * 单例实现
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

	//这里会遇到到同步问题，所以会加上同步代码块
	private synchronized void connectBinderPoolService() {
		//CountDownLatch这个类能够使一个线程等待其他线程完成各自的工作后再执行
		mcCountDownLatch = new CountDownLatch(1);
		//绑定service
		Intent service = new Intent(mContext, BinderPoolService.class);
		mContext.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE);

		try {
			//等待
			mcCountDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private ServiceConnection mBinderPoolConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.d(TAG, "onServiceDisconnected");
			//关闭连接之后，在这里释放资源，也可以进行重连，暂不处理
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			//获取IBinderPool接口对象
			mBinderPool = IBinderPool.Stub.asInterface(arg1);
			try {
				//在这里加入了重连机制
				mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			//当前线程任务完成
			mcCountDownLatch.countDown();
		}
	};

	private IBinder.DeathRecipient mBinderPoolDeathRecipient = new DeathRecipient() {

		@Override
		public void binderDied() {
			Log.w(TAG, "binder died");
			//binder 死亡之后，让Binder连接池断开连接
			mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
			mBinderPool = null;
			//重新连接
			connectBinderPoolService();
		}
	};

	/**
	 * 通过IBinderPool接口根据binderCode去获取对应的AIDL接口对象
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
