-- 清理图片URL中的"-blob"后缀
-- 这个脚本用于修复现有的图片URL，移除"-blob"后缀

-- 更新艺术家头像URL
UPDATE tb_artist 
SET avatar = REPLACE(avatar, '-blob', '') 
WHERE avatar LIKE '%-blob';

-- 更新歌曲封面URL
UPDATE tb_song 
SET cover_url = REPLACE(cover_url, '-blob', '') 
WHERE cover_url LIKE '%-blob';

-- 更新歌单封面URL
UPDATE tb_playlist 
SET cover_url = REPLACE(cover_url, '-blob', '') 
WHERE cover_url LIKE '%-blob';

-- 更新用户头像URL (注意：字段名是user_avatar，不是avatar)
UPDATE tb_user 
SET user_avatar = REPLACE(user_avatar, '-blob', '') 
WHERE user_avatar LIKE '%-blob';

-- 更新横幅图片URL
UPDATE tb_banner 
SET banner_url = REPLACE(banner_url, '-blob', '') 
WHERE banner_url LIKE '%-blob';

-- 显示更新结果
SELECT '艺术家头像URL更新完成' as message;
SELECT COUNT(*) as updated_artists FROM tb_artist WHERE avatar NOT LIKE '%-blob';

SELECT '歌曲封面URL更新完成' as message;
SELECT COUNT(*) as updated_songs FROM tb_song WHERE cover_url NOT LIKE '%-blob';

SELECT '歌单封面URL更新完成' as message;
SELECT COUNT(*) as updated_playlists FROM tb_playlist WHERE cover_url NOT LIKE '%-blob';

SELECT '用户头像URL更新完成' as message;
SELECT COUNT(*) as updated_users FROM tb_user WHERE user_avatar NOT LIKE '%-blob';

SELECT '横幅图片URL更新完成' as message;
SELECT COUNT(*) as updated_banners FROM tb_banner WHERE banner_url NOT LIKE '%-blob'; 