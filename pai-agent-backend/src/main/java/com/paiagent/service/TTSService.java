package com.paiagent.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * 阿里云 TTS 语音合成服务
 */
@Slf4j
@Service
public class TTSService {

    @Value("${aliyun.tts.access-key-id:}")
    private String accessKeyId;

    @Value("${aliyun.tts.access-key-secret:}")
    private String accessKeySecret;

    @Value("${aliyun.tts.app-key:}")
    private String appKey;

    @Value("${storage.local.path:./uploads}")
    private String storagePath;

    @Value("${storage.local.url-prefix:http://localhost:8080/files}")
    private String urlPrefix;

    private final OkHttpClient httpClient;

    public TTSService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 合成语音
     */
    public TTSResult synthesize(String text, String voice, Double speed, Integer volume, String format) {
        try {
            // 确保存储目录存在
            Path audioDir = Paths.get(storagePath, "audio");
            Files.createDirectories(audioDir);

            // 生成文件名
            String fileName = IdUtil.fastSimpleUUID() + "." + (format != null ? format : "mp3");
            Path filePath = audioDir.resolve(fileName);

            // 调用阿里云 TTS API（这里使用简化的 HTTP 方式，实际生产环境建议使用官方 SDK）
            byte[] audioData = callTTSApi(text, voice, speed, volume, format);

            // 保存文件
            Files.write(filePath, audioData);

            // 构建返回结果
            String fileUrl = urlPrefix + "/audio/" + fileName;
            
            log.info("TTS 合成成功: {}", fileUrl);
            
            return TTSResult.builder()
                    .fileName(fileName)
                    .filePath(filePath.toString())
                    .fileUrl(fileUrl)
                    .fileSize((long) audioData.length)
                    .format(format != null ? format : "mp3")
                    .build();
        } catch (Exception e) {
            log.error("TTS 合成失败", e);
            throw new RuntimeException("语音合成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 调用阿里云 TTS API
     * 注意：这是一个简化的实现，实际生产环境建议使用阿里云官方 SDK
     */
    private byte[] callTTSApi(String text, String voice, Double speed, Integer volume, String format) throws IOException {
        // 如果没有配置阿里云密钥，返回模拟数据（用于测试）
        if (accessKeyId == null || accessKeyId.isEmpty()) {
            log.warn("阿里云 TTS 未配置，返回模拟音频数据");
            return createMockAudioData();
        }

        // 构建请求
        String url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/tts";
        
        JSONObject body = new JSONObject();
        body.set("appkey", appKey);
        body.set("text", text);
        body.set("format", format != null ? format : "mp3");
        body.set("voice", voice != null ? voice : "zhixiaoxia");
        if (speed != null) {
            body.set("speech_rate", (int) ((speed - 1) * 500)); // 转换为阿里云的语速参数
        }
        if (volume != null) {
            body.set("volume", volume);
        }

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("X-NLS-Token", getToken())
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("TTS API 调用失败: " + response.code());
            }
            return response.body().bytes();
        }
    }

    /**
     * 获取阿里云 Token（简化实现）
     */
    private String getToken() {
        // 实际实现需要调用阿里云的 Token 获取接口
        // 这里返回空字符串，实际使用时需要实现完整的 Token 获取逻辑
        return "";
    }

    /**
     * 创建模拟音频数据（用于测试）
     */
    private byte[] createMockAudioData() {
        // 返回一个简单的静音 MP3 数据（用于测试）
        return new byte[1024];
    }

    /**
     * TTS 合成结果
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TTSResult {
        private String fileName;
        private String filePath;
        private String fileUrl;
        private Long fileSize;
        private Double duration;
        private String format;
    }
}
