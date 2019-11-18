package petrinet;

import java.util.*;

public class PetriNet<T> {
    private final Map<Place, T> revMap;
    private final MutableMarking marking;
    private final TokenCondition<T> strategy;

    /*
    * parameter 'fair' is ignored
    * it might be a good idea to pass it to the ReentrantLock constructor instead to potentially
    * improve efficiency, but we only care about correctness - the slower version is good enough
    */
    public PetriNet(Map<T, Integer> initial, boolean fair) {
        HashMap<Place, Integer> map = new HashMap<>();
        HashMap<Place, T> revMap = new HashMap<>();
        for (T i: initial.keySet()) {
            Place place = Place.make(i);
            map.put(place, initial.get(i));
            revMap.put(place, i);
        }
        this.revMap = revMap;
        marking = new MutableMarking(map);
        strategy = makeCondition(marking, fair);
    }

    public int get(T value) {
        return marking.getCount(Place.make(value));
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        return boundedReachable(transitions, Integer.MAX_VALUE);
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {
        if (transitions.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }
        Transition<T> ans = strategy.resolve(transitions);
        marking.payFor(ans.input);
        marking.reset(ans.reset);
        marking.addTokens(ans.output);
        // inform the strategy about added tokens
        strategy.retry(ans.output);
        marking.l.unlock();
        return ans;
    }

    private Set<Map<T, Integer>> boundedReachable(Collection<Transition<T>> transitions, int limit) {
        ArrayDeque<ImmutableMarking> q = new ArrayDeque<>();
        HashSet<Map<T, Integer>> ans = new HashSet<>();
        marking.l.lock();
        HashMap<Place, T> reverse = new HashMap<>(revMap);
        q.add(new ImmutableMarking(marking.map));
        marking.l.unlock();
        for (Transition<T> i: transitions)
            reverse.putAll(i.reverse);

        for (int i = 0; i < limit; ++i) {
            if (q.isEmpty())
                break;
            ImmutableMarking marking = q.removeFirst();
            boolean skip = false;
            for (Map<T, Integer> j: ans) {
                HashMap<Place, Integer> map = new HashMap<>();
                for (T k: j.keySet()) {
                     map.put(Place.make(k), j.get(k));
                }
                if (map.equals(marking.mapView)) {
                    skip = true;
                    break;
                }
            }
            if (skip)
                continue;
            for (Transition j: transitions)
                if (j.isUnblocked(marking) && j.isInputReady(marking))
                    q.addLast(j.step(marking));
            HashMap<T, Integer> element = new HashMap<>();
            for (Place j: marking.getKeys()) {
                assert(reverse.containsKey(j));
                element.put(reverse.get(j), marking.getCount(j));
            }
            ans.add(element);
        }
        return ans;
    }

    // uses the fair subclass regardless of boolean parameter
    private TokenCondition<T> makeCondition(MutableMarking marking, boolean fair) {
        return new FairCondition<>(marking);
    }
}