## 引入依赖

```xml

<!-- https://mvnrepository.com/artifact/com.googlecode.juniversalchardet/juniversalchardet -->
<dependency>
    <groupId>com.googlecode.juniversalchardet</groupId>
    <artifactId>juniversalchardet</artifactId>
    <version>1.0.3</version>
</dependency>

<!--        //这个也是同一个依赖 未尝试-->
<dependency>
<groupId>com.github.albfernandez</groupId>
<artifactId>juniversalchardet</artifactId>
<version>2.4.0</version>
</dependency>


```

## 代码

```java
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description 字符串编码工具类
 * @date 2025/1/22 15:10
 */
@Slf4j
public class StrCharsetUtils {
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    /**
     * 任意编码格式字符串转为utf8格式编码字符串
     * @param bytes
     * @return
     */
    public static String toUtf8Str(byte[] bytes){
        String charsetName = detect(bytes);
        Charset charset;
        try {
            charset = Charset.forName(charsetName);
        } catch (UnsupportedCharsetException e) {
            // 日志打印
            log.warn("不支持的编码,将使用默认编码UTF_8");
            charset = UTF_8;
        }
        if (UTF_8.name().equals(charsetName)){
            return new String(bytes, UTF_8);
        }

        String str = new String(bytes, charset);
        return new String(str.getBytes(UTF_8),UTF_8);
    }

    /**
     * 获取字符串真实编码
     * @param content
     * @return
     */
    public static String detect(byte[] content) {
        UniversalDetector detector = new UniversalDetector(null);
        //开始给一部分数据，让学习一下啊，官方建议是1000个byte左右（当然这1000个byte你得包含中文之类的）
        detector.handleData(content, 0, 5000);
        //识别结束必须调用这个方法
        detector.dataEnd();
        //神奇的时刻就在这个方法了，返回字符集编码。
        return detector.getDetectedCharset();
    }
}

```
