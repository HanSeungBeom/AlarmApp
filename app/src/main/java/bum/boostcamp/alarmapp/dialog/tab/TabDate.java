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
import android.widget.DatePicker;

import bum.boostcamp.alarmapp.R;
import bum.boostcamp.alarmapp.dialog.RepeatDialog;

/**
 * Created by han sb on 2017-01-27.
 */

public class TabDate extends android.support.v4.app.Fragment {
    private Button mButton;
    private int mDateYear, mDateMonth ,mDateDays;
    private boolean mIsRepeat;
    private DatePicker mDataPicker;

    private BtnClickListener mHandler;

    public interface BtnClickListener{
        void onClickDaysBtn(DatePicker datePicker);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_date, container,false);
        mHandler =(BtnClickListener)getActivity();
        mButton = (Button)view.findViewById(R.id.btn_tab_ok);
        mDataPicker = (DatePicker)view.findViewById(R.id.datePicker);
        if(!mIsRepeat)
            mDataPicker.updateDate(mDateYear,mDateMonth,mDateDays);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("###","date click");
                mHandler.onClickDaysBtn(mDataPicker);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(getActivity()!=null && getActivity() instanceof RepeatDialog){
            mIsRepeat=((RepeatDialog) getActivity()).getRepeat();
            if(!mIsRepeat) {
                mDateYear = ((RepeatDialog) getActivity()).getYear();
                mDateMonth = ((RepeatDialog) getActivity()).getMonth();
                mDateDays = ((RepeatDialog) getActivity()).getDays();
            }
        }
    }
}
