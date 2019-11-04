package petrinet;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Semaphore;

public abstract class TokenCondition<T> {
    final Tokens marking;

    TokenCondition(Tokens marking) {
        this.marking = marking;
    }

    abstract Transition<T> resolve(Collection<Transition<T>> transitions) throws InterruptedException;
    abstract void retry(Map<Place, Integer> map) throws InterruptedException;
}
