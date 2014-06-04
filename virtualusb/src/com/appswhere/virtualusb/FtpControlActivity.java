package com.appswhere.virtualusb;

import java.io.File;
import java.net.InetAddress;
import java.util.Date;

import com.appswhere.core.Defaults;
import com.appswhere.core.FTPServerService;
import com.appswhere.core.Globals;
import com.appswhere.virtualusb.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.admogo.AdMogoTargeting;
import com.mobclick.android.MobclickAgent;
import com.mobclick.android.UmengFeedbackListener;


public class FtpControlActivity extends Activity {
	
	private Button startButton;
	private Button stopButton;
	//private Button feedBackButton;
	private Button helpButton;
	//private Button exitButton;
	private TextView ipText;
	private TextView ftpstate;
	
	private AlertDialog accessDialog;
	
	/*����˵�*/
	final int FEED_BACK_MENU = 0; //�������
	final int SET_MENU = 1;  //����
	final int EXIT_MENU = 2;  //�˳�
	
	/*ȫ��������Ϣ*/
	boolean[] mulFlags = null;
	
	/*���º��˼���ʱ��*/
	long backTime = 0;
	
	/*״̬��
	NotificationManager notificationMgr = null;*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		/*swiftp��������Ҫ�õ�context*/
		Context myContext = Globals.getContext();
		if(myContext == null) {
			myContext = getApplicationContext();
			if(myContext == null) {
				throw new NullPointerException("Null context!?!?!?");
			}
			Globals.setContext(myContext);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		startButton = (Button) findViewById(R.id.btn_start);
		stopButton = (Button) findViewById(R.id.btn_stop);
		//feedBackButton = (Button) findViewById(R.id.btn_feedback);
		helpButton = (Button) findViewById(R.id.btn_help);
		//exitButton = (Button) findViewById(R.id.btn_exit);
		ipText = (TextView) findViewById(R.id.text_visitftp);
		ftpstate = (TextView) findViewById(R.id.text_ftpstate);
		
		startButton.setOnClickListener(startButtonListener);
		stopButton.setOnClickListener(stopButtonListener);
		//feedBackButton.setOnClickListener(feedButtonListener);
		helpButton.setOnClickListener(helpButtonListener);
		//exitButton.setOnClickListener(exitButtonListener);		
		
		/*�Զ���������*/
		 MobclickAgent.setUpdateOnlyWifi(false); 
		 MobclickAgent.update(this);
		
		/*���ʹ��󱨸�*/
		MobclickAgent.onError(this); 
		
		/*�û����������ʾ*/
		MobclickAgent.setFeedbackListener(new UmengFeedbackListener(){    
			public  void onFeedbackReturned(int arg0) {     
				if (arg0 == 0) {
					Toast.makeText(FtpControlActivity.this, R.string.feedback_ok,
							Toast.LENGTH_SHORT).show();
				} else if (arg0 == 1) {
					Toast.makeText(FtpControlActivity.this, R.string.feedback_failed,
							Toast.LENGTH_SHORT).show();
				} else if (arg0 == 2) {
					Toast.makeText(FtpControlActivity.this, R.string.feedback_no_connect,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		/*��ӹ��,�û��״�ʹ����������ʾ���*/
		//long systemTime = System.currentTimeMillis(); 
		//SharedPreferences settings = getSharedPreferences(
	    //    		Defaults.getSettingsName(),	Defaults.getSettingsMode());
		//long firstUseTime = settings.getLong("firstUseTime", systemTime);
		//
		//if (firstUseTime == systemTime) { //�״�ʹ�ã���¼ʱ��
		//	SharedPreferences.Editor editor = settings.edit();
		//	editor.putLong("firstUseTime", systemTime);
		//	editor.commit();
		//}		
		//if ( ((systemTime - firstUseTime) / 864000000) > 3 ) { //864000000=24*60*60*1000
			AdMogoTargeting.setTestMode(false);
		//} 
	}
	
	/*����FTP*/
	OnClickListener startButtonListener = new OnClickListener() {
        public void onClick(View v) {
        	SharedPreferences settings = getSharedPreferences(
	   	        		Defaults.getSettingsName(),	Defaults.getSettingsMode());
        	boolean isPassword = settings.getBoolean("isPassword", false);
        	//boolean stayWake = settings.getBoolean("stayWake", false);
        	if (isPassword) {
        		/*�����Ի���ǿ����������û������룬�����������*/
        		FtpControlActivity.this.showDialog(0); 
        	} else {
        		startServer();
        	}  
        }
    };
    
    /*ֹͣFTP*/
    OnClickListener stopButtonListener = new OnClickListener() {
        public void onClick(View v) {
    		Context context = getApplicationContext();
    		Intent intent = new Intent(context,	FTPServerService.class);
    		context.stopService(intent);
        	
    		 /*�л�������ť��ֹͣ��ť*/
    		startButton.setVisibility(View.VISIBLE);
        	stopButton.setVisibility(View.GONE);
        	
        	/*�л�һ����ʾ״̬*/
        	ipText.setVisibility(View.GONE);
        	ftpstate.setText(R.string.server_stop);
        	Toast.makeText(FtpControlActivity.this, R.string.stop_server,
					Toast.LENGTH_SHORT).show();
        }
    };
    
    /*�˳�����
    private OnClickListener exitButtonListener = new OnClickListener() {
    	public void onClick(View v) {
    		FtpControlActivity.this.showDialog(1);
    	}
    };*/
    
    /*�û�����
    private OnClickListener feedButtonListener = new OnClickListener() {
    	public void onClick(View v) {
    		MobclickAgent.openFeedbackActivity(FtpControlActivity.this); 
    	}
    };*/ 
    
    /*����*/
    private OnClickListener helpButtonListener = new OnClickListener() {
    	public void onClick(View v) {
    		Intent intent = new Intent(FtpControlActivity.this, HelpActivity.class);
    		startActivity(intent);
    	}
    };
    
    /*�ж��û���չ�洢�Ƿ����*/
    private void warnIfNoExternalStorage() {
    	String storageState = Environment.getExternalStorageState();
    	if(!storageState.equals(Environment.MEDIA_MOUNTED)) {
    		Toast toast = Toast.makeText(this, R.string.storage_warning,
					Toast.LENGTH_LONG);
	    	toast.setGravity(Gravity.CENTER, 0, 0);
	    	toast.show();
    	}
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    	/*ֹͣFTP*/
    	Context context = getApplicationContext();
		Intent intent = new Intent(context,	FTPServerService.class);
		context.stopService(intent);
		Log.d("w","onDestroy()");
    }
    

    /**
     * �����Ի���
     * 0����������˺�����Ի���
     * 2�����öԻ���
     */
    @Override
    protected Dialog onCreateDialog(int id) {    	
    	AlertDialog.Builder ad = new AlertDialog.Builder(this);   	
    	LayoutInflater inflater = LayoutInflater.from(this);    	
    	SharedPreferences settings = null;
    	
    	switch (id) {
    	case 0: /*�����*/
    		View setView = inflater.inflate(R.layout.setting, null); 		
    		settings = getSharedPreferences(
   	        		Defaults.getSettingsName(),	Defaults.getSettingsMode());
    		
    		String username = settings.getString("username", null);
       		String password = settings.getString("password", null);
       		//boolean isSave = settings.getBoolean("isSave", true);
       		((CheckBox)setView.findViewById(R.id.chk)).setChecked(true);
       		((EditText)setView.findViewById(R.id.username)).setText(username);
            ((EditText)setView.findViewById(R.id.pwd)).setText(password);   	           
            
    		ad.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    			 public void onClick(DialogInterface dialog, int which){
    	    		//boolean isSave = ((CheckBox) superDialog.findViewById(R.id.chk)).isChecked();
    	    		String username = ((EditText) accessDialog.findViewById(R.id.username)).getText().toString().trim();
    	        	String password = ((EditText) accessDialog.findViewById(R.id.pwd)).getText().toString().trim();
    	        	
    	        	if (username == null || password == null || "".equals(username) || "".equals(password)) {
    	        		Toast.makeText(FtpControlActivity.this, R.string.login_error,
    	    					Toast.LENGTH_LONG).show();
    	        		return;
    	        	} else {
    	        		/*�����˺�����*/
        	        	SharedPreferences settings = getSharedPreferences(
        	   	        		Defaults.getSettingsName(),	Defaults.getSettingsMode());
        	        	SharedPreferences.Editor editor = settings.edit();
        	        	//editor.putBoolean("isSave", isSave);
        	        	editor.putString("username", username);
        	        	editor.putString("password", password);
        	        	editor.commit();
    	        	}   	
    	        	
    	        	startServer(); //��������
    	    	}
    	    });
    		
    		ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
   			   public void onClick(DialogInterface dialog, int which){
   				 
   			   }
    		});
    		
    		CheckBox isSave = (CheckBox)setView.findViewById(R.id.chk);
    		isSave.setOnClickListener(new CheckBox.OnClickListener() {  			
    			public void onClick(View v) {
   				   if ( ((CheckBox) v).isChecked() ) {
   					SharedPreferences settings = getSharedPreferences(
   		   	        		Defaults.getSettingsName(),	Defaults.getSettingsMode());
   		    		
   		    		String username = settings.getString("username", null);
   		       		String password = settings.getString("password", null);
   		       		//boolean isSave = settings.getBoolean("isSave", true);
   		       		//((CheckBox)setView.findViewById(R.id.chk)).setChecked(isSave);
   		       		
   		       		((EditText)accessDialog.findViewById(R.id.username)).setText(username);
   		            ((EditText)accessDialog.findViewById(R.id.pwd)).setText(password);
   				   } else {
   					((EditText)accessDialog.findViewById(R.id.username)).setText(null);
   		            ((EditText)accessDialog.findViewById(R.id.pwd)).setText(null);
   				   }
   			 	}
    		});
    		//((Button)dialog.findViewById(R.id.btn_ok)).setOnClickListener(doSetUserListener);
    		ad.setTitle(R.string.set_access);  	 		
    		ad.setView(setView);
    		AlertDialog dialog = ad.create();
    		/*���öԻ����С
    		LayoutParams params = dialog.getWindow().getAttributes();   */ 

    		dialog.show();
    		accessDialog = dialog;
    		return dialog;/*
    	case 1:*/ /*�˳���ʾ�Ի���
    		AlertDialog dialogExit = ad.setTitle("��ʾ").setMessage("�Ƿ���Ҫ�رշ���")
    			     .setPositiveButton("�ر�", new DialogInterface.OnClickListener() {
    		    			public void onClick(DialogInterface dialog, int which) {
    		      				 finish();
    		   			 	}
    		    		}).setNegativeButton("�ں�̨����", new DialogInterface.OnClickListener() {
    		    			public void onClick(DialogInterface dialog, int which) {
    		    				String ns = Context.NOTIFICATION_SERVICE;
    		    				notificationMgr = (NotificationManager) getSystemService(ns);
    		    				Notification notification = new Notification(R.drawable.logo, "������������������", System.currentTimeMillis());
    		    				Intent notificationIntent = new Intent(FtpControlActivity.this, FtpControlActivity.class);
    		    				PendingIntent contentIntent = PendingIntent.getActivity(FtpControlActivity.this, 0, 
    		    				notificationIntent, 0);
    		    				notification.setLatestEventInfo(getApplicationContext(), 
    		    				"", "", contentIntent);
    		    				notification.flags |= Notification.FLAG_ONGOING_EVENT;*/
    		    				
    		    				/* Pass Notification to NotificationManager
    		    				notificationMgr.notify(0, notification);
   		   			 	}}).create();
    		dialogExit.show();
    		return dialogExit;*/
    	case 2:  /*���öԻ���*/
    		settings = getSharedPreferences(
   	        		Defaults.getSettingsName(),	Defaults.getSettingsMode());
    		mulFlags = new boolean[]{settings.getBoolean("isPassword", false), settings.getBoolean("stayAwake", true)};
    		String[] item = this.getResources().getStringArray(R.array.setting_item);
    		ad.setTitle(R.string.setting);
    		ad.setMultiChoiceItems(item, mulFlags, new DialogInterface.OnMultiChoiceClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					// TODO Auto-generated method stub
					mulFlags[which] = isChecked;
					Log.d("w", which+":"+isChecked);
				}
			});
    		
    		AlertDialog dialogSet =  ad.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				SharedPreferences settings = getSharedPreferences(
    	   	        		Defaults.getSettingsName(),	Defaults.getSettingsMode());
    				SharedPreferences.Editor editor = settings.edit();
    				editor.putBoolean("isPassword", mulFlags[0]);
    				editor.putBoolean("stayAwake", mulFlags[1]);
    				editor.commit();
    				if (mulFlags[1] == true) {
    					//Toast.makeText(FtpControlActivity.this, R.string.stay_awake_notice, Toast.LENGTH_SHORT).show();
    				}
  			 	}
    		}).setNegativeButton(R.string.cancel, null).create();
    		return dialogSet;
    		
    	}    	
    	return null;	
    }
    
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	  if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //˫�����˼��˳�Ӧ�ó���
    		  long currentTime = (new Date()).getTime();
    		  if (currentTime - backTime > 2000) {
    			  Toast.makeText(this, R.string.exit_notice, Toast.LENGTH_SHORT).show();
    			  backTime = currentTime;
    		  } else {
    			  finish();
    		  }
    		  return true;
    	  } 
    	  return super.onKeyDown(keyCode,event);   
    }
    
    public void onResume() { 
        super.onResume(); 
        MobclickAgent.onResume(this); 

    }
    
    public void onPause() { 
        super.onPause(); 
        MobclickAgent.onPause(this); 
    } 
    
    public void onStop() { 
        super.onStop(); 
              
    } 

    /*�����˵�*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, FEED_BACK_MENU, 0, R.string.feedback).setIcon(android.R.drawable.ic_menu_info_details);
    	menu.add(Menu.NONE, SET_MENU, 1, R.string.setting).setIcon(android.R.drawable.ic_menu_preferences);
    	menu.add(Menu.NONE, EXIT_MENU, 2, R.string.exit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	
    	return true;
    }
    
    /*����˵��¼�*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case FEED_BACK_MENU:
    		MobclickAgent.openFeedbackActivity(this); 
    		return true;
    	case SET_MENU:
    		this.showDialog(2);
    		return true;
    	case EXIT_MENU:
    		finish();
    		return true;
    	}
    	return false;
    }    
    
    /*����FTP����*/
    private void startServer() {
    	Context context = getApplicationContext();
		Intent intent = new Intent(context,	FTPServerService.class);
		
		/*����FTP*/
		if(!FTPServerService.isRunning()) {
    		warnIfNoExternalStorage();
    		context.startService(intent);
    	}
    		
		// ��ʾFTP��ַ
    	InetAddress address =  FTPServerService.getWifiIp();
        if(address != null) {
        	ipText.setText(getString(R.string.notice) + address.getHostAddress() + 
    		              ":" + "7799"); /*��֪��Ϊë��Ĭ�϶˿ںŲ������ã�д����Ѿ��*/
    		//ipText.setText("test");
        	ftpstate.setText(R.string.server_start);
        	ipText.setVisibility(View.VISIBLE);
        		
        	/*�л�������ť��ֹͣ��ť*/ 
        	startButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.VISIBLE);
            
            Toast.makeText(FtpControlActivity.this, R.string.start_server,
					Toast.LENGTH_SHORT).show();
        } else {
        	Toast.makeText(FtpControlActivity.this, R.string.error_wifi, Toast.LENGTH_LONG).show();
        }      
    }
}
