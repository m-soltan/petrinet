package petrinet;

import java.util.TreeMap;
import java.util.concurrent.Semaphore;

class NumberCondition {
    private int value;
    private final Semaphore mutex = new Semaphore(1, false);
    private final TreeMap<Integer, Semaphore> semaphoreMap = new TreeMap<>();
    private final TreeMap<Integer, Integer> waitingCount = new TreeMap<>();

    NumberCondition(int value) {
        this.value = value;
    }

    void setTo(int newValue) throws InterruptedException {
        mutex.acquire();
        if (newValue > value) {
            for (int i: waitingCount.keySet()) {
                if (i > newValue)
                    break;
                semaphoreMap.get(i).release();
            }
        }
        value = newValue;
        mutex.release();
    }

    void waitUntil(int targetValue) throws InterruptedException {
        mutex.acquire();
        if (targetValue <= value) {
            mutex.release();
        } else {
            add(targetValue);
            mutex.release();
            semaphoreMap.get(targetValue).acquire();
            remove(targetValue);
        }
    }

    private void add(int value) throws InterruptedException {
        assert(mutex.availablePermits() == 0);
        int count = waitingCount.getOrDefault(value, 0);
        if (count == 0) {
            semaphoreMap.put(value, new Semaphore(0, false));
        }
        waitingCount.put(value, count + 1);
    }

    private void remove(int value) throws InterruptedException {
        mutex.acquire();
        int count = waitingCount.get(value);
        if (count > 1) {
            waitingCount.put(value, count - 1);
            semaphoreMap.get(value).release();
        } else if (count == 1) {
            waitingCount.remove(value);
            semaphoreMap.remove(value);
        } else {
            assert(false);
        }
        mutex.release();
    }
}
