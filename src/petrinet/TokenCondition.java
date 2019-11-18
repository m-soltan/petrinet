package petrinet;

import java.util.Collection;
import java.util.Map;

abstract class TokenCondition<T> {
    final MutableMarking marking;

    TokenCondition(MutableMarking marking) {
        this.marking = marking;
    }

    abstract Transition<T> resolve(Collection<Transition<T>> transitions) throws InterruptedException;
    abstract void retry(Map<Place, Integer> map);
}
