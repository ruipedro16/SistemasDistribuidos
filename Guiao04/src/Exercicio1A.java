import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Barrier {
    private int N;
    private int count = 0;
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();

    private Barrier(int N) {
        this.N = N;
    }

    void await() throws InterruptedException {
        lock.lock();
        try {
            count++;
            if (count < N) {
                while (count < N)
                    cond.await();
            } else cond.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
