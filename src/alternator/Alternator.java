package alternator;

import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;

public class Alternator {
    private static final int duration = 30;
    private static final String a = "A", b = "B", c = "C", middle = "M";
    private static final PetriNet<String> net = new PetriNet<>(Map.of(middle, 1), true);
    private static final String[] names = new String[] {a, b, c};
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
                    net.fire(enter);
                    System.out.println(name);
                    net.fire(leave);
                }
            } catch (InterruptedException ignored) {}
        }
    }

    public static void main(String[] args) {
        final int len = names.length;
        Thread[] threads = new Thread[len];

        ArrayList<Transition<String>> allTransitions;
        {
            allTransitions = new ArrayList<>();
            for (String i: names) {
                allTransitions.add(enter(i));
                allTransitions.add(leave(i));
            }
        }

        Set<Map<String, Integer>> reachable = net.reachable(allTransitions);

        if (!check(reachable)) {
            System.err.println("safety check failed");
            return;
        }

        for (int i = 0; i < len; ++i)
            threads[i] = new Thread(new P(names[i]));

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

    private static boolean check(Set<Map<String, Integer>> markings) {
        for (Map<String, Integer> i: markings) {
            for (String j: names) {
                if (i.get(j) > 1)
                    return false;
            }
        }
        return true;
    }

    private static Set<String> others(String name) {
        // 'list' is 'names' concatenated with itself
        ArrayList<String> list = new ArrayList<>(Arrays.asList(names));
        list.addAll(Arrays.asList(names));

        int start = 0;
        while (!list.get(start).equals(name))
            ++start;
        return Set.of(block(list.get(1 + start)), block(list.get(2 + start)));
    }

    private static String block(String x) {
        return "block" + x;
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
