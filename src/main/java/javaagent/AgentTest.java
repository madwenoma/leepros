package javaagent;
//1.0
//https://blog.csdn.net/f59130/article/details/78367045
//启动jvm参数：-javaagent:f:/my-agent.jar=first -javaagent:f:/my-agent.jar=seconde

//2.0
//https://blog.csdn.net/f59130/article/details/78481594
//
public class AgentTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("this is main");
        AgentTest test = new AgentTest();
        test.fun1();
        test.fun2();
    }

    private void fun1() throws InterruptedException {
        System.out.println("this is fun 1.");
        Thread.sleep(500);
    }

    private void fun2() throws InterruptedException {
        System.out.println("this is fun 2.");
        Thread.sleep(300);
    }
}


