package petrinet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Tokens {
    final Semaphore mutex;
    final HashMap<Place, Integer> marking;

    Tokens(Map<Place, Integer> marking) {
        this.marking = new HashMap<>(marking);
        this.mutex = new Semaphore(1, false);
    }

    boolean canPass(Map<Place, Integer> requirements) {
        for (Place i: requirements.keySet())
            if (getCount(i) < requirements.get(i))
                return false;
        return true;
    }

    int getCount(Place place) {
        return marking.getOrDefault(place, 0);
    }

    void addTokens(Map<Place, Integer> map) {
        assert(mutex.availablePermits() == 0);
        for (Place i: map.keySet()) {
            int updatedCount = map.get(i) + marking.getOrDefault(i, 0);
            marking.remove(i);
            marking.put(i, updatedCount);
        }
    }

    void payFor(Map<Place, Integer> map) {
        assert(mutex.availablePermits() == 0);
        for (Place i: map.keySet()) {
            int held = marking.get(i);
            int needed = map.get(i);
            if (held > needed) {
                marking.replace(i, held - needed);
            } else if (held == needed) {
                marking.remove(i);
            } else {
                assert(false);
            }
        }
    }

}
