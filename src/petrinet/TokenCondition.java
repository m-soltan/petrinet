package petrinet;

import java.util.Collection;
import java.util.Map;

public abstract class TokenCondition<T> {
    final Marking marking;

    TokenCondition(Marking marking) {
        this.marking = marking;
    }

    abstract Transition<T> resolve(Collection<Transition<T>> transitions) throws InterruptedException;
    abstract void retry(Map<Place, Integer> map) throws InterruptedException;
}
