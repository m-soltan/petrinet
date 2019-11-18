package petrinet;

import java.util.*;

public class Transition<T> {
    private static class MapWriter<K, V> {
        private final Map<K, V> map;

        private MapWriter(Map<K, V> map) {
            this.map = Map.copyOf(map);
        }

        @Override
        public String toString() {
            StringBuilder ans = new StringBuilder();
            ans.append("{");
            boolean first = true;
            for (K i: map.keySet()) {
                if (first) {
                    first = false;
                } else {
                    ans.append(",  ");
                }
                ans.append("(")
                        .append(i.toString())
                        .append(", ")
                        .append(map.get(i))
                        .append(")");
            }
            ans.append("}");
            return ans.toString();
        }
    }

    final Map<Place, T> reverse;
    final Map<Place, Integer> input, output;
    final Set<Place> inhibitor, reset;

    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        this.input = asPlaces(input);
        this.inhibitor = asPlaces(inhibitor);
        this.reset = asPlaces(reset);
        this.output = asPlaces(output);
        HashMap<Place, T> temp = new HashMap<>();
        addReverse(temp, input.keySet());
        addReverse(temp, reset);
        addReverse(temp, inhibitor);
        addReverse(temp, output.keySet());
        reverse = Map.copyOf(temp);
    }

    boolean isUnblocked(Marking marking) {
        for (Place i: inhibitor) {
            if (marking.getCount(i) > 0)
                return false;
        }
        return true;
    }

    boolean isInputReady(Marking marking) {
        for (Place i: input.keySet()) {
            if (marking.getCount(i) < input.get(i))
                return false;
        }
        return true;
    }

    ImmutableMarking step(Marking start) {
        MutableMarking marking = new MutableMarking(start);
        marking.payFor(input);
        marking.reset(reset);
        marking.addTokens(output);
        return new ImmutableMarking(marking);
    }

    private Map<Place, Integer> asPlaces(Map<T, Integer> map) {
        HashMap<Place, Integer> ans = new HashMap<>();
        for (T i: map.keySet())
            ans.put(Place.make(i), map.get(i));
        return Collections.unmodifiableMap(Map.copyOf(ans));
    }

    private Set<Place> asPlaces(Collection<T> collection) {
        HashSet<Place> ans = new HashSet<>();
        for (T i: collection)
            ans.add(Place.make(i));
        return Collections.unmodifiableSet(Set.copyOf(ans));
    }

    private void addReverse(Map<Place, T> result, Collection<T> set) {
        for (T i: set) {
            result.put(Place.make(i), i);
        }
    }
}