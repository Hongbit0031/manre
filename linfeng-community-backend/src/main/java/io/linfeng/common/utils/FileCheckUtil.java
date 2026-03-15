package io.linfeng.common.utils;

import io.linfeng.common.exception.LinfengException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author linfeng
 * @date 2022/2/21 10:38
 */
public class FileCheckUtil {


    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif"
    ));

    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = new HashSet<>(Arrays.asList(
            "mp4"
    ));

    private static final Set<String> ALLOWED_FILE_EXTENSIONS = new HashSet<>();

    static {
        ALLOWED_FILE_EXTENSIONS.addAll(ALLOWED_IMAGE_EXTENSIONS);
        ALLOWED_FILE_EXTENSIONS.addAll(ALLOWED_VIDEO_EXTENSIONS);
    }

    public static void checkSize(long maxSize, long size) {
        // 单位 M
        int len = 1024 * 1024;
        if(size > (maxSize * len)){
            throw new LinfengException("上传文件超出规定大小");
        }
    }

    public static boolean checkFileIsImage(String fileName){
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            return true;
        }
        return false;
    }

    /**
     * 校验文件格式是否合法（只允许常见图片格式和mp4视频格式）
     * @param fileName 文件名
     * @throws LinfengException 如果文件格式不合法则抛出异常
     */
    public static void checkFileFormat(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new LinfengException("文件名不能为空");
        }

        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new LinfengException("上传文件格式非法");
        }

        String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        if (!ALLOWED_FILE_EXTENSIONS.contains(extension)) {
            throw new LinfengException("上传文件格式非法");
        }
    }
}
