package net.cnki.standard.data.web.util;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.aspose.words.Document;
import com.aspose.words.FontSettings;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description 对于doc docx文件转换为pdf文件工具类
 * @date 2024/3/15 11:01
 * @read linux 无桌面环境下运行时 jvm启动参数需要加java.awt.headless=true 否则会报错
 */

@Slf4j
public class WordsToPDFUtil {
    private static FontSettings fontSettings;
    static {
        //加载一次jvm参数信息, 设置无桌面环境下运行时的参数
        log.info("java.awt.headless  set true");
        System.setProperty("java.awt.headless", "true");
        if (isLinuxOs()){
            log.info("current os is linux,begin load fonts.  default path is current ./Fonts ,  you also can load it  by add vm args : font.home=/path/to/fonts");
            fontSettings = loadFonts();
            if (Objects.nonNull(fontSettings)) {
                log.info("load fonts finish!");
            } else {
                log.info("load fonts failed!");
            }
        }
    }
    private static boolean isLinuxOs() {
        String os = System.getProperty("os.name").toLowerCase();
        log.debug("当前系统版本是:{}", os);
        if (os.startsWith("linux")) {
            return true;
        } else { //其它操作系统
            return false;
        }
    }
    /**
     * doc docx文件转换为pdf文件 输出到指定的目录   生成结果  /path/to/fileName.pdf
     *
     * @param sourceFile 源文件
     * @param toPath     存储路径,不包含文件名  例如 /path/to/
     * @param fileName   文件名称 不包含文件后缀 ,文件后缀固定为pdf
     */
    public static void wordToPdf(String sourceFile, String toPath, String fileName) {
        wordToPdf(new File(sourceFile), toPath, fileName);
    }
    /**
     * doc docx文件转换为pdf文件 输出到指定的目录   生成结果  /path/to/fileName.pdf
     *
     * @param sourceFile 源文件
     * @param targetFile     存储路径,不包含文件名  例如 /path/to/
     */
    @SneakyThrows
    public static void wordToPdf(File sourceFile, File targetFile) {
        FileInputStream is = IoUtil.toStream(sourceFile);

        FileOutputStream os = new FileOutputStream(targetFile);
        wordToPdf(is, os);
        IoUtil.close(is);
        IoUtil.close(os);
    }
    /**
     * doc docx文件转换为pdf文件 输出到指定的目录   生成结果  /path/to/fileName.pdf
     *
     */
    @SneakyThrows
    public static void wordToPdf(InputStream is, OutputStream os) {
        //去水印
        if (!judgeLicense()) {
            log.info("license error");
        }
        Document document = new Document(is);
        if (isLinuxOs()){
            checkFonts();
        }
        if (Objects.nonNull(fontSettings)){
            document.setFontSettings(fontSettings);
        }
        document.save(os, SaveFormat.PDF);
        IoUtil.close(is);
        IoUtil.close(os);
    }
    /**
     * doc docx文件转换为pdf文件 输出到指定的目录   生成结果  /path/to/fileName.pdf
     *
     * @param sourceFile 源文件
     * @param toPath     存储路径,不包含文件名  例如 /path/to/
     * @param fileName   文件名称 不包含文件后缀 ,文件后缀固定为pdf
     */
    @SneakyThrows
    public static void wordToPdf(File sourceFile, String toPath, String fileName) {
        FileInputStream is = IoUtil.toStream(sourceFile);
        //去水印
        if (!judgeLicense()) {
            log.info("license error");
        }
        String resultPath = toPath + fileName + ".pdf";
        FileOutputStream os = new FileOutputStream(resultPath);
            Document document = new Document(is);
            if (isLinuxOs()){
                checkFonts();
            }
            if (Objects.nonNull(fontSettings)){
                document.setFontSettings(fontSettings);
            }
            document.save(os, SaveFormat.PDF);
    }

    /**
     * 校验license
     */
    private static boolean judgeLicense() {
        try {
            License aposeLic = new License();
            aposeLic.setLicense(new ClassPathResource("license.xml").getStream());
            return true;
        } catch (Exception e) {
            log.error("", e);
        }
        return false;
    }
    //字体库加载 默认从工作目录下的Font文件夹下
    private static FontSettings loadFonts() {
        String fontFolder = checkFonts();
        if (Objects.nonNull(fontFolder)){
            log.info("begin load fonts from {}",fontFolder);
            FontSettings fontSettings = FontSettings.getDefaultInstance();
            fontSettings.setFontsFolder(fontFolder, false);
            return fontSettings;
        }
        return null;
    }
    private static String checkFonts() {
        String fontFolder = Paths.get("").toAbsolutePath() +File.separator+"Fonts";
        //查询是否设置了font字体参数
        String fontHome = System.getProperty("font.home");
        if (StrUtil.isNotBlank(fontHome)){
            fontFolder = fontHome;
        }
        //判断对应目录下是否存在字体文件
        if (FileUtil.isDirectory(Paths.get(fontFolder)) && !FileUtil.isDirEmpty(Paths.get(fontFolder))){
            log.info(" fonts home is {}",fontFolder);
            return fontFolder;
        }else {
            log.info("current os is linux,  default path is current ./Fonts ,  you also can load it  by add vm args : font.home=/path/to/fonts");
            log.info("fonts folder is not exist or empty,please check it");
            return null;
        }
    }

}

