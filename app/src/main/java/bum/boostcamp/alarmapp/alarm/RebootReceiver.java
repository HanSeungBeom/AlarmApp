package bum.boostcamp.alarmapp.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by han sb on 2017-01-24.
 */

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            //Log.d("###","리붓 완료");
            Intent i = new Intent(context,RestartAlarmService.class);
            context.startService(i);
        }
    }
}
