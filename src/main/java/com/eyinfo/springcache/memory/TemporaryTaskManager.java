package com.eyinfo.springcache.memory;


import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.entity.TaskEntry;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("ALL")
class TemporaryTaskManager {

    private static TemporaryTaskManager temporaryTaskManager;
    private ConcurrentLinkedQueue<TaskEntry> referenceQueue = new ConcurrentLinkedQueue<TaskEntry>();

    private TemporaryTaskManager() {
        //init
    }

    public static TemporaryTaskManager getTemporaryTaskManager() {
        if (temporaryTaskManager == null) {
            synchronized (TemporaryTaskManager.class) {
                if (temporaryTaskManager == null) {
                    temporaryTaskManager = new TemporaryTaskManager();
                }
            }
        }
        return temporaryTaskManager;
    }


    public void put(String key, Object value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) {
            TaskEntry next = null;
            Iterator<? super TaskEntry> iterator = referenceQueue.iterator();
            while (iterator.hasNext()) {
                next = (TaskEntry) iterator.next();
                if (next == null) {
                    continue;
                }
                if (TextUtils.equals(next.getKey(), key)) {
                    break;
                }
            }
            if (next != null) {
                referenceQueue.remove(next);
            }
            return;
        }
        TaskEntry entry = new TaskEntry();
        entry.setKey(key);
        entry.setValue(value);
        referenceQueue.add(entry);
    }

    public void remove(String key) {
        put(key, null);
    }

    public TaskEntry get() {
        Iterator<? super TaskEntry> iterator = referenceQueue.iterator();
        if (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof TaskEntry) {
                return (TaskEntry) next;
            }
        }
        return null;
    }
}
