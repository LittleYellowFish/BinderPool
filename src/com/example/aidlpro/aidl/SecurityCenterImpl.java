package com.example.aidlpro.aidl;

import android.os.RemoteException;

public class SecurityCenterImpl extends ISecurityCenter.Stub{

	private static final char SECRET_CODE='^';
	@Override
	public String encrpt(String content) throws RemoteException {
		char[] chars=content.toCharArray();
		for(int i=0;i<chars.length;i++){
			chars[i]^=SECRET_CODE;
		}
		return new String(chars);
	}

	@Override
	public String decrpt(String password) throws RemoteException {
		
		return encrpt(password);
	}

}
