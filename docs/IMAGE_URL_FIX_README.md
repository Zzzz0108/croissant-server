# 图片URL修复说明

## 问题描述

在音乐服务器项目中，图片URL生成时包含了"-blob"后缀，这会导致图片无法正常显示。这个问题主要出现在以下情况：

1. 从浏览器上传文件时，原始文件名包含"-blob"后缀
2. 数据库中已存在的图片URL包含"-blob"后缀

## 修复内容

### 1. 文件上传逻辑修复

**文件位置**: `src/main/java/cn/edu/seig/vibemusic/service/impl/MinioServiceImpl.java`

**修复内容**:
- 在文件上传时自动移除"-blob"后缀
- 确保文件扩展名正确保留
- 生成干净的URL

**修改前**:
```java
String fileName = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
```

**修改后**:
```java
// 获取原始文件名并处理
String originalFilename = file.getOriginalFilename();
String fileExtension = "";

// 提取文件扩展名
if (originalFilename != null && originalFilename.contains(".")) {
    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
}

// 移除"-blob"后缀（如果存在）
if (originalFilename != null && originalFilename.contains("-blob")) {
    originalFilename = originalFilename.replace("-blob", "");
}

// 生成唯一文件名，确保扩展名正确
String fileName = folder + "/" + UUID.randomUUID() + fileExtension;
```

### 2. URL清理工具类

**文件位置**: `src/main/java/cn/edu/seig/vibemusic/util/UrlCleanupUtil.java`

**功能**:
- 提供静态方法清理图片URL
- 移除"-blob"后缀
- 检查URL是否包含"-blob"后缀

**主要方法**:
```java
public static String cleanupImageUrl(String url)
public static boolean containsBlobSuffix(String url)
```

### 3. 服务层URL清理

在以下服务实现类中添加了URL清理逻辑：

- `ArtistServiceImpl.java` - 艺术家头像URL清理
- `SongServiceImpl.java` - 歌曲封面URL清理  
- `PlaylistServiceImpl.java` - 歌单封面URL清理

**清理逻辑**:
```java
// 清理图片URL，移除"-blob"后缀
if (artistVO.getAvatar() != null) {
    artistVO.setAvatar(UrlCleanupUtil.cleanupImageUrl(artistVO.getAvatar()));
}
```

### 4. 数据库清理脚本

**文件位置**: `sql/cleanup_image_urls.sql`

**功能**:
- 清理数据库中现有的图片URL
- 移除所有"-blob"后缀
- 支持以下表的URL清理：
  - `tb_artist` (艺术家头像)
  - `tb_song` (歌曲封面)
  - `tb_playlist` (歌单封面)
  - `tb_user` (用户头像)
  - `tb_banner` (横幅图片)

## 使用方法

### 1. 部署修复后的代码

重新编译并部署项目，新的文件上传将自动处理"-blob"后缀问题。

### 2. 清理现有数据库

执行SQL清理脚本：

```sql
-- 在MySQL中执行
source sql/cleanup_image_urls.sql;
```

或者手动执行各个UPDATE语句。

### 3. 验证修复结果

检查以下接口返回的图片URL是否不再包含"-blob"后缀：

- `/artist/getAllArtists` - 艺术家列表
- `/artist/getRandomArtists` - 随机艺术家
- `/artist/getArtistDetail/{id}` - 艺术家详情
- `/song/getAllSongs` - 歌曲列表
- `/song/getRecommendedSongs` - 推荐歌曲
- `/song/getSongDetail/{id}` - 歌曲详情
- `/playlist/getAllPlaylists` - 歌单列表

## 注意事项

1. **缓存清理**: 由于使用了Spring Cache，建议在部署后清理相关缓存
2. **文件存储**: 确保MinIO中的实际文件没有"-blob"后缀
3. **前端兼容**: 前端代码无需修改，URL会自动清理
4. **性能影响**: URL清理逻辑对性能影响很小，在内存中处理

## 测试建议

1. 上传新的图片文件，验证URL生成是否正确
2. 检查现有图片URL是否正常显示
3. 验证各种图片相关接口的返回结果
4. 测试图片上传、更新、删除功能

## 相关文件

- `MinioServiceImpl.java` - 文件上传核心逻辑
- `UrlCleanupUtil.java` - URL清理工具类
- `ArtistServiceImpl.java` - 艺术家服务实现
- `SongServiceImpl.java` - 歌曲服务实现
- `PlaylistServiceImpl.java` - 歌单服务实现
- `cleanup_image_urls.sql` - 数据库清理脚本 