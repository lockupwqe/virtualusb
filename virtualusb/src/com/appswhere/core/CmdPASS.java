
package com.appswhere.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class CmdPASS extends FtpCmd implements Runnable {
	String input;
	
	public CmdPASS(SessionThread sessionThread, String input) {
		// We can just discard the password for now. We're just
		// following the expected dialogue, we're going to allow
		// access in any case.
		super(sessionThread, CmdPASS.class.toString());
		this.input = input;
	}
	
	public void run() {
		Context ctx = Globals.getContext();// by wangc,可以设置不用密码
		if(ctx == null) {
			// This will probably never happen, since the global 
			// context is configured by the Service
			myLog.l(Log.ERROR, "No global context in PASS\r\n");
		}
		SharedPreferences settings = ctx.getSharedPreferences(
				Defaults.getSettingsName(), Defaults.getSettingsMode()); // by wangc,可以设置不用密码
		// User must have already executed a USER command to
		// populate the Account object's username
		myLog.l(Log.DEBUG, "Executing PASS");
	
		String attemptPassword = getParameter(input, true); // silent
		String attemptUsername = sessionThread.account.getUsername();
		if(attemptUsername == null && settings.getBoolean("isPassword", false)) { // modify by wangc
			sessionThread.writeString("503 Must send USER first\r\n");
			return;
		}
		
		String password;
		String username;
		/*SharedPreferences settings = ctx.getSharedPreferences(
				Defaults.getSettingsName(), Defaults.getSettingsMode()); by wangc*/
		username = settings.getString("username", null);
		password = settings.getString("password", null);
		/*add by wangc,可以设置不需要密码*/
		if (!settings.getBoolean("isPassword", false)) {
			username = "";
			password = "";
		}
		if(username == null || password == null) {
			myLog.l(Log.ERROR, "Username or password misconfigured");
			sessionThread.writeString("500 Internal error during authentication");
		} else if( (username.equals(attemptUsername) &&  /*modify by wangc,可以设置不需要密码*/
				password.equals(attemptPassword)) || !settings.getBoolean("isPassword", false)) {
			sessionThread.writeString("230 Access granted\r\n");
			myLog.l(Log.INFO, "User " + username + " password verified");
			sessionThread.authAttempt(true);
		} else {
			try {
				// If the login failed, sleep for one second to foil
				// brute force attacks
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
			myLog.l(Log.INFO, "Failed authentication");
			sessionThread.writeString("530 Login incorrect.\r\n");
			sessionThread.authAttempt(false);
		}
	}
}
