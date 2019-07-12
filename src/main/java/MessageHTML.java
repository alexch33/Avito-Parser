
public class MessageHTML {
    private String title;
    private String messageText;
    private String[] imgsUrls;

    public MessageHTML(String title, String messageText, String[] messageImgs) {
        this.title = title;
        this.messageText = messageText;
        this.imgsUrls = messageImgs;
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
                        "<h1>\n" +
                            this.title +
                        "</h1>\n" +
                    "\n" +
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
            String img = "<img src=" + imgUrl + ">" + "</img>";
            stringBuffer.append(img);
        }

        return "<div>" + stringBuffer.toString() + "</div>";
    }
}
