package bum.boostcamp.alarmapp.dialog.tab;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import bum.boostcamp.alarmapp.R;
import bum.boostcamp.alarmapp.dialog.RepeatDialog;

/**
 * Created by han sb on 2017-01-27.
 */

public class TabDays extends android.support.v4.app.Fragment {
    private Button mButton;

    private boolean[] mWeeks;
    private BtnClickListener mHandler;
    private CheckBox[] mCheckbox;
    private boolean mIsRepeat;

    public interface BtnClickListener{
        void onClickDaysBtn(boolean[] weeks);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_days, container,false);
        mCheckbox = new CheckBox[7];
        mHandler = (BtnClickListener)getActivity();
        mCheckbox[0] = (CheckBox)view.findViewById(R.id.cb_sun);
        mCheckbox[1] = (CheckBox)view.findViewById(R.id.cb_mon);
        mCheckbox[2] = (CheckBox)view.findViewById(R.id.cb_tue);
        mCheckbox[3] = (CheckBox)view.findViewById(R.id.cb_wed);
        mCheckbox[4] = (CheckBox)view.findViewById(R.id.cb_thu);
        mCheckbox[5] = (CheckBox)view.findViewById(R.id.cb_fri);
        mCheckbox[6] = (CheckBox)view.findViewById(R.id.cb_sat);


        mButton = (Button)view.findViewById(R.id.btn_tab_ok);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("###","days click");
                for(int i=0;i<7;i++){
                    if(mCheckbox[i].isChecked())mWeeks[i]=true;
                    else mWeeks[i]=false;
                }
                mHandler.onClickDaysBtn(mWeeks);
            }
        });

        if(!mIsRepeat) {
            clearWeeks();
        }
        else{
            setWeeks();
        }
        return view;
    }

    private void setWeeks(){
        for(int i=0;i<7;i++){
            mCheckbox[i].setChecked(mWeeks[i]);
        }
    }
    private void clearWeeks(){
        for(int i=0;i<7;i++){
            mCheckbox[i].setChecked(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(getActivity()!=null && getActivity() instanceof RepeatDialog){
            mIsRepeat=((RepeatDialog) getActivity()).getRepeat();
            mWeeks =((RepeatDialog) getActivity()).getWeeks();
        }
    }
}
