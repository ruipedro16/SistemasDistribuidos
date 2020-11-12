import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse {

    private Map<String, Product> m = new HashMap<String, Product>();
    private Lock lock = new ReentrantLock();

    private class Product {
        int q = 0;
        Condition cond = lock.newCondition();
    }

    private Product get(String s) {
        Product p = m.get(s);
        if (p != null) return p;
        p = new Product();
        m.put(s, p);
        return p;
    }

    public void supply(String s, int q) {
        lock.lock();
        try {
            Product p = get(s);
            p.q += q;
            p.cond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void consume(String[] a) throws InterruptedException {
        lock.lock();
        try {
            for (int i = 0; i < a.length; ) {
                Product p = get(a[i]);
                i++;
                if (p.q == 0) {
                    p.cond.await();
                    i = 0;
                }
            }
            for (String s : a)
                get(s).q--;
        } finally {
            lock.unlock();
        }
    }
}
