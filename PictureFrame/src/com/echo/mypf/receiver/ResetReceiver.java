package com.echo.mypf.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
//no used
public class ResetReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String  action = intent.getAction();  
	    if(action.equals("android.intent.action.MASTER_CLEAR"))  
	    {  
	    	PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//	        PowerManager.materClear(context); 
	    } 
	}

}
