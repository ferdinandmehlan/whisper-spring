package io.github.ferdinandmehlan.whisperspring.live;

import java.io.InterruptedIOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioRingBuffer {

    private static final Logger log = LoggerFactory.getLogger(AudioRingBuffer.class);

    private final int capacity;
    private final byte[] buffer;

    private int head = 0;
    private int tail = 0;
    private int size = 0;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    public AudioRingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new byte[capacity];
    }

    public void enqueue(byte[] data) {
        if (data == null || data.length == 0) return;

        lock.lock();
        try {
            if (size + data.length > capacity) {
                log.warn("Ring buffer full ({} bytes queued, capacity {}), dropping {} bytes",
                        size, capacity, data.length);
                return;
            }

            int part = Math.min(data.length, capacity - tail);
            System.arraycopy(data, 0, buffer, tail, part);
            if (part < data.length) {
                System.arraycopy(data, part, buffer, 0, data.length - part);
            }

            tail = (tail + data.length) % capacity;
            size += data.length;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return size;
        } finally {
            lock.unlock();
        }
    }

    public byte[] dequeue(int length) throws InterruptedIOException {
        if (length <= 0) return new byte[0];

        lock.lock();
        try {
            while (size < length) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException("Transcription thread interrupted while waiting for audio packets.");
                }
            }

            byte[] result = new byte[length];
            int part = Math.min(length, capacity - head);
            System.arraycopy(buffer, head, result, 0, part);
            if (part < length) {
                System.arraycopy(buffer, 0, result, part, length - part);
            }

            head = (head + length) % capacity;
            size -= length;
            return result;
        } finally {
            lock.unlock();
        }
    }
}
