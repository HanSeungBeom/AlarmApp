package bum.boostcamp.alarmapp.alarm;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

import bum.boostcamp.alarmapp.AlarmScreen;
import bum.boostcamp.alarmapp.data.AlarmContract;

/**
 * Created by han sb on 2017-01-23.
 */

public class AlarmReceiver extends BroadcastReceiver
{

    private static PowerManager.WakeLock sCpuWakeLock;

    public static final String EXTRA_URI = "uri";
    @Override
    public void onReceive(Context context, Intent intent)
    {

        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "hi");

        sCpuWakeLock.acquire();

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }



        Bundle extra = intent.getExtras();
        if (extra != null)
        {
            boolean isOneTime = extra.getBoolean("one_time");
            Uri uri = Uri.parse(extra.getString("target_uri"));

            if (isOneTime)
            {

                // 알람 액티비티.
                Intent i = new Intent(context,AlarmScreen.class);
                i.putExtra(EXTRA_URI,uri.toString());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);

                //알람이 울리면 해당 DB의 active는 off하기.
                ContentValues cv = new ContentValues();
                cv.put(AlarmContract.AlarmEntry.COLUMN_ACTIVE,0);
                context.getContentResolver().update(uri,cv,null,null);

            }
            else
            {

                boolean[] week = extra.getBooleanArray("day_of_week");

                Calendar cal = Calendar.getInstance();
                //반복인 경우에 알람을 다시 등록.
                AlarmTask.registAlarm(context,uri);
                //Log.d("###","일루옴 calandar.dayweek"+cal.get(Calendar.DAY_OF_WEEK));
                if (!week[cal.get(Calendar.DAY_OF_WEEK)-1])
                    return;


                Intent i = new Intent(context,AlarmScreen.class);
                i.putExtra(EXTRA_URI,uri.toString());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);
                //알람울리기.

            }
        }
    }
}