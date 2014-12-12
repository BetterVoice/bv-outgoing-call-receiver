/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.bettervoice.phonegap.plugin.outgoingCallReceiver;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.os.Parcelable;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
 
public class OutgoingCallReceiver extends BroadcastReceiver {
  public static String flag = "";
  @Override
  public void onReceive(Context context, Intent intent) {	
    // Extract phone number reformatted by previous receivers
    String phoneNumber = getResultData();
    // Extract original URI passed.
    String phoneURI = intent.getStringExtra("android.phone.extra.ORIGINAL_URI");
    if (phoneURI == null) {
      // No reformatted number, use the original
    	phoneURI = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);      
    }

    //Using Shared Preferences to allow the app to enable or disable the Dialer Integration
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
    String dialerIntegration = settings.getString("BV_DialerIntegration", "false");
    String dialerOverride = settings.getString("BV_DialerOverride", "false");
    Log.d("dialerIntegration", dialerIntegration);
    Log.d("dialerOverride", dialerOverride);
    Log.d("phoneURI", phoneURI);
    Log.d("phoneNumber", phoneNumber);
    Log.d("Flag", flag);
    if(!dialerIntegration.equals("false")) {
        if(!phoneURI.equals(flag) && !dialerOverride.equals("true")) {
            Log.d("OutboundCallReceiver", "Number: " + phoneNumber);     
            
            //If you just wanted a standard Chooser, you could use this.
            /*
            Intent callIntent = new Intent(Intent.ACTION_CALL);         
            callIntent.setData(Uri.parse(phoneNumber+",,0000"));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent chooser = Intent.createChooser(callIntent, "Call");
            chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
            setResultData(null);  
            */
            
            //This builds a customized Chooser with only 2 options: Your App, and the Standard Android Dialer            
            List<Intent> targetShareIntents=new ArrayList<Intent>();
            Intent shareIntent=new Intent(Intent.ACTION_CALL);
            shareIntent.setData(Uri.parse("tel:"+phoneNumber));
            List<ResolveInfo> resInfos=context.getPackageManager().queryIntentActivities(shareIntent, 0);
            if(!resInfos.isEmpty()){
                //Log.d("OutboundCallReceiver", "Have Packages");
                for(ResolveInfo resInfo : resInfos){
                    String packageName=resInfo.activityInfo.packageName;
                    //Log.d("OutboundCallReceiver", "Package Name:"+packageName);
                    if(packageName.contains("com.android.server.telecom") || packageName.contains("com.android.phone") || packageName.contains(context.getPackageName())){
                        Intent selectedCallIntent=new Intent();
                        selectedCallIntent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                        selectedCallIntent.setAction(Intent.ACTION_CALL);
                        selectedCallIntent.putExtra("BV_NumberToDial", "tel:"+phoneNumber);
                        //selectedCallIntent.setData(Uri.parse(phoneNumber+flag));
                        selectedCallIntent.setData(Uri.parse("tel:"+phoneNumber));
                        selectedCallIntent.setPackage(packageName);
                        selectedCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        targetShareIntents.add(selectedCallIntent);
                    }
                }
                if(!targetShareIntents.isEmpty()){
                    //Log.d("OutboundCallReceiver", "Have Intent to Share");
                    Intent chooserIntent=Intent.createChooser(targetShareIntents.remove(0), "Choose A Dialer");
                    chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                    context.startActivity(chooserIntent);
                    setResultData(null);
                    flag = phoneNumber;
                }
            }
            else {
                Log.d("OutboundCallReceiver", "No Packages Found");
            }
        }
        else {
            Log.d("OutboundCallReceiver", "Number contains flag. Ignoring and passing through.");
            settings.edit().putString("BV_DialerOverride", "false").commit();
            setResultData(phoneNumber);
            flag = "";
        }
    }
  }
}
