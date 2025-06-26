```java
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.system.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2024/9/9 11:12
 */
@Slf4j
@Component("mySpringSystemUtils")
public class AppUtils implements ApplicationContextAware {

    private ApplicationPid appPid;
    private ApplicationHome appHome;
    private ApplicationTemp temp;


    @Getter
    private static ApplicationContext appContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppUtils.appContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        appPid = new ApplicationPid();
        appHome = new ApplicationHome();
        temp = new ApplicationTemp();

    }

    private static AppUtils getThis(){
        return AppUtils.appContext.getBean(AppUtils.class, "mySpringSystemUtils");
    }

    /**
     * 获取程序PID进程
     *
     * @return
     */
    public static String getPid() {
        return getThis().appPid.toString();
    }

    /**
     * 获取程序运行主目录
     * 获取应用程序所在的目录路径。如果应用程序在一个Jar包中运行，则返回Jar包所在的目录路径；
     * 如果应用程序在一个解压缩的目录中运行，则返回该目录的路径。
     * <br>
     *
     *  备注:  System.getProperty("user.dir")的方式获取程序路径不推荐,
     *        因为获得到的结果会是 执行程序时所在目录  不是程序所在目录
     *
     *
     *        结论
     *        从实际情况看，System.getProperty(“user.dir”)返回结果和用户没有关系，和jar包没有关系，和运行jar包的sh脚本也没有关系，
     *        只有和执行运行java命令的路径有关系（无论是直接运行，还是通过sh脚本运行）。所以这里需要注意的就是运行java程序的时候要统一在
     *        某个路径下执行。如果上次在/home下执行，这次在/root下执行，得到的结果也是不一样的。如果涉及到文件的上传和下载，那么这个路径也就
     *         出现了改变。
     *         详细说明 <a href="https://blog.csdn.net/liangcha007/article/details/88526181">SpringBoot项目jar发布获取jar包所在目录路径</a>
     * @return
     */
    public static String getAppHome() {
        return getThis().appHome.getDir().getAbsolutePath();
    }

    /**
     * 获取jar全路径 @example /path/to/xxx.jar   仅在jar运行时可用
     * 获取应用程序所在的Jar包的路径。如果应用程序是在一个Jar包中运行，则返回该Jar包的路径
     * @return
     */
    @Nullable
    public static String getAppJarPath() {
        File source = getThis().appHome.getSource();

        if (Objects.nonNull(source)) {
            return getThis().appHome.getSource().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取临时目录路径 @example /tmp/xxx
     *
     * @return
     */
    public static String getAppTmpPath() {
        return getThis().temp.getDir().getAbsolutePath();
    }

    public static List<String> getBasePackages() {
        return AutoConfigurationPackages.get(appContext);
    }


    /**
     * 获取java版本
     * @return
     */
    public static String getJavaVersion(){
        return JavaVersion.getJavaVersion().toString();
    }


    /**
     * 打印所有系统参数
     */
    public static void printSystemProperty() {
        Properties properties = System.getProperties();
        //遍历所有的属性
        for (String key : properties.stringPropertyNames()) {
            //输出对应的键和值
            log.info("{} : {}", key, properties.getProperty(key));
        }
    }
    /**
     * 获取系统参数,系统属性/环境变量访问  @example  key为java.home
     *
     * 属性名	说明	示例值
     * java.version	Java版本号	11.0.5
     * java.vendor.version	Java供应商版本	18.9
     * java.home	Java安装根目录	/usr/lib/jvm/jdk-11.0.5
     * java.class.version	Java 类文件版本号	55.0
     * os.name	操作系统名	Linux
     * os.arch	操作系统架构	amd64
     * os.version	操作系统版本	5.0.0-37-generic
     * file.separator	文件分隔符	/
     * path.separator	路径分隔符	:
     *
     *  备注:  System.getProperty("user.dir")的方式获取程序路径不推荐,
     *        因为获得到的结果会是 执行程序时所在目录
     *   结论:
     *        从实际情况看，System.getProperty(“user.dir”)返回结果和用户没有关系，和jar包没有关系，和运行jar包的sh脚本也没有关系，
     *        只有和执行运行java命令的路径有关系（无论是直接运行，还是通过sh脚本运行）。所以这里需要注意的就是运行java程序的时候要统一在
     *        某个路径下执行。如果上次在/home下执行，这次在/root下执行，得到的结果也是不一样的。如果涉及到文件的上传和下载，那么这个路径也就
     *         出现了改变。
     *         详细说明 <a href="https://blog.csdn.net/ColdFireMan/article/details/105968160">关于对System.getProperty中user.dir的理解</a>
     * @param key
     * @return
     */
    @Nullable
    public static String getSystemProperty(String key){
        return SystemProperties.get(key);
    }


}

```
