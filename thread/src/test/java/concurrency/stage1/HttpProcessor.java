package concurrency.stage1;

public class HttpProcessor implements Runnable {

    private final User user;
    private final UserServlet userServlet;

    public HttpProcessor(final User user, final UserServlet userServlet) {
        this.user = user;
        this.userServlet = userServlet;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread());
        userServlet.service(user);
    }
}
