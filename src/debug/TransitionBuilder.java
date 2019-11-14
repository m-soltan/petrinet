package debug;

import petrinet.Transition;

import java.util.HashMap;
import java.util.HashSet;

class TransitionBuilder<U> {
    private final HashMap<U, Integer> input = new HashMap<>();
    private final HashMap<U, Integer> output = new HashMap<>();
    private final HashSet<U> inhibitor = new HashSet<>();
    private final HashSet<U> reset = new HashSet<>();

    TransitionBuilder<U> addInhibitor(U key) {
        inhibitor.add(key);
        return this;
    }

    TransitionBuilder<U> addInput(U key, int value) {
        input.put(key, value);
        return this;
    }

    TransitionBuilder<U> addOutput(U key, int value) {
        output.put(key, value);
        return this;
    }

    TransitionBuilder<U> addReset(U key) {
        reset.add(key);
        return this;
    }

    Transition<U> get() {
        return new Transition<>(input, reset, inhibitor, output);
    }
}