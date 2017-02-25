package bum.boostcamp.alarmapp.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import bum.boostcamp.alarmapp.R;
import bum.boostcamp.alarmapp.data.AlarmContract;
import bum.boostcamp.alarmapp.util.Utils;

/**
 * Created by han sb on 2017-01-23.
 */


public class AlarmTask {

    public static void registAlarm(Context context, Cursor cursor)
    //REBOOT시 서비스에 의해 호출되는 메소드
    //cursor로 들어온 알람들을 모두 활성화 시킨다.
    //cursor에는 ACTIVE한 알람들만 검색해서 보내줘야한다.
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (cursor.getCount() != 0) {

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);


                int year = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_YEAR));
                int month = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_MONTH));
                int day = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_DAYS));

                int alarmid = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry._ID));
                int hourOfDay = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_HOUR_OF_DAY));
                int minute = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_MINUTES));
                String days = cursor.getString(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_DAYS));
                Uri targetUri = ContentUris.withAppendedId(AlarmContract.AlarmEntry.CONTENT_URI, alarmid);

                boolean[] week = Utils.getDayRepeat(days);
                boolean isRepeat = Utils.isRepeat(week);

                // 알람 등록
                Intent intent = new Intent(context, AlarmReceiver.class);

                long triggerTime = 0;
                long intervalTime = 24 * 60 * 60 * 1000;// 24시간
                if (isRepeat) {
                    intent.putExtra("one_time", false);
                    intent.putExtra("day_of_week", week);
                    intent.putExtra("target_uri", targetUri.toString());
                    PendingIntent pending = PendingIntent.getBroadcast(context, alarmid, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    triggerTime = setTriggerTime(hourOfDay, minute);

                    if (Build.VERSION.SDK_INT >= 23) {
                        //doze 모드 대응
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pending);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pending);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pending);
                    }
                   Log.d("###", "hourOfDay=" + hourOfDay + "/minute=" + minute + "/days" + days + ":::ADDED");

                } else {
                    intent.putExtra("one_time", true);
                    intent.putExtra("target_uri", targetUri.toString());
                    PendingIntent pending = PendingIntent.getBroadcast(context, alarmid, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    triggerTime = setTriggerTime(year, month, day, hourOfDay, minute);


                    //alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pending);

                    if (Build.VERSION.SDK_INT >= 23) {
                        //doze 모드 대응
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pending);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pending);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pending);
                    }
                    Log.d("###", "hourOfDay=" + hourOfDay + "/minute=" + minute + "/days" + days + ":::ADDED");

                }
            }


        }


    }

    //앱에서 알람을 등록 했을시 호출되는 메소드
    public static void registAlarm(Context context, Uri targetUri) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int alarmid = Integer.parseInt(targetUri.getPathSegments().get(1));

        //ID가 같은 알람이라면 해당 알람취소.
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, alarmid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pending);

        Cursor cursor = context.getContentResolver().query(targetUri, null, null, null, null);
        cursor.moveToFirst();

        int year = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_YEAR));
        int month = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_MONTH));
        int day = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_DAYS));

        int hourOfDay = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_HOUR_OF_DAY));
        int minute = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_MINUTES));
        String days = cursor.getString(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_DAYS));


        boolean[] week = Utils.getDayRepeat(days);
        boolean isRepeat = Utils.isRepeat(week);

        // 알람 등록
        intent = new Intent(context, AlarmReceiver.class);
        long triggerTime = 0;
        long intervalTime = 24 * 60 * 60 * 1000;// 24시간
        if (isRepeat) {
            intent.putExtra("one_time", false);
            intent.putExtra("day_of_week", week);
            intent.putExtra("target_uri", targetUri.toString());
            pending = PendingIntent.getBroadcast(context, alarmid, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            triggerTime = setTriggerTime(hourOfDay, minute);
            if (Build.VERSION.SDK_INT >= 23) {
                //doze 모드 대응
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pending);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pending);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pending);
            }

        } else {
            intent.putExtra("one_time", true);
            intent.putExtra("target_uri", targetUri.toString());
            pending = PendingIntent.getBroadcast(context, alarmid, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            triggerTime = setTriggerTime(year, month, day, hourOfDay, minute);

            //alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pending);

            if (Build.VERSION.SDK_INT >= 23) {
                //doze 모드 대응
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pending);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pending);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pending);
            }

        }
        Toast.makeText(context, context.getString(R.string.alarm_time_set_toast), Toast.LENGTH_SHORT);

    }

    public static long setTriggerTime(int year, int month, int days, int hourOfday, int minutes) {
        // current Time
        long atime = System.currentTimeMillis();
        // timepicker
        Calendar curTime = Calendar.getInstance();
        curTime.set(Calendar.YEAR, year);
        curTime.set(Calendar.MONTH, month - 1); // 0~11이므로 1뺴야함
        curTime.set(Calendar.DAY_OF_MONTH, days);
        curTime.set(Calendar.HOUR_OF_DAY, hourOfday);
        curTime.set(Calendar.MINUTE, minutes);
        curTime.set(Calendar.SECOND, 0);
        curTime.set(Calendar.MILLISECOND, 0);

        long btime = curTime.getTimeInMillis();
        long triggerTime = btime;


        if (atime > btime)
            triggerTime += 1000 * 60 * 60 * 24;


        return triggerTime;
    }

    public static long setTriggerTime(int hourOfday, int minutes) {
        // current Time
        long atime = System.currentTimeMillis();
        // timepicker
        Calendar curTime = Calendar.getInstance();

        curTime.set(Calendar.HOUR_OF_DAY, hourOfday);
        curTime.set(Calendar.MINUTE, minutes);
        curTime.set(Calendar.SECOND, 0);
        curTime.set(Calendar.MILLISECOND, 0);

        long btime = curTime.getTimeInMillis();
        long triggerTime = btime;


        if (atime > btime)
            triggerTime += 1000 * 60 * 60 * 24;


        return triggerTime;
    }

    public static void cancelAlarm(Context context, Uri targetUri) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int alarmid = Integer.parseInt(targetUri.getPathSegments().get(1));
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, alarmid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pending);
    }

    public static void ActiveAlarmFromDB(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(AlarmContract.AlarmEntry.CONTENT_URI,
                null,
                AlarmContract.AlarmEntry.COLUMN_ACTIVE + "=?",
                new String[]{"1"},
                null);
        registAlarm(context, cursor);
    }


}