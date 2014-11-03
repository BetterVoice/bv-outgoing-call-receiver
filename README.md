# Outgoing Call Broadcast Receiver Plugin (bv-outgoing-call-receiver)

## Quick summary
This is a Cordova plugin which creates a simple Android BroadcastReceiver used to receive `android.intent.action.NEW_OUTGOING_CALL` broadcasts in order to allow users to choose your App as a Dialer option.

## Getting started

All you need to do is add this plugin.

Note that in order for the App Chooser to display when making calls, you need to set the `BV_DialerIntegration` SharedPreference to `true`. For that, I reccomend this nice SharedPreferences plugin:
cordova plugin add me.apla.cordova.app-preferences

## Licence
Copyright 2013 BetterVoice.com
    
Licensed under the Apache License, Version 2.0 (the "License");   
you may not use this file except in compliance with the License.   
You may obtain a copy of the License at       
  
http://www.apache.org/licenses/LICENSE-2.0   
 
Unless required by applicable law or agreed to in writing, software   
distributed under the License is distributed on an "AS IS" BASIS,   
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   
See the License for the specific language governing permissions and   
limitations under the License.