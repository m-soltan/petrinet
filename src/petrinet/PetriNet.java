package petrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PetriNet<T> {
    private final Marking marking;
    private final TokenCondition<T> strategy;

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        HashMap<Place, Integer> map = new HashMap<>();
        for (T i: initial.keySet())
            map.put(Place.make(i), initial.get(i));
        marking = new Marking(map);
        strategy = makeCondition(marking, fair);
    }

    public int getCount(Place place) {
        return marking.getCount(place);
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        throw new NullPointerException(); // todo
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {
        Map<Place, Integer> output;
        if (transitions.isEmpty()) {
            throw new IllegalArgumentException("empty set");
        }
        Transition<T> ans = strategy.resolve(transitions);
        marking.payFor(ans.getInput());
        output = ans.getOutput();
        marking.addTokens(output);
        strategy.retry(output);
        marking.mutex.release();
        return ans;
    }

    // uses the fair subclass regardless of boolean parameter
    private TokenCondition<T> makeCondition(Marking marking, boolean fair) {
        return new FairCondition<>(marking);
    }
}