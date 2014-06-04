package com.appswhere.virtualusb;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.protocol.HTTP;
import com.appswhere.virtualusb.R;
import com.mobclick.android.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class HelpActivity extends Activity {
	private WebView wv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setTitle(" π”√∞Ô÷˙");
		setContentView(R.layout.wv_help);
		
		wv = (WebView) findViewById(R.id.wvHelp);
		
		InputStream inputStream = getResources().openRawResource(R.raw.help);   
		byte[] reader;
		try {
			reader = new byte[inputStream.available()];
			while (inputStream.read(reader) != -1) {}  
			wv.loadDataWithBaseURL("file:///android_asset/", new String(reader), "text/html", HTTP.UTF_8, "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Toast.makeText(this, "º”‘ÿ ß∞‹!",
					Toast.LENGTH_SHORT).show();
		}       	
	}
	
	public void onResume() { 
        super.onResume(); 
        MobclickAgent.onResume(this); 

    }
    
    public void onPause() { 
        super.onPause(); 
        MobclickAgent.onPause(this); 
    } 
}
