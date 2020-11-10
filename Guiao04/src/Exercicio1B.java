import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Barrier1 {
    private int N;
    private int count = 0;
    private int epoch = 0;
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();

    private Barrier1(int N) {
        this.N = N;
    }

    void await() throws InterruptedException {
        lock.lock();
        try {
            int e = epoch; // época em que o thread entra na barreira
            count++;
            if (count < N) {
                while (epoch == e)
                    cond.await();
            } else {
                cond.signalAll();
                count = 0;
                epoch++;
            }
        } finally {
            lock.unlock();
        }
    }
}

class Barrier2 {
    private int N;
    private int count = 0;
    private boolean open = false;
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();

    private Barrier2(int N) {
        this.N = N;
    }

    void await() throws InterruptedException {
        lock.lock();
        try {
            while (open)
                cond.await();
            count++;
            if (count < N) {
                while (!open)
                    cond.await();
            } else {
                cond.signalAll();
                open = true;
            }
            count--;
            if (count == 0) {
                open = false;
                cond.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
