package petrinet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Transition<T> {
    private static class Data {
        final Map<Place, Integer> input, output;
        final Set<Place> inhibitor, reset;

        Data(
                Map<Place,
                Integer> input,
                Collection<Place> reset,
                Collection<Place> inhibitor,
                Map<Place, Integer> output
        ) {
            this.input = makeView(input);
            this.inhibitor = makeView(inhibitor);
            this.reset = makeView(reset);
            this.output = makeView(output);
        }

        private Map<Place, Integer> makeView(Map<Place, Integer> map) {
            return Collections.unmodifiableMap(Map.copyOf(map));
        }

        private Set<Place> makeView(Collection<Place> collection) {
            return Collections.unmodifiableSet(Set.copyOf(collection));
        }
    }
    private final Data data;

    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        assert(reset.isEmpty()); // todo: reset is not implemented
        assert(inhibitor.isEmpty()); // todo: inhibitor is not implemented
        this.data = new Data(asPlaces(input), asPlaces(reset), asPlaces(inhibitor), asPlaces(output));
    }

    public Map<Place, Integer> getInput() {
        return data.input;
    }

    public Map<Place, Integer> getOutput() {
        return data.output;
    }

    private Map<Place, Integer> asPlaces(Map<T, Integer> map) {
        HashMap<Place, Integer> ans = new HashMap<>();
        for (T i: map.keySet())
            ans.put(Place.make(i), map.get(i));
        return ans;
    }

    private Set<Place> asPlaces(Collection<T> collection) {
        HashSet<Place> ans = new HashSet<>();
        for (T i: collection)
            ans.add(Place.make(i));
        return ans;
    }

    private Transition(Data data) {
        this.data = data;
    }
}