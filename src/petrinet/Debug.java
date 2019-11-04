package petrinet;

import java.util.concurrent.Semaphore;

public class Debug {
    static final SeriesCondition nc = new FairSeriesCondition(new int[]{0, 0, 0, 0});
    static private class P implements Runnable {
        static private Semaphore mutex = new Semaphore(1, false);
        private final boolean bp;
        private final int[] neededValues;
        private final String message;
        P(int[] neededValues, String message, boolean bp) {
            this.bp = bp;
            this.neededValues = neededValues;
            this.message = message;
        }

        @Override
        public void run() {
            if (bp) {
                breakPoint();
            }
            try {
                mutex.acquire();
                nc.waitUntil(neededValues);
                System.out.println("start: " + message);
                Thread.sleep(1000);
                System.out.println("end: " + message);
                mutex.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void breakPoint() {
            int a = 0;
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        System.out.println("started");
//        nc.setTo(4, 0);
//        makeWriter(new int[]{0, 1, 0}, "second", false);
//        makeWriter(new int[]{0, 1, 1}, "third", false);
//        System.out.println("first");
//        nc.setTo(1, 1);
//        System.out.println();
    }

    private static void makeWriter(int[] values, String message, boolean bp) {
        new Thread(new P(values, message, bp)).start();
    }
}
