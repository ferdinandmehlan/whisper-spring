package io.github.ferdinandmehlan.whisperspring.live;

import java.io.InterruptedIOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fixed-capacity thread-safe ring buffer for audio PCM data.
 * Supports blocking dequeue and non-blocking enqueue operations.
 * Uses a {@link ReentrantLock} with a {@link Condition} to coordinate
 * producer and consumer threads.
 */
public class AudioRingBuffer {

    private static final Logger log = LoggerFactory.getLogger(AudioRingBuffer.class);

    private final int capacity;
    private final byte[] buffer;

    private int head = 0;
    private int tail = 0;
    private int size = 0;

    private final int bytesPerSecond;
    private long totalBytesEnqueued = 0;
    private long totalBytesDequeued = 0;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    /**
     * Creates a new AudioRingBuffer with the specified capacity.
     *
     * @param capacity   the maximum number of bytes the buffer can hold
     * @param sampleRate e.g., 16000 for Whisper
     * @param channels   e.g., 1 for mono
     * @param bytesPerSample e.g., 2 for 16-bit audio
     */
    public AudioRingBuffer(int capacity, int sampleRate, int channels, int bytesPerSample) {
        this.capacity = capacity;
        this.buffer = new byte[capacity];
        this.bytesPerSecond = sampleRate * channels * bytesPerSample;
    }

    /**
     * Enqueues data into the ring buffer. If the buffer does not have
     * enough free space, the data is silently dropped and a warning is logged.
     *
     * @param data the PCM audio bytes to enqueue
     */
    public void enqueue(byte[] data) {
        if (data == null || data.length == 0) return;

        lock.lock();
        try {
            if (size + data.length > capacity) {
                log.warn(
                        "Ring buffer full ({} bytes queued, capacity {}), dropping {} bytes",
                        size,
                        capacity,
                        data.length);
                return;
            }

            int part = Math.min(data.length, capacity - tail);
            System.arraycopy(data, 0, buffer, tail, part);
            if (part < data.length) {
                System.arraycopy(data, part, buffer, 0, data.length - part);
            }

            tail = (tail + data.length) % capacity;
            size += data.length;
            totalBytesEnqueued += data.length;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Dequeues a specified number of bytes from the buffer, blocking
     * until enough data is available.
     *
     * @param length the number of bytes to dequeue
     * @return a byte array containing the dequeued audio data
     * @throws InterruptedIOException if the thread is interrupted while waiting
     */
    public byte[] dequeue(int length) throws InterruptedIOException {
        if (length <= 0) return new byte[0];

        lock.lock();
        try {
            while (size < length) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException(
                            "Transcription thread interrupted while waiting for audio packets.");
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
            totalBytesDequeued += length;
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the absolute timestamp (in milliseconds) of the first byte
     * currently waiting at the head of the buffer.
     */
    public long getHeadTimestampMs() {
        lock.lock();
        try {
            return (totalBytesDequeued * 1000) / bytesPerSecond;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the absolute timestamp (in milliseconds) of the last byte
     * currently waiting at the tail of the buffer.
     */
    public long getTailTimestampMs() {
        lock.lock();
        try {
            return (totalBytesEnqueued * 1000) / bytesPerSecond;
        } finally {
            lock.unlock();
        }
    }
}
