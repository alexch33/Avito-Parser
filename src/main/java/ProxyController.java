import java.util.concurrent.TimeUnit;

public class ProxyController implements Runnable {
  private Thread searcherProxy;
  private Thread setterProxy;

  public void run() {
    go();

    while (true) {
      if (!searcherProxy.isAlive() || !setterProxy.isAlive()) {
        go();
        System.out.println("Critical ERROR!!! Restarting Proxy Threads......");
      }
      try {
        TimeUnit.MILLISECONDS.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }


  private void go() {
    try {
      ProxySearcher proxySearcher = new ProxySearcher();
      ProxySetter proxySetter = new ProxySetter(proxySearcher);
      Searcher.setProxySetter(proxySetter);
      searcherProxy = new Thread(proxySearcher);
      searcherProxy.setDaemon(true);
      searcherProxy.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable throwable) {
          System.out.println("Error in thread searcher Level: WARNING");
          searcherProxy.start();
        }
      });
      searcherProxy.start();
      setterProxy = new Thread(proxySetter);
      setterProxy.setDaemon(true);
      setterProxy.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable throwable) {
          System.out.println("Error in thread setter Level: WARNING");
          setterProxy.start();
        }
      });
      setterProxy.start();
    } catch (Exception e) {
      e.printStackTrace();
      go();
    }
  }
}
