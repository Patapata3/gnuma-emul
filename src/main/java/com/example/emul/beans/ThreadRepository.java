package com.example.emul.beans;

import com.example.emul.runnables.TrainThread;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ThreadRepository {
    private Map<UUID, TrainThread> threadMap = new HashMap<>();

    public void put(UUID id, TrainThread thread) {
        threadMap.put(id, thread);
    }

    public void stop(UUID id) {
        if (threadMap.containsKey(id)) {
            threadMap.get(id).stop();
            threadMap.remove(id);
        }
    }

    public void pause(UUID id) {
        if (threadMap.containsKey(id)) {
            threadMap.get(id).pause();
        }
    }

    public void resume(UUID id) {
        if (threadMap.containsKey(id)) {
            threadMap.get(id).resume();
        }
    }
}
