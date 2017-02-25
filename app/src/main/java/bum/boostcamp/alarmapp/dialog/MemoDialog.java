package bum.boostcamp.alarmapp.dialog;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import bum.boostcamp.alarmapp.AddAlaramActivity;
import bum.boostcamp.alarmapp.R;

public class MemoDialog extends Activity {

    private String mMemo;
    private EditText mMemoView;
    private Button mOkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_memo);

        mMemoView = (EditText)findViewById(R.id.dialog_et_memo);
        mOkBtn = (Button)findViewById(R.id.dialog_memo_btn);
        setMemo();

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushMemo();
            }
        });
    }

    private void setMemo(){
        Intent intent = getIntent();
        mMemo = intent.getStringExtra(AddAlaramActivity.EXTRA_MEMO);
        mMemoView.setText(mMemo);
    }
    private void pushMemo(){
        Intent intent = getIntent();
        intent.putExtra(AddAlaramActivity.EXTRA_MEMO,mMemoView.getText().toString().trim());
        setResult(RESULT_OK,intent);
        finish();
    }
}
