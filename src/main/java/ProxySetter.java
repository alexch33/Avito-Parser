import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProxySetter implements Runnable {
  private List<Proxy> proxies;
  private ProxySearcher proxySearcher;

  public ProxySetter(ProxySearcher proxySearcher) {
    this.proxySearcher = proxySearcher;
  }

  private void setGoodProxiesList() {
    List<Proxy> searceherProxyList = Searcher.getProxyList();
    for (Proxy proxy : proxies) {
      if (!searceherProxyList.contains(proxy)) {
        searceherProxyList.add(proxy);
      }
    }
    Searcher.setProxyList(searceherProxyList);
  }

  public void deleteProxy(Proxy proxy) {
    if (proxies.contains(proxy)) {
      proxies.remove(proxy);
      proxySearcher.deleteProxy(proxy);
    }
  }

  @Override
  public void run() {
    go();
  }

  private void addProxy(Proxy proxy) {
    if (!proxies.contains(proxy)) proxies.add(proxy);
    setGoodProxiesList();
  }

  private void go() {
    try {
      while (true) {
        if (proxies == null) {
          proxies = proxySearcher.getProxies();
        } else {
          for (Proxy proxy : proxySearcher.getProxies()) {
            addProxy(proxy);
          }
        }
        try {
          TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      go();
      try {
        TimeUnit.MILLISECONDS.sleep(15000);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
    }
  }
}
