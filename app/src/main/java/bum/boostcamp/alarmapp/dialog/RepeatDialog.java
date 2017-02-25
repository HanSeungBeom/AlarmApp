package bum.boostcamp.alarmapp.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import bum.boostcamp.alarmapp.R;
import bum.boostcamp.alarmapp.dialog.tab.Pager;
import bum.boostcamp.alarmapp.dialog.tab.TabDate;
import bum.boostcamp.alarmapp.dialog.tab.TabDays;

import static bum.boostcamp.alarmapp.AddAlaramActivity.EXTRA_DAYS;
import static bum.boostcamp.alarmapp.AddAlaramActivity.EXTRA_IS_REPEAT;
import static bum.boostcamp.alarmapp.AddAlaramActivity.EXTRA_MONTH;
import static bum.boostcamp.alarmapp.AddAlaramActivity.EXTRA_WEEKS;
import static bum.boostcamp.alarmapp.AddAlaramActivity.EXTRA_YEAR;

/**
 * Created by han sb on 2017-01-27.
 */

public class RepeatDialog extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,
        TabDays.BtnClickListener,
        TabDate.BtnClickListener{
    private boolean[] mWeeks;
    private boolean mIsRepeat;
    private int mDateYear, mDateMonth ,mDateDays;

    private CheckBox[] mWeeksCheckBox;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private DatePicker mDatePicker;
    private Button mBtnOK;
    private static View.OnClickListener mButtonClick = new View.OnClickListener(){
        public void onClick(View v){
            switch(v.getId())
            {
                case R.id.btn_tab_ok:
                    Log.d("##","ddddasdf");
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_repeat);

        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText("날짜"));
        mTabLayout.addTab(mTabLayout.newTab().setText("요일"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mViewPager = (ViewPager)findViewById(R.id.pager);
        Pager adapter = new Pager(getSupportFragmentManager(),mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mTabLayout.addOnTabSelectedListener(this);


        mWeeksCheckBox = new CheckBox[7];
        mWeeks = new boolean[7];

        //AddalarmActivity에서 데이터 받아오기.
        Intent intent = getIntent();
        mWeeks = intent.getBooleanArrayExtra(EXTRA_WEEKS);
        mIsRepeat = intent.getBooleanExtra(EXTRA_IS_REPEAT,false);
        mDateYear = intent.getIntExtra(EXTRA_YEAR,1111);
        mDateMonth = intent.getIntExtra(EXTRA_MONTH,11);
        mDateDays =intent.getIntExtra(EXTRA_DAYS,11);


        if(mIsRepeat){
            mViewPager.setCurrentItem(2);
            mTabLayout.setupWithViewPager(mViewPager);
            //이걸 안하면 텍스트가 사라진다.
            mTabLayout.getTabAt(0).setText("날짜");
            mTabLayout.getTabAt(1).setText("요일");
        }


    }
    public boolean[] getWeeks(){
        return mWeeks;
    }
    public int getYear(){return mDateYear;}
    public int getMonth(){return mDateMonth;}
    public int getDays(){return mDateDays;}
    public boolean getRepeat(){return mIsRepeat;}

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private boolean isWeekSelected(){
        boolean isweek = false;
        for(int i=0;i<7;i++){
            if(mWeeks[i]){
                isweek = true;
                break;
            }
        }
        return isweek;
    }

    @Override
    public void onClickDaysBtn(boolean[] weeks) {
        mWeeks = weeks;
        if(!isWeekSelected()){
            Toast.makeText(this, getString(R.string.pick_week_day), Toast.LENGTH_SHORT).show();
            return;
        }
        mIsRepeat = true;
        Intent intent = getIntent();
        intent.putExtra(EXTRA_IS_REPEAT,mIsRepeat);
        intent.putExtra(EXTRA_WEEKS,weeks);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onClickDaysBtn(DatePicker datePicker) {
        mDateYear = datePicker.getYear();
        mDateMonth = datePicker.getMonth()+1;
        mDateDays = datePicker.getDayOfMonth();
        mIsRepeat = false;
        Intent intent = getIntent();
        intent.putExtra(EXTRA_IS_REPEAT,mIsRepeat);
        intent.putExtra(EXTRA_YEAR,mDateYear);
        intent.putExtra(EXTRA_MONTH,mDateMonth);
        intent.putExtra(EXTRA_DAYS,mDateDays);

        setResult(RESULT_OK,intent);
        finish();
    }
}
