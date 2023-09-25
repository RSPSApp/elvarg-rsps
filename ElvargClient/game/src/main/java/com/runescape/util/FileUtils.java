package com.runescape.util;

import com.runescape.sign.SignLink;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class FileUtils {

    public static byte[] readFile(String name) {
        try {
            RandomAccessFile raf = new RandomAccessFile(name, "r");
            ByteBuffer buf =
                    raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
            try {
                if (buf.hasArray()) {
                    return buf.array();
                } else {
                    byte[] array = new byte[buf.remaining()];
                    buf.get(array);
                    return array;
                }
            } finally {
                raf.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] fileToByteArray(int cacheIndex, int index) {
        try {
            if (SignLink.indexLocation(cacheIndex, index).length() <= 0) {
                return null;
            } else {
                SignLink.indexLocation(cacheIndex, index);
            }
            File file = new File(SignLink.indexLocation(cacheIndex, index));
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileData);
            fis.close();
            return fileData;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFileNameWithoutExtension(String fileName) {
        File tmpFile = new File(fileName);
        tmpFile.getName();
        int whereDot = tmpFile.getName().lastIndexOf('.');
        if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
            return tmpFile.getName().substring(0, whereDot);
        }
        return "";
    }

}
