package bum.boostcamp.alarmapp.util;

/**
 * Created by han sb on 2017-01-23.
 */

public class Utils {


    public static int getTimeAMPM(int hourOfday){
        if (hourOfday >= 12) return 1;
        else return 0;
    }


    public static int getTimeHour(int hourOfday){
        if(hourOfday>=12)return hourOfday-12;
        else return hourOfday;
    }

    public static String getTimeString(int hourOfday, int minute) {
        //시간과 분을 주면 그거에 대한 스트링을 출력한다.
        String strHour, strMin, strAMPM;
        if (hourOfday >= 12) strAMPM = "PM";
        else strAMPM = "AM";


        if (strAMPM.equals("PM")) {
            if (hourOfday != 12) hourOfday = hourOfday - 12;
        }

        if (hourOfday < 10) {
            strHour = "0" + String.valueOf(hourOfday);
        }
        else{
            strHour = String.valueOf(hourOfday);
        }
        if (minute < 10) {
            strMin = "0" + String.valueOf(minute);
        }
        else{
            strMin = String.valueOf(minute);
        }

        String result = strHour + ":"+strMin +" "+strAMPM;
        return result;
    }

    public static boolean[] getDayRepeat(String days){
        boolean[] repeatArr = new boolean[7];
        for(int i=0;i<7;i++){
            if(String.valueOf(days.charAt(i)).equals("1"))
                repeatArr[i] = true;
            else
                repeatArr[i] = false;
        }
        return repeatArr;
    }

    public static boolean isRepeat(boolean[] days){
        boolean isRepeat = false;
        for (int i = 0; i < 7; i++)
        {
            if (days[i])
            {
                isRepeat = true;
                break;
            }
        }
        return isRepeat;
    }


}
