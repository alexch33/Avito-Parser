import java.io.Serializable;
import java.net.URL;
import java.util.Date;

public class Ad implements Serializable {
  private int price;
  private String description;
  private String title;
  private URL url;
  private Date date;
  private String[] photos;


  Ad(int price, String description, String title, URL url, Date date, String[] imgsUrls) {
    this.price = price;

    if (description.length() > 2)
      this.description = description;
    else this.description = "no description";


    this.title = title;
    this.url = url;
    this.photos = imgsUrls;
    this.date = date;
  }


  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTitle() {
    return title == null ? "no title" : title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public URL getUrl() {
    return url;
  }

  public void setUrl(URL url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return "Ad{" +
            "price='" + price + '\'' +
            ", description='" + description + '\'' +
            ", title='" + title + '\'' +
            ", url=" + url +
            ", date=" + date +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Ad ad = (Ad) o;

    if (price != ad.price) return false;
    if (description != null ? !description.equals(ad.description) : ad.description != null) return false;
    if (title != null ? !title.equals(ad.title) : ad.title != null) return false;
    if (url != null ? !url.equals(ad.url) : ad.url != null) return false;
    return date != null ? date.equals(ad.date) : ad.date == null;
  }

  @Override
  public int hashCode() {
    int result = price;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (url != null ? url.hashCode() : 0);
    result = 31 * result + (date != null ? date.hashCode() : 0);
    return result;
  }

  public String[] getPhotos() {
    return photos;
  }
}
