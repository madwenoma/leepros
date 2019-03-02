package hystrix;

import java.util.concurrent.Semaphore;

/** 命令模式demo
 *
 * 调用者角色与接收者角色之间没有任何依赖关系，
 * 调用者实现功能时只需调用Command 抽象类的execute方法就可以，不需要了解到底是哪个接收者执行。
 *
 * 可扩展性：Command的子类可以非常容易地扩展，而调用者Invoker和高层次的模块Client不产生严 重的代码耦合。
 */
public class CommendDemo {


    public static void main(String[] args) {
        Executor checkExecutor = new CheckExecutor();
        Executor beginExecutor = new BeginExecutor();

        Commend checkCmd = new CheckCommend(checkExecutor);
        Commend beginCmd = new BeginCommend(beginExecutor);

        Invoker invoker = new Invoker(checkCmd);
        invoker.invoke();
        invoker = new Invoker(beginCmd);
        invoker.invoke();

        for (int i = 0; i < 10; i++) {
            Invoker finalInvoker = invoker;
            new Thread(() -> {
                finalInvoker.invoke();
            }).start();

        }


    }

}

interface Executor {
    void execute();
}

class CheckExecutor implements Executor {

    @Override
    public void execute() {
        System.out.println("do check...");
    }
}

class BeginExecutor implements Executor {

    @Override
    public void execute() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("begin...");
    }
}

interface Commend {
    void run();
}

class CheckCommend implements Commend {
    private Executor executor;

    public CheckCommend(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void run() {
        this.executor.execute();
    }

}

class BeginCommend implements Commend {
    private Executor executor;

    public BeginCommend(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void run() {
        this.executor.execute();
    }

}

class Invoker {
    private Commend commend;

    //在invoker里实现了对执行者并发数量的控制，
    Semaphore semaphore = new Semaphore(2);

    public Invoker(Commend commend) {
        this.commend = commend;
    }

    public void invoke(){
        if(semaphore.tryAcquire()){
            commend.run();
            semaphore.release();
        } else {
            System.out.println("未获取到信号量");
        }
    }
}