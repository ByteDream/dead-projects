//http://batik.2283329.n4.nabble.com/Help-with-transcoding-SVG-to-PNG-Batik-1-9-td4656983.html

package org.blueshard.theosUI.utils;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class SVGToGenericImage {

    private final ByteArrayOutputStream genericImage;

    public SVGToGenericImage(String file, Transcoder transcoder, float height, float width) throws TranscoderException, IOException {
        this.genericImage = convert(new FileInputStream(file), transcoder, height, width);
    }

    public SVGToGenericImage(InputStream inputStream, Transcoder transcoder, float height, float width) throws TranscoderException, IOException {
        this.genericImage = convert(inputStream, transcoder, height, width);
    }

    private ByteArrayOutputStream convert(InputStream inputStream, Transcoder transcoder, float height, float width) throws TranscoderException {
        ImageTranscoder imageTranscoder;

        if (transcoder.equals(Transcoder.JPEG)) {
            imageTranscoder = new JPEGTranscoder();
        } else if (transcoder.equals(Transcoder.PNG)) {
            imageTranscoder = new PNGTranscoder();
        } else if (transcoder.equals(Transcoder.TIFF)) {
            imageTranscoder = new TIFFTranscoder();
        } else {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        TranscoderInput input = new TranscoderInput(inputStream);
        TranscoderOutput output = new TranscoderOutput(byteArrayOutputStream);

        imageTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height);
        imageTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width);

        imageTranscoder.transcode(input, output);

        return byteArrayOutputStream;
    }

    public BufferedImage asBufferedImage() throws IOException {
        return ImageIO.read(asByteArrayInputStream());
    }

    public ByteArrayInputStream asByteArrayInputStream() {
        return new ByteArrayInputStream(genericImage.toByteArray());
    }

    public ByteArrayOutputStream asByteArrayOutputStream() {
        return genericImage;
    }

    public enum Transcoder {

        JPEG,
        PNG,
        TIFF

    }

}
