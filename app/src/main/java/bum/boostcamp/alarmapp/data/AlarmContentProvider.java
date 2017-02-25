package bum.boostcamp.alarmapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static bum.boostcamp.alarmapp.data.AlarmContract.AlarmEntry.TABLE_NAME;

/**
 * Created by han sb on 2017-01-18.
 */

//작성 완료.

public class AlarmContentProvider extends ContentProvider {
    public static final int ALARMS = 100;
    public static final int ALARM_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static UriMatcher buildUriMatcher() {


        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AlarmContract.AUTHORITY, AlarmContract.PATH_ALARMS, ALARMS);
        uriMatcher.addURI(AlarmContract.AUTHORITY, AlarmContract.PATH_ALARMS + "/#", ALARM_WITH_ID);

        return uriMatcher;
    }

    private AlarmDbHelper mAlarmDbHelper;
    @Override
    public boolean onCreate() {

        Context context = getContext();
        mAlarmDbHelper = new AlarmDbHelper(context);
        return true;
    }
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mAlarmDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case ALARMS:
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(AlarmContract.AlarmEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }


    // Implement query to handle requests for data by URI
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mAlarmDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case ALARMS:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            case ALARM_WITH_ID:
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                retCursor = db.query(TABLE_NAME,
                        projection,
                        "_id=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }


    // Implement delete to delete a single row of data
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mAlarmDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int alaramDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case ALARM_WITH_ID:
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                alaramDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (alaramDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return alaramDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mAlarmDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int alaramUpdated;
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case ALARM_WITH_ID:
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                alaramUpdated = db.update(TABLE_NAME,values,"_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (alaramUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return alaramUpdated;
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
