package bum.boostcamp.alarmapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import bum.boostcamp.alarmapp.alarm.AlarmReceiver;
import bum.boostcamp.alarmapp.data.AlarmContract;

import static android.media.AudioManager.FLAG_ALLOW_RINGER_MODES;
import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static bum.boostcamp.alarmapp.util.Utils.getTimeAMPM;
import static bum.boostcamp.alarmapp.util.Utils.getTimeHour;
import static bum.boostcamp.alarmapp.util.Utils.getTimeString;

public class AlarmScreen extends AppCompatActivity {

     public static final int BELLTYPE_RING =0;
    public static final int BELLTYPE_VIBRATION =1;
    public static final int BELLTYPE_RING_AND_VIBRATION = 2;

    private final long SECONDS_FOR_VIB =1;
    private final long VIB_TIME_FOR_MILLISECONDS = SECONDS_FOR_VIB*1000;
    private final int VIB_REPEAT = 5;

    private TextView mTimeTextView;
    private int mHourofday,mMinute;
    private ImageView mRingTypeImageView,mRepeatImageView,mMemoImageView;
    private TextView mRingTypeTextView,mRepeatTextView,mMemoTextView;
    //
    private int mBelltype;
    //
    private boolean mIsRepeat;
    private boolean[] mWeeks;
    private int mDateYear, mDateMonth ,mDateDays;
    //
    private String mMemo;

    private String[] mRingTypeString={"소리","진동","소리+진동"};
    private String[] mWeeksString ={"일","월","화","수","목","금","토"};
    private String mWeekBinary;

