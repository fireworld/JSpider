package cc.colorcat.jspider.test.download;

import cc.colorcat.jspider.internal.Log;
import com.sun.istack.internal.Nullable;

import java.io.*;

/**
 * Created by cxx on 17-11-15.
 * xx.ch@outlook.com
 */
public class FileUtils {
    public static final String USER_HOME;

    static {
        USER_HOME = System.getProperty("user.home");
    }

    public static File createSavePath(File directory, String folderName, String fileName) {
        File folder = directory;
        if (folderName != null && !folderName.isEmpty()) {
            folder = new File(folder, folderName);
        }
        if (folder.exists() || folder.mkdirs()) {
            String baseName, extName;
            int dot = fileName.lastIndexOf('.');
            if (dot != -1) {
                baseName = fileName.substring(0, dot);
                extName = fileName.substring(dot);
            } else {
                baseName = fileName;
                extName = "";
            }
            File savePath = new File(folder, baseName + extName);
            for (int i = 0; savePath.exists(); i++) {
                savePath = new File(folder, baseName + "_" + i + extName);
            }
            return savePath;
        }
        throw new RuntimeException("create directory failed, path = " + folder.toString());
    }

    public static File createDirWithinHome(String... children) {
        return createDir(USER_HOME, children);
    }

    public static File createDir(String directory, String... children) {
        StringBuilder dirPath = new StringBuilder();
        for (String child : children) {
            dirPath.append(File.separator).append(child);
        }
        File result = new File(directory, dirPath.toString());
        if (result.exists() || result.mkdirs()) {
            return result;
        }
        throw new RuntimeException("create directory failed, path = " + result.getAbsolutePath());
    }

    public static void createParentDirsIfNotExists(File path) {
        File parent = path.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new RuntimeException("create parent directory failed, path = " + path.getAbsolutePath());
        }
    }

    public static boolean writeString(File file, String str, boolean append) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, append))) {
            bw.write(str);
            bw.flush();
            return true;
        } catch (IOException e) {
            Log.e(e);
            return false;
        }
    }

    @Nullable
    public static String readString(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            for (int length = br.read(buffer); length != -1; length = br.read(buffer)) {
                sb.append(buffer, 0, length);
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e(e);
            return null;
        }
    }

    public static boolean dumpAndCloseQuietly(InputStream is, File to) {
        try {
            dumpAndClose(is, new FileOutputStream(to));
            return true;
        } catch (IOException e) {
            Log.e(e);
            return false;
        }
    }


    public static boolean dumpAndCloseQuietly(InputStream is, OutputStream os) {
        try {
            dumpAndClose(is, os);
            return true;
        } catch (IOException e) {
            Log.e(e);
            return false;
        }
    }

    public static void dumpAndClose(InputStream is, OutputStream os) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(os)) {
            byte[] buffer = new byte[2048];
            for (int length = bis.read(buffer); length != -1; length = bis.read(buffer)) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
        }
    }
}
