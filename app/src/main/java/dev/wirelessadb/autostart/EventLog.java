package dev.wirelessadb.autostart;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 运行记录。
 * system_server 钩子写 Settings.Global（开机早期可靠）；
 * App/Receiver 写 device-protected prefs；界面合并两者。
 */
public final class EventLog {
    public static final String PREFS = "events";
    public static final String KEY_LOG = "log";
    public static final String KEY_LAST_ADDRESS = "last_address";
    public static final String GLOBAL_LOG = "wirelessadb_autostart_log";
    public static final String GLOBAL_LAST_ADDRESS = "wirelessadb_autostart_last_address";
    private static final int MAX_CHARS = 16384;
    private static final Pattern ADDRESS =
            Pattern.compile("(\\d{1,3}(?:\\.\\d{1,3}){3}:\\d{1,5})");

    private EventLog() {}

    /** 供 system_server 钩子：写入 Settings.Global。 */
    public static boolean appendGlobal(Context systemContext, String line) {
        if (systemContext == null || line == null || line.isEmpty()) return false;
        try {
            String prev = Settings.Global.getString(systemContext.getContentResolver(), GLOBAL_LOG);
            if (prev == null) prev = "";
            String updated = prev + line + "\n";
            if (updated.length() > MAX_CHARS) {
                updated = updated.substring(updated.length() - MAX_CHARS);
            }
            Settings.Global.putString(systemContext.getContentResolver(), GLOBAL_LOG, updated);
            if (line.contains("已复制无线 ADB 地址：") || line.contains("地址已就绪")) {
                Matcher m = ADDRESS.matcher(line);
                if (m.find()) {
                    Settings.Global.putString(systemContext.getContentResolver(),
                            GLOBAL_LAST_ADDRESS, m.group(1));
                }
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /** 供 App / LogReceiver：写入 DE prefs。 */
    public static boolean append(Context context, String line) {
        if (context == null || line == null || line.isEmpty()) return false;
        try {
            Context storage = context.createDeviceProtectedStorageContext();
            SharedPreferences prefs = storage.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            String updated = prefs.getString(KEY_LOG, "") + line + "\n";
            if (updated.length() > MAX_CHARS * 2) {
                updated = updated.substring(updated.length() - MAX_CHARS * 2);
            }
            SharedPreferences.Editor editor = prefs.edit().putString(KEY_LOG, updated);
            if (line.contains("已复制无线 ADB 地址：") || line.contains("地址已就绪")) {
                Matcher m = ADDRESS.matcher(line);
                if (m.find()) editor.putString(KEY_LAST_ADDRESS, m.group(1));
            }
            return editor.commit();
        } catch (Throwable t) {
            return false;
        }
    }

    /** 界面读取：Global + prefs 合并（去重保序）。 */
    public static String readMerged(Context context) {
        String global = "";
        String prefs = "";
        try {
            String g = Settings.Global.getString(context.getContentResolver(), GLOBAL_LOG);
            if (g != null) global = g;
        } catch (Throwable ignored) { }
        try {
            prefs = context.createDeviceProtectedStorageContext()
                    .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(KEY_LOG, "");
            if (prefs == null) prefs = "";
        } catch (Throwable ignored) { }
        if (global.isEmpty()) return prefs;
        if (prefs.isEmpty()) return global;
        LinkedHashSet<String> seen = new LinkedHashSet<>();
        StringBuilder out = new StringBuilder();
        for (String src : new String[]{prefs, global}) {
            for (String line : src.split("\n", -1)) {
                if (line.isEmpty() || !seen.add(line)) continue;
                if (out.length() > 0) out.append('\n');
                out.append(line);
            }
        }
        return out.toString();
    }

    public static String readLastAddress(Context context) {
        try {
            String g = Settings.Global.getString(context.getContentResolver(), GLOBAL_LAST_ADDRESS);
            if (g != null && !g.isEmpty()) return g;
        } catch (Throwable ignored) { }
        try {
            return context.createDeviceProtectedStorageContext()
                    .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(KEY_LAST_ADDRESS, null);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
