#!/bin/bash

#$1     pkg
get_all_depends()
{
        apt-cache depends --no-pre-depends --no-suggests --no-recommends \
                --no-conflicts --no-breaks --no-enhances\
                --no-replaces --recurse $1 | awk '{print $2}'| tr -d '<>' | sort --unique
}


## 遍历命令行参数，参数应为包名。
for pkg in $*
do
        all_depends=$(get_all_depends $pkg)
        
        declare -a pkg_array
        for depend in $all_depends
        do
#			echo -n "$pkg/lib/$depend.deb "
			pkg_array+=("$pkg/lib/$depend.deb ")
		done
				
		echo -e "倒序输出依赖"
        # 倒序遍历数组并输出，不换行
        length=${#pkg_array[@]}
		for ((i=length  - 1; i>=0; i--))
		do
			echo -n "${pkg_array[$i]}"
		done
		echo  ""
		echo -e "倒序输出完成"
		
        mkdir $pkg
        mkdir $pkg/lib
        chmod -R 755 $pkg
        cd ./$pkg
        sudo apt-get download $pkg     
        cd lib
        
        echo -e "所有依赖共计"$(echo $all_depends | wc -w)"个"
        echo $all_depends
        i=0
        for depend in $all_depends
        do
                i=$((i+1))
                echo -e "\033[1;32m正在下载第$i个依赖："$depend "\033[0m"
                sudo apt-get download $depend
        done
        cd ../../
done

