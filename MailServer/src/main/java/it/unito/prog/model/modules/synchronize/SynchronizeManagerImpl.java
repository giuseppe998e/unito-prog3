package it.unito.prog.model.modules.synchronize;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Singleton class
 */
public class SynchronizeManagerImpl<T> implements SynchronizeManager<T> {
    private final ConcurrentHashMap<T, SimpleLock> lockList;

    public SynchronizeManagerImpl() {
        lockList = new ConcurrentHashMap<>();
    }

    @Override
    public void createLock(T key) {
        SimpleLock lockClass = lockList.getOrDefault(key, new SimpleLock());
        lockClass.occurrences.incrementAndGet();
        lockList.putIfAbsent(key, lockClass);
    }

    @Override
    public Lock getWriteLock(T key) {
        SimpleLock lockClass = lockList.get(key);
        if (lockClass == null) throw new NullPointerException("A lock that does not exist was requested.");

        return lockClass.writeLock();
    }

    @Override
    public Lock getReadLock(T key) {
        SimpleLock lockClass = lockList.get(key);
        if (lockClass == null) throw new NullPointerException("A lock that does not exist was requested.");

        return lockClass.readLock();
    }

    @Override
    public void deleteLock(T key) {
        SimpleLock lockClass = lockList.get(key);
        if (lockClass == null) throw new NullPointerException("A lock that does not exist was requested.");

        Lock writeLock = lockClass.writeLock();
        try {
            writeLock.lock();
            if (lockClass.occurrences.decrementAndGet() == 0) {
                lockList.remove(key);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Lock class
     */
    private static final class SimpleLock extends ReentrantReadWriteLock {
        // Atomic and Volatile
        private final AtomicInteger occurrences = new AtomicInteger();
    }
}