package co.schrodingertech.safeveremotemonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

import java.util.List;

/**
 * Created by Ajwad on 27-02-2018.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Get Bundle object contained in the SMS intent passed in
        Bundle bundle = intent.getExtras();
        SmsMessage[] smsm = null;
        String sms_str ="";

        DBHandler db = new DBHandler(context);
        List<Users> users = db.getAllUsers();

        if (bundle != null)
        {
            // Get the SMS message
            Object[] pdus = (Object[]) bundle.get("pdus");
            smsm = new SmsMessage[pdus.length];
            for (int i=0; i<smsm.length; i++){
                smsm[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String Sender = smsm[i].getOriginatingAddress();
                sms_str += Sender;
                boolean check = false;
                for(Users u: users){
                    String tref = u.getAddress();
                    if(tref.equals(Sender))
                    {
                        check = true;
                        sms_str += (" " + u.getName() + " ");
                        break;
                    }
                }
                if(check) {
                    //sms_str += " ";
                    sms_str += ("\n"+smsm[i].getMessageBody().toString());
                    //sms_str += " ";
                    Intent smsIntent = new Intent("otp");
                    smsIntent.putExtra("message", sms_str);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
                }
            }
        }
    }
}
