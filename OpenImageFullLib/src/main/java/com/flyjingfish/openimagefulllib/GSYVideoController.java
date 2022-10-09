package com.flyjingfish.openimagefulllib;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GSYVideoController {
    private static ConcurrentHashMap<String, GSYVideoPlayerManager> GSYVideoPlayerManagerMap = new ConcurrentHashMap<>();

    public static synchronized GSYVideoPlayerManager getGSYVideoPlayerManager(String key) {
        if (TextUtils.isEmpty(key)) {
            throw new IllegalStateException("key can not be empty !!!");
        }
        GSYVideoPlayerManager GSYVideoPlayerManager = GSYVideoPlayerManagerMap.get(key);
        if (GSYVideoPlayerManager == null) {
            GSYVideoPlayerManager = new GSYVideoPlayerManager();
            GSYVideoPlayerManagerMap.put(key, GSYVideoPlayerManager);
        }
        return GSYVideoPlayerManager;
    }

    public static void cancelByActKey(String actKey) {
        if (GSYVideoPlayerManagerMap.size() > 0) {
            List<String> keyList = new ArrayList<>();
            for (Map.Entry<String, GSYVideoPlayerManager> helper : GSYVideoPlayerManagerMap.entrySet()) {
                if (helper.getKey().contains(actKey)) {
                    cancelByKey(helper.getKey());
                    keyList.add(helper.getKey());
                }
            }
            for (String key : keyList) {
                removeVideoHolderBykey(key);
            }
        }
    }

    public static void cancelByPageKey(String pageKey) {
        if (GSYVideoPlayerManagerMap.size() > 0) {
            List<String> keyList = new ArrayList<>();
            for (Map.Entry<String, GSYVideoPlayerManager> helper : GSYVideoPlayerManagerMap.entrySet()) {
                if (helper.getKey().contains(pageKey)) {
                    cancelByKey(helper.getKey());
                    keyList.add(helper.getKey());
                }
            }
            for (String key : keyList) {
                removeVideoHolderBykey(key);
            }
        }
    }

    public static void removeVideoHolderBykey(String key) {
        GSYVideoPlayerManagerMap.remove(key);
    }

    public static void cancelByKey(String key) {
        if (getGSYVideoPlayerManager(key).listener() != null) {
            getGSYVideoPlayerManager(key).listener().onCompletion();
        }
        getGSYVideoPlayerManager(key).releaseMediaPlayer();
    }

    public static void cancelByKeyAndDeleteKey(String key) {
        cancelByKey(key);
        removeVideoHolderBykey(key);
    }

    public static void resumeByKey(String key) {
        if (GSYVideoPlayerManagerMap.size() > 0 && GSYVideoPlayerManagerMap.containsKey(key)) {
            GSYVideoPlayerManager GSYVideoPlayerManager = GSYVideoPlayerManagerMap.get(key);
            if (GSYVideoPlayerManager != null) {
                GSYVideoPlayerManager.resumeVideoPlayer();
            }
        }
    }

    public static void pauseByActivityKey(String activityKey) {
        if (GSYVideoPlayerManagerMap.size() > 0) {
            for (Map.Entry<String, GSYVideoPlayerManager> helper : GSYVideoPlayerManagerMap.entrySet()) {
                if (helper.getKey().contains(activityKey)) {
                    helper.getValue().pauseVideoPlayer();
                }
            }
        }
    }

    public static void pauseByPageKey(String pageKey) {
        if (GSYVideoPlayerManagerMap.size() > 0) {
            for (Map.Entry<String, GSYVideoPlayerManager> helper : GSYVideoPlayerManagerMap.entrySet()) {
                if (helper.getKey().contains(pageKey)) {
                    helper.getValue().pauseVideoPlayer();
                }
            }
        }
    }

    public static void pauseByKey(String key) {
        if (GSYVideoPlayerManagerMap.size() > 0) {
            GSYVideoPlayerManager GSYVideoPlayerManager = GSYVideoPlayerManagerMap.get(key);
            if (GSYVideoPlayerManager != null) {
                GSYVideoPlayerManager.pauseVideoPlayer();
            }
        }
    }
}
