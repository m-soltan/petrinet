import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;

public class Alternator {
    private static final int duration = 30;
    private static final String middle = "prev";
    private static class Debug {
        static class Delayer {
            static final int a = 500;
            static final int b = 250;
            static final int c = 100;
        }
    }
    private static class P implements Runnable {
        private final Collection<Transition<String>> enter, leave;
        private final String name;


        private P(String name) {
            this.enter = Set.of(enter(name));
            this.leave = Set.of(leave(name));
            this.name = name;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (name.equals("A")) {
                        Thread.sleep(Debug.Delayer.a);
                    } else if (name.equals("B")) {
                        Thread.sleep(Debug.Delayer.b);
                    } else if (name.equals("C")) {
                        Thread.sleep(Debug.Delayer.c);
                    } else {
                        assert(false);
                    }
                    net.fire(enter);
                    System.out.println(name);
                    net.fire(leave);
                }
            } catch (InterruptedException ignored) {}
        }
    }
    private static PetriNet<String> net = new PetriNet<>(Map.of("prev", 1), true);

    public static void main(String[] args) {
        String[] names = new String[] {"A", "B", "C"};
        final int len = names.length;
        Thread[] threads = new Thread[len];

        for (int i = 0; i < len; ++i) {
            threads[i] = new Thread(new P(names[i]));
        }

        for (Thread i: threads)
            i.start();

        try {
            Thread.sleep(1000 * duration);
        } catch (InterruptedException e) {
            System.err.println("main thread interrupted");
        } finally {
            for (Thread i: threads)
                i.interrupt();
        }
    }

    private static Set<String> others(String name) {
        String[] arr = new String[] {"A", "B", "C", "A", "B"};
        int start = 0;
        while (!arr[start].equals(name))
            ++start;
        return Set.of(block(arr[1 + start]), block(arr[2 + start]));
    }

    private static String block(String x) {
        return "block" + x;
    }

    private static Transition<String> simple(String from, String to) {
        return new Transition<>(
                Map.of(from, 1),
                Set.of(),
                Set.of(),
                Map.of(to, 1)
        );
    }

    private static Transition<String> enter(String name) {
        return new Transition<>(
                Map.of(middle, 1),
                Set.of(),
                Set.of(block(name)),
                Map.of(name, 1, block(name), 1)
        );
    }

    private static Transition<String> leave(String name) {
        return new Transition<>(
                Map.of(name, 1),
                others(name),
                Set.of(),
                Map.of(middle, 1)
        );
    }
}
