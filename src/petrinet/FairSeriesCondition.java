package petrinet;

import java.util.Map;
import java.util.concurrent.Semaphore;

public class FairSeriesCondition extends SeriesCondition {
    private int idMax = 0;
    FairSeriesCondition(int[] values) {
        super(values);
    }

    @Override
    public void setTo(int value, int position) throws InterruptedException {
        mutex.acquire();
        int oldValue = values[position];
        values[position] = value;
        for (Integer i: map.keySet()) {
            int[] arr = map.get(i).getValue();
            if (oldValue < arr[position] && arr[position] <= value) {
                if (canPass(arr)) {
                    map.get(i).getKey().release();
                    break;
                }
            } else {
                assert(!canPass(arr));
            }
        }
        mutex.release();
    }

    @Override
    public void waitUntil(int[] requirements) throws InterruptedException {
        mutex.acquire();
        if (canPass(requirements)) {
            mutex.release();
        } else {
            int id = getId();
            Semaphore s = new Semaphore(0, false);
            map.put(id, Map.entry(s, requirements));
            mutex.release();
            s.acquire();
        }
    }

    @Override
    int getId() {
        assert(mutex.availablePermits() == 0);
        if (map.isEmpty()) {
            return idMax = 1;
        } else if (map.lastKey() > 4 * map.size() + 1000) {
            idMax = 0;
            for (Integer i : map.keySet()) {
                if (i > idMax) {
                    map.put(idMax, map.get(i));
                    map.remove(i);
                }
                ++idMax;
            }
        }
        ++idMax;
        return idMax;
    }
}
