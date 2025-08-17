package cn.edu.seig.vibemusic.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UrlCleanupUtil 测试类
 * 
 * @author system
 * @since 2025-01-09
 */
public class UrlCleanupUtilTest {

    @Test
    public void testCleanupImageUrl_WithBlobSuffix() {
        // 测试包含"-blob"后缀的URL
        String urlWithBlob = "http://localhost:9000/vibe-music-data/artists/98b29849-aa07-4d91-b23f-e10ff91807cc-blob";
        String expected = "http://localhost:9000/vibe-music-data/artists/98b29849-aa07-4d91-b23f-e10ff91807cc";
        
        String result = UrlCleanupUtil.cleanupImageUrl(urlWithBlob);
        assertEquals(expected, result);
    }

    @Test
    public void testCleanupImageUrl_WithoutBlobSuffix() {
        // 测试不包含"-blob"后缀的URL
        String urlWithoutBlob = "http://localhost:9000/vibe-music-data/artists/98b29849-aa07-4d91-b23f-e10ff91807cc";
        
        String result = UrlCleanupUtil.cleanupImageUrl(urlWithoutBlob);
        assertEquals(urlWithoutBlob, result);
    }

    @Test
    public void testCleanupImageUrl_WithMultipleBlobSuffixes() {
        // 测试包含多个"-blob"后缀的URL（虽然这种情况不应该发生）
        String urlWithMultipleBlob = "http://localhost:9000/vibe-music-data/artists/98b29849-aa07-4d91-b23f-e10ff91807cc-blob-blob";
        String expected = "http://localhost:9000/vibe-music-data/artists/98b29849-aa07-4d91-b23f-e10ff91807cc";
        
        String result = UrlCleanupUtil.cleanupImageUrl(urlWithMultipleBlob);
        assertEquals(expected, result);
    }

    @Test
    public void testCleanupImageUrl_NullUrl() {
        // 测试null URL
        String result = UrlCleanupUtil.cleanupImageUrl(null);
        assertNull(result);
    }

    @Test
    public void testCleanupImageUrl_EmptyUrl() {
        // 测试空字符串URL
        String result = UrlCleanupUtil.cleanupImageUrl("");
        assertEquals("", result);
    }

    @Test
    public void testContainsBlobSuffix_WithBlobSuffix() {
        // 测试检查包含"-blob"后缀的URL
        String urlWithBlob = "http://localhost:9000/vibe-music-data/artists/98b29849-aa07-4d91-b23f-e10ff91807cc-blob";
        
        boolean result = UrlCleanupUtil.containsBlobSuffix(urlWithBlob);
        assertTrue(result);
    }

    @Test
    public void testContainsBlobSuffix_WithoutBlobSuffix() {
        // 测试检查不包含"-blob"后缀的URL
        String urlWithoutBlob = "http://localhost:9000/vibe-music-data/artists/98b29849-aa07-4d91-b23f-e10ff91807cc";
        
        boolean result = UrlCleanupUtil.containsBlobSuffix(urlWithoutBlob);
        assertFalse(result);
    }

    @Test
    public void testContainsBlobSuffix_NullUrl() {
        // 测试检查null URL
        boolean result = UrlCleanupUtil.containsBlobSuffix(null);
        assertFalse(result);
    }

    @Test
    public void testRealWorldExamples() {
        // 测试真实的URL示例
        String[] testUrls = {
            "http://localhost:9000/vibe-music-data/artists/98b29849-aa07-4d91-b23f-e10ff91807cc-blob",
            "http://localhost:9000/vibe-music-data/songCovers/6ed73c09-6e2d-4630-a506-e33e8f3daf88-blob",
            "http://localhost:9000/vibe-music-data/playlists/70dece9e-debc-4a3b-ba56-0742ed95ebe9-blob",
            "http://localhost:9000/vibe-music-data/users/ee425603-fa15-454f-925e-b8fdf4a19add-blob"
        };

        String[] expectedUrls = {
            "http://localhost:9000/vibe-music-data/artists/98b29849-aa07-4d91-b23f-e10ff91807cc",
            "http://localhost:9000/vibe-music-data/songCovers/6ed73c09-6e2d-4630-a506-e33e8f3daf88",
            "http://localhost:9000/vibe-music-data/playlists/70dece9e-debc-4a3b-ba56-0742ed95ebe9",
            "http://localhost:9000/vibe-music-data/users/ee425603-fa15-454f-925e-b8fdf4a19add"
        };

        for (int i = 0; i < testUrls.length; i++) {
            String result = UrlCleanupUtil.cleanupImageUrl(testUrls[i]);
            assertEquals(expectedUrls[i], result, "URL " + i + " 清理失败");
        }
    }
} 