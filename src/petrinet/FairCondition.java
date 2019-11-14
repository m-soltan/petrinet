package petrinet;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

class FairCondition<T> extends TokenCondition<T> {

    FairCondition(MutableMarking marking) {
        super(marking);
    }

    @Override
    Transition<T> resolve(Collection<Transition<T>> transitions) throws InterruptedException {
        marking.l.lock();
        for (;;) {
            Optional<Transition<T>> ansOpt = chooseTransition(transitions);
            if (ansOpt.isPresent())
                return ansOpt.get();
            marking.ready.await();
        }
    }

    @Override
    void retry(Map<Place, Integer> map) {
        marking.ready.signalAll();
    }

    private Optional<Transition<T>> chooseTransition(Collection<Transition<T>> transitions) {
        for (Transition<T> i: transitions) {
            if (i.isInputReady(marking) && i.isUnblocked(marking)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
}
