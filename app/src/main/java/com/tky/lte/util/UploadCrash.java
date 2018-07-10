package com.tky.lte.util;

import com.tky.lte.constants.Config;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ttz on 2018/3/28.
 */

public class UploadCrash {

    private static UploadCrash uploadCrash;
    private String crash;

    public void saveCrashInfo(Map<String, String> datas) {
        try {
            Iterator<Map.Entry<String, String>> iterator = datas.entrySet().iterator();
            JSONObject object = new JSONObject();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                object.put(entry.getKey(), entry.getValue());
            }
            PreferenceUtil.getInstance().putPreferences(Config.KEY_CRASH, object.toString());
            LogUtils.d("crash", "保存crash信息");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getCrashInfo() {
        Map<String, String> itemMap = new HashMap<>();
        try {
            String result = PreferenceUtil.getInstance().getPreferences(Config.KEY_CRASH, "");
            JSONObject itemObject = new JSONObject(result);
            JSONArray names = itemObject.names();
            if (names != null) {
                for (int j = 0; j < names.length(); j++) {
                    String name = names.getString(j);
                    String value = itemObject.getString(name);
                    itemMap.put(name, value);
                }
            }
            LogUtils.d("crash", "获取到crash信息");
            return itemMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemMap;
    }

    public static UploadCrash getInstance() {
        if (uploadCrash == null) {
            uploadCrash = new UploadCrash();
        }
        return uploadCrash;
    }

    private UploadCrash() {
        crash = PreferenceUtil.getInstance().getPreferences(Config.KEY_CRASH, "");
    }
}
