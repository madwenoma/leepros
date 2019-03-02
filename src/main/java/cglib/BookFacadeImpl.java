package cglib;

public class BookFacadeImpl implements BookFacade {
    public BookFacadeImpl() {
        System.out.println("init...");
    }

    @Override
    public void addBook() {
        System.out.println("增加图书方法。。。");
    }

}