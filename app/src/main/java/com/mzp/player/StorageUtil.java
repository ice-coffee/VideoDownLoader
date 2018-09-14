package com.mzp.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/12/16 上午11:59
 * 描述:
 */
class StorageUtil {

    private StorageUtil() {
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean createOrExistsFile(final File file) {
        if (file == null){
            return false;
        }

        if (file.exists()){
            return file.isFile();
        }

        if (!createOrExistsDir(file.getParentFile())){
            return false;
        }

        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存 apk 文件
     *
     * @param is
     * @param saveFile
     * @return
     */
    static File saveFile(InputStream is, File saveFile) {

        File parentFile = saveFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }

        if (writeFile(saveFile, is)) {
            return saveFile;
        } else {
            return null;
        }
    }



    static boolean writeStringToFile(File saveFile, String content) {
        BufferedWriter out = null;
        try {
            if (!saveFile.exists()) {
                saveFile.createNewFile(); // 创建新文件
            }
            out = new BufferedWriter(new FileWriter(saveFile));
            out.write(content); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件

        } catch (Exception e) {
            if (saveFile != null && saveFile.exists()) {
                saveFile.deleteOnExit();
            }
            e.printStackTrace();
        } finally {
            closeStream(out); // 最后记得关闭文件
        }

        return false;
    }

    /**
     * 根据输入流，保存文件
     *
     * @param saveFile 下载目录
     * @param is
     * @return
     */
    static boolean writeFile(File saveFile, InputStream is) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(saveFile);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = is.read(data)) != -1) {
                os.write(data, 0, length);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            if (saveFile != null && saveFile.exists()) {
                saveFile.deleteOnExit();
            }
            e.printStackTrace();
        } finally {
            closeStream(os);
            closeStream(is);
        }
        return false;
    }

    /**
     * Write file from string.
     *
     * @param file    The file.
     * @param content The string of content.
     * @param append  True to append, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromString(final File file,
                                              final String content,
                                              final boolean append) {

        if (file == null || content == null){
            return false;
        }

        if (!createOrExistsFile(file)){
            return false;
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void removeLineFromFile(String file, String lineToRemove) {

        BufferedReader br = null;
        PrintWriter pw = null;
        try {

            File inFile = new File(file);

            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            br = new BufferedReader(new FileReader(file));
            pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {

                if (!line.trim().equals(lineToRemove)) {

                    pw.println(line);
                    pw.flush();
                }
            }

            //Delete the original file
            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile)) {
                System.out.println("Could not rename file");
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            closeStream(pw);
            closeStream(br);
        }
    }

    /**
     * 删除文件或文件夹
     *
     * @param file
     */
    static void deleteFile(File file) {
        try {
            if (file == null || !file.exists()) {
                return;
            }

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        if (f.exists()) {
                            if (f.isDirectory()) {
                                deleteFile(f);
                            } else {
                                f.delete();
                            }
                        }
                    }
                }
            } else {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭流
     *
     * @param closeable
     */
    static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
