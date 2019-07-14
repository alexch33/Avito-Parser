
public class MessageHTML {
    private String title;
    private String messageText;
    private String[] imgsUrls;
    private String phoneNumber;

    public MessageHTML(String title, String messageText, String[] messageImgs, String phoneNumber) {
        this.title = title;
        this.messageText = messageText;
        this.imgsUrls = messageImgs;
        this.phoneNumber = phoneNumber;
    }

    public String getTittle() {
        return this.title;
    }

    public String getBody() {
        String body = "<!DOCTYPE html>\n" +
                "<html>\n" +
                    "<head>\n" +
                        "<title>\n" +
                            this.title +
                        "</title>\n" +
                        "<meta " + "http-equiv"+"="+"Content" + "-Type"+ " content"+"="+"text/html; charset=utf-8"+">" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                        "<h3>\n" +
                            this.title +
                        "</h3>\n" +
                    "\n" +
                        getPhoneBlock() + "\n" +
                        "<p>\n" +
                            this.messageText +
                        "</p>\n" +
                        this.getImagesBlock() +
                    "\n" +
                    "</body>\n" +
                "</html>\n";
        return body;
    }

    private String getImagesBlock() {
        StringBuilder stringBuffer = new StringBuilder();

        if (this.imgsUrls == null) {
          return null;
        }

        for (String imgUrl : this.imgsUrls) {
            String img = "<img src='" + imgUrl + "'>" + "</img>";
            stringBuffer.append(img);
        }

        return "<div>" + stringBuffer.toString() + "</div>";
    }

    private String getPhoneBlock() {
          return "<a href=" + "tel:" + this.phoneNumber.trim() + ">" + this.phoneNumber + "</a>";
    }
}
