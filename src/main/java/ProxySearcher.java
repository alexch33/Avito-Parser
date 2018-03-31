import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class ProxySearcher implements Runnable {
  private static final String PROXY_URL = "https://www.proxynova.com/proxy-server-list";
  private static HashMap<String, Integer> proxyList = new HashMap<>();
  private List<Proxy> proxies = new CopyOnWriteArrayList<>();

  public synchronized List<Proxy> getProxies() {
    return proxies;
  }

  public void deleteProxy(Proxy proxy) {
    if (proxies.contains(proxy)) proxies.remove(proxy);
  }

  private boolean fetchProxyList() {
  try {
    Document proxyPage = Searcher.parse(PROXY_URL);
    assert proxyPage != null;
    Elements elements = proxyPage.select("#tbl_proxy_list > tbody:nth-child(2) > tr");
    for (Element el : elements) {
      try {
        String ip = el.select("td > abbr").attr("title");
        Integer port = Integer.parseInt(el.tagName("abbr").text().split(" ")[0]);
//           System.out.println(ip +  " " + port + " IP AND PORT");
        proxyList.put(ip, port);
      } catch (Exception e) {
      }
    }
    return proxyList.size() > 0;
  }catch(Exception e) {
    e.printStackTrace();
  }
    return false;
  }

  private void getGoodProxies() {
   if(fetchProxyList()) {
     for (Map.Entry<String, Integer> ipPort : proxyList.entrySet()) {
       try {
         Proxy test = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ipPort.getKey(), ipPort.getValue()));
         Jsoup.connect("https://www.google.ru/").timeout(10000).proxy(test).get();
         System.out.println("Proxy was found!" + test);
         proxies.add(test);
       } catch (Exception e) {
       }
     }
   }
  }

  @Override
  public void run() {
    while (true) {
      getGoodProxies();
      try {
        proxies = new CopyOnWriteArrayList<>();
        TimeUnit.MINUTES.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
