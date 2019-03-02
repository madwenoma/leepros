package thread;

public class MyLock {
    public static class MySync extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire(int arg) {
            boolean casResult = super.compareAndSetState(0, 1);
            String log = casResult ? "-CAS SUCCESS" : "-CAS FAILED";
            System.out.println(Thread.currentThread() + log);
            return casResult;
        }

        @Override
        protected boolean tryRelease(int arg) {
            setState(0);
            return true;
        }

        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

    }

    MySync mySync = new MySync();

    public void lock() {
        mySync.acquire(1);
    }

    public void unlock() {
        mySync.release(1);
    }

}