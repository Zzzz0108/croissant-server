package cn.edu.seig.vibemusic.util;

/**
 * URL清理工具类
 * 用于清理图片URL中的"-blob"后缀
 * 
 * @author system
 * @since 2025-01-09
 */
public class UrlCleanupUtil {
    
    /**
     * 清理图片URL，移除"-blob"后缀
     * 
     * @param url 原始URL
     * @return 清理后的URL
     */
    public static String cleanupImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        
        // 移除"-blob"后缀
        if (url.contains("-blob")) {
            url = url.replace("-blob", "");
        }
        
        return url;
    }
    
    /**
     * 检查URL是否包含"-blob"后缀
     * 
     * @param url 要检查的URL
     * @return 如果包含"-blob"后缀返回true，否则返回false
     */
    public static boolean containsBlobSuffix(String url) {
        return url != null && url.contains("-blob");
    }
} 