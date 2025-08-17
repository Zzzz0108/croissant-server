package cn.edu.seig.vibemusic.interceptor;


import cn.edu.seig.vibemusic.config.RolePermissionManager;
import cn.edu.seig.vibemusic.constant.JwtClaimsConstant;
import cn.edu.seig.vibemusic.constant.MessageConstant;
import cn.edu.seig.vibemusic.constant.PathConstant;
import cn.edu.seig.vibemusic.util.JwtUtil;
import cn.edu.seig.vibemusic.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RolePermissionManager rolePermissionManager;

    public void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8"); // 设置字符编码为UTF-8
        response.setContentType("application/json;charset=UTF-8"); // 设置响应的Content-Type
        response.getWriter().write(message);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 允许 CORS 预检请求（OPTIONS 方法）直接通过
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true; // 直接放行，确保 CORS 预检请求不会被拦截
        }

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
        }
        String path = request.getRequestURI();

        // 获取 Spring 的 PathMatcher 实例
        PathMatcher pathMatcher = new AntPathMatcher();

        // 定义允许访问的路径
        List<String> allowedPaths = Arrays.asList(
                PathConstant.PLAYLIST_DETAIL_PATH,
                PathConstant.ARTIST_DETAIL_PATH,
                PathConstant.SONG_LIST_PATH,
                PathConstant.SONG_DETAIL_PATH
        );

        // 检查路径是否匹配
        boolean isAllowedPath = allowedPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (token == null || token.isEmpty()) {
            if (isAllowedPath) {
                return true; // 允许未登录用户访问这些路径
            }

            sendErrorResponse(response, 401, MessageConstant.NOT_LOGIN); // 缺少令牌
            return false;
        }

        try {
            // 从redis中获取相同的token
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);
            if (redisToken == null) {
                // token失效
                log.warn("❌ Token失效 - token: {}", token);
                throw new RuntimeException();
            }

            Map<String, Object> claims = JwtUtil.parseToken(token);
            String role = (String) claims.get(JwtClaimsConstant.ROLE);
            String requestURI = request.getRequestURI();
            
            log.info("🔐 权限验证 - 角色: {}, 请求路径: {}, Token: {}", role, requestURI, token.substring(0, Math.min(20, token.length())) + "...");

            if (rolePermissionManager.hasPermission(role, requestURI)) {
                // 把业务数据存储到ThreadLocal中
                ThreadLocalUtil.set(claims);
                log.info("✅ 权限验证通过 - 用户 {} 可以访问 {}", role, requestURI);
                return true;
            } else {
                log.warn("❌ 权限验证失败 - 用户 {} 无法访问 {}", role, requestURI);
                sendErrorResponse(response, 403, MessageConstant.NO_PERMISSION); // 无权限访问
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Token验证异常: {}", e.getMessage(), e);
            sendErrorResponse(response, 401, MessageConstant.SESSION_EXPIRED); // 令牌无效
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清空ThreadLocal中的数据
        ThreadLocalUtil.remove();
    }
}
