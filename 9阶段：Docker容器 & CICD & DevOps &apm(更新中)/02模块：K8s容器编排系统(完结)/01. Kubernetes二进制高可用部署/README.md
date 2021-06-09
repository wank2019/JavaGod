# K8s二进制高可用部署

## 说在前面
>**本章相关代码及笔记地址：**[**飞机票🚀**](https://github.com/EayonLee/JavaGod/tree/main/9%E9%98%B6%E6%AE%B5%EF%BC%9ADocker%E5%AE%B9%E5%99%A8%20%26%20CICD%20%26%20DevOps%20%26apm/02%E6%A8%A1%E5%9D%97%EF%BC%9AK8s%E5%AE%B9%E5%99%A8%E7%BC%96%E6%8E%92%E7%B3%BB%E7%BB%9F/01.%20Kubernetes%E4%BA%8C%E8%BF%9B%E5%88%B6%E9%AB%98%E5%8F%AF%E7%94%A8%E9%83%A8%E7%BD%B2)
>
>🌍Github：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://github.com/EayonLee/JavaGod)<br>
>🪐CSDN：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://blog.csdn.net/qq_20492277/article/details/114269863)


# 前言
本文所有涉及软件包等文件均在如下网盘，当然，你也可以根据文中给出链接自行逐个下载
```text
链接：https://pan.baidu.com/s/1c4OjB9HsSb9hRupFHsgCyw 
提取码：6awp 
```

# 目录
- [前言](#前言)
- [目录](#目录)
- [一. 部署准备](#一-部署准备)
  - [1.1 部署Kubernetes集群的两种方式](#11-部署kubernetes集群的两种方式)
  - [1.2 版本](#12-版本)
  - [1.3 高可用集群节点规划](#13-高可用集群节点规划)
  - [1.4 系统初始化（包含Docker安装）](#14-系统初始化包含docker安装)
- [二. Etcd集群部署](#二-etcd集群部署)
  - [2.1 介绍 & 节点规划](#21-介绍--节点规划)
  - [2.2 准备cfssl证书生成工具](#22-准备cfssl证书生成工具)
  - [2.3 为Etcd生成证书](#23-为etcd生成证书)
    - [2.3.1 生成自签证书颁发机构（CA）](#231-生成自签证书颁发机构ca)
    - [2.3.2 使用自签CA为Etcd签发证书](#232-使用自签ca为etcd签发证书)
  - [2.4 下载Etcd二进制文件](#24-下载etcd二进制文件)
  - [2.5 部署Etcd集群](#25-部署etcd集群)
    - [2.5.1 上传、解压、创建Etcd工作目录](#251-上传解压创建etcd工作目录)
    - [2.5.2 创建Etcd配置文件](#252-创建etcd配置文件)
    - [2.5.3 systemd管理Etcd](#253-systemd管理etcd)
    - [2.5.4 拷贝Etcd证书到其工作目录](#254-拷贝etcd证书到其工作目录)
    - [2.5.5 将Master1的Etcd工作目录发送到Node1、Node2节点](#255-将master1的etcd工作目录发送到node1node2节点)
    - [2.5.6 修改Node1、Node2节点的Etcd配置文件](#256-修改node1node2节点的etcd配置文件)
    - [2.5.7 启动Etcd集群 & 查看状态 & 查询日志](#257-启动etcd集群--查看状态--查询日志)
- [三. 部署Master节点](#三-部署master节点)
  - [3.1 生成kube-apisever证书](#31-生成kube-apisever证书)
    - [3.1.1 生成自签证书颁发机构（CA）](#311-生成自签证书颁发机构ca)
    - [3.1.2 使用自签CA为kube-apiserver签发证书](#312-使用自签ca为kube-apiserver签发证书)
  - [3.2 部署kube-apisever](#32-部署kube-apisever)
    - [3.2.1 从Github下载二进制文件](#321-从github下载二进制文件)
    - [3.2.2 上传、解压、创建工作目录](#322-上传解压创建工作目录)
    - [3.2.3 创建kube-apiserver配置文件](#323-创建kube-apiserver配置文件)
    - [3.2.4 将生成的apiserver证书拷贝到其工作目录下](#324-将生成的apiserver证书拷贝到其工作目录下)
    - [3.2.5 启动TLS Bootstrapping机制](#325-启动tls-bootstrapping机制)
    - [3.2.6 systemd管理kube-apiserver](#326-systemd管理kube-apiserver)
    - [3.2.7 启动](#327-启动)
  - [3.3 部署kube-controller-manager](#33-部署kube-controller-manager)
    - [3.3.1 创建controller-manager配置文件](#331-创建controller-manager配置文件)
    - [3.3.2 生成kube-controller-manager证书](#332-生成kube-controller-manager证书)
    - [3.3.3 生成kubeconfig文件（以下是shell命令，直接在终端执行）](#333-生成kubeconfig文件以下是shell命令直接在终端执行)
    - [3.3.4 systemd管理kube-controller-manager](#334-systemd管理kube-controller-manager)
    - [3.3.5 启动](#335-启动)
  - [3.4 部署kube-scheduler](#34-部署kube-scheduler)
    - [3.4.1 创建kube-scheduler配置文件](#341-创建kube-scheduler配置文件)
    - [3.4.2 生成kubeconfig文件](#342-生成kubeconfig文件)
    - [3.4.3 systemd管理kube-scheduler](#343-systemd管理kube-scheduler)
    - [3.4.3 启动](#343-启动)
  - [3.5 查看当前K8s集群组件状态](#35-查看当前k8s集群组件状态)
    - [3.5.1 生成kubectl连接集群的证书](#351-生成kubectl连接集群的证书)
    - [3.5.2 生成kubeconfig文件](#352-生成kubeconfig文件)
    - [3.5.3 通过Kubectl工具查看当前集群组件状态](#353-通过kubectl工具查看当前集群组件状态)
    - [3.5.4 授权kubelet-bootstrap用户允许请求证书](#354-授权kubelet-bootstrap用户允许请求证书)
- [四. 部署Work Node节点](#四-部署work-node节点)
  - [4.1 创建工作目录并拷贝二进制文件](#41-创建工作目录并拷贝二进制文件)
  - [4.2 部署kubelet](#42-部署kubelet)
    - [4.2.1 创建kubelete配置文件](#421-创建kubelete配置文件)
    - [4.2.2 创建配置文件的参数文件](#422-创建配置文件的参数文件)
    - [4.2.3 生成kubelet初次加入集群引导.kubeconfig文件](#423-生成kubelet初次加入集群引导kubeconfig文件)
    - [4.2.4 system管理kubelet](#424-system管理kubelet)
    - [4.2.5 启动kubelet](#425-启动kubelet)
    - [4.2.6 批准kubelet证书的申请并加入集群](#426-批准kubelet证书的申请并加入集群)
  - [4.3 部署kube-proxy](#43-部署kube-proxy)
    - [4.3.1 创建kube-proxy配置文件](#431-创建kube-proxy配置文件)
    - [4.3.2 创建配置参数文件](#432-创建配置参数文件)
    - [4.3.3 生成kube-proxy.kubeconfig文件](#433-生成kube-proxykubeconfig文件)
    - [4.3.4 system管理kube-proxy](#434-system管理kube-proxy)
    - [4.3.5 启动](#435-启动)
- [五. 部署CNI网络（Calico）](#五-部署cni网络calico)
  - [5.1 下载Calico资源清单](#51-下载calico资源清单)
  - [5.2 应用并查看](#52-应用并查看)
  - [5.3 授权apiserver访问kubelet](#53-授权apiserver访问kubelet)
- [六. 部署集群内部DNS（CoreDNS）](#六-部署集群内部dnscoredns)
  - [6.1 创建coredns资源清单（不用改动）](#61-创建coredns资源清单不用改动)
  - [6.2 应用及测试](#62-应用及测试)
- [七. 扩容Work Node节点](#七-扩容work-node节点)
  - [7.1 拷贝已部署好的Node相关文件到扩容节点](#71-拷贝已部署好的node相关文件到扩容节点)
  - [7.2 删除扩容节点上的kubelet证书及kubeconfig文件](#72-删除扩容节点上的kubelet证书及kubeconfig文件)
  - [7.3 修改扩容节点配置文件](#73-修改扩容节点配置文件)
  - [7.4 启动扩容节点kube-proxy](#74-启动扩容节点kube-proxy)
  - [7.5 在Master节点批准扩容节点的kubelet证书申请](#75-在master节点批准扩容节点的kubelet证书申请)
  - [7.6 查看扩容Node状态](#76-查看扩容node状态)
- [八. 扩容Master（实现高可用架构）](#八-扩容master实现高可用架构)
  - [8.1 部署Master2扩容节点](#81-部署master2扩容节点)
    - [8.1.1 创建Etcd证书目录](#811-创建etcd证书目录)
    - [8.1.2 拷贝已部署好的Master节点相关文件到扩容节点](#812-拷贝已部署好的master节点相关文件到扩容节点)
    - [8..1.3 删除证书文件](#813-删除证书文件)
    - [8.1.4 修改配置文件IP和主机名](#814-修改配置文件ip和主机名)
    - [8.1.5 启动](#815-启动)
    - [8.1.6 查看集群状态](#816-查看集群状态)
  - [8.2 部署Nginx + Keepalived高可用负载均衡器](#82-部署nginx--keepalived高可用负载均衡器)
    - [8.2.1 安装软件包](#821-安装软件包)
    - [8.2.2 Nginx配置文件](#822-nginx配置文件)
    - [8.2.3 Keepalived配置文件（LB主节点）](#823-keepalived配置文件lb主节点)
    - [8.2.4 Keepalived配置文件（LB备节点）](#824-keepalived配置文件lb备节点)
    - [8.2.5 启动](#825-启动)
    - [8.2.6 查看keepalived工作状态](#826-查看keepalived工作状态)
    - [8.2.7 Nginx+Keepalived高可用测试](#827-nginxkeepalived高可用测试)
    - [8.2.8 通过VIP访问K8s集群测试](#828-通过vip访问k8s集群测试)
  - [8.3 修改所有Node节点连接VIP（重要）](#83-修改所有node节点连接vip重要)

# 一. 部署准备

## 1.1 部署Kubernetes集群的两种方式

目前生产部署Kubernetes集群主要有两种方式：

**Kubeadm**

Kubeadm是K8S官方的部署工具，提供``kubeadm init``和``kubeadm join``，用户快速构建Kubernetes集群及加入Node节点

[Kubeadm文档]: https://kubernetes.io/docs/reference/setup-tools/kubeadm/

**二进制部署**

从github下载发行版的二进制包，手动部署每个组件，组成Kubernetes集群。

Kubeadm降低部署门槛，但屏蔽了很多细节，遇到问题很难排查。

如果想更容易可控，推荐使用二进制包部署Kubernetes集群，虽然手动部署麻烦点，期间可以学习很多工作原理，也利于后期维护。

本章也是通过二进制包来进行部署简介的。

**其他部署方式**

* monikube（通常大家都是使用minikube对K8S发行的新版本进行测试使用的）
* kubespray（google官方提供的自动化部署工具）
* rancher、kubesphere（全自动部署工具）

## 1.2 版本

| 软件       | 版本     |
| ---------- | -------- |
| Docker     | 20.10.2  |
| Kubernetes | 1.20.4   |
| Etcd       | 3.4.9    |
| Nginx      | 1.16.0-1 |



## 1.3 高可用集群节点规划

| 主机名        | 角色         | IP                                   | 包含组件                                                     |
| ------------- | ------------ | ------------------------------------ | ------------------------------------------------------------ |
| yf-k8s-160005 | Master1      | 10.0.160.5                           | kube-apiserver<br />kube-controller-manager<br />kube-scheduler<br />etcd<br />docker |
| yf-k8s-160008 | Master2      | 10.0.160.8                           | kube-apiserver<br />kube-controller-manager<br />kube-scheduler<br />docker |
| yf-k8s-160006 | Work Node1   | 10.0.160.6                           | kubelet<br />kube-proxy<br />docker<br />etcd                |
| yf-k8s-160007 | Work Node2   | 10.0.160.7                           | kubelet<br />kube-proxy<br />docker<br />etcd                |
| yf-k8s-160111 | LB（Master） | 10.0.160.111<br />10.0.160.60（VIP） | Nginx<br />keepalived                                        |
| yf-k8s-160112 | LB（BackUp） | 10.0.160.112<br />10.0.160.60（VIP） | Nginx<br />keepalived                                        |

![image-20210429105431413](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210429105431413.png)

> 由于机器数量有限，这里我们复用了Master1、Node1、Node2来部署ETCD集群来存储K8S相关信息，当然你也可以不将ETCD集群部署在K8S集群节点当中，采用外挂的方式。
>
> 使用了独立的两台机器部署Nginx为Master节点做负载均衡，并使用Keepalived生成VIP来对Nginx主备节点进行负载均衡的高可用处理



## 1.4 系统初始化（包含Docker安装）

对于全新的机器我们需要对系统进行一系列的初始化配置操作

**关闭dash**

```shell
#执行
dpkg-reconfigure dash
#选择No
```

**更改用户shell**

```shell
#使用root权限将所自己创建的用户的 /bin/sh 改为 /bin/bash
vim /etc/passwd

#修改前
web:x:1001:1001::/home/web:/bin/sh
#修改后
web:x:1001:1001::/home/web:/bin/bash
```

**bashrc配置（备注：web为所创建的用户名，执行时需要修改成对应的）**

```shell
cp ~/.bashrc /home/web/
chown web:web /home/web/.bashrc
source /home/web/.bashrc
```

**更改软件源为阿里云**

```shell
cp /etc/apt/sources.list /etc/apt/sources.list_bak
#查看/etc/apt/sources.list中的URL是archive.ubuntu还是cn.archive.ubuntu 
vim /etc/apt/sources.list
#archive.ubuntu执行命令如下
sudo sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list
#cn.archive.ubuntu执行命令如下
sudo sed -i 's/cn.archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list

#更新
apt-get update -y && apt-get upgrade -y
```

**更新时区**

```shell
#执行
tzselect
#依次选择 4 9 1 1(yes)
#然后执行
cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
```

**设置hostname**

```shell
hostnamectl set-hostname yf-k8s-160005
```

**设置HOSTS**

```shell
vim /etc/hosts
10.0.160.5 yf-k8s-160005
10.0.160.6 yf-k8s-160006
10.0.160.7 yf-k8s-160007
```

**配置DNS**

```shell
systemctl stop systemd-resolved
systemctl disable systemd-resolved

cat>/etc/resolv.conf<<EOF
nameserver 10.0.22.14
nameserver 10.0.12.8
nameserver 10.0.34.14
nameserver 114.114.114.114
EOF
```

**安装必要软件（含Docker）**

```shell
apt install -y lrzsz iotop bzip2 python htop iftop dos2unix supervisor lsof autojump openjdk-8-jdk python-pip  libjsoncpp-dev jq unzip python-pip docker.io
```

**Docker配置**

```shell
useradd -m web
passwd web
cmbjxccwtn19
cmbjxccwtn19
usermod -aG docker web
mkdir -p /data/docker/data
cat>/etc/docker/daemon.json<<EOF
{
        "registry-mirrors":[
                "https://registry.docker-cn.com/",
                "http://hub-mirror.c.163.com/"
        ],
        "log-driver":"json-file",
        "log-opts": {
                "max-size":"1024m",
                "max-file":"7"
        },
        "data-root": "/data/docker/data"
}
EOF
systemctl daemon-reload
systemctl restart docker
systemctl enable docker
```

**基本文件权限设置**

```shell
chmod 1777 /data
chmod 1777 /ssd
chmod 1777 /usr/local/lib/python*/dist-packages
mkdir -p /data/apollo/config
chmod 1777 /data/apollo/config
mkdir -p /opt/settings/
echo 'idc=yf' > /opt/settings/server.properties
```

**limits设置**

```shell
vim /etc/security/limits.conf
#添加如下内容
* soft nofile 1024000
* hard nofile 1024000
# 有时候 * 会没用，需要指定用户如：
root soft nofile 1024000
root hard nofile 1024000
```

**docker其他**

```shell
#docker仓库的hosts
echo '10.0.193.39 docker-hub.kuwo.cn' >> /etc/hosts

#设置定时登录
sudo select-editor #选择编辑器  选择2
crontab -e
#添加如下内容
0 */1 * * * /usr/bin/docker login -u deployment -p deploy@kuwo docker-hub.kuwo.cn >> /tmp/dockerLogin.log 2>&1
#查看定时任务
crontab -l

#登录docker仓库
docker login -u deployment -p deploy@kuwo docker-hub.kuwo.cn
```

**关闭幽灵补丁**

```shell
vim /etc/default/grub

#在GRUB_CMDLINE_LINUX变量中添加
noibrs noibpb nopti nospectre_v2 nospectre_v1 l1tf=off nospec_store_bypass_disable no_stf_barrier mds=off tsx=on tsx_async_abort=off mitigations=off

# 更新配置文件 开机启动 配置文件(只针对ubuntu)
grub-mkconfig
grub-mkconfig -o /boot/grub/grub.cfg
```



**以下是K8s必须的配置**

**关闭Swap**

```shell
# 如果开启了 swap 分区，kubelet 会启动失败(可以通过将参数 --fail-swap-on 设置为false 来忽略 swap on)，故需要在每台机器上关闭 swap 分区：
sudo swapoff -a
#为了防止开机自动挂载 swap 分区，可以注释 /etc/fstab 中相应的条目：
sudo sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab

#测试   可以看到最后一行是0 0 0，则表示当前没有开启。
root@yf-k8s-160005:/etc/selinux# sudo free -m
              total        used        free      shared  buff/cache   available
Mem:         128555        1298      124561           6        2695      126280
Swap:             0           0           0
```

**将桥接的Ipv4流量传递到iptables的链**

```shell
cat > /etc/sysctl.d/k8s.conf << EOF
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF

#生效
sysctl --system
```



# 二. Etcd集群部署

## 2.1 介绍 & 节点规划

Etcd是一个分布式键值存储数据库，K8s官方默认使用的是Etcd来进行数据存储的，所以构建K8s集群第一步就是准备Etcd数据库。

为了预防Etcd单点故障导致K8s集群崩溃的问题，前面``1.3 高可用集群节点规划``那个章节已经提到使用3台节点组建集群，可容忍1台机器故障。

并且为了节省机器，Etcd的集群部署我复用了K8s集群中的节点Master1、Node1、Node2，当然也可以独立于K8s集群之外去部署，只要apiServer能够连接到即可。

| 节点名称（非机器主机名） | IP         | 所在机器主机名 |
| ------------------------ | ---------- | -------------- |
| etcd-1                   | 10.0.160.5 | yf-k8s-160005  |
| etcd-2                   | 10.0.160.6 | yf-k8s-160006  |
| etcd-3                   | 10.0.160.7 | yf-k8s-160007  |



## 2.2 准备cfssl证书生成工具

cfssl是一个开源的证书管理工具，使用json文件生成证书，相比openssl更方便使用。
**找任意一台服务器操作，这里我是使用的Master1节点作为签发证书的节点**

```shell
#仅在Master1操作
#下载工具  下载不下来就手动下载上传服务器
wget https://pkg.cfssl.org/R1.2/cfssl_linux-amd64 --no-check-certificate
wget https://pkg.cfssl.org/R1.2/cfssljson_linux-amd64  --no-check-certificate
wget https://pkg.cfssl.org/R1.2/cfssl-certinfo_linux-amd64  --no-check-certificate

#授权
chmod +x cfssl_linux-amd64 cfssljson_linux-amd64 cfssl-certinfo_linux-amd64

#移动
mv cfssl_linux-amd64 /usr/local/bin/cfssl
mv cfssljson_linux-amd64 /usr/local/bin/cfssljson
mv cfssl-certinfo_linux-amd64 /usr/bin/cfssl-certinfo
```



## 2.3 为Etcd生成证书

### 2.3.1 生成自签证书颁发机构（CA）

**创建证书工作目录**

```shell
# 在Master1创建证书工作目录
mkdir -p ~/TLS/{etcd,k8s}
cd ~/TLS/etcd
```

**创建CA文件**

```shell
#在Master1的~/TLS/etcd目录下创建
cat > ca-config.json << EOF
{
  "signing": {
    "default": {
      "expiry": "87600h"
    },
    "profiles": {
      "www": {
         "expiry": "87600h",
         "usages": [
            "signing",
            "key encipherment",
            "server auth",
            "client auth"
        ]
      }
    }
  }
}
EOF
```

```shell
#在Master1的~/TLS/etcd目录下创建
cat > ca-csr.json << EOF
{
    "CN": "etcd CA",
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "L": "Beijing",
            "ST": "Beijing"
        }
    ]
}
EOF
```

> ca-config.json文件中的："expiry": "87600h"代表生成证书有效期为10年。10年即可，别设置太小，到时候证书到期导致K8s机器无法通信而崩溃
>
> kubeadm部署的话默认有效期为1年，到期只能升级或续期，非常麻烦，或者去修改他的源码重新编译从而解决

**生成CA证书**

上面我们创建了自签CA文件后就可以通过cfssl工具根据``ca-config.json  ca-csr.json``两个文构建一个颁发证书的CA机构在``/root/TLS/etcd/``目录下

```shell
#在Master1执行
cfssl gencert -initca ca-csr.json | cfssljson -bare ca -
```

![image-20210420165759295](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210420165759295.png)

> 执行完成之后在当前目录下就会多出两个文件：ca-key.pem（CA数字证书）和ca.pem（CA私钥） 就是通过这两个CA自签机构文件去为Etcd颁发证书的。

### 2.3.2 使用自签CA为Etcd签发证书

**创建Etcd证书申请文件**

```shell
# 在Master1节点操作
cd /root/TLS/etcd
#创建如下Etcd证书申请文件
cat > server-csr.json << EOF
{
    "CN": "etcd",
    "hosts": [
    "10.0.160.5",
    "10.0.160.6",
    "10.0.160.7",
    "10.0.160.8",
    "10.0.160.9",
    "10.0.160.60"
    ],
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "L": "BeiJing",
            "ST": "BeiJing"
        }
    ]
}
EOF
```

> 上述文件hosts字段中IP为所有Etcd节点的集群内部通信IP，一个都不能少！为了方便后期扩容可以多写几个预留的IP。

**为Etcd生成证书**

我们就可以通过cfssl工具根据server-csr.json文件向CA机构去请求证书

```shell
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=www server-csr.json | cfssljson -bare server
```

![image-20210420170131480](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210420170131480.png)



## 2.4 下载Etcd二进制文件

[etcd-v3.4.9-linux-amd64.tar.gz 下载链接](https://github.com/etcd-io/etcd/releases/download/v3.4.9/etcd-v3.4.9-linux-amd64.tar.gz)



## 2.5 部署Etcd集群

``以下在Master1上操作，为简化操作，待会将Master1生成的所有Etcd文件拷贝到Node1和Node2。``

### 2.5.1 上传、解压、创建Etcd工作目录

```shell
# 在Master1操作
rz -be
tar zxvf etcd-v3.4.9-linux-amd64.tar.gz

#工作目录
mkdir /opt/etcd/{bin,cfg,ssl} -p
#移动至工作目录
mv etcd-v3.4.9-linux-amd64/{etcd,etcdctl} /opt/etcd/bin/
```

### 2.5.2 创建Etcd配置文件

```shell
# 在Master1操作
# 注意:这里的主机地址需要根据所在节点IP配置
cat > /opt/etcd/cfg/etcd.conf << EOF
#[Member]
ETCD_NAME="etcd-1"
ETCD_DATA_DIR="/var/lib/etcd/default.etcd"
ETCD_LISTEN_PEER_URLS="https://10.0.160.5:2380"
ETCD_LISTEN_CLIENT_URLS="https://10.0.160.5:2379"
#[Clustering]
ETCD_INITIAL_ADVERTISE_PEER_URLS="https://10.0.160.5:2380"
ETCD_ADVERTISE_CLIENT_URLS="https://10.0.160.5:2379"
ETCD_INITIAL_CLUSTER="etcd-1=https://10.0.160.5:2380,etcd-2=https://10.0.160.6:2380,etcd-3=https://10.0.160.7:2380"
ETCD_INITIAL_CLUSTER_TOKEN="etcd-cluster"
ETCD_INITIAL_CLUSTER_STATE="new"
EOF
```

> **配置解读**
>
> ETCD_NAME：节点名称，集群中唯一``（需要变更）``
> ETCD_DATA_DIR：数据目录
> ETCD_LISTEN_PEER_URLS：ETCD监听的集群IP端口,集群间通讯用``（需要变更，根据所在机器IP修改）``
> ETCD_LISTEN_CLIENT_URLS：客户端访问监听地址``（需要变更，根据所在机器IP修改）``
> ETCD_INITIAL_ADVERTISE_PEER_URLS：集群通告地址``（需要变更，根据所在机器IP修改）``
> ETCD_ADVERTISE_CLIENT_URLS：客户端通告地址``（需要变更，根据所在机器IP修改）``
> ETCD_INITIAL_CLUSTER：集群所有节点的地址``（需要变更）``
> ETCD_INITIAL_CLUSTER_TOKEN：集群token，节点在集群内的安全认证
> ETCD_INITIAL_CLUSTER_STATE：集群状态 如果对于已存在集群加入的节点可以改成exsiting

### 2.5.3 systemd管理Etcd

```shell
# 在Master1操作
cat >  /lib/systemd/system/etcd.service << EOF
[Unit]
Description=Etcd Server
After=network.target
After=network-online.target
Wants=network-online.target
[Service]
Type=notify
EnvironmentFile=/opt/etcd/cfg/etcd.conf
ExecStart=/opt/etcd/bin/etcd \
--cert-file=/opt/etcd/ssl/server.pem \
--key-file=/opt/etcd/ssl/server-key.pem \
--peer-cert-file=/opt/etcd/ssl/server.pem \
--peer-key-file=/opt/etcd/ssl/server-key.pem \
--trusted-ca-file=/opt/etcd/ssl/ca.pem \
--peer-trusted-ca-file=/opt/etcd/ssl/ca.pem \
--logger=zap
Restart=on-failure
LimitNOFILE=65536
[Install]
WantedBy=multi-user.target
EOF
```

> 此配置只需要注意Etcd的证书地址路径是否正确即可。当然我们这里还没有将Etcd的证书拷贝到他的工作目录，下一步会去拷贝

### 2.5.4 拷贝Etcd证书到其工作目录

```shell
# 在Master1操作
cp ~/TLS/etcd/ca*pem ~/TLS/etcd/server*pem /opt/etcd/ssl/
```



### 2.5.5 将Master1的Etcd工作目录发送到Node1、Node2节点

```shell
# 在Master1操作
# 拷贝到Node1
scp -r /opt/etcd root@10.0.160.6:/opt/
scp /usr/lib/systemd/system/etcd.service root@10.0.160.6:/lib/systemd/system/

# 拷贝到Node2
scp -r /opt/etcd root@10.0.160.7:/opt/
scp /usr/lib/systemd/system/etcd.service root@10.0.160.7:/lib/systemd/system/
```

### 2.5.6 修改Node1、Node2节点的Etcd配置文件

```shell
# 修改Node1节点的etcd.conf文件
# 修改ETCD_NAME以及其它配置项里面涉及到的机器ip

#[Member]
ETCD_NAME="etcd-2"
ETCD_DATA_DIR="/var/lib/etcd/default.etcd"
ETCD_LISTEN_PEER_URLS="https://10.0.160.6:2380"
ETCD_LISTEN_CLIENT_URLS="https://10.0.160.6:2379"
#[Clustering]
ETCD_INITIAL_ADVERTISE_PEER_URLS="https://10.0.160.6:2380"
ETCD_ADVERTISE_CLIENT_URLS="https://10.0.160.6:2379"
ETCD_INITIAL_CLUSTER="etcd-1=https://10.0.160.5:2380,etcd-2=https://10.0.160.6:2380,etcd-3=https://10.0.160.7:2380"
ETCD_INITIAL_CLUSTER_TOKEN="etcd-cluster"
ETCD_INITIAL_CLUSTER_STATE="new"
EOF
```

```shell
# 修改Node2节点的etcd.conf文件
# 修改ETCD_NAME以及其它配置项里面涉及到的机器ip

#[Member]
ETCD_NAME="etcd-3"
ETCD_DATA_DIR="/var/lib/etcd/default.etcd"
ETCD_LISTEN_PEER_URLS="https://10.0.160.7:2380"
ETCD_LISTEN_CLIENT_URLS="https://10.0.160.7:2379"
#[Clustering]
ETCD_INITIAL_ADVERTISE_PEER_URLS="https://10.0.160.7:2380"
ETCD_ADVERTISE_CLIENT_URLS="https://10.0.160.7:2379"
ETCD_INITIAL_CLUSTER="etcd-1=https://10.0.160.5:2380,etcd-2=https://10.0.160.6:2380,etcd-3=https://10.0.160.7:2380"
ETCD_INITIAL_CLUSTER_TOKEN="etcd-cluster"
ETCD_INITIAL_CLUSTER_STATE="new"
EOF
```



### 2.5.7 启动Etcd集群 & 查看状态 & 查询日志

**启动**

```shell
#分别在Master1、Node1、Node2节点去启动Etcd
systemctl daemon-reload
systemctl start etcd
systemctl enable etcd

#查看启动状态
systemctl status etcd.service
```

**查看Etcd集群状态**

```shell
#任意Etcd集群中的节点都可以使用如下命令查看
ETCDCTL_API=3 /opt/etcd/bin/etcdctl \
--cacert=/opt/etcd/ssl/ca.pem \
--cert=/opt/etcd/ssl/server.pem \
--key=/opt/etcd/ssl/server-key.pem \
--endpoints="https://10.0.160.5:2379,https://10.0.160.6:2379,https://10.0.160.7:2379" endpoint health
```

![image-20210420173459880](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210420173459880.png)

> 如果输出上面信息，则表明Etcd集群部署成功。如果那个节点有问题可以使用如下命令查看日志：
>
> `` journalctl -u etcd``

**查看Etcd集群节点列表**

```shell
# 查看集群节点列表
ETCDCTL_API=3  /opt/etcd/bin/etcdctl \
--cacert=/opt/etcd/ssl/ca.pem \
--cert=/opt/etcd/ssl/server.pem \
--key=/opt/etcd/ssl/server-key.pem \
--endpoints="https://10.0.160.5:2379,https://10.0.160.6:2379,https://10.0.160.7:2379"   member list \
--write-out=table
```

![image-20210420173553276](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210420173553276.png)

> 如果集群节点列表中某节点出现问题，可以通过如下命令查看具体报错信息：
>
> `` systemctl status etcd.service`` #在报错的节点通过该命令查看该节点etcd状态
>
> ``journalctl -u etcd``或``journalctl -xe`` #在报错的节点通过该命令可以查看日志



# 三. 部署Master节点

**说一说**：

我们前面完成了需多基础部署和配置，接下来进入真正的K8s部署环节。

通过``1.3章节``我们发现是需要部署两个Master节点，即Master1和Master2，但是这里我只部署一个Master1节点。

为什么呢？因为我想先构建一个单Master的K8s集群，然后最后通过扩容Master节点去组建一个高可用的多Master的K8s集群。

这样我们不仅可以学习到如何部署K8s高可用集群，也可以学习到如果对K8s集群节点进行扩容。

**注意**：``先将K8s集群环境中所有节点（包含所有Master和Node）的IP及主机名添加至所有Master节点的HOST文件中``

## 3.1 生成kube-apisever证书

### 3.1.1 生成自签证书颁发机构（CA）

**创建CA文件**

```shell
#注意：在Master1节点操作
cd ~/TLS/k8s/
cat > ca-config.json << EOF
{
  "signing": {
    "default": {
      "expiry": "87600h"
    },
    "profiles": {
      "kubernetes": {
         "expiry": "87600h",
         "usages": [
            "signing",
            "key encipherment",
            "server auth",
            "client auth"
        ]
      }
    }
  }
}
EOF
```

```shell
#注意：在Master1节点操作
cd ~/TLS/k8s/
cat > ca-csr.json << EOF
{
    "CN": "kubernetes",
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "L": "Beijing",
            "ST": "Beijing",
            "O": "k8s",
            "OU": "System"
        }
    ]
}
EOF
```

**生成CA证书**

上面我们创建了自签CA文件后就可以通过cfssl工具根据``ca-config.json  ca-csr.json``两个文构建一个颁发证书的CA机构在``/root/TLS/k8s/``目录下

```shell
#在Master1执行
cfssl gencert -initca ca-csr.json | cfssljson -bare ca -
```

![image-20210420173930109](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210420173930109.png)

> 执行完成之后在当前目录下就会多出两个文件：``ca-key.pem（CA数字证书）和ca.pem（CA私钥）`` 就是通过这两个CA自签机构文件去为k8s的``apiserver、kube-controller-manager、kube-scheduler``颁发证书的。

### 3.1.2 使用自签CA为kube-apiserver签发证书

```shell
#注意：在Master1节点操作
cd ~/TLS/k8s/
# 创建证书申请文件
cat > server-csr.json << EOF
{
    "CN": "kubernetes",
    "hosts": [
      "10.0.0.1",
      "127.0.0.1",
      "10.0.160.5",
      "10.0.160.6",
      "10.0.160.7",
      "10.0.160.8",
      "10.0.160.9",
      "10.0.160.60",
      "kubernetes",
      "kubernetes.default",
      "kubernetes.default.svc",
      "kubernetes.default.svc.cluster",
      "kubernetes.default.svc.cluster.local"
    ],
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "L": "BeiJing",
            "ST": "BeiJing",
            "O": "k8s",
            "OU": "System"
        }
    ]
}
EOF
```

> 注：上述文件``hosts``字段中IP为所有**Master1、Master2、Node1、Node2、VIP的IP**，一个都不能少！为了方便后期扩容可以多写几个预留的IP。
>
> ``10.0.160.60``是VIP

**生成证书**

```shell
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=kubernetes server-csr.json | cfssljson -bare server
```

![image-20210420174226275](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210420174226275.png)



## 3.2 部署kube-apisever

### 3.2.1 从Github下载二进制文件

[kubernetes-server-linux-amd64.tar.gz 下载链接](https://dl.k8s.io/v1.18.3/kubernetes-server-linux-amd64.tar.gz)

### 3.2.2 上传、解压、创建工作目录

```shell
#注意：在Master1节点操作
#将二进制包上传并解压
rz -be
tar -zxvf kubernetes-server-linux-amd64.tar.gz

#为k8s创建工作目录
mkdir -p /opt/kubernetes/{bin,cfg,ssl,logs}

#进入解压后的目录/root/kubernetes/server/bin 将apiserver、scheduler、controller-manager文件移动
cd /root/kubernetes/server/bin
cp kube-apiserver kube-scheduler kube-controller-manager /opt/kubernetes/bin
cp kubectl /usr/bin/
```

### 3.2.3 创建kube-apiserver配置文件

```shell
#注意：在Master1节点执行
cat > /opt/kubernetes/cfg/kube-apiserver.conf << EOF
KUBE_APISERVER_OPTS="--logtostderr=false \\
--v=2 \\
--log-dir=/opt/kubernetes/logs \\
--etcd-servers=https://10.0.160.5:2379,https://10.0.160.6:2379,https://10.0.160.7:2379 \\
--bind-address=10.0.160.5 \\
--secure-port=6443 \\
--advertise-address=10.0.160.5 \\
--allow-privileged=true \\
--service-cluster-ip-range=10.0.0.0/24 \\
--enable-admission-plugins=NamespaceLifecycle,LimitRanger,ServiceAccount,ResourceQuota,NodeRestriction \\
--authorization-mode=RBAC,Node \\
--enable-bootstrap-token-auth=true \\
--token-auth-file=/opt/kubernetes/cfg/token.csv \\
--service-node-port-range=30000-32767 \\
--kubelet-client-certificate=/opt/kubernetes/ssl/server.pem \\
--kubelet-client-key=/opt/kubernetes/ssl/server-key.pem \\
--tls-cert-file=/opt/kubernetes/ssl/server.pem  \\
--tls-private-key-file=/opt/kubernetes/ssl/server-key.pem \\
--client-ca-file=/opt/kubernetes/ssl/ca.pem \\
--service-account-key-file=/opt/kubernetes/ssl/ca-key.pem \\
--service-account-issuer=api \\
--service-account-signing-key-file=/opt/kubernetes/ssl/server-key.pem \\
--etcd-cafile=/opt/etcd/ssl/ca.pem \\
--etcd-certfile=/opt/etcd/ssl/server.pem \\
--etcd-keyfile=/opt/etcd/ssl/server-key.pem \\
--requestheader-client-ca-file=/opt/kubernetes/ssl/ca.pem \\
--proxy-client-cert-file=/opt/kubernetes/ssl/server.pem \\
--proxy-client-key-file=/opt/kubernetes/ssl/server-key.pem \\
--requestheader-allowed-names=kubernetes \\
--requestheader-extra-headers-prefix=X-Remote-Extra- \\
--requestheader-group-headers=X-Remote-Group \\
--requestheader-username-headers=X-Remote-User \\
--enable-aggregator-routing=true \\
--audit-log-maxage=30 \\
--audit-log-maxbackup=3 \\
--audit-log-maxsize=100 \\
--audit-log-path=/opt/kubernetes/logs/k8s-audit.log"
EOF
```

> **参数说明**
>
> -- logtostderr：启用日志
> --- v：日志级别 0-8 从小到大越来越详细 一般为2
> -- log-dir：日志目录
> -- etcd-servers：etcd集群地址（**需要修改**）
> -- bind-address：apiserver监听的ip地址，一般是当前机器的内网ip（**需要修改**）
> -- secure-port：https安全端口
> -- advertise-address：集群通告地址，一般与bind-address保持一致，其他node通过这个ip连接你的apiserver（**需要修改**）
> -- allow-privileged：启用授权
> -- service-cluster-ip-range：Service虚拟IP地址段
> -- enable-admission-plugins：准入控制模块
> -- authorization-mode：认证授权，启用RBAC授权和节点自管理
> -- enable-bootstrap-token-auth：**启用TLS bootstrap机制**
> -- token-auth-file：**bootstrap token文件 下面4.2.5章节会创建**
> -- service-node-port-range：Service nodeport类型默认分配端口范围
> -- kubelet-client-xxx：apiserver访问kubelet客户端证书
> -- tls-xxx-file：apiserver https证书
> -- etcd-xxxfile：连接Etcd集群证书
> -- audit-log-xxx：审计日志

### 3.2.4 将生成的apiserver证书拷贝到其工作目录下

```shell
cp ~/TLS/k8s/ca*pem ~/TLS/k8s/server*pem /opt/kubernetes/ssl/
```

``四个缺一不可``

![image-20210420175918732](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210420175918732.png)

### 3.2.5 启动TLS Bootstrapping机制

我们在``apiserver.conf``配置文件中其实去开启了``bootstrapping``机制，我们可以看一下

![image-20210420175956406](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210420175956406.png)

启用TLS Bootstrapping这个机制，是为了当机器越来越多时，手动为kubelet颁发证书比较麻烦，所以引用这个机制去自动颁发证书

如上图引入了``token.csv``文件，这个文件中就会配置一个账号，这个账号的格式是：``token,用户,uid,用户组``

**创建token.csv**

```shell
#注意：在master1节点执行
cat > /opt/kubernetes/cfg/token.csv << EOF
e9eefb2b458c373f779665de3099a7bd,kubelet-bootstrap,10001,"system:node-bootstrapper"
EOF
```

> 格式：token，用户名，UID，用户组
>
> 当然token我们也可以自己生成去替换：``head -c 16 /dev/urandom | od -An -t x | tr -d ' '``
>
> 主要用于让node拿着这个token过来，然后就会使用kubelet-bootstrap这个账号权限去自动颁发证书



### 3.2.6 systemd管理kube-apiserver

```shell
#注意：在Master1节点操作
cat > /lib/systemd/system/kube-apiserver.service << EOF
[Unit]
Description=Kubernetes API Server
Documentation=https://github.com/kubernetes/kubernetes

[Service]
EnvironmentFile=/opt/kubernetes/cfg/kube-apiserver.conf
ExecStart=/opt/kubernetes/bin/kube-apiserver \$KUBE_APISERVER_OPTS
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF
```



### 3.2.7 启动

```shell
#注意：在Master1节点操作
systemctl daemon-reload
systemctl start kube-apiserver
systemctl enable kube-apiserver
```

> 可以通过：``systemctl status kube-apiserver.service`` 查看是否启动成功
>
> apiserver的配置文件中配置的地址：``tail /opt/kubernetes/logs/kube-apiserver.INFO -f``



## 3.3 部署kube-controller-manager

### 3.3.1 创建controller-manager配置文件

```shell
#注意：在Master1节点操作
cat > /opt/kubernetes/cfg/kube-controller-manager.conf << EOF
KUBE_CONTROLLER_MANAGER_OPTS="--logtostderr=false \\
--v=2 \\
--log-dir=/opt/kubernetes/logs \\
--leader-elect=true \\
--kubeconfig=/opt/kubernetes/cfg/kube-controller-manager.kubeconfig \\
--bind-address=127.0.0.1 \\
--allocate-node-cidrs=true \\
--cluster-cidr=10.244.0.0/16 \\
--service-cluster-ip-range=10.0.0.0/24 \\
--cluster-signing-cert-file=/opt/kubernetes/ssl/ca.pem \\
--cluster-signing-key-file=/opt/kubernetes/ssl/ca-key.pem  \\
--root-ca-file=/opt/kubernetes/ssl/ca.pem \\
--service-account-private-key-file=/opt/kubernetes/ssl/ca-key.pem \\
--cluster-signing-duration=87600h0m0s"
EOF
```

> **参数说明**：
>
> --master：通过本地非安全本地端口8080连接apiserver。
>
> --leader-elect：当该组件启动多个时，自动选举（HA）
>
> --cluster-signing-cert-file/--cluster-signing-key-file：自动为kubelet颁发证书的CA，与apiserver
> 保持一致
>
> --cluster-cidr：集群pod的ip段，要与cni插件的ip段相同
>
> --service-cluster-ip-range：service的虚拟ip就是从这个``10.0.0.0/24``段中分配的 和apiserver中配置的要相同

### 3.3.2 生成kube-controller-manager证书

``` shell
#在Master1执行
# 切换工作目录
cd ~/TLS/k8s

# 创建证书请求文件
cat > kube-controller-manager-csr.json << EOF
{
  "CN": "system:kube-controller-manager",
  "hosts": [],
  "key": {
    "algo": "rsa",
    "size": 2048
  },
  "names": [
    {
      "C": "CN",
      "L": "BeiJing", 
      "ST": "BeiJing",
      "O": "system:masters",
      "OU": "System"
    }
  ]
}
EOF

# 生成证书
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=kubernetes kube-controller-manager-csr.json | cfssljson -bare kube-controller-manager
```

### 3.3.3 生成kubeconfig文件（以下是shell命令，直接在终端执行）

``` shell
#在Master1执行
KUBE_CONFIG="/opt/kubernetes/cfg/kube-controller-manager.kubeconfig"
KUBE_APISERVER="https://10.0.160.5:6443"

kubectl config set-cluster kubernetes \
  --certificate-authority=/opt/kubernetes/ssl/ca.pem \
  --embed-certs=true \
  --server=${KUBE_APISERVER} \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-credentials kube-controller-manager \
  --client-certificate=./kube-controller-manager.pem \
  --client-key=./kube-controller-manager-key.pem \
  --embed-certs=true \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-context default \
  --cluster=kubernetes \
  --user=kube-controller-manager \
  --kubeconfig=${KUBE_CONFIG}
kubectl config use-context default --kubeconfig=${KUBE_CONFIG}
```

### 3.3.4 systemd管理kube-controller-manager
```shell
#注意：在Master1节点操作
cat > /lib/systemd/system/kube-controller-manager.service << EOF
[Unit]
Description=Kubernetes Controller Manager
Documentation=https://github.com/kubernetes/kubernetes

[Service]
EnvironmentFile=/opt/kubernetes/cfg/kube-controller-manager.conf
ExecStart=/opt/kubernetes/bin/kube-controller-manager \$KUBE_CONTROLLER_MANAGER_OPTS
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF
```

### 3.3.5 启动
```shell
#注意：在Master1节点操作
systemctl daemon-reload
systemctl start kube-controller-manager
systemctl enable kube-controller-manager
```

>查看启动状态： `systemctl status kube-controller-manager.service` 
>日志： `tail /opt/kubernetes/logs/kube-controller-manager.INFO -f`



## 3.4 部署kube-scheduler

### 3.4.1 创建kube-scheduler配置文件

```shell
#注意：在Master1节点操作
cat > /opt/kubernetes/cfg/kube-scheduler.conf << EOF
KUBE_SCHEDULER_OPTS="--logtostderr=false \\
--v=2 \\
--log-dir=/opt/kubernetes/logs \\
--leader-elect \\
--kubeconfig=/opt/kubernetes/cfg/kube-scheduler.kubeconfig \\
--bind-address=127.0.0.1"
EOF
```

> **参数说明**：
>
> --master：通过本地非安全本地端口8080连接apiserver。
> --leader-elect：当该组件启动多个时，自动选举（HA）

### 3.4.2 生成kubeconfig文件
**生成kube-scheduler证书**
``` shell
#在Master1执行
# 切换工作目录
cd ~/TLS/k8s

# 创建证书请求文件
cat > kube-scheduler-csr.json << EOF
{
  "CN": "system:kube-scheduler",
  "hosts": [],
  "key": {
    "algo": "rsa",
    "size": 2048
  },
  "names": [
    {
      "C": "CN",
      "L": "BeiJing",
      "ST": "BeiJing",
      "O": "system:masters",
      "OU": "System"
    }
  ]
}
EOF

# 生成证书
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=kubernetes kube-scheduler-csr.json | cfssljson -bare kube-scheduler
```

**生成kubeconfig文件（以下是shell命令，直接在终端执行）**
``` shell
#在Master1执行
KUBE_CONFIG="/opt/kubernetes/cfg/kube-scheduler.kubeconfig"
KUBE_APISERVER="https://10.0.160.5:6443"

kubectl config set-cluster kubernetes \
  --certificate-authority=/opt/kubernetes/ssl/ca.pem \
  --embed-certs=true \
  --server=${KUBE_APISERVER} \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-credentials kube-scheduler \
  --client-certificate=./kube-scheduler.pem \
  --client-key=./kube-scheduler-key.pem \
  --embed-certs=true \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-context default \
  --cluster=kubernetes \
  --user=kube-scheduler \
  --kubeconfig=${KUBE_CONFIG}
kubectl config use-context default --kubeconfig=${KUBE_CONFIG}
```

### 3.4.3 systemd管理kube-scheduler
```shell
#注意：在Master1节点操作
cat > /lib/systemd/system/kube-scheduler.service << EOF
[Unit]
Description=Kubernetes Scheduler
Documentation=https://github.com/kubernetes/kubernetes

[Service]
EnvironmentFile=/opt/kubernetes/cfg/kube-scheduler.conf
ExecStart=/opt/kubernetes/bin/kube-scheduler \$KUBE_SCHEDULER_OPTS
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF
```

### 3.4.3 启动
```shell
#注意：在Master1节点操作
systemctl daemon-reload
systemctl start kube-scheduler
systemctl enable kube-scheduler
```
>查看启动状态： `systemctl status kube-scheduler.service` 
>日志： `tail /opt/kubernetes/logs/kube-scheduler.INFO -f`



## 3.5 查看当前K8s集群组件状态

### 3.5.1 生成kubectl连接集群的证书
``` shell
#在Master1执行
cat > admin-csr.json <<EOF
{
  "CN": "admin",
  "hosts": [],
  "key": {
    "algo": "rsa",
    "size": 2048
  },
  "names": [
    {
      "C": "CN",
      "L": "BeiJing",
      "ST": "BeiJing",
      "O": "system:masters",
      "OU": "System"
    }
  ]
}
EOF

cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=kubernetes admin-csr.json | cfssljson -bare admin
```

### 3.5.2 生成kubeconfig文件

``` shell
#在Master1执行
mkdir /root/.kube

KUBE_CONFIG="/root/.kube/config"
KUBE_APISERVER="https://10.0.160.5:6443"

kubectl config set-cluster kubernetes \
  --certificate-authority=/opt/kubernetes/ssl/ca.pem \
  --embed-certs=true \
  --server=${KUBE_APISERVER} \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-credentials cluster-admin \
  --client-certificate=./admin.pem \
  --client-key=./admin-key.pem \
  --embed-certs=true \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-context default \
  --cluster=kubernetes \
  --user=cluster-admin \
  --kubeconfig=${KUBE_CONFIG}
kubectl config use-context default --kubeconfig=${KUBE_CONFIG}
```

### 3.5.3 通过Kubectl工具查看当前集群组件状态

现在我们已经可以通过``kubectl``工具去查看当前K8s集群组件的状态了：

```shell
#注意：在Master1节点操作
root@yf-k8s-160005:~# kubectl get cs
NAME                 STATUS    MESSAGE             ERROR
scheduler            Healthy   ok                  
controller-manager   Healthy   ok                  
etcd-0               Healthy   {"health":"true"}   
etcd-2               Healthy   {"health":"true"}   
etcd-1               Healthy   {"health":"true"}
```

> 如果发现有哪个组件健康异常，请返回该组件的部署章节查看问题

### 3.5.4 授权kubelet-bootstrap用户允许请求证书
那虽然我们现在已经在 `token.csv` 中设置了token和用户组等信息，但是没有进行权限授权，我们需要给kubelet-bootstrap授权
概述：将kubelet-bootstrap用户绑定到 `system:node-bootstrapper` 组中

```shell
# 在master1节点进行授权
kubectl create clusterrolebinding kubelet-bootstrap \
--clusterrole=system:node-bootstrapper \
--user=kubelet-bootstrap
```
![undefined](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210420180245938.png)

# 四. 部署Work Node节点
**注意**：``先将该Node节点的IP及主机名添加至所有Master节点的HOST文件中``
## 4.1 创建工作目录并拷贝二进制文件

```shell
#在Node1节点创建工作目录
mkdir -p /opt/kubernetes/{bin,cfg,ssl,logs}
#在Master1节点 将kube-proxy和kubelet发送到Node1节点
cd /root/kubernetes/server/bin
scp kubelet kube-proxy 10.0.160.6:/opt/kubernetes/bin
```

## 4.2 部署kubelet

### 4.2.1 创建kubelete配置文件

```shell
#在Node1节点创建配置文件
cat > /opt/kubernetes/cfg/kubelet.conf << EOF
KUBELET_OPTS="--logtostderr=false \\
--v=2 \\
--log-dir=/opt/kubernetes/logs \\
--hostname-override=yf-k8s-160006 \\
--network-plugin=cni \\
--kubeconfig=/opt/kubernetes/cfg/kubelet.kubeconfig \\
--bootstrap-kubeconfig=/opt/kubernetes/cfg/bootstrap.kubeconfig \\
--config=/opt/kubernetes/cfg/kubelet-config.yml \\
--cert-dir=/opt/kubernetes/ssl \\
--pod-infra-container-image=lizhenliang/pause-amd64:3.0"
EOF
```

> **参数说明**：
>
> --hostname-override：显示名称，集群中唯一（**需根据当前节点修改**）
> --network-plugin：启用CNI
> --kubeconfig：kubelet初次加入集群引导kubeconfig文件
> --bootstrap-kubeconfig：首次启动向apiserver申请证书，**现在还没有创建该文件，下一步4.23会进行创建**
> --config：配置参数文件 ，**下一步4.22会进行创建**
> --cert-dir：kubelet证书生成目录
> --pod-infra-container-image：管理Pod网络容器的镜像



### 4.2.2 创建配置文件的参数文件

```shell
#在Node1节点创建配置参数文件
cat > /opt/kubernetes/cfg/kubelet-config.yml << EOF
kind: KubeletConfiguration
apiVersion: kubelet.config.k8s.io/v1beta1
address: 0.0.0.0
port: 10250
readOnlyPort: 10255
cgroupDriver: cgroupfs
clusterDNS:
- 10.0.0.2
clusterDomain: cluster.local 
failSwapOn: false
authentication:
  anonymous:
    enabled: false
  webhook:
    cacheTTL: 2m0s
    enabled: true
  x509:
    clientCAFile: /opt/kubernetes/ssl/ca.pem 
authorization:
  mode: Webhook
  webhook:
    cacheAuthorizedTTL: 5m0s
    cacheUnauthorizedTTL: 30s
evictionHard:
  imagefs.available: 15%
  memory.available: 100Mi
  nodefs.available: 10%
  nodefs.inodesFree: 5%
maxOpenFiles: 1000000
maxPods: 110
EOF
```

```shell
#上述配置中还需要ca.pem证书：clientCAFile: /opt/kubernetes/ssl/ca.pem
#所以我们得从Master1节点发送到Node1
scp /opt/kubernetes/ssl/ca.pem 10.0.160.6:/opt/kubernetes/ssl
```

### 4.2.3 生成kubelet初次加入集群引导.kubeconfig文件

```shell
#在Master1节点设置变量
KUBE_CONFIG="/opt/kubernetes/cfg/bootstrap.kubeconfig"
KUBE_APISERVER="https://10.0.160.5:6443" # apiserver IP:PORT
TOKEN="c47ffb939f5ca36231d9e3121a252940" # 与 /opt/kubernetes/cfg/token.csv里保持一致
```

```shell
#在Master1执行
# 生成 bootstrap.kubeconfig 配置文件
cd /opt/kubernetes/cfg

kubectl config set-cluster kubernetes \
  --certificate-authority=/opt/kubernetes/ssl/ca.pem \
  --embed-certs=true \
  --server=${KUBE_APISERVER} \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-credentials "kubelet-bootstrap" \
  --token=${TOKEN} \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-context default \
  --cluster=kubernetes \
  --user="kubelet-bootstrap" \
  --kubeconfig=${KUBE_CONFIG}
kubectl config use-context default --kubeconfig=${KUBE_CONFIG}
```

通过上面的命令我们会在``/opt/kubernetes/cfg``目录下生成``bootstrap.kubeconfig``配置文件，现在需要将该文件发送到Node1，因为该配置文件是``4.2.1步骤``中``kubelet.conf``这个配置文件需要加载的。

```shell
#在Master1节点操作
scp /opt/kubernetes/cfg/bootstrap.kubeconfig 10.0.160.6:/opt/kubernetes/cfg
```



### 4.2.4 system管理kubelet

```shell
#在Node1
cat > /lib/systemd/system/kubelet.service << EOF
[Unit]
Description=Kubernetes Kubelet
After=docker.service

[Service]
EnvironmentFile=/opt/kubernetes/cfg/kubelet.conf
ExecStart=/opt/kubernetes/bin/kubelet \$KUBELET_OPTS
Restart=on-failure
LimitNOFILE=65536

[Install]
WantedBy=multi-user.target
EOF
```

### 4.2.5 启动kubelet

```shell
#在Node1
systemctl daemon-reload
systemctl start kubelet
systemctl enable kubelet
```

> 查看启动状态：``systemctl status kubelet.service``
> 查看日志：``tail /opt/kubernetes/logs/kubelet.INFO -f``

### 4.2.6 批准kubelet证书的申请并加入集群

```shell
#在Master1节点先查询kubelet证书请求
root@yf-k8s-160005:/opt/kubernetes/cfg# kubectl get csr
NAME                                                   AGE   SIGNERNAME                                    REQUESTOR           CONDITION
node-csr-SCqccQZbTjDgFsAnL3fjJEZBd0bMbPHkTJrltpAOZ8g   22s   kubernetes.io/kube-apiserver-client-kubelet   kubelet-bootstrap   Pending
```

可以看出我们Node节点的kubelet证书申请已经可以查询到了，那么我们现在就来审批

```shell
#在Master1节点审批
#批准申请  注意:此命令不要直接复制执行,将后面的node-csr-*  替换为kubectl get csr得到的name值
kubectl certificate approve node-csr-SCqccQZbTjDgFsAnL3fjJEZBd0bMbPHkTJrltpAOZ8g
```

再次查看这条申请记录可以发现状态从pending变为批准、发布

![image-20210420190523102](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210420190523102.png)

现在我们查看整个K8s集群的节点状态

```shell
#在Master1节点查看
# 查看节点
kubectl get node
NAME         STATUS     ROLES    AGE   VERSION
yf-k8s-160006   NotReady   <none>   7s    v1.20.4

#注：由于网络插件还没有部署，节点会没有准备就绪 NotReady
```



## 4.3 部署kube-proxy

### 4.3.1 创建kube-proxy配置文件

```shell
#在Node1节点创建
cat > /opt/kubernetes/cfg/kube-proxy.conf << EOF
KUBE_PROXY_OPTS="--logtostderr=false \\
--v=2 \\
--log-dir=/opt/kubernetes/logs \\
--config=/opt/kubernetes/cfg/kube-proxy-config.yml"
EOF
```

### 4.3.2 创建配置参数文件

```shell
#在Node1节点创建如下文件 hostnameOverride: 修改成对应主机名
cat > /opt/kubernetes/cfg/kube-proxy-config.yml << EOF
kind: KubeProxyConfiguration
apiVersion: kubeproxy.config.k8s.io/v1alpha1
bindAddress: 0.0.0.0
metricsBindAddress: 0.0.0.0:10249
clientConnection:
  kubeconfig: /opt/kubernetes/cfg/kube-proxy.kubeconfig
hostnameOverride: yf-k8s-160006
clusterCIDR: 10.0.0.0/24
EOF
```

> 如上文件中还需要加载kube-proxy.kubeconfig，我们下一步去创建

### 4.3.3 生成kube-proxy.kubeconfig文件

**创建kube-proxy证书请求文件**

```shell
# 在Master1节点操作
cd ~/TLS/k8s
# 创建证书请求文件
cat > kube-proxy-csr.json << EOF
{
  "CN": "system:kube-proxy",
  "hosts": [],
  "key": {
    "algo": "rsa",
    "size": 2048
  },
  "names": [
    {
      "C": "CN",
      "L": "BeiJing",
      "ST": "BeiJing",
      "O": "k8s",
      "OU": "System"
    }
  ]
}
EOF
```

**生成kube-proxy证书**

```shell
# 在Master1节点操作
# 生成证书
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=kubernetes kube-proxy-csr.json | cfssljson -bare kube-proxy

# 查看证书
root@yf-k8s-160005:~/TLS/k8s# ls kube-proxy*pem
kube-proxy-key.pem  kube-proxy.pem
```

**生成kube-proxy.kubeconfig文件**

```shell
# 在Master1节点操作
#设置变量
KUBE_CONFIG="/opt/kubernetes/cfg/kube-proxy.kubeconfig"
KUBE_APISERVER="https://10.0.160.5:6443" #Master apiserver地址


#在Master1节点生成kube-proxy.kubeconfig
cd /root/TLS/k8s

kubectl config set-cluster kubernetes \
  --certificate-authority=/opt/kubernetes/ssl/ca.pem \
  --embed-certs=true \
  --server=${KUBE_APISERVER} \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-credentials kube-proxy \
  --client-certificate=./kube-proxy.pem \
  --client-key=./kube-proxy-key.pem \
  --embed-certs=true \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-context default \
  --cluster=kubernetes \
  --user=kube-proxy \
  --kubeconfig=${KUBE_CONFIG}
kubectl config use-context default --kubeconfig=${KUBE_CONFIG}
```

通过上面的命令我们会在``/root/TLS/k8s``目录下生成``kube-proxy.kubeconfig``配置文件，现在需要将该文件发送到Node1节点，因为该配置文件是``4.3.2步骤``中``kube-proxy-config.yml``这个配置文件需要加载的。

```shell
#在Master1节点操作
scp /root/TLS/k8s/kube-proxy.kubeconfig 10.0.160.6:/opt/kubernetes/cfg
```



### 4.3.4 system管理kube-proxy

```shell
# 在Node1节点操作
cat > /lib/systemd/system/kube-proxy.service << EOF
[Unit]
Description=Kubernetes Proxy
After=network.target

[Service]
EnvironmentFile=/opt/kubernetes/cfg/kube-proxy.conf
ExecStart=/opt/kubernetes/bin/kube-proxy \$KUBE_PROXY_OPTS
Restart=on-failure
LimitNOFILE=65536

[Install]
WantedBy=multi-user.target
EOF
```

### 4.3.5 启动

```shell
# 在Node1节点操作
systemctl daemon-reload
systemctl start kube-proxy
systemctl enable kube-proxy
```

> 查看启动状态：``systemctl status kubelet.service``
> 查看日志：``tail /opt/kubernetes/logs/kube-proxy.INFO -f``



# 五. 部署CNI网络（Calico）

## 5.1 下载Calico资源清单

``因为文件内容太多，就不直接贴代码了，直接从我的百度云下载，然后上传到Master1节点即可。``

```text
链接：https://pan.baidu.com/s/1NVlH5HgQIBIFZBEqjZ-h4A 
提取码：52gt
```

## 5.2 应用并查看
``` shell
#在Master1节点应用calico资源清单
kubectl apply -f calico.yaml

#查看启动情况
[root@k8s-master1 ~]# kubectl get pods -n kube-system
NAME                          READY   STATUS    RESTARTS   AGE
calico-node-b4qf5              1/1     Running   0          98s
```
``等Calico Pod都Running，节点也会准备就绪``

``` 
kubectl get node
NAME            STATUS   ROLES    AGE   VERSION
yf-k8s-160006   Ready    <none>   37m   v1.20.4
```

## 5.3 授权apiserver访问kubelet
应用场景：例如``kubectl logs``
``` shell
#在Master1节点
cat > apiserver-to-kubelet-rbac.yaml << EOF
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  annotations:
    rbac.authorization.kubernetes.io/autoupdate: "true"
  labels:
    kubernetes.io/bootstrapping: rbac-defaults
  name: system:kube-apiserver-to-kubelet
rules:
  - apiGroups:
      - ""
    resources:
      - nodes/proxy
      - nodes/stats
      - nodes/log
      - nodes/spec
      - nodes/metrics
      - pods/log
    verbs:
      - "*"
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: system:kube-apiserver
  namespace: ""
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: system:kube-apiserver-to-kubelet
subjects:
  - apiGroup: rbac.authorization.k8s.io
    kind: User
    name: kubernetes
EOF


#应用
kubectl apply -f apiserver-to-kubelet-rbac.yaml
```


# 六. 部署集群内部DNS（CoreDNS）
CoreDNS用于集群内部Service名称解析。
## 6.1 创建coredns资源清单（不用改动）
``` shell
#在Master1操作
vim coredns.yaml

#内容如下
# Warning: This is a file generated from the base underscore template file: coredns.yaml.base

apiVersion: v1
kind: ServiceAccount
metadata:
  name: coredns
  namespace: kube-system
  labels:
      kubernetes.io/cluster-service: "true"
      addonmanager.kubernetes.io/mode: Reconcile
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  labels:
    kubernetes.io/bootstrapping: rbac-defaults
    addonmanager.kubernetes.io/mode: Reconcile
  name: system:coredns
rules:
- apiGroups:
  - ""
  resources:
  - endpoints
  - services
  - pods
  - namespaces
  verbs:
  - list
  - watch
- apiGroups:
  - ""
  resources:
  - nodes
  verbs:
  - get
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  annotations:
    rbac.authorization.kubernetes.io/autoupdate: "true"
  labels:
    kubernetes.io/bootstrapping: rbac-defaults
    addonmanager.kubernetes.io/mode: EnsureExists
  name: system:coredns
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: system:coredns
subjects:
- kind: ServiceAccount
  name: coredns
  namespace: kube-system
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: coredns
  namespace: kube-system
  labels:
      addonmanager.kubernetes.io/mode: EnsureExists
data:
  Corefile: |
    .:53 {
        log
        errors
        health {
            lameduck 5s
        }
        ready
        kubernetes cluster.local in-addr.arpa ip6.arpa {
            pods insecure
            fallthrough in-addr.arpa ip6.arpa
            ttl 30
        }
        prometheus :9153
        forward . /etc/resolv.conf
        cache 30
        loop
        reload
        loadbalance
    }
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: coredns
  namespace: kube-system
  labels:
    k8s-app: kube-dns
    kubernetes.io/cluster-service: "true"
    addonmanager.kubernetes.io/mode: Reconcile
    kubernetes.io/name: "CoreDNS"
spec:
  # replicas: not specified here:
  # 1. In order to make Addon Manager do not reconcile this replicas parameter.
  # 2. Default is 1.
  # 3. Will be tuned in real time if DNS horizontal auto-scaling is turned on.
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
  selector:
    matchLabels:
      k8s-app: kube-dns
  template:
    metadata:
      labels:
        k8s-app: kube-dns
      annotations:
        seccomp.security.alpha.kubernetes.io/pod: 'runtime/default'
    spec:
      priorityClassName: system-cluster-critical
      serviceAccountName: coredns
      tolerations:
        - key: "CriticalAddonsOnly"
          operator: "Exists"
      nodeSelector:
        kubernetes.io/os: linux
      containers:
      - name: coredns
        image: lizhenliang/coredns:1.6.7
        imagePullPolicy: IfNotPresent
        resources:
          limits:
            memory: 512Mi 
          requests:
            cpu: 100m
            memory: 70Mi
        args: [ "-conf", "/etc/coredns/Corefile" ]
        volumeMounts:
        - name: config-volume
          mountPath: /etc/coredns
          readOnly: true
        ports:
        - containerPort: 53
          name: dns
          protocol: UDP
        - containerPort: 53
          name: dns-tcp
          protocol: TCP
        - containerPort: 9153
          name: metrics
          protocol: TCP
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
            scheme: HTTP
          initialDelaySeconds: 60
          timeoutSeconds: 5
          successThreshold: 1
          failureThreshold: 5
        readinessProbe:
          httpGet:
            path: /ready
            port: 8181
            scheme: HTTP
        securityContext:
          allowPrivilegeEscalation: false
          capabilities:
            add:
            - NET_BIND_SERVICE
            drop:
            - all
          readOnlyRootFilesystem: true
      dnsPolicy: Default
      volumes:
        - name: config-volume
          configMap:
            name: coredns
            items:
            - key: Corefile
              path: Corefile
---
apiVersion: v1
kind: Service
metadata:
  name: kube-dns
  namespace: kube-system
  annotations:
    prometheus.io/port: "9153"
    prometheus.io/scrape: "true"
  labels:
    k8s-app: kube-dns
    kubernetes.io/cluster-service: "true"
    addonmanager.kubernetes.io/mode: Reconcile
    kubernetes.io/name: "CoreDNS"
spec:
  selector:
    k8s-app: kube-dns
  clusterIP: 10.0.0.2 
  ports:
  - name: dns
    port: 53
    protocol: UDP
  - name: dns-tcp
    port: 53
    protocol: TCP
  - name: metrics
    port: 9153
    protocol: TCP
```

## 6.2 应用及测试
``` 
kubectl apply -f coredns.yaml 
 
kubectl get pods -n kube-system  
NAME                          READY   STATUS    RESTARTS   AGE 
coredns-5ffbfd976d-j6shb      1/1     Running   0          32s
```

**DNS解析测试**
``` 
kubectl run -it --rm dns-test --image=busybox:1.28.4 sh 
If you don't see a command prompt, try pressing enter. 
 
/ # nslookup kubernetes 
Server:    10.0.0.2 
Address 1: 10.0.0.2 kube-dns.kube-system.svc.cluster.local 
 
Name:      kubernetes 
Address 1: 10.0.0.1 kubernetes.default.svc.cluster.local
```
解析没问题。



# 七. 扩容Work Node节点

扩容之前该新节点应该完成本文档``1.4系统初始化（包含Docker安装）``的操作。

## 7.1 添加HOSTS
**注意**：将该扩容的Node节点主机名及IP添加至**所有**Master节点的HOST中
```shell
#在所有Master节点上操作
vim /etc/hosts
#添加该扩容的Node节点主机名及IP
1.0.0xx.xxx k8s-node #举例
```

## 7.2 拷贝已部署好的Node相关文件到扩容节点

在Node1节点将Worker Node涉及文件拷贝到扩容节点``Node2：10.0.160.7``

```shell
#在Node1节点操作
scp -r /opt/kubernetes root@10.0.160.7:/opt/

scp -r /usr/lib/systemd/system/{kubelet,kube-proxy}.service root@10.0.160.7:/usr/lib/systemd/system

scp -r /opt/kubernetes/ssl/ca.pem root@10.0.160.7:/opt/kubernetes/ssl

scp -r /opt/cni root@10.0.160.7:/opt
```



## 7.3 删除扩容节点上的kubelet证书及kubeconfig文件

```shell
#在Node2节点操作
rm -f /opt/kubernetes/cfg/kubelet.kubeconfig 
rm -f /opt/kubernetes/ssl/kubelet*
```

> 这几个文件是证书申请审批后自动生成的，每个Node不同，必须删除



## 7.4 修改扩容节点配置文件

**修改kubelet.conf**

```shell
#在Node2节点操作 
vi /opt/kubernetes/cfg/kubelet.conf

#修改kubelet.conf文件中的主机名为当前节点主机名
--hostname-override=yf-k8s-160007
```

**修改kube-proxy-config.yml**

```shell
#在Node2节点操作 
vi /opt/kubernetes/cfg/kube-proxy-config.yml

#修改kube-proxy-config.yml文件中的主机名为当前节点主机名
hostnameOverride: yf-k8s-160007
```



## 7.5 启动扩容节点kube-proxy

```shell
systemctl daemon-reload
systemctl start kubelet kube-proxy
systemctl enable kubelet kube-proxy
```



## 7.6 在Master节点批准扩容节点的kubelet证书申请

```shell
# 查看证书请求
kubectl get csr
NAME           AGE   SIGNERNAME                    REQUESTOR           CONDITION
node-csr-4zTjsaVSrhuyhIGqsefxzVoZDCNKei-aE2jyTP81Uro   89s   kubernetes.io/kube-apiserver-client-kubelet   kubelet-bootstrap   Pending

# 授权请求
kubectl certificate approve node-csr-4zTjsaVSrhuyhIGqsefxzVoZDCNKei-aE2jyTP81Uro
```



## 7.7 查看扩容Node状态

```shell
kubectl get node
NAME            STATUS   ROLES    AGE     VERSION
yf-k8s-160006   Ready    <none>   47m     v1.20.4
yf-k8s-160007    Ready    <none>   6m49s   v1.20.4
```
> 如果新增的扩容节点是NotReady状态的话可以通过如下命令查看该节点的``calico``组件是否启动成功
  
  ``kubectl get pod -n kube-system -owide``


# 八. 扩容Master（实现高可用架构）

扩容之前该新节点应该完成本文档``1.4系统初始化（包含Docker安装）``的操作。

## 8.1 部署Master2扩容节点

Master2 与已部署的Master1所有操作一致。所以我们只需将Master1所有K8s文件拷贝过来，再修改下服务器IP和主机名启动即可。

### 8.1.1 修改HOSTS
**将K8s集群中所有节点的主机名和IP添加到该扩容Master节点的HOSTS中**
``也可以从别的Master节点直接复制过来``

### 8.1.2 创建Etcd证书目录

```shell
#在Master2扩容节点创建etcd证书目录
mkdir -p /opt/etcd/ssl
```

### 8.1.3 拷贝已部署好的Master节点相关文件到扩容节点

```shell
#拷贝Master1上所有K8s文件和etcd证书到Master2：
scp -r /opt/kubernetes root@10.0.160.8:/opt
scp -r /opt/etcd/ssl root@10.0.160.8:/opt/etcd
scp /usr/lib/systemd/system/kube* root@10.0.160.8:/usr/lib/systemd/system
scp /usr/bin/kubectl  root@10.0.160.8:/usr/bin
scp -r ~/.kube root@10.0.160.8:~
```

### 8..1.4 删除证书文件

删除kubelet证书和kubeconfig文件

```shell
#在Master2操作
rm -f /opt/kubernetes/cfg/kubelet.kubeconfig 
rm -f /opt/kubernetes/ssl/kubelet*
```

### 8.1.5 修改配置文件IP和主机名

修改apiserver、kubelet和kube-proxy配置文件为本地IP

```shell
#在Master2节点操作
vi /opt/kubernetes/cfg/kube-apiserver.conf 
...
--bind-address=10.0.160.8 \
--advertise-address=10.0.160.8 \
...

vi /opt/kubernetes/cfg/kube-controller-manager.kubeconfig
server: https://10.0.160.8:6443

vi /opt/kubernetes/cfg/kube-scheduler.kubeconfig
server: https://10.0.160.8:6443

vi /opt/kubernetes/cfg/kubelet.conf
--hostname-override=yf-k8s-160008

vi /opt/kubernetes/cfg/kube-proxy-config.yml
hostnameOverride: yf-k8s-160008

vi ~/.kube/config
...
server: https://10.0.160.8:6443

```

### 8.1.6 启动

```shell
systemctl daemon-reload
systemctl start kube-apiserver kube-controller-manager kube-scheduler kubelet kube-proxy
systemctl enable kube-apiserver kube-controller-manager kube-scheduler kubelet kube-proxy
```

### 8.1.7 查看集群状态

```shell
#在Master2节点操作
kubectl get cs
NAME                   STATUS    MESSAGE             ERROR
scheduler              Healthy   ok                  
controller-manager     Healthy   ok                  
etcd-1                 Healthy   {"health":"true"}   
etcd-2                 Healthy   {"health":"true"}   
etcd-0                 Healthy   {"health":"true"}
```



## 8.2 部署Nginx + Keepalived高可用负载均衡器

### 8.2.1 安装软件包

```shell
#在lb主备两个节点操作
#yum install epel-release -y
apt-get install nginx -y
apt-get install keepalived -y

#彻底卸载nginx
apt-get --purge autoremove nginx
```

### 8.2.2 Nginx配置文件

```shell
#在lb主备两个节点的Nginx配置文件相同，都是如下配置
cat > /etc/nginx/nginx.conf << "EOF"
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /run/nginx.pid;

load_module  modules/ngx_stream_module.so;

events {
    worker_connections 1024;
}

# 四层负载均衡，为两台Master apiserver组件提供负载均衡
stream {

    log_format  main  '$remote_addr $upstream_addr - [$time_local] $status $upstream_bytes_sent';

    access_log  /var/log/nginx/k8s-access.log  main;

    upstream k8s-apiserver {
       server 10.0.160.5:6443;   # Master1 APISERVER IP:PORT
    }
    
    server {
       listen 16443; # 由于nginx与master节点复用，这个监听端口不能是6443，否则会冲突
       proxy_pass k8s-apiserver;
    }
}

http {
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile            on;
    tcp_nopush          on;
    tcp_nodelay         on;
    keepalive_timeout   65;
    types_hash_max_size 2048;

    include             /etc/nginx/mime.types;
    default_type        application/octet-stream;

}
EOF
```

### 8.2.3 Keepalived配置文件（LB主节点）

```shell
#在lb主节点修改keepalived配置文件
cat > /etc/keepalived/keepalived.conf << EOF
global_defs { 
   notification_email { 
     acassen@firewall.loc 
     failover@firewall.loc 
     sysadmin@firewall.loc 
   } 
   notification_email_from Alexandre.Cassen@firewall.loc  
   smtp_server 127.0.0.1 
   smtp_connect_timeout 30 
   router_id NGINX_MASTER
} 

vrrp_script check_nginx {
    script "/etc/keepalived/check_nginx.sh"
}

vrrp_instance VI_1 { 
    state MASTER 
    interface eno2  # 修改为实际网卡名
    virtual_router_id 51 # VRRP 路由 ID实例，每个实例是唯一的 
    priority 100    # 优先级，备服务器设置 90 
    advert_int 1    # 指定VRRP 心跳包通告间隔时间，默认1秒 
    authentication { 
        auth_type PASS      
        auth_pass 1111 
    }  
    # 虚拟IP
    virtual_ipaddress { 
        10.0.160.60/24
    } 
    track_script {
        check_nginx
    } 
}
EOF
```

> •   vrrp_script：指定检查nginx工作状态脚本（根据nginx状态判断是否故障转移）
>
> •   virtual_ipaddress：虚拟IP（VIP）

**创建上述配置文件中检查Nginx运行状态的脚本**

```shell
#在lb主节点
cat > /etc/keepalived/check_nginx.sh  << "EOF"
#!/bin/bash
count=$(ss -antp |grep 16443 |egrep -cv "grep|$$")

if [ "$count" -eq 0 ];then
    exit 1
else
    exit 0
fi
EOF

#授权
chmod +x /etc/keepalived/check_nginx.sh
```

### 8.2.4 Keepalived配置文件（LB备节点）

```shell
#在lb备节点操作
cat > /etc/keepalived/keepalived.conf << EOF
global_defs { 
   notification_email { 
     acassen@firewall.loc 
     failover@firewall.loc 
     sysadmin@firewall.loc 
   } 
   notification_email_from Alexandre.Cassen@firewall.loc  
   smtp_server 127.0.0.1 
   smtp_connect_timeout 30 
   router_id NGINX_BACKUP
} 

vrrp_script check_nginx {
    script "/etc/keepalived/check_nginx.sh"
}

vrrp_instance VI_1 { 
    state BACKUP 
    interface eno2
    virtual_router_id 51 # VRRP 路由 ID实例，每个实例是唯一的 
    priority 90
    advert_int 1
    authentication { 
        auth_type PASS      
        auth_pass 1111 
    }  
    virtual_ipaddress { 
        10.0.160.60/24
    } 
    track_script {
        check_nginx
    } 
}
EOF
```

**创建上述配置文件中检查Nginx允许状态的脚本**

```shell
#在lb备节点
cat > /etc/keepalived/check_nginx.sh  << "EOF"
#!/bin/bash
count=$(ss -antp |grep 16443 |egrep -cv "grep|$$")

if [ "$count" -eq 0 ];then
    exit 1
else
    exit 0
fi
EOF


#授权
chmod +x /etc/keepalived/check_nginx.sh
```

> 注：keepalived根据脚本返回状态码（0为工作正常，非0不正常）判断是否故障转移。

### 8.2.5 启动

```shell
systemctl daemon-reload
systemctl start nginx keepalived
systemctl enable nginx keepalived
```

### 8.2.6 查看keepalived工作状态

在LB Master节点通过``ip a``命令查看``eno2``网卡是否绑定了``10.0.160.60``虚拟IP。

### 8.2.7 Nginx+Keepalived高可用测试

关闭LB Master节点的Nginx，测试VIP是否会漂移到LB Backup节点上

### 8.2.8 通过VIP访问K8s集群测试

```shell
curl -k https://10.0.160.60:16443/version
{
  "major": "1",
  "minor": "20",
  "gitVersion": "v1.20.4",
  "gitCommit": "e87da0bd6e03ec3fea7933c4b5263d151aafd07c",
  "gitTreeState": "clean",
  "buildDate": "2021-02-18T16:03:00Z",
  "goVersion": "go1.15.8",
  "compiler": "gc",
  "platform": "linux/amd64"
}
```

可以正确获取到K8s版本信息，说明负载均衡器搭建正常。该请求数据流程：``curl -> vip(nginx) -> apiserver``

通过查看Nginx日志也可以看到转发apiserver IP：

```shell
tail /var/log/nginx/k8s-access.log -f
```



## 8.3 修改所有Node节点连接VIP（重要）

试想下，虽然我们增加了Master2 Node和负载均衡器，但是我们是从单Master架构扩容的，也就是说目前所有的Worker Node组件连接都还是Master1 Node，如果不改为连接VIP走负载均衡器，那么Master还是单点故障。

因此接下来就是要改所有Worker Node（kubectl get node命令查看到的节点）组件配置文件，由原来``10.0.160.5``修改为``10.0.160.60（VIP）``。

**在所有Worker Node执行**

```shell
#建议批量修改完成之后再一个个检查一下
sed -i 's#10.0.160.5:6443#10.0.160.60:16443#' /opt/kubernetes/cfg/*
#重启
systemctl restart kubelet kube-proxy
```

**查看节点状态**

```shell
kubectl get node 
NAME            STATUS   ROLES    AGE   VERSION 
yf-k8s-160005    Ready    <none>   31d   v1.20.4 
yf-k8s-160006    Ready    <none>   31d   v1.20.4 
yf-k8s-160007    Ready    <none>   31d   v1.20.4
```

**多说一嘴**：

>**本章相关代码及笔记地址：**[**飞机票🚀**](https://github.com/EayonLee/JavaGod/tree/main/9%E9%98%B6%E6%AE%B5%EF%BC%9ADocker%E5%AE%B9%E5%99%A8%20%26%20CICD%20%26%20DevOps%20%26apm/02%E6%A8%A1%E5%9D%97%EF%BC%9AK8s%E5%AE%B9%E5%99%A8%E7%BC%96%E6%8E%92%E7%B3%BB%E7%BB%9F/01.%20Kubernetes%E4%BA%8C%E8%BF%9B%E5%88%B6%E9%AB%98%E5%8F%AF%E7%94%A8%E9%83%A8%E7%BD%B2)
>
>🌍Github：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://github.com/EayonLee/JavaGod)
>🪐CSDN：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://blog.csdn.net/qq_20492277/article/details/114269863)