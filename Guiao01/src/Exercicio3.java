import java.util.concurrent.locks.ReentrantLock;

class Bank {

    private static class Account {
        private int balance;

        Account(int balance) {
            this.balance = balance;
        }

        int balance() {
            return balance;
        }

        boolean deposit(int value) {
            balance += value;
            return true;
        }
    }

    // Our single account, for now
    private Account savings = new Account(0);
    private ReentrantLock lock = new ReentrantLock();

    // Account balance
    public int balance() {
        lock.lock();
        try {
            return savings.balance();
        } finally {
            lock.unlock();
        }
    }

    // Deposit
    boolean deposit(int value) {
        lock.lock();
        try {
            return savings.deposit(value);
        } finally {
            lock.unlock();
        }
    }
}

class Deposits implements Runnable {
    private Bank b;

    Deposits(Bank b) {
        this.b = b;
    }

    public void run() {
        final long I = 1000;
        for (long i = 0; i < I; i++)
            b.deposit(100);
    }
}

class Exercicio3 {
    public static void main(String[] args) {
        final int N = 10;
        Bank b = new Bank();
        Deposits worker = new Deposits(b);
        Thread[] tv = new Thread[N];

        for (int i = 0; i < N; i++)
            tv[i] = new Thread(worker);

        for (int i = 0; i < N; i++)
            tv[i].start();

        try {
            for (Thread t : tv)
                t.join();
        } catch (InterruptedException e) {}

        System.out.println(b.balance());
    }
}
