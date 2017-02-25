package bum.boostcamp.alarmapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import bum.boostcamp.alarmapp.alarm.AlarmReceiver;
import bum.boostcamp.alarmapp.alarm.AlarmTask;
import bum.boostcamp.alarmapp.data.AlarmContract;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AlarmAdapter.AlarmAdpterOnClickHandler {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private AlarmAdapter mAlarmAdapter;

    private FloatingActionButton fab;
    private static final int ALARM_LOADER = 0;
    public static final int ACTIVE_ON = 1;
    public static final int ACTIVE_OFF = 0;
    public static final String EXTRA_URI = "uri";
    public static final int REQUEST_CODE_UPDATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // FakeData.insertFakeData(this);
        mEmptyView = (TextView)findViewById(R.id.empty_view);
        mAlarmAdapter = new AlarmAdapter(this, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.listview_alarm);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAlarmAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAlarmIntent = new Intent(MainActivity.this, AddAlaramActivity.class);
                startActivity(addAlarmIntent);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                int id = (int) viewHolder.itemView.getTag();

                String stringId = Integer.toString(id);
                Uri uri = AlarmContract.AlarmEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                getContentResolver().delete(uri, null, null);

                getSupportLoaderManager().restartLoader(ALARM_LOADER, null, MainActivity.this);


            }
        }).attachToRecyclerView(mRecyclerView);


        getSupportLoaderManager().initLoader(ALARM_LOADER, null, this);
        updateEmptyView();
    }

    public void updateEmptyView() {
        if(mAlarmAdapter.getItemCount()==0){
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);

        }
        else{
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                AlarmContract.AlarmEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAlarmAdapter.swapCursor(data);
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAlarmAdapter.swapCursor(null);
    }


    @Override
    public void onClickActiveIcon(Uri dataUri, boolean onOff) {
        //Log.d("###","ActiveIcon"+dataid);

        //ACTIVE 버튼 활성/비활성화 적용
        ContentValues cv = new ContentValues();
        int active = (onOff) ? ACTIVE_OFF : ACTIVE_ON;
        cv.put(AlarmContract.AlarmEntry.COLUMN_ACTIVE, active);//현재와 반대인 active값을 넣어준다.
        getContentResolver().update(dataUri, cv, null, null);

        //알람 매니저 활성/비활성화
        if (active == ACTIVE_ON) {
            AlarmTask.registAlarm(this, dataUri);

        } else {//ACTIVE_OFF
            AlarmTask.cancelAlarm(this, dataUri);
        }
        //cursorLoader로 연결되어 있어서 자동으로 notify가됨.
        mAlarmAdapter.notifyDataSetChanged();
        //getSupportLoaderManager().restartLoader(ALARM_LOADER, null, MainActivity.this);
    }

    @Override
    public void onClickOtherField(Uri dataUri) {
        // Log.d("###","otherIcon"+dataid);
        Intent intent = new Intent(this, UpdateAlarmActivity.class);
        intent.putExtra(EXTRA_URI, dataUri.toString());
        startActivityForResult(intent, REQUEST_CODE_UPDATE);
        //수정 페이지에서 확인 값받아오면 restart실행.
    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().restartLoader(ALARM_LOADER, null, this);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_UPDATE:
                    getSupportLoaderManager().restartLoader(ALARM_LOADER, null, this);
                    break;
            }

        }
    }
}
