package com.tencent.filechecher;

/**
 * Created by haoozhou on 2016/5/17.
 */
public class DataFile {

    //tencent/wecarmusic
    public String path;
    public long length;
    public long offset;
    public long checkSize;
    public String md5;

    @Override
    public String toString() {
        return path + FileUtils.SEG_STR
                + length + FileUtils.SEG_STR
                + offset + FileUtils.SEG_STR
                + checkSize + FileUtils.SEG_STR
                + md5;
    }
}
