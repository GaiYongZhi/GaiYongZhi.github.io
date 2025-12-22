#!/bin/bash
# 一键迁移本地Maven仓库到私服


# 要修改的地方
# 配置参数：私服地址和认证ID
NEXUS_URL="http://10.31.65.111:8081/repository/maven-releases/"
REPO_ID="cnki-nexus"  # 必须匹配settings.xml配置
LOCAL_REPO=/f/tmp/rep  # 仓库路径
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

  # 提取JAR文件名（不含路径和扩展名）
  JAR_BASENAME=$(basename "$JAR_FILE" .jar)

  # 检测是否有classifier
  # 模式1: artifactId-version-classifier.jar
  # 模式2: artifactId-version.jar
  BASE_PATTERN="${ARTIFACT_ID}-${VERSION}"

  if [[ "$JAR_BASENAME" == "$BASE_PATTERN" ]]; then
    # 没有classifier的情况
    CLASSIFIER=""
    POM_FILE="${JAR_FILE%.jar}.pom"
    echo "📦 标准构件: $GROUP_ID:$ARTIFACT_ID:$VERSION" | tee -a "$LOG_FILE"
  else
    # 有classifier的情况，需要从文件名中提取classifier
    # 移除基础模式部分和末尾的连字符
    CLASSIFIER="${JAR_BASENAME#$BASE_PATTERN-}"

    # 验证classifier不为空且是有效的（不包含特殊字符）
    if [[ -n "$CLASSIFIER" && "$CLASSIFIER" =~ ^[a-zA-Z0-9._-]+$ ]]; then
      # POM文件名为artifactId-version.pom，而不是artifactId-version-classifier.pom
      POM_FILE="${JAR_FILE%/*}/${ARTIFACT_ID}-${VERSION}.pom"
      echo "🎯 带分类器构件: $GROUP_ID:$ARTIFACT_ID:$VERSION:$CLASSIFIER" | tee -a "$LOG_FILE"
    else
      echo "⚠️  异常文件名: $REL_PATH (无法解析classifier)" | tee -a "$LOG_FILE"
      continue
    fi
  fi

  echo "📄 尝试POM文件: $(basename "$POM_FILE")" | tee -a "$LOG_FILE"

  # 验证并上传
  if [[ -f "$POM_FILE" ]]; then
    echo "🚀 上传中: $GROUP_ID:$ARTIFACT_ID:$VERSION${CLASSIFIER:+:$CLASSIFIER}" | tee -a "$LOG_FILE"

    # 构建上传命令
    DEPLOY_CMD="mvn -B deploy:deploy-file \
      -Dfile=\"$JAR_FILE\" \
      -DpomFile=\"$POM_FILE\" \
      -DrepositoryId=\"$REPO_ID\" \
      -Durl=\"$NEXUS_URL\" \
      -DgeneratePom=false \
      -Dpackaging=jar"

    # 如果有classifier，则添加classifier参数
    if [[ -n "$CLASSIFIER" ]]; then
      DEPLOY_CMD="$DEPLOY_CMD -Dclassifier=\"$CLASSIFIER\""
    fi

    # 执行上传命令
    echo "执行命令: $DEPLOY_CMD" >> "$LOG_FILE"
    eval $DEPLOY_CMD >> "$LOG_FILE" 2>&1

    if [[ $? -eq 0 ]]; then
      echo "✅ 完成: $REL_PATH" | tee -a "$LOG_FILE"
    else
      echo "❌ 失败: $REL_PATH" | tee -a "$LOG_FILE"
    fi
  else
    # 尝试寻找alternative POM文件（有时POM文件名可能不同）
    echo "⚠️  缺失POM文件: $(basename "$POM_FILE")" | tee -a "$LOG_FILE"

    # 尝试查找目录下的其他POM文件
    ALTERNATIVE_POM=$(find "$(dirname "$JAR_FILE")" -name "*.pom" -type f | head -1)
    if [[ -n "$ALTERNATIVE_POM" ]]; then
      echo "🔄 使用替代POM: $(basename "$ALTERNATIVE_POM")" | tee -a "$LOG_FILE"
      POM_FILE="$ALTERNATIVE_POM"

      # 重新构建上传命令
      DEPLOY_CMD="mvn -B deploy:deploy-file \
        -Dfile=\"$JAR_FILE\" \
        -DpomFile=\"$POM_FILE\" \
        -DrepositoryId=\"$REPO_ID\" \
        -Durl=\"$NEXUS_URL\" \
        -DgeneratePom=false \
        -Dpackaging=jar"

      if [[ -n "$CLASSIFIER" ]]; then
        DEPLOY_CMD="$DEPLOY_CMD -Dclassifier=\"$CLASSIFIER\""
      fi

      echo "执行命令: $DEPLOY_CMD" >> "$LOG_FILE"
      eval $DEPLOY_CMD >> "$LOG_FILE" 2>&1

      if [[ $? -eq 0 ]]; then
        echo "✅ 完成(使用替代POM): $REL_PATH" | tee -a "$LOG_FILE"
      else
        echo "❌ 失败(使用替代POM): $REL_PATH" | tee -a "$LOG_FILE"
      fi
    else
      echo "⛔ 跳过: 完全缺失POM文件 - $REL_PATH" | tee -a "$LOG_FILE"
    fi
  fi

  echo "---" >> "$LOG_FILE"
done

echo "===== 上传完成! 详见日志: $LOG_FILE ====="
