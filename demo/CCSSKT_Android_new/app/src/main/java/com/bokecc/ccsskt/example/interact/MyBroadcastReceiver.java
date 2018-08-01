package com.bokecc.ccsskt.example.interact;



import android.app.Service;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.SubscribeRemoteStream;


/**
 *
 * Created by wdh on 2018/3/10.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {


    private static MyBroadcastReceiver instance = null;
    private AudioManager audioManager;
    private  boolean flag = true;
    private boolean isAudioFlag = false;
    private MyBroadcastReceiver() {
    }

    public static MyBroadcastReceiver getInstance() {
        if (instance == null) {
            synchronized (MyBroadcastReceiver.class) {
                if (instance == null) {
                    instance = new MyBroadcastReceiver();
                }
            }
        }
        return instance;
    }

    public void initial(AudioManager mAudioManager) {
       audioManager = mAudioManager;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        audioManager.setSpeakerphoneOn(true);
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent
                    .getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            if (tm != null) {
                tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }
        if (action.equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)) {
            int scoAudioState = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
            if (scoAudioState == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
                // This method should be called only after SCO is connected!
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setBluetoothScoOn(true);
                audioManager.startBluetoothSco();
                flag = false;
            }
        }
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
            resetSco();
        } else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            if (intent.getExtras().getInt(BluetoothHeadset.EXTRA_STATE) == BluetoothHeadset.STATE_DISCONNECTED) {

            } else if (intent.getExtras().getInt(BluetoothHeadset.EXTRA_STATE) == BluetoothHeadset.STATE_CONNECTED) {

                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setBluetoothScoOn(true);
                audioManager.startBluetoothSco();
                flag = false;
            } else if (intent.getExtras().getInt(BluetoothHeadset.EXTRA_STATE) == BluetoothHeadset.STATE_DISCONNECTING) {

            } else if (intent.getExtras().getInt(BluetoothHeadset.EXTRA_STATE) == BluetoothHeadset.STATE_CONNECTING) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setBluetoothScoOn(true);
                audioManager.startBluetoothSco();
                flag = false;

            }
        } else if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", 0);
            if (state == 0) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setSpeakerphoneOn(true);
                flag = true;
            } else if (state == 1) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                flag = false;
                audioManager.setSpeakerphoneOn(false);
            }
        }
    }
    private void resetSco() {
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
        audioManager.setWiredHeadsetOn(false);
        audioManager.setSpeakerphoneOn(true);
        flag = true;
    }

    PhoneStateListener listener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE:
                    if(isAudioFlag){
                        CCInteractSession.getInstance().enableAudio(true);
                        CCInteractSession.getInstance().setSubStreamAudio(true);
                        isAudioFlag = false;
                    }
                    if(flag){
                        audioManager.setSpeakerphoneOn(true);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    CCInteractSession.getInstance().disableAudio(true);
                    CCInteractSession.getInstance().setSubStreamAudio(false);
                    audioManager.setSpeakerphoneOn(false);
                    isAudioFlag = true;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    CCInteractSession.getInstance().disableAudio(true);
                    CCInteractSession.getInstance().setSubStreamAudio(false);
                    audioManager.setSpeakerphoneOn(false);
                    isAudioFlag = true;
                    break;
            }
        }
    };
}
