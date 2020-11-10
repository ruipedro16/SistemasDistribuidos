import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import static java.lang.Math.max;

class Agreement {

    private static class Instance {
        int result = Integer.MIN_VALUE;
        int count = 0;
    }

    private int N;
    private Instance agmnt = new Instance();
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();

    Agreement(int N) {
        this.N = N;
    }

    int propose(int choice) throws InterruptedException {
        lock.lock();
        try {
            Instance agmt = this.agmnt;
            agmt.count++;
            agmnt.result = max(agmt.result, choice);
            if (agmt.count < N) {
                while (this.agmnt == agmnt)
                    cond.await();
            } else {
                cond.signalAll();
                this.agmnt = new Instance();
            }
            return agmt.result;
        } finally {
            lock.unlock();
        }
    }
}
