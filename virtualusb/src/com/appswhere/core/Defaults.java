
package com.appswhere.core;

import java.io.File;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class Defaults {
	protected static int inputBufferSize = 256;
	protected static int dataChunkSize = 65536;  //90k //65536;  // do file I/O in 64k chunks 
	protected static int sessionMonitorScrollBack = 10;
	protected static int serverLogScrollBack = 10;
	protected static int uiLogLevel = Defaults.release ? Log.INFO : Log.DEBUG;
	protected static int consoleLogLevel = Defaults.release ? Log.INFO : Log.DEBUG;
	protected static String settingsName = "SwiFTP";
	//protected static String username = "user";
	//protected static String password = "";
	protected static int portNumber = 7799; 
//	protected static int ipRetrievalAttempts = 5;
	public static final int tcpConnectionBacklog = 5;
	public static  String chrootDir;//= "/";
	static {
		if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
			chrootDir ="/mnt/sdcard/";
		} else {
			chrootDir ="/sdcard/";
		}
		     
		
	}
	public static final boolean acceptWifi = true;
	public static final boolean acceptNet = false; // don't incur bandwidth charges
	public static final boolean stayAwake = true;
	public static final int REMOTE_PROXY_PORT = 2222;
	public static final String STRING_ENCODING = "GBK";
	public static final int SO_TIMEOUT_MS = 30000; // socket timeout millis
	// FTP control sessions should start out in ASCII, according to the RFC.
	// However, many clients don't turn on UTF-8 even though they support it,
	// so we just turn it on by default.
	public static final String SESSION_ENCODING = "GBK"; 
	
	// This is a flag that should be true for public builds and false for dev builds
	public static final boolean release = true;
	
	// Try to fix the transfer stall bug, reopen the destination file periodically
	//public static final boolean do_reopen_hack = false;
	//public static final int bytes_between_reopen = 4000000;
	
	// Try to fix the transfer stall bug, flush the file periodically
	//public static final boolean do_flush_hack = false;
	//public static final int bytes_between_flush = 500000;
	
	public static final boolean do_mediascanner_notify = true;
	
	
//	public static int getIpRetrievalAttempts() {
//		return ipRetrievalAttempts;
//	}

//	public static void setIpRetrievalAttempts(int ipRetrievalAttempts) {
//		Defaults.ipRetrievalAttempts = ipRetrievalAttempts;
//	}

	public static int getPortNumber() {
		return portNumber;
	}

	public static void setPortNumber(int portNumber) {
		Defaults.portNumber = 7799; /*by wanc*/
	}

	public static String getSettingsName() {
		return settingsName;
	}

	public static void setSettingsName(String settingsName) {
		Defaults.settingsName = settingsName;
	}

	public static int getSettingsMode() {
		return settingsMode;
	}

	public static void setSettingsMode(int settingsMode) {
		Defaults.settingsMode = settingsMode;
	}

	public static void setServerLogScrollBack(int serverLogScrollBack) {
		Defaults.serverLogScrollBack = serverLogScrollBack;
	}

	protected static int settingsMode = Context.MODE_WORLD_WRITEABLE;
	
	public static int getUiLogLevel() {
		return uiLogLevel;
	}

	public static void setUiLogLevel(int uiLogLevel) {
		Defaults.uiLogLevel = uiLogLevel;
	}

	public static int getInputBufferSize() {
		return inputBufferSize;
	}

	public static void setInputBufferSize(int inputBufferSize) {
		Defaults.inputBufferSize = inputBufferSize;
	}

	public static int getDataChunkSize() {
		return dataChunkSize;
	}

	public static void setDataChunkSize(int dataChunkSize) {
		Defaults.dataChunkSize = dataChunkSize;
	}

	public static int getSessionMonitorScrollBack() {
		return sessionMonitorScrollBack;
	}

	public static void setSessionMonitorScrollBack(
			int sessionMonitorScrollBack) 
	{
		Defaults.sessionMonitorScrollBack = sessionMonitorScrollBack;
	}

	public static int getServerLogScrollBack() {
		return serverLogScrollBack;
	}

	public static void setLogScrollBack(int serverLogScrollBack) {
		Defaults.serverLogScrollBack = serverLogScrollBack;
	}

	public static int getConsoleLogLevel() {
		return consoleLogLevel;
	}

	public static void setConsoleLogLevel(int consoleLogLevel) {
		Defaults.consoleLogLevel = consoleLogLevel;
	}


}