    private Uri mUri;
    private Ringtone mRingtone;
    private Vibrator mVibrator;
    private AudioManager mAudioManager;
    private int firstState; //처음 진동모드이면 나중에 다시 진동모드로 바꿔주기 위해서.
    private int firstStateVolume; //처음 알람 벨소리 크기
    long[] pattern = { 0, 500, 200, 400, 100 };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);
        mWeeks = new boolean[7];
        mTimeTextView = (TextView)findViewById(R.id.tv_time);
        mRingTypeImageView =(ImageView)findViewById(R.id.iv_bellType);
        mRepeatImageView =(ImageView)findViewById(R.id.iv_repeat);
        mMemoImageView =(ImageView)findViewById(R.id.iv_memo) ;
        mRingTypeTextView=(TextView)findViewById(R.id.tv_bellType);
        mRepeatTextView = (TextView)findViewById(R.id.tv_repeat);
        mMemoTextView = (TextView)findViewById(R.id.tv_memo);

        init();

        //잠금화면 위로 액티비티 나오게.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        //알람중 화면 안꺼지게
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        mRingtone = RingtoneManager.getRingtone(this, alarmUri);
        mVibrator =(Vibrator)getSystemService(VIBRATOR_SERVICE);

        mAudioManager =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        firstState = mAudioManager.getRingerMode();
        firstStateVolume =mAudioManager.getStreamVolume(AudioManager.STREAM_RING);

        switch (mBelltype){
            case BELLTYPE_RING:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
                        mAudioManager.getStreamVolume(AudioManager.STREAM_RING),
                        AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
                mRingtone.play();
                break;
            case BELLTYPE_VIBRATION:
                mVibrator.vibrate(pattern,0);
                break;
            case BELLTYPE_RING_AND_VIBRATION:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                mRingtone.play();
                mVibrator.vibrate(pattern,0);
                break;
            default:
        }



    }


    //앱이 종료되거나 사용자가 멈추면 알람을 종료하고,
    //앱을 다시 진동모드로 변경해놓고 알람 소리도 원래대로 돌려놓는다.
    @Override
    protected void onDestroy() {

         super.onDestroy();

         if(mRingtone.isPlaying()){
             mRingtone.stop();
         }
        if(mVibrator.hasVibrator()){
            mVibrator.cancel();
        }
        mAudioManager.setRingerMode(firstState);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
                firstStateVolume,
                AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
    }

    private void init(){
        Intent i = getIntent();
        String uriStr = i.getStringExtra(AlarmReceiver.EXTRA_URI);
        mUri = Uri.parse(uriStr);
        Cursor cursor = getContentResolver().query(mUri,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        mBelltype = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_BELLTYPE));
        mWeekBinary =cursor.getString(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_DAYS));
        mIsRepeat = isRepeat(mWeekBinary);
        setMweeks();


        mHourofday = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_HOUR_OF_DAY));
        mMinute = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_MINUTES));
        mDateYear =cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_YEAR));
        mDateMonth =cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_MONTH));
        mDateDays = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_DAYS));
        mTimeTextView.setText(getTimeString(mHourofday,mMinute));

        mMemo = cursor.getString(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_MEMO));

        Log.d("###",mBelltype+"/"+mWeekBinary+"/"+mIsRepeat);
        setBellTypeData();
        setRepeatData();
        setMemoData();
    }


    private void setTime(){
        TimePickerDialog dialog = new TimePickerDialog(this, listener, mHourofday, mMinute, false);
        dialog.show();
    }
    private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mTimeTextView.setText(getTimeString(hourOfDay,minute));
            mHourofday = hourOfDay;
            mMinute = minute;
        }
    };

    private void setBellTypeData() {
        switch(mBelltype){
            case BELLTYPE_RING:
                Picasso.with(this).load(R.mipmap.belltype_ring).into(mRingTypeImageView);
                mRingTypeTextView.setText(mRingTypeString[BELLTYPE_RING]);
                break;
            case BELLTYPE_VIBRATION:
                Picasso.with(this).load(R.mipmap.belltype_vib).into(mRingTypeImageView);
                mRingTypeTextView.setText(mRingTypeString[BELLTYPE_VIBRATION]);
                break;
            case BELLTYPE_RING_AND_VIBRATION:
                Picasso.with(this).load(R.mipmap.belltype_ring_vib).into(mRingTypeImageView);
                mRingTypeTextView.setText(mRingTypeString[BELLTYPE_RING_AND_VIBRATION]);
                 break;
            default:

        }
    }

    private void setRepeatData(){
        if(!mIsRepeat){
            Picasso.with(this).load(R.mipmap.repeat_off).into(mRepeatImageView);
            mRepeatTextView.setText(mDateYear+"/"+mDateMonth+"/"+mDateDays +"\n한번 반복");
            mWeekBinary ="0000000";
        }
        else{
            Picasso.with(this).load(R.mipmap.repeat).into(mRepeatImageView);
            String binaryWeek="";
            mRepeatTextView.setText("");
            for(int i=0;i<7;i++){
                if(mWeeks[i]) {
                    binaryWeek= binaryWeek +"1";
                    mRepeatTextView.append(mWeeksString[i]);
                }
                else{
                    binaryWeek=binaryWeek +"0";
                }
            }
            mWeekBinary = binaryWeek;
        }
    }

    private void setMemoData(){
        if(mMemo.equals("")){
            Picasso.with(this).load(R.mipmap.memo_off).into(mMemoImageView);
            mMemoTextView.setText(getString(R.string.add_alaram_tv_memo));
        }
        else{
            Picasso.with(this).load(R.mipmap.memo_on).into(mMemoImageView);
            mMemoTextView.setText(mMemo);
        }
    }

    private boolean isRepeat(String weekBinaryStr){
        boolean isRepeat = false;
        for(int i=0;i<7;i++){
            if(weekBinaryStr.charAt(i)=='1'){
                isRepeat=true;
                break;
            }
        }
        return isRepeat;
    }

    private void setMweeks(){
        //저장한 "1000111"같은 문자를 mWeek에다가 true,false,false,false,true,true,true 로 넣어주는 함수
        for(int i=0;i<7;i++){
            if(mWeekBinary.charAt(i)=='1'){
                mWeeks[i]=true;
            }
            else{
                mWeeks[i]=false;
            }
        }
    }
    public void alarmBtn(View view) {
        finish();
    }

}
