package io.linfeng.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML富文本图片URL提取工具类
 * 用于从富文本内容中提取所有图片URL
 * 
 * @author linfeng
 * @date 2025/01/XX
 */
@Slf4j
public class HtmlImageExtractor {

    /**
     * 匹配img标签的src属性中的URL（支持单引号、双引号、无引号）
     * 匹配模式：<img src="http://..." 或 <img src='https://...' 或 <img src=https://...
     */
    private static final Pattern IMG_SRC_PATTERN = Pattern.compile(
        "<img[^>]+src\\s*=\\s*['\"]?([^'\"\\s>]+)['\"]?[^>]*>",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    /**
     * 匹配以http://或https://开头的URL（用于提取文本中的直接URL）
     */
    private static final Pattern HTTP_URL_PATTERN = Pattern.compile(
        "(https?://[^\\s<>\"']+)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * 从富文本内容中提取所有图片URL
     * 
     * @param htmlContent 富文本HTML内容
     * @return 图片URL列表（去重，保持顺序）
     */
    public static List<String> extractImageUrls(String htmlContent) {
        List<String> imageUrls = new ArrayList<>();
        
        if (StrUtil.isBlank(htmlContent)) {
            return imageUrls;
        }

        // 使用LinkedHashSet去重并保持顺序
        Set<String> urlSet = new LinkedHashSet<>();

        // 1. 提取img标签中的src属性
        extractFromImgTags(htmlContent, urlSet);

        // 2. 提取文本中直接出现的http/https URL（可能是图片URL）
        extractDirectUrls(htmlContent, urlSet);

        imageUrls.addAll(urlSet);
        return imageUrls;
    }

    /**
     * 从img标签中提取图片URL
     * 
     * @param htmlContent HTML内容
     * @param urlSet URL集合
     */
    private static void extractFromImgTags(String htmlContent, Set<String> urlSet) {
        Matcher matcher = IMG_SRC_PATTERN.matcher(htmlContent);
        while (matcher.find()) {
            String url = matcher.group(1).trim();
            if (isValidImageUrl(url)) {
                urlSet.add(url);
            }
        }
    }

    /**
     * 提取文本中直接出现的http/https URL
     * 
     * @param htmlContent HTML内容
     * @param urlSet URL集合
     */
    private static void extractDirectUrls(String htmlContent, Set<String> urlSet) {
        Matcher matcher = HTTP_URL_PATTERN.matcher(htmlContent);
        while (matcher.find()) {
            String url = matcher.group(1).trim();
            // 过滤掉已经在img标签中提取的URL，避免重复
            // 这里简单判断，如果URL看起来像图片URL就添加
            if (isValidImageUrl(url) && !isInImgTag(htmlContent, url)) {
                urlSet.add(url);
            }
        }
    }

    /**
     * 判断URL是否是有效的图片URL
     * 
     * @param url URL字符串
     * @return 是否有效
     */
    private static boolean isValidImageUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return false;
        }
        
        // 必须以http://或https://开头
        String lowerUrl = url.toLowerCase();
        if (!lowerUrl.startsWith("http://") && !lowerUrl.startsWith("https://")) {
            return false;
        }

        // 可选：检查是否是常见的图片格式（可选，因为有些URL可能没有扩展名）
        // 这里不做严格限制，只要是以http/https开头就认为是有效的图片URL
        
        return true;
    }

    /**
     * 判断URL是否在img标签中（用于去重）
     * 
     * @param htmlContent HTML内容
     * @param url URL
     * @return 是否在img标签中
     */
    private static boolean isInImgTag(String htmlContent, String url) {
        // 简单判断：如果URL出现在<img...src=...>模式中，则认为已经在img标签中提取过了
        Pattern pattern = Pattern.compile(
            "<img[^>]*src\\s*=\\s*['\"]?" + Pattern.quote(url) + "['\"]?[^>]*>",
            Pattern.CASE_INSENSITIVE
        );
        return pattern.matcher(htmlContent).find();
    }

    /**
     * 从富文本内容中提取所有图片URL（仅从img标签提取，更精确）
     * 
     * @param htmlContent 富文本HTML内容
     * @return 图片URL列表（去重，保持顺序）
     */
    public static List<String> extractImageUrlsFromImgTags(String htmlContent) {
        List<String> imageUrls = new ArrayList<>();
        
        if (StrUtil.isBlank(htmlContent)) {
            return imageUrls;
        }

        Set<String> urlSet = new LinkedHashSet<>();
        extractFromImgTags(htmlContent, urlSet);
        imageUrls.addAll(urlSet);
        
        return imageUrls;
    }
}

