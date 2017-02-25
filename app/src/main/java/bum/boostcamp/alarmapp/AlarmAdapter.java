package bum.boostcamp.alarmapp;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import bum.boostcamp.alarmapp.data.AlarmContract;
import bum.boostcamp.alarmapp.util.Utils;

/**
 * Created by han sb on 2017-01-22.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private Context mContext;
    private Cursor mCursor;


    private final AlarmAdpterOnClickHandler mClickHandler;
    private String[] days_weeks={
            "일",
            "월",
            "화",
            "수",
            "목",
            "금",
            "토"
    };

    public interface AlarmAdpterOnClickHandler{
        void onClickActiveIcon(Uri dataUri,boolean onOff);
        void onClickOtherField(Uri dataUri);
    }


    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_alarm,parent,false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        int newposition = getItemCount()-1-position; //새로 등록되는걸 위로 보여주기 위해서.
        mCursor.moveToPosition(newposition);

        int id = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry._ID));
        int hourOfDay = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_HOUR_OF_DAY));
        int minutes = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_MINUTES));
        int active = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_ACTIVE));
        String days = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_DAYS));
        String memo = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_MEMO));
        int belltype = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_BELLTYPE));
        int year = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_YEAR));
        int month = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_MONTH));
        int day = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NO_REPEAT_DAYS));
        boolean mWeeks[] = new boolean[7];

        holder.itemView.setTag(id);
        holder.timeView.setText(Utils.getTimeString(hourOfDay,minutes));

        if(memo.equals("")){
            holder.memoView.setText(memo);
        }
        else{
            holder.memoView.setText(memo);
        }
        mWeeks = getWeeks(days);
        boolean repeat = isRepeat(days);


        if(repeat){
            String str = "";
            for(int i=0;i<7;i++){
                if(mWeeks[i]){
                    str = str +" "+days_weeks[i];
                }
            }

            holder.dateView.setText(str.trim());
         }
        else{
            holder.dateView.setText("["+year+"."+month+"."+day+"]");
        }

        switch(belltype){
            case AddAlaramActivity.BELLTYPE_RING:
                Picasso.with(mContext).load(R.mipmap.belltype_ring_low).into(holder.bellTypeView);
                break;
            case AddAlaramActivity.BELLTYPE_VIBRATION:
                Picasso.with(mContext).load(R.mipmap.belltype_vib_low).into(holder.bellTypeView);
                break;
            case AddAlaramActivity.BELLTYPE_RING_AND_VIBRATION:
                Picasso.with(mContext).load(R.mipmap.belltype_ring_vib_low).into(holder.bellTypeView);
                break;
            default:
        }



        if(active==0)
            Picasso.with(mContext).load(R.drawable.off).into(holder.activeView);
        else{
            Picasso.with(mContext).load(R.drawable.on).into(holder.activeView);
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView timeView;
        ImageView bellTypeView;

        ImageView memoImageView;
        TextView memoView;

        TextView dateView;

        ImageView[] days;
        ImageView activeView;



        public AlarmViewHolder(View itemView){
            super(itemView);
            days = new ImageView[7];
            timeView = (TextView)itemView.findViewById(R.id.list_item_tv_time);
            bellTypeView = (ImageView)itemView.findViewById(R.id.list_item_iv_belltype);
            memoView = (TextView)itemView.findViewById(R.id.list_item_tv_memo);
            dateView = (TextView)itemView.findViewById(R.id.list_item_tv_alarmdate);

            activeView = (ImageView)itemView.findViewById(R.id.list_item_active_icon);
            itemView.setOnClickListener(this);
            activeView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int viewid = v.getId();
            int adapterPosition = getAdapterPosition();
            int adapterNewPosition = getItemCount()-1-adapterPosition;

            mCursor.moveToPosition(adapterNewPosition);
            int dataid = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry._ID));
            Uri uri = AlarmContract.AlarmEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(String.valueOf(dataid)).build();

            if(viewid == R.id.list_item_active_icon){
                int isactive=mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_ACTIVE));
                boolean active = (isactive==1)?true:false;
                mClickHandler.onClickActiveIcon(uri,active);
            }else{
                mClickHandler.onClickOtherField(uri);
            }
        }
    }

    public AlarmAdapter(Context context, AlarmAdpterOnClickHandler clickHandler){
        mContext=context;
        mClickHandler = clickHandler;
    }


    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    private boolean[] getWeeks(String str){
        //저장한 "1000111"같은 문자를 mWeek에다가 true,false,false,false,true,true,true 로 넣어주는 함수
        boolean[] weeks = new boolean[7];
        for(int i=0;i<7;i++){
            if(str.charAt(i)=='1'){
                weeks[i]=true;
            }
            else{
                weeks[i]=false;
            }
        }
        return weeks;
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

}
