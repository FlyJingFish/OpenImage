package com.flyjingfish.openimagefulllib;

import java.util.HashMap;

enum RecordPlayerPosition {
    INSTANCE;
    private final HashMap<Long, Long> recordPlayPositionMap = new HashMap<>();

    public void setPlayPosition(long key, long position) {
        recordPlayPositionMap.put(key, position);
    }

    public long getPlayPosition(long key) {
        Long position = recordPlayPositionMap.get(key);
        return position != null ?position:0;
    }

}
