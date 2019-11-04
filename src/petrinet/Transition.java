package petrinet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Transition<T> {
    private static class Data {
        public final Map<Place, Integer> input, output;
        public final Set<Place> inhibitor, reset;

        public Data(
                Map<Place,
                Integer> input,
                Collection<Place> reset,
                Collection<Place> inhibitor,
                Map<Place, Integer> output
        ) {
            this.input = Collections.unmodifiableMap(Map.copyOf(input));
            this.output = Collections.unmodifiableMap(Map.copyOf(output));
            this.inhibitor = Collections.unmodifiableSet(Set.copyOf(inhibitor));
            this.reset = Collections.unmodifiableSet(Set.copyOf(reset));
        }
    }
    private final Data data;

    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        assert(reset.isEmpty()); // todo: reset is not implemented
        assert(inhibitor.isEmpty()); // todo: inhibitor is not implemented

        throw new NullPointerException(); // todo
    }

    public boolean isReady(Map<Place, Integer> values) {
        for (Place i: data.input.keySet()) {
            Integer value = values.getOrDefault(i, 0);
            if (value < data.input.get(i))
                return false;
        }
        return true;
    }

    public Map<Place, Integer> getInput() {
        return data.input;
    }

    public Map<Place, Integer> getOutput() {
        return data.output;
    }

    Transition<Object> general() {
        return new Transition<>(this.data);
    }

    private Transition(Data data) {
        this.data = data;
    }
}