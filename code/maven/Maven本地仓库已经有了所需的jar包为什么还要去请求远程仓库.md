问题
IDEA 中的maven 项目，一个jar包一直导入不进来，reimport 无效。
从另一仓库把这个jar包拷贝到当前仓库，还是无效。
mvn clean install -e U 发现加载这个jar包时直接访问远程仓库，都没有从本地查找一下

解决办法

最终我也没找到问题的原因所在，但是手动install一次就可以读取本地了，而不是远程。

把本地仓库的jar 包目录删除掉，使用mvn install 命令，手动install 进仓库。

mvn install:install-file -Dfile=xxx.jar -DgroupId=aaa -DartifactId=bbb -Dversion=1.0.0 -Dpackaging=jar


mvn install:install-file -Dfile=ibmxml-1.0.0.jar -DgroupId=com.ibm -DartifactId=ibmxml -Dversion=1.0.0 -Dpackaging=jar


mvn install:install-file -Dfile=/src/main/resources/lib/kbase-java.jar-DgroupId=cnki.kbase -DartifactId=kbase-java -Dversion=1.1.0 -Dpackaging=jar

mvn install:install-file -Dfile=/src/main/resources/lib/cnki-kbase-java-1.0-SNAPSHOT.jar -DgroupId=cnki.kbase -DartifactId=cnki-kbase-java -Dversion=cnki-kbase-java -Dpackaging=jar

mvn install:install-file -Dfile=E:\zhiwangBase\NanWang\newPro\commons\commons-cypher\src\main\resources\lib\csp-cypher-module-1.0-SNAPSHOT.jar -DgroupId=csp.cypher -DartifactId=bbb -Dversion=1.0.0 -Dpackaging=jar



mvn install:install-file -Dfile=/src/main/resources/lib/cnki-kbase-java-1.0-SNAPSHOT.jar -DgroupId=commons-io -DartifactId=commons-io -Dversion=2.2 -Dpackaging=jar


mvn install:install-file -Dfile=commons-io-2.2.jar -DgroupId=org.apache.commons -DartifactId=commons-io -Dversion=2.2 -Dpackaging=jar

mvn install:install-file -Dfile=xxx.jar -DgroupId=net.cnki.common -DartifactId=cnki-common -Dversion=1.0-SNAPSHOT -Dpackaging=jar
