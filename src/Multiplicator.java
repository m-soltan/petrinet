import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;

public class Multiplicator {
    private static class P implements Runnable {
        private int i = 0;
        private final PetriNet<String> net;
        private final List<Transition<String>> list;

        private P(PetriNet<String> net, List<Transition<String>> list) {
            this.net = net;
            this.list = list;
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(i);
                }
            }));
        }

        @Override
        public void run() {
            for (;!Thread.currentThread().isInterrupted(); ++i){
                try {
                     net.fire(list);
                } catch (InterruptedException ignored) {
                    System.out.println(i);
                    Thread.currentThread().interrupt();
                }
            }

        }
    }
    private static final int THREAD_COUNT = 4;
    public static void main(String[] args) throws InterruptedException {

        // the numbers "A" and "B", in places a and f respectively
        int initA, initF;
        // this will be fired by the main thread
        Set<Transition<String>> t;
        // these will be fired by the helper threads
        ArrayList<Transition<String>> u = new ArrayList<>();

        // some strings to mark the Places
        String a = "A", b = "B", c = "C", d = "D", e = "E", f = "F";
        String done = "done";


        // Transition u0
        u.add(new Transition<>(
                Map.of(a, 1, c, 1),
                Set.of(),
                Set.of(done),
                Map.of(b, 1, c, 1, d, 1)
        ));

        // Transition u1
        u.add(new Transition<>(
                Map.of(b, 1, e, 1),
                Set.of(),
                Set.of(done),
                Map.of(a, 1, e, 1)
        ));

        // Transition u2
        u.add(new Transition<>(
                Map.of(e, 1, f, 1),
                Set.of(),
                Set.of(b, done),
                Map.of(c, 1)
        ));

        // Transition u3
        u.add(new Transition<>(
                Map.of(c, 1),
                Set.of(),
                Set.of(done),
                Map.of(e, 1)
        ));

        // Transition u4
        u.add(new Transition<>(
                Map.of(),
                Set.of(),
                Set.of(c, e, done),
                Map.of(e, 1)
        ));

        // Transition t0
        t = Collections.singleton(new Transition<>(
                Map.of(),
                Set.of(),
                Set.of(b, c, f, done),
                Map.of(done, 1)
        ));


        {
            Map.Entry<Integer, Integer> pair = readInput();
            initA = pair.getKey();
            initF = pair.getValue();
        }

        PetriNet<String> net = new PetriNet<>(
                Map.of(a, initA, f, initF),
                true
        );

        Thread[] threads = make_threads(net, u);
        for (Thread i: threads)
            i.start();
        net.fire(t);

        System.out.println(net.get(d));
        System.out.println("");

        // "wątek przerywa wątki pomocnicze" - this doesn't mention calling interrupt()
        // calling interrupt() wouldn't work anyway
        System.exit(0);
    }

    private static Map.Entry<Integer, Integer> readInput() {
        Scanner s = new Scanner(System.in);
        int a = s.nextInt(), b = s.nextInt();
        return new AbstractMap.SimpleImmutableEntry<>(a, b);
    }

    private static Thread[] make_threads(PetriNet<String> net, List<Transition<String>> list) {
        Thread[] ans = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i)
            ans[i] = new Thread(new P(net, list));
        return ans;
    }
}
