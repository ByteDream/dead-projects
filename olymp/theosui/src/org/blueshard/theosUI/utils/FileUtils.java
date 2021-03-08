package org.blueshard.theosUI.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtils {

    public static ArrayList<File> getAllFilesInDirectory(String path) throws IOException {
        ArrayList<File> files = new ArrayList<>();
        Files.walk(Paths.get(path)).map(Path::toFile).forEach(files::add);
        return files;
    }

    public static File[] sortFilesInDirectory(File path) {
        File[] files = path.listFiles();

        Arrays.sort(files, (object1, object2) -> object1.getName().compareToIgnoreCase(object2.getName()));

        return files;
    }

    public class CreateHashSum {

        String file;

        CreateHashSum(String file) {
            this.file = file;
        }

        public String MD5() throws IOException {
            try {
                StringBuilder hexString = new StringBuilder();
                MessageDigest md5 = MessageDigest.getInstance("MD5");

                InputStream inputStream = Files.newInputStream(Paths.get(file));
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    md5.update(buffer, 0 , length);
                }

                byte[] digest = md5.digest();

                for (byte b : digest) {
                    if ((0xff & b) < 0x10) {
                        hexString.append("0").append(Integer.toHexString((0xFF & b)));
                    } else {
                        hexString.append(Integer.toHexString(0xFF & b));
                    }
                }

                return hexString.toString();

            } catch (NoSuchAlgorithmException ignore) {
                return null;
            }
        }

    }

}
