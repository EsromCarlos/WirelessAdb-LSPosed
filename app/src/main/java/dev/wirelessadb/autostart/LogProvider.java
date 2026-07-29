package dev.wirelessadb.autostart;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

/** 供 system_server 钩子写入运行记录（以应用 UID 落盘）。 */
public final class LogProvider extends ContentProvider {
    public static final String AUTHORITY = "dev.wirelessadb.autostart.log";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY);
    public static final String METHOD_APPEND = "append";

    @Override public boolean onCreate() {
        return true;
    }

    @Override public Bundle call(String method, String arg, Bundle extras) {
        if (METHOD_APPEND.equals(method) && arg != null && !arg.isEmpty()) {
            EventLog.append(getContext(), arg);
        }
        return Bundle.EMPTY;
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection,
                                  String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override public String getType(Uri uri) {
        return null;
    }

    @Override public Uri insert(Uri uri, ContentValues values) {
        if (values != null) {
            String line = values.getAsString("line");
            if (line != null && !line.isEmpty()) EventLog.append(getContext(), line);
        }
        return uri;
    }

    @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override public int update(Uri uri, ContentValues values, String selection,
                                String[] selectionArgs) {
        return 0;
    }
}
