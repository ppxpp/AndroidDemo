package com.tencent.filechecher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoozhou on 2016/5/8.
 */
public class FileUtils {

    // SD卡根目录
    public static String PREFIX_SDCARD_EXTERNAL = "/storage/extsd";
    // 车机存储根目录
    public static String PREFIX_NATIVE_EXTERNAL = "/storage/extsd/ztest";

    public static final int ERR_SUCCESS = 0;
    public static final int ERR_FILE_NOT_FOUND = 1;
    public static final int ERR_IS_NOT_FILE = 2;
    public static final int ERR_IO_EXCEPTION = 3;


    private static final int BUFFER_SIZE = 1024 * 4;

    public static List<String> traverseFiles(String prefix, String filePath, FilenameFilter filenameFilter){
        List<String> list = new ArrayList<>();
        String fullPath = prefix + File.separator + filePath;
        File file = new File(fullPath);
        if (file.isFile()) {
            list.add(filePath);
            System.out.println("add " + filePath);
        }else if (file.isDirectory()) {
            String[] filesNames = file.list(filenameFilter);
            if (filesNames != null) {
                for (String fileName : filesNames) {
                    list.addAll(traverseFiles(prefix, filePath + File.separator + fileName, filenameFilter));
                }
            }
        }
        return list;
    }



    public static int copy(String srcFilePath, String dstFilePath){
        File srcFile = new File(srcFilePath);
        File dstFile = new File(dstFilePath);
        if (!srcFile.exists()){
            return ERR_FILE_NOT_FOUND;
        }
        if (!srcFile.isFile()){
            return ERR_IS_NOT_FILE;
        }
        if (dstFile.exists()){
            dstFile.delete();
        }
        //mkdirs
        int idx = dstFilePath.lastIndexOf(File.separator);
        String dstDirStr = dstFilePath.substring(0, idx);
        File dstDir = new File(dstDirStr);
        if (!dstDir.exists()) {
            dstDir.mkdirs();
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dstFile));
            byte[] buffer = new byte[BUFFER_SIZE];
            int count = 0;
            while ((count = bis.read(buffer, 0, buffer.length)) > 0){
                bos.write(buffer, 0, count);
            }
            bos.flush();
            bos.close();
            bis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ERR_FILE_NOT_FOUND;
        } catch (IOException e) {
            e.printStackTrace();
            return ERR_IO_EXCEPTION;
        }
        return ERR_SUCCESS;
    }

    public static String calculateMD5(String filePath, long byteOffset, long byteCount){
        String md5 = null;
        File file = new File(filePath);
        if (file.exists() && file.isFile()){
            long length = file.length();
            if ((byteOffset + byteCount) > length){
                if (byteCount >= length){
                    byteOffset = 0L;
                    byteCount = length;
                }else{
                    byteOffset = length - byteCount;
                }
            }

            try {
                byte[] content = new byte[(int)byteCount];
                int count = 0;
                int total = 0;
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.skip(byteOffset);
                while ((count = bis.read(content, total, content.length - total)) > 0){
                    total += count;
                }
                byte[] hash = MessageDigest.getInstance("MD5").digest(content);
                StringBuilder hex = new StringBuilder(hash.length * 2);
                for (byte b : hash) {
                    if ((b & 0xFF) < 0x10) {
                        hex.append("0");
                    }
                    hex.append(Integer.toHexString(b & 0xFF));
                }
                md5 = hex.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return md5;

    }
}
