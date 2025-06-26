```shell
clean package  -DskipTests -Dmaven.test.skip=true

```

内网遇到问题,可以先编译再打包
```shell
clean compile package  -DskipTests
```
![img.png](img.png)

运行带有额外jar包
java  -Djava.ext.dirs=./lib -jar music-core-1.0-SNAPSHOT.jar

## maven仓库清理无用未成功下载垃圾
```bash
for /r %i in (*.lastUpdated) do del %i
```

## 指定settings配置文件
> mvn install clean -s 制定settings无效

> 根据版本对应使用-s或-settings无效时
    可以使用-gs代替 强制执行
    mvn install clean -gs settings路径


## maven路径操作获取
```java
A.class.getClassLoader().getResource("yml").getPath();
```


> 在Java编码过程中，我们常常希望读取项目内的配置文件，按照Maven的习惯，这些文件一般放在项目的src/main/resources下，读取的时候使用：
Stringpath ="config.properties";InputStreamin =this.class.getResource(path).openStream();Copy to clipboardErrorCopied
使用当前类来获得资源其实就是使用当前类的类加载器获取资源，最后openStream()方法获取输入流来读取文件流。


## 私服
```shell
mvn deploy:deploy-file \
-DgroupId=csp.cypher  \
-DartifactId=csp-cypher-module \
-Dversion=1.0-SNAPSHOT \
-Dpackaging=jar -Dfile=csp-cypher-module-1.0-SNAPSHOT.jar \
-Durl=http://admin:admin123@http://192.168.3.102:8079/repository/maven-snapshots/
```

Nexus3.x批量导入本地库(Windows版)
```shell
#!/bin/bash
# copy and run this script to the root of the repository directory containing files
# this script attempts to exclude uploading itself explicitly so the script name is important
# Get command line params
while getopts ":r:u:p:" opt; do
	case $opt in
		r) REPO_URL="$OPTARG"
		;;
		u) USERNAME="$OPTARG"
		;;
		p) PASSWORD="$OPTARG"
		;;
	esac
done
find . -type f -not -path './mavenimport\.sh*' -not -path '*/\.*' -not -path '*/\^archetype\-catalog\.xml*' -not -path '*/\^maven\-metadata\-local*\.xml' -not -path '*/\^maven\-metadata\-deployment*\.xml' | sed "s|^\./||" | xargs -I '{}' curl -u "$USERNAME:$PASSWORD" -X PUT -v -T {} ${REPO_URL}/{} ;

```
[nexus3批量导入本地仓库windows.html](code/maven/nexus3%E6%89%B9%E9%87%8F%E5%AF%BC%E5%85%A5%E6%9C%AC%E5%9C%B0%E4%BB%93%E5%BA%93windows.html)
