package petrinet;

import java.util.*;
import java.util.concurrent.Semaphore;

public class UnfairCondition<T> extends TokenCondition<T> {
    private class SemaphoreMap {
        private final HashMap<Place, ArrayList<Semaphore>> map = new HashMap<>();

        List<Semaphore> get(Place place) {
            assert(marking.mutex.availablePermits() == 0);
            return map.get(place);
        }

        void addAll(Collection<Place> collection, Semaphore s) {
            assert(marking.mutex.availablePermits() == 0);
            for (Place i: collection) {
                ArrayList<Semaphore> list = map.getOrDefault(i, new ArrayList<>());
                list.add(s);
                map.replace(i, list);
            }
        }

        void removeAll(Collection<Place> collection) {
            assert(marking.mutex.availablePermits() == 0);
            for (Place i: collection) {
                assert(map.containsKey(i));
            }
            for (Place i: collection) {
                map.remove(i);
            }
        }
    }

    public UnfairCondition(Marking marking) {
        super(marking);
    }

    @Override
    Transition<T> resolve(Collection<Transition<T>> transitions) {
        return null;
    }

    @Override
    void retry(Map<Place, Integer> map) throws InterruptedException {
        assert(false); // todo
    }
}
