package cn.edu.seig.vibemusic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "role-path-permissions")
@Slf4j
public class RolePathPermissionsConfig {

    private Map<String, List<String>> permissions;

    public Map<String, List<String>> getPermissions() {
        log.info("📋 获取权限配置: {}", permissions);
        return permissions;
    }

    public void setPermissions(Map<String, List<String>> permissions) {
        log.info("🔧 设置权限配置: {}", permissions);
        this.permissions = permissions;
    }
}
