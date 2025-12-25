package net.cnki.translate.web.utils;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.map.MapUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2025/11/28 16:30
 */
@Slf4j
public class ServletDownUtils {
    public static class CommonContentType{
        //二进制流，不知道下载文件类型
        public static final String common = "application/octet-stream";

        //2010 Excel	.xlsx
        public static final String excel2010 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        //    2003 Excel	.xls
        public static final String excel2003 = "application/vnd.ms-excel";

        public static final String pdf = "application/pdf";

        public static final String ppt = "application/vnd.ms-powerpoint";
        public static final String pptx = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

        public static final String doc="application/msword";
        public static final String docx="application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        //文本类型
        public static final String txt="text/plain";
        //图片 .png/.jpg/.gif
        public static final String image="image/*";
        //.avi/ .mpg/ .mpeg/ .mp4
        public static final String video="video/*";
        //音频	.mp3/ .wav/
        public static final String audio="audio/*";

        static Map<String, String> map = MapUtil.builder(new HashMap<String, String>())
                .put("xls", excel2003)
                .put("xlsx", excel2010)
                .put("pdf", pdf)
                .put("ppt", ppt)
                .put("pptx", pptx)
                .put("doc", doc)
                .put("docx", docx)
                .put("txt", txt)

                .put("jpg", image)
                .put("jpeg", image)
                .put("png", image)
                .put("gif", image)

                .put("avi", video)
                .put("mp4", video)
                .put("mpeg", video)
                .put("mpg", video)

                .put("mp3", audio)
                .put("wav", audio)

                .build();

        /**
         * 根据文件常见后缀获取文件ContentType
         * @param suffix
         * @return
         */
        public static String get(String suffix){
            if (StringUtils.isBlank(suffix)){
                return common;
            }
            suffix = suffix.toLowerCase();
            return map.getOrDefault(suffix,common);
        }

    }

    private static final String Content_Disposition = "Content-Disposition";
    private static final String attachmentStr = "attachment;filename=";
//    /**
//     * 默认二进制文件下载
//     * @param fileName
//     * @param fileByte
//     */
//    public static void down(String fileName, byte[] fileByte) {
//        down(fileName,fileByte,CommonContentType.common);
//    }
    /**
     * 文件下载 自动判断文件后缀
     * @param fileName
     * @param fileByte
     */
    public static void down(String fileName, byte[] fileByte) {
        String suffix = FileNameUtil.getSuffix(fileName);
        down(fileName,fileByte,CommonContentType.get(suffix));
    }
    public static void down(String fileName, byte[] fileByte, String contentType) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        response.reset();
        try (
                BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream());
        ) {
            response.setContentType(contentType);
            response.addHeader(Content_Disposition, attachmentStr + URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20"));
            os.write(fileByte);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
