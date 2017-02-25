package bum.boostcamp.alarmapp.alarm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by han sb on 2017-01-24.
 */

public class RestartAlarmService extends IntentService{

    public RestartAlarmService(){
        super(RestartAlarmService.class.getName());
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("###","restartALARMSERVICE START");
        AlarmTask.ActiveAlarmFromDB(this);
    }
}
