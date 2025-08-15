-  向量化数据库

```shell
docker run -p 6333:6333 \
    -v /data/docker/qdrant/data:/qdrant/storage \
    docker.io/qdrant/qdrant
    
```

> 批量重启java的docker容器
```shell
docker ps |grep java  | awk '{print $1}' |  xargs -r docker restart
```
