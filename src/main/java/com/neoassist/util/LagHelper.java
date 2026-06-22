package com.neoassist.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Shared packet-hold buffer used by FakeLag and ConnectionMixin. */
public final class LagHelper {
    /** When true the mixin must let packets through so the buffer can be replayed. */
    public static volatile boolean flushing = false;

    private static final List<Runnable> QUEUE = Collections.synchronizedList(new ArrayList<>());

    private LagHelper() {
    }

    public static void queue(Runnable sender) {
        QUEUE.add(sender);
    }

    public static int size() {
        return QUEUE.size();
    }

    public static void flush() {
        List<Runnable> copy;
        synchronized (QUEUE) {
            if (QUEUE.isEmpty()) {
                return;
            }
            copy = new ArrayList<>(QUEUE);
            QUEUE.clear();
        }
        flushing = true;
        try {
            for (Runnable sender : copy) {
                sender.run();
            }
        } finally {
            flushing = false;
        }
    }
}
