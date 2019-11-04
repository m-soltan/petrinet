package petrinet;


import java.util.*;
import java.util.concurrent.Semaphore;

public class PetriNet<T> {
    private final Tokens tokens;
    private final TokenCondition<T> strategy;

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        HashMap<Place, Integer> map = new HashMap<>();
        for (T i: initial.keySet()) {
            map.put(Place.make(i), initial.get(i));
        }
        tokens = new Tokens(map);
        if (fair) {
            strategy = new FairCondition<T>(tokens);
        } else {
            strategy = new UnfairCondition<T>(tokens);
        }

        assert(false); // todo
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        throw new NullPointerException(); // todo
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {
        Map<Place, Integer> output;
        if (transitions.isEmpty()) {
            throw new IllegalArgumentException("empty set argument");
        }
        Transition<T> ans = strategy.resolve(transitions);
        tokens.payFor(ans.getInput());
        output = ans.getOutput();
        tokens.addTokens(output);
        strategy.retry(output);
        tokens.mutex.release();
        return ans;
    }

    private void addInput(T key, Transition<T> transition) {
        Place.make(key).addInput(transition.general());
    }

    private void addOutput(T key, Transition<T> transition) {
        Place.make(key).addOutput(transition.general());
    }
}