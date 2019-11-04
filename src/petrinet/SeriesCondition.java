package petrinet;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

public abstract class SeriesCondition {
    final int[] values;
    final Semaphore mutex = new Semaphore(1);
    final TreeMap<Integer, Map.Entry<Semaphore, int[]>> map = new TreeMap<>();


    public SeriesCondition(int[] values) {
        this.values = values;
    }

    boolean canPass(int[] requirements) {
        assert(mutex.availablePermits() == 0);
        int length = requirements.length;
        assert(length == values.length);
        for (int i = 0; i < length; ++i) {
            if (requirements[i] > values[i]) {
                return false;
            }
        }
        return true;
    }

    public abstract void setTo(int value, int position) throws InterruptedException;
    public abstract void waitUntil(int[] requirements) throws InterruptedException;
    abstract int getId();

}
