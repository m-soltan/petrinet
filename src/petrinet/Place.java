package petrinet;

import java.util.ArrayList;
import java.util.HashMap;

class Place {
    private static final HashMap<Object, Place> all = new HashMap<>();

    private final Object key;
    final ArrayList<Transition<Object>> input = new ArrayList<>();
    final ArrayList<Transition<Object>> output = new ArrayList<>();


    static Place make(Object key) {
        if (!all.containsKey(key)) {
            Place p = new Place(key);
            all.put(key, p);
        }
        return all.get(key);
    }

    private Place(Object key) {
        this.key = key;
    }

}