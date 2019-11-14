import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;

public class Multiplicator {

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Transition<String>> list = new ArrayList<>();

        String a = "A", b = "B", c = "C", d = "D", e = "E", f = "F";
//        // t0
//        list.add(new Transition<>(
//                Map.of(a, 1),
//                Set.of(),
//                Set.of(c),
//                Map.of(b, 1, d, 1)
//        ));
//
//        // t1
//        list.add(new Transition<>(
//                Map.of(b, 1),
//                Set.of(),
//                Set.of(e),
//                Map.of(a, 1)
//        ));
//
//        // t2
//        list.add(new Transition<>(
//                Map.of(e, 1),
//                Set.of(),
//                Set.of(a),
//                Map.of(c, 1)
//        ));
//
//        // t3
//        list.add(new Transition<>(
//                Map.of(c, 1, f, 1),
//                Set.of(),
//                Set.of(b),
//                Map.of(e, 1)
//        ));

        // u0
        list.add(new Transition<>(
                Map.of(a, 1, c, 1),
                Set.of(),
                Set.of(),
                Map.of(b, 1, c, 1, d, 1)
        ));

        // u1
        list.add(new Transition<>(
                Map.of(b, 1, e, 1),
                Set.of(),
                Set.of(),
                Map.of(a, 1, e, 1)
        ));

        // u2
        list.add(new Transition<>(
                Map.of(e, 1, f, 1),
                Set.of(),
                Set.of(b),
                Map.of(c, 1)
        ));

        // u3
        list.add(new Transition<>(
                Map.of(c, 1),
                Set.of(),
                Set.of(),
                Map.of(e, 1)
        ));

        // u4
        list.add(new Transition<>(
                Map.of(),
                Set.of(),
                Set.of(c, e),
                Map.of(e, 1)
        ));


        int initA = 12, initF = 12;
        PetriNet<String> net = new PetriNet<>(
//                Map.of(a, initA, f, initF, c, 1),
                Map.of(a, initA, f, initF),
                true
        );

        System.out.println("firing");
        for (int i = 0;; ++i) {
            System.out.println(net);
            net.fire(list);
            if (i == 34)
                System.out.println(i);
        }
    }
}
