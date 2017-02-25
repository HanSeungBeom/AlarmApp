package bum.boostcamp.alarmapp.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import bum.boostcamp.alarmapp.AddAlaramActivity;
import bum.boostcamp.alarmapp.R;

public class BellTypeDialog extends Activity implements View.OnClickListener {
    private int mBellType;
    private ImageView mRingView,mVibView,mRingVibView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_belltype);
        mRingView = (ImageView)findViewById(R.id.dialog_iv_belltype_ring);
        mVibView = (ImageView)findViewById(R.id.dialog_iv_belltype_vib);
        mRingVibView = (ImageView)findViewById(R.id.dialog_iv_belltype_ring_vib);
        mRingView.setOnClickListener(this);
        mVibView.setOnClickListener(this);
        mRingVibView.setOnClickListener(this);
        //initScreen();
    }


    private void initScreen(){
        Point pt = new Point();
        getWindowManager().getDefaultDisplay().getSize(pt);
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(pt);
        int deviceHeight = pt.x;
        int deviceWidth = pt.y;

        getWindow().getAttributes().width = (int) (deviceWidth*0.5);
        getWindow().getAttributes().height = (int)(deviceHeight * 0.5);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.dialog_iv_belltype_ring:
                mBellType = AddAlaramActivity.BELLTYPE_RING;
                pushBellType();
                break;
            case R.id.dialog_iv_belltype_vib:
                mBellType = AddAlaramActivity.BELLTYPE_VIBRATION;
                pushBellType();
                break;
            case R.id.dialog_iv_belltype_ring_vib:
                mBellType = AddAlaramActivity.BELLTYPE_RING_AND_VIBRATION;
                pushBellType();
                break;
        }

    }
    private void pushBellType(){
        Intent intent = getIntent();
        intent.putExtra(AddAlaramActivity.EXTRA_BELLTYPE, mBellType);
        setResult(RESULT_OK,intent);
        finish();
    }
}
