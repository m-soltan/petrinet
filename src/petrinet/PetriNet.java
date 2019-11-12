package petrinet;

import java.util.*;

public class PetriNet<T> {
    private final Map<Place, T> revMap;
    private final Marking marking;
    private final TokenCondition<T> strategy;

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        HashMap<Place, Integer> map = new HashMap<>();
        HashMap<Place, T> revMap = new HashMap<>();
        for (T i: initial.keySet()) {
            Place place = Place.make(i);
            map.put(place, initial.get(i));
            revMap.put(place, i);
        }
        this.revMap = revMap;
        marking = new Marking(map);
        strategy = makeCondition(marking, fair);
    }

    // todo: remove
    int getCount(Place place) {
        return marking.getCount(place);
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        ArrayDeque<Marking> q = new ArrayDeque<>();
        HashSet<Map<T, Integer>> ans = new HashSet<>();
        q.add(marking);
        while (!q.isEmpty()) {
            Marking i = q.pollFirst();
            for (Transition j: transitions) {
                if (j.isUnblocked(i) && j.isInputReady(i))
                    q.addLast(j.step(i));
            }
            HashMap<T, Integer> element = new HashMap<>();
            for (Place j: i.getKeys()) {
                element.put(revMap.get(j), i.getCount(j));
            }
            ans.add(element);
        }
        return ans;
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
        marking.mutex.release();
        return ans;
    }

    // uses the fair subclass regardless of boolean parameter
    private TokenCondition<T> makeCondition(Marking marking, boolean fair) {
        return new FairCondition<>(marking);
    }

}