package petrinet;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;


class FairCondition<T> extends TokenCondition<T> {
    static class P {
        private Semaphore v = null;
        Optional<Semaphore> get() {
            return Optional.ofNullable(v);
        }

        void set(Semaphore s) {
            assert(s != null);
            v = s;
        }
    }
    private P last = new P();

    FairCondition(Marking marking) {
        super(marking);
    }

    @Override
    Transition<T> resolve(Collection<Transition<T>> transitions) throws InterruptedException {
        Semaphore s = new Semaphore(0, false);
        for (;;) {
            marking.mutex.acquire();
            // try to pass
            for (Transition<T> i: transitions) {
                Map<Place, Integer> input = i.getInput();
                boolean inputFull = true;
                for (Place j: i.getInput().keySet()) {
                    if (marking.getCount(j) < input.get(j))
                        inputFull = false;
                }
                if (inputFull) {
                    return i;
                }
            }
            // failed to pass, allow the next thread to try
            last.get().ifPresent(Semaphore::release);
            // set this so someone can release us
            last.set(s);
            marking.mutex.release();
            // start waiting
            s.acquire();
        }
    }

    @Override
    void retry(Map<Place, Integer> map) {
        last.get().ifPresent(Semaphore::release);
    }

}
