class Incrementer implements Runnable {
    public void run() {
        final long I = 100;

        for (long i = 0; i < I; i++)
            System.out.println(i);
    }
}

class Exercicio1 {
    public static void main(String[] args) {
        final int N = 10;
        Incrementer worker = new Incrementer();
        Thread[] tv = new Thread[N];

        for (int i = 0; i < N; i++)
            tv[i] = new Thread(worker);

        for (int i = 0; i < N; i++)
            tv[i].start();

        try {
            for (Thread t : tv)
                t.join();
        } catch (InterruptedException e) {}

        System.out.println("fim");
    }
}
