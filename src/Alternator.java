import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;

public class Alternator {
    private static final String[] places = new String[] {"AE", "AL", "BE", "BL", "CE", "CL"};
    private static class P implements Runnable {
        private final Collection<Transition<String>> enter, leave;
        private final String name;

        private static Set<Transition<String>> starter = Set.of(
                starterTransition(2),
                starterTransition(1),
                starterTransition(0)
        );

        private P(
                Set<Transition<String>> enter,
                Set<Transition<String>> leave,
                String name
        ) {
            this.enter = new HashSet<>(enter);
            this.enter.addAll(starter);
            this.leave = leave;
            this.name = name;
            System.out.println(enter);
            System.out.println(leave);
            System.out.println("\n");
        }

        @Override
        public void run() {
             try {
                 while (!Thread.currentThread().isInterrupted()) {
                    net.fire(enter);
                    System.out.println(name);
                    net.fire(leave);
                }
            } catch (InterruptedException ignored) {}
        }
    }
    private static PetriNet<String> net = new PetriNet<>(Collections.emptyMap(), true);

    public static void main(String[] args) {
        String[] names = new String[] {"A", "B", "C"};
        final int len = names.length;
        Thread[] threads = new Thread[len];

        for (int i = 0; i < len; ++i) {
            String prev = placeL((i + 2) % len);
            String next = placeL((i + 1) % len);
            Transition<String> leave = simple(placeE(i), placeL(i));
            Transition<String> enterPrev = simple(prev, placeE(i));
            Transition<String> enterNext = simple(next, placeE(i));
            threads[i] = new Thread(new P(
                    Set.of(enterPrev, enterNext),
                    Set.of(leave), names[i]
            ));
        }

        for (Thread i: threads)
            i.start();

        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            System.err.println("main thread interrupted");
        } finally {
            for (Thread i: threads)
                i.interrupt();
        }
    }

    private static Transition<String> simple(String from, String to) {
        return new Transition<>(
                Map.of(from, 1),
                Set.of(),
                Set.of(),
                Map.of(to, 1)
        );
    }

    private static String placeE(int x) {
        return places[2 * x];
    }

    private static String placeL(int x) {
        return places[2 * x + 1];
    }

    private static Transition<String> starterTransition(int x) {
        return new Transition<>(
                Map.of(),
                Set.of(),
                Arrays.asList(places),
                Map.of(placeL(x), 1)
        );
    }
}
