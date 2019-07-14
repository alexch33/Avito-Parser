import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Base64;

import static org.bytedeco.leptonica.global.lept.pixDestroy;
import static org.bytedeco.leptonica.global.lept.pixReadMem;
import static org.bytedeco.tesseract.presets.tesseract.LC_ALL;
import static org.bytedeco.tesseract.presets.tesseract.setlocale;

public class OcrPhoneScrapper {
    private static final int OCR_COLOR_THRESHOLD = 100;
    private static OcrPhoneScrapper ocrPhoneScrapper = null;

    private OcrPhoneScrapper(){}

    public static OcrPhoneScrapper getInstance() {
        if (ocrPhoneScrapper == null)
            ocrPhoneScrapper = new OcrPhoneScrapper();

        return ocrPhoneScrapper;
    }

    public String ocrPhoneFromBase64Picture(String base64Picture) {
        return  ocrPhoneFromBufferedImage(getImageFromBase64(base64Picture));
    }

    private String ocrPhoneFromBufferedImage(BufferedImage imgBuff) {
        if (imgBuff == null) return null;

        String parsedOut = null;

        try {
            // Color image to pure black and white
            for (int x = 0; x < imgBuff.getWidth(); x++) {
                for (int y = 0; y < imgBuff.getHeight(); y++) {
                    Color color = new Color(imgBuff.getRGB(x, y));
                    int red = color.getRed();
                    int green = color.getBlue();
                    int blue = color.getGreen();
                    if (red + green + blue > OCR_COLOR_THRESHOLD) {
                        red = green = blue = 0; // Black
                    } else {
                        red = green = blue = 255; // White
                    }
                    Color col = new Color(red, green, blue);
                    imgBuff.setRGB(x, y, col.getRGB());
                }
            }

            // OCR recognition
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imgBuff, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            TessBaseAPI api = new TessBaseAPI();

            setlocale(LC_ALL(), "C");

            api.Init(ParserManager.getData().getAbsolutePath(), "eng");

            ByteBuffer imgBB = ByteBuffer.wrap(imageBytes);

            PIX image = pixReadMem(imgBB, imageBytes.length);
            api.SetImage(image);

            // Get OCR result
            BytePointer outText = api.GetUTF8Text();

            // Destroy used object and release memory
            api.End();
            api.close();
            outText.deallocate();
            pixDestroy(image);
            System.out.println("parsed text from picture: " + outText.getString().trim());

            parsedOut = outText.getString().trim().replaceAll(" ", "").replaceAll("-", "").trim().substring(1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        parsedOut = "+7" + parsedOut;
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("Scrapped phone number: " + parsedOut);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        return parsedOut;
    }

    private BufferedImage getImageFromBase64(String base64Picture) {
        if (base64Picture == null) {
            return null;
        }

        String imageDataBytes = base64Picture.substring(base64Picture.indexOf(",")+1);

        InputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(imageDataBytes.getBytes()));
        BufferedImage bImage2 = null;
        try {
            bImage2 = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bImage2;
    }
}
