package petrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

class MutableMarking extends Marking {
    final HashMap<Place, Integer> map;
    final Semaphore mutex;

    MutableMarking(Map<Place, Integer> map) {
        this.map = new HashMap<>(map);
        this.mutex = new Semaphore(1, false);
    }

    MutableMarking(Marking marking) {
        HashMap<Place, Integer> map = new HashMap<>();
        for (Place i: marking.getKeys()) {
            map.put(i, marking.getCount(i));
        }
        this.map = map;
        this.mutex = new Semaphore(1, false);
    }

    void addTokens(Map<Place, Integer> map) {
        assert(hasMutex());
        for (Place i: map.keySet()) {
            int updatedCount = map.get(i) + this.map.getOrDefault(i, 0);
            this.map.remove(i);
            this.map.put(i, updatedCount);
        }
    }

    void payFor(Map<Place, Integer> map) {
        assert(hasMutex());
        for (Place i: map.keySet()) {
            int held = this.map.get(i);
            int needed = map.get(i);
            if (held > needed) {
                this.map.replace(i, held - needed);
            } else if (held == needed) {
                this.map.remove(i);
            } else {
                assert(false);
            }
        }
    }

    void reset(Collection<Place> collection) {
        assert(hasMutex());
        for (Place i: collection)
            map.remove(i);
    }

    private boolean hasMutex() {
        return mutex.availablePermits() == 0;
    }

    @Override
    int getCount(Place place) {
        return map.getOrDefault(place, 0);
    }

    @Override
    Set<Place> getKeys() {
        return map.keySet();
    }
}
