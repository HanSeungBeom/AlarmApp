package bum.boostcamp.alarmapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

//작성 완료.

public class AlarmContract {
    public static final String AUTHORITY = "bum.boostcamp.alarmapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_ALARMS = "alarms";
    public static final class AlarmEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALARMS).build();

        public static final String TABLE_NAME = "alarms";
        public static final String COLUMN_AMPM = "ampm";                //오전,오후인지
        public static final String COLUMN_HOUR = "hour";                //시간 12시간 단위
        public static final String COLUMN_HOUR_OF_DAY = "hour_of_day"; //시간 24시간 단위
        public static final String COLUMN_MINUTES = "minutes";         //분
        public static final String COLUMN_DAYS = "days";                //알람 지정요일
        public static final String COLUMN_ACTIVE = "active";           //현재알람 활성화여부
        public static final String COLUMN_MEMO = "memo";                //알람시 작성한 메모
        public static final String COLUMN_DATE = "date";                //알람 저장한 날짜
        public static final String COLUMN_BELLTYPE = "belltype";        //0:소리 1:진동 2:소리+진동
        public static final String COLUMN_NO_REPEAT_YEAR = "nor_year"; //반복아닐때 날짜지정
        public static final String COLUMN_NO_REPEAT_MONTH = "nor_month";
        public static final String COLUMN_NO_REPEAT_DAYS ="nor_days";


    }
}
