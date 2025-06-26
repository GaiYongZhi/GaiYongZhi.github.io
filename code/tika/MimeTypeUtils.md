```xml
        <!-- tika 文章解析 -->
        <dependency>
            <groupId>org.apache.tika</groupId>
            <artifactId>tika-core</artifactId>
            <version>3.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.tika</groupId>
            <artifactId>tika-parsers-standard-package</artifactId>
            <version>3.1.0</version>
        </dependency>
```


```java

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
/**
 *
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2025/4/23 10:23
 */

@Slf4j
public class MimeTypeUtils {
    static TikaConfig tika;

    static TikaConfig getTika() {
        try {
            if (tika == null) {
                synchronized (MimeTypeUtils.class) {
                    tika = new TikaConfig();
                }
            }
        } catch (IOException | TikaException e) {
            throw new RuntimeException(e);
        }
        return tika;
    }

    /**
     * 获取文件格式-从文件路径
     * @param path
     * @return
     * @throws TikaException
     * @throws IOException
     */
    public static String mimetype(String path) throws TikaException, IOException {
        File f = new File(path);
        return mimetype(f);
    }

    /***
     * 获取文件格式-从文件
     * @param f
     * @return
     * @throws TikaException
     * @throws IOException
     */
    public static String mimetype(File f) throws TikaException, IOException {
        Metadata metadata = new Metadata();
        MediaType mediaType = getTika()
                .getDetector()
                .detect(TikaInputStream.get(f, metadata), metadata);
        String mimetype = String.valueOf(mediaType);
        System.out.println("File " + f + " is " + mimetype);
        log.info("文件{}的格式为:{}", f.getAbsolutePath(), mimetype);
        return mimetype;
    }

    /**
     * 获取文件格式-从文件流
     * @param is
     * @return
     * @throws TikaException
     * @throws IOException
     */
    public static String mimetype(InputStream is) throws TikaException, IOException {
        Metadata metadata = new Metadata();
        //if you know the file name, it is a good idea to
        //set it in the metadata, e.g.
        //metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, "somefile.pdf");
        String mimetype = String.valueOf(tika.getDetector().detect(
                TikaInputStream.get(is), metadata));
        log.info("Stream文件{}的格式为:{}", is, mimetype);
        return mimetype;
    }

    @SneakyThrows
    public static void main(String[] args) {
        mimetype("E:\\OneDrive\\常用代码\\浏览器快捷键.pdf");
    }
}

```
