package com.bootlabs.utils.qr;

import com.bootlabs.common.exception.ValidatorException;
import com.bootlabs.common.exception.TOTPException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.logging.Logger;


@UtilityClass
public class QrCodeGeneratorUtils {

    private final Logger LOGGER = Logger.getLogger(QrCodeGeneratorUtils.class.getName());

    private final int DEFAULT_BLACK_COLOR = 0xff000000;

    private final int DEFAULT_WHITE_COLOR = 0xFFFFFFFF;

    private final String DEFAULT_IMAGE_FORMAT = "png";

    private final int QR_CODE_DEFAULT_SIZE = 400;

    public String generateQRCode(String content) {
        try {
            byte[] bytes = getQRCodeByteImage(content);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (WriterException | IOException e) {
            throw new TOTPException("An unexpected error occurred while generating the QR code, please try again");
        }
    }

    /**
     * @param codeContent content text
     * @return Byte image
     * @throws WriterException if an error occurs during encoding
     * @throws IOException if an error occurs during writing or when not
     *       able to create required ImageOutputStream.
     */
    private byte[] getQRCodeByteImage(String codeContent) throws WriterException, IOException {
        if(StringUtils.isBlank(codeContent)){
            throw new ValidatorException("QR Content cannot be null");
        }

        BufferedImage qrcodeImage = qrCodeImageProcessing(codeContent, QR_CODE_DEFAULT_SIZE);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        ImageIO.write(qrcodeImage, DEFAULT_IMAGE_FORMAT, pngOutputStream);

        LOGGER.info("Encoding byte successful.");
        return pngOutputStream.toByteArray();
    }
    
    /**
     * @param codeContent qr code content
     * @param qrCodeSize qr code size
     * @return @{@link BufferedImage}
     * @throws WriterException if cannot encode
     * @throws IOException if path not found
     */
    private BufferedImage qrCodeImageProcessing(String codeContent, int qrCodeSize) throws WriterException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(codeContent, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, getEncodeHintType());

        int qrcodeWidth = bitMatrix.getWidth();
        int qrcodeHeight = bitMatrix.getHeight();

        // Generate QR code image
        BufferedImage qrcodeImage = new BufferedImage(qrcodeWidth, qrcodeHeight, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < qrcodeWidth; x++) {
            for (int y = 0; y < qrcodeHeight; y++) {
                qrcodeImage.setRGB(x, y, (bitMatrix.get(x, y) ? DEFAULT_BLACK_COLOR : DEFAULT_WHITE_COLOR));
            }
        }

        return qrcodeImage;
    }

    /**
     * @return EnumMap EncodeHintType
     */
    private EnumMap<EncodeHintType, Object> getEncodeHintType() {
        EnumMap<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CharacterSetECI.UTF8);
        hints.put(EncodeHintType.MARGIN, 4);

        return hints;
    }
}
