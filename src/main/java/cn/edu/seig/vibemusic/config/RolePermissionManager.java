package cn.edu.seig.vibemusic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 角色权限管理器
 */
@Slf4j
@Component
public class RolePermissionManager {

    private final RolePathPermissionsConfig rolePathPermissionsConfig;

    @Autowired
    public RolePermissionManager(RolePathPermissionsConfig rolePathPermissionsConfig) {
        this.rolePathPermissionsConfig = rolePathPermissionsConfig;
    }

    // 判断当前角色是否有权限访问请求的路径
    public boolean hasPermission(String role, String requestURI) {
        log.info("🔐 权限检查 - 角色: {}, 请求路径: {}", role, requestURI);
        
        Map<String, List<String>> permissions = rolePathPermissionsConfig.getPermissions();
        log.info("📋 权限配置: {}", permissions);
        
        List<String> allowedPaths = permissions.get(role);
        log.info("🎯 角色 {} 允许的路径: {}", role, allowedPaths);
        
        if (allowedPaths != null) {
            for (String path : allowedPaths) {
                log.info("🔍 检查路径匹配: {} 是否以 {} 开头", requestURI, path);
                if (requestURI.startsWith(path)) {
                    log.info("✅ 权限验证通过 - 路径 {} 匹配 {}", requestURI, path);
                    return true;
                }
            }
        }
        
        log.warn("❌ 权限验证失败 - 角色 {} 无法访问路径 {}", role, requestURI);
        return false;
    }
}

