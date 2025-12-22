#!/bin/bash
# 一键迁移本地Maven仓库到私服
# 配置参数：私服地址和认证ID
NEXUS_URL="http://10.31.65.111:8081/repository/maven-releases/"
REPO_ID="cnki-nexus"  # 必须匹配settings.xml配置
LOCAL_REPO=/f/tmp/rep
LOG_FILE="maven-upload.log"
 
# 创建日志文件
echo "===== 开始上传: $(date) =====" > "$LOG_FILE"
 
# 智能遍历所有有效Jar包
find "$LOCAL_REPO" -type f -name "*.jar" \
  ! -name "*maven-metadata*" \
  ! -name "*SNAPSHOT*" \
  ! -name "*-javadoc*" \
  ! -name "*-sources*" | while read -r JAR_FILE; do
  
  # 计算相对路径
  REL_PATH="${JAR_FILE#$LOCAL_REPO/}"
  
  # 解析GAV坐标
  DIR_PATH="$(dirname "$REL_PATH")"
  VERSION="$(basename "$DIR_PATH")"
  ARTIFACT_ID="$(basename "$(dirname "$DIR_PATH")")"
  GROUP_ID="$(dirname "$(dirname "$DIR_PATH")" | tr '/' '.')"
  
  # 定位POM文件
  POM_FILE="${JAR_FILE%.jar}.pom"
  echo $POM_FILE
  # 验证并上传
  if [[ -f "$POM_FILE" ]]; then
    echo "🚀 上传中: $GROUP_ID:$ARTIFACT_ID:$VERSION" | tee -a "$LOG_FILE"
    
    mvn -B deploy:deploy-file \
      -Dfile="$JAR_FILE" \
      -DpomFile="$POM_FILE" \
      -DrepositoryId="$REPO_ID" \
      -Durl="$NEXUS_URL" \
      -DgeneratePom=false \
      -Dpackaging=jar >> "$LOG_FILE" 2>&1
      
    echo "✅ 完成: $REL_PATH" >> "$LOG_FILE"
  else
    echo "⚠️  跳过: 缺失POM文件 - $REL_PATH" | tee -a "$LOG_FILE"
  fi
done
 
echo "===== 上传完成! 详见日志: $LOG_FILE ====="
AI构建项目
bash
