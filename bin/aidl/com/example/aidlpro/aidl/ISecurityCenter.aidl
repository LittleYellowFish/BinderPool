package com.example.aidlpro.aidl;

interface ISecurityCenter{
	String encrpt(String content);
	String decrpt(String password);
}