package bum.boostcamp.alarmapp.dialog.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by han sb on 2017-01-27.
 */

public class Pager extends FragmentStatePagerAdapter{
    int tabCount;

    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.d("###","tabdate생성");
                TabDate tabDate = new TabDate();
                return tabDate;
            case 1:
                Log.d("###","tab생성days");
                TabDays tabDays = new TabDays();
                return tabDays;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
