package cn.edu.seig.vibemusic.service.impl;

import cn.edu.seig.vibemusic.constant.MessageConstant;
import cn.edu.seig.vibemusic.service.MinioService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * 上传文件到 Minio
     *
     * @param file   文件
     * @param folder 文件夹
     * @return 可访问的 URL
     */
    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
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

            // 获取文件流
            InputStream inputStream = file.getInputStream();

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 返回可访问的 URL
            return endpoint + "/" + bucketName + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException(MessageConstant.FILE_UPLOAD + MessageConstant.FAILED + "：" + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件 URL
     */
    @Override
    public void deleteFile(String fileUrl) {
        try {
            // 解析 URL，获取文件路径
            String filePath = fileUrl.replace(endpoint + "/" + bucketName + "/", "");

            // 删除文件
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath)
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("文件删除失败: " + e.getMessage());
        }
    }
}
