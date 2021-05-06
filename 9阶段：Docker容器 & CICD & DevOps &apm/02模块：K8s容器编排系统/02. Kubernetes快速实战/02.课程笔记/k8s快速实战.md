# K8s快速实战

# 说在前面

>**本章相关代码及笔记地址：**[**飞机票🚀**](https://github.com/EayonLee/JavaGod/tree/main/9%E9%98%B6%E6%AE%B5%EF%BC%9ADocker%E5%AE%B9%E5%99%A8%20%26%20CICD%20%26%20DevOps%20%26apm/02%E6%A8%A1%E5%9D%97%EF%BC%9AK8s%E5%AE%B9%E5%99%A8%E7%BC%96%E6%8E%92%E7%B3%BB%E7%BB%9F/02.%20Kubernetes%E5%BF%AB%E9%80%9F%E5%AE%9E%E6%88%98)
>
>🌍Github：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://github.com/EayonLee/JavaGod)<br>
>🪐CSDN：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://blog.csdn.net/qq_20492277/article/details/114269863)

# 目录
- [K8s快速实战](#k8s快速实战)
- [说在前面](#说在前面)
- [目录](#目录)
- [一. Kubectl 命令行管理工具](#一-kubectl-命令行管理工具)
  - [1.1 设置kubectl工具命令自动补全](#11-设置kubectl工具命令自动补全)
  - [1.2 常用命令](#12-常用命令)
- [二. 在K8s部署Java应用](#二-在k8s部署java应用)
  - [2.1 制作镜像](#21-制作镜像)
  - [2.2 控制器管理Pod](#22-控制器管理pod)
  - [2.3 暴露应用](#23-暴露应用)
- [三. YAML资源编排](#三-yaml资源编排)
  - [3.1 YAML文件格式说明](#31-yaml文件格式说明)
  - [3.2 YAML文件创建资源对象](#32-yaml文件创建资源对象)
    - [3.2.1 YAML文件示例](#321-yaml文件示例)
    - [3.2.2 YAML文件字段简介](#322-yaml文件字段简介)
    - [3.2.3 YAML字段太多记不住](#323-yaml字段太多记不住)
    - [3.2.3 使用YAML部署一个Pod](#323-使用yaml部署一个pod)
- [四. 深入理解Pod](#四-深入理解pod)
  - [4.1 Pod基本概念](#41-pod基本概念)
  - [4.2 Pod实现机制](#42-pod实现机制)
  - [4.3 Pod资源限制](#43-pod资源限制)
  - [4.4 Pod重启策略](#44-pod重启策略)
  - [4.5 Pod健康检查](#45-pod健康检查)
  - [4.6 Pod调度策略](#46-pod调度策略)
    - [4.6.1 nodeName方式](#461-nodename方式)
    - [4.6.2 nodeSelector方式](#462-nodeselector方式)
  - [4.7 Pod故障排查](#47-pod故障排查)
- [五. Pod升级、回滚、删除、扩容](#五-pod升级回滚删除扩容)
  - [5.1 升级](#51-升级)
  - [5.2 回滚](#52-回滚)
  - [5.3 删除](#53-删除)
  - [5.4 扩容](#54-扩容)
- [六. 深入理解Service](#六-深入理解service)
  - [6.1 Service存在的意义](#61-service存在的意义)
  - [6.2 Pod与Service的关系](#62-pod与service的关系)
  - [6.3 Service三种类型](#63-service三种类型)
  - [6.4 Service两种负载均衡模式（代理模式）](#64-service两种负载均衡模式代理模式)
- [七. Ingress对外暴露应用](#七-ingress对外暴露应用)
  - [7.1 Ingress-Nginx](#71-ingress-nginx)
  - [7.2 部署高可用Ingress-Nginx](#72-部署高可用ingress-nginx)
- [八. 部署Mertucs-Server](#八-部署mertucs-server)
  - [8.1 开启ApiServer聚合层](#81-开启apiserver聚合层)
  - [8.2 所有Master节点部署kube-proxy、kubelet、CNI](#82-所有master节点部署kube-proxykubeletcni)
  - [8.3 创建Mertrics-Server资源清单](#83-创建mertrics-server资源清单)


# 一. Kubectl 命令行管理工具

kubectl是用来管理和操作k8s集群的工具，我们主要使用Kubectl来进行创建资源、部署应用、管理应用等等操作

![image-20210506151447168](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506151447168.png)

![image-20210506151502031](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506151502031.png)



## 1.1 设置kubectl工具命令自动补全

```shell
#所有Master节点都要安装
yum install bash-completion -y
#启用
source <(kubectl completion bash)
#然后就可以通过键盘的tab键进行命令补全了
```



## 1.2 常用命令

**查看集群中的Node**

```shell
k8s@yf-k8s-160005:~$ kubectl get node       
NAME            STATUS   ROLES    AGE   VERSION
yf-k8s-160005   Ready    master   14d   v1.20.4
yf-k8s-160006   Ready    node     14d   v1.20.4
yf-k8s-160007   Ready    node     14d   v1.20.4
```

**查看K8s版本**

```shell
k8s@yf-k8s-160005:~$ kubectl version
Client Version: version.Info{Major:"1", Minor:"20", GitVersion:"v1.20.4", GitCommit:"e87da0bd6e03ec3fea7933c4b5263d151aafd07c", GitTreeState:"clean", BuildDate:"2021-02-18T16:12:00Z", GoVersion:"go1.15.8", Compiler:"gc", Platform:"linux/amd64"}
Server Version: version.Info{Major:"1", Minor:"20", GitVersion:"v1.20.4", GitCommit:"e87da0bd6e03ec3fea7933c4b5263d151aafd07c", GitTreeState:"clean", BuildDate:"2021-02-18T16:03:00Z", GoVersion:"go1.15.8", Compiler:"gc", Platform:"linux/amd64"}
```

**查看集群状态**

```shell
k8s@yf-k8s-160005:~$ kubectl cluster-info
Kubernetes control plane is running at https://10.0.160.5:6443
CoreDNS is running at https://10.0.160.5:6443/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy
```

**打印Pod日志信息**

```shell
#查看Pod中某个容器的日志
kubectl logs Pod名称 -f -c 容器名称 -n 命名空间名称 (-f 持续监控，-c 容器名称 如果pod中只有一个容器不用加)
#查看Pod所有容器的日志
kubectl logs -f Pod名称 -n 命名空间名称
```

**进入Pod容器中**

```shell
kubectl exec -it Pod名称 /bin/sh    （进入容器的交互式shell）
```

**查看命名空间**

```shell
k8s@yf-k8s-160005:~$ kubectl get ns
NAME              STATUS   AGE
default           Active   15d
ingress-nginx     Active   12d
kube-node-lease   Active   15d
kube-public       Active   15d
kube-system       Active   15d
site              Active   10d
test              Active   10d
```

**查看Pod**

```shell
#指定命名空间查看
k8s@yf-k8s-160005:~$ kubectl get pod -n test
NAME                                                 READY   STATUS    RESTARTS   AGE
kw-web-message-robot-service-test-6c6f6b668d-zshbv   1/1     Running   0          9d
```

**查看Pod详情**

```shell
kubectl describe pod Pod名称 -n 所在命名空间名称
```

**查看Pod或Node标签**

```shell
#查看所有Node标签
kubectl get nodes --show-labels
#查看Pods标签
kubectl get pods --show-labels
```



# 二. 在K8s部署Java应用

![image-20210506152652771](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506152652771.png)

## 2.1 制作镜像

**流程**：准备代码->编译构建->将编译出的war包构建为镜像->推送镜像到镜像仓库

该步骤本文不再赘述，可以自行百度解决，下面我们使用现成的镜像来做实验



## 2.2 控制器管理Pod

大部分应用我们都通过``Deployment``这个Pod控制器去进行部署，所以本文只介绍该控制器

**Deployment控制器功能**

- 部署无状态应用
- 管理Pod和ReplicaSet
- 据有上线部署、副本设定、滚动升级、回滚等功能
- 提供声明式更新，例如只更新一个新的Image
- 应用场景：Web服务、微服务

**使用Deployment去部署Java应用**

```shell
# 通过java-demo这个镜像部署一个名为web的Java应用并使用Deployment去进行Pod管理
k8s@yf-k8s-160005:~$ kubectl create deployment web --image=lizhenliang/java-demo:1
deployment.apps/web created

#查看Pod
k8s@yf-k8s-160005:~$ kubectl get pod
NAME                  READY   STATUS              RESTARTS   AGE
web-b666c4864-r62hn   0/1     ContainerCreating   0          9s

#如果Pod异常或者一直在创建中我们可通过如下命令查看某pod的状态  主要看最后一行 会展示他的创建过程
kubectl describe pod web-b666c4864-r62hn

#如果通过看Pod状态发现一直处于kubelet拉取镜像或其他操作 我们可以查看kubelet的日志  需要在该pod所创建的node机器上查看
#我是输出到这个目录的
tail /opt/kubernetes/logs/kubelet.INFO -f
```



## 2.3 暴露应用

暴露应用可是使用``Service的NodePort方式暴露``、``Ingress暴露``在这里我们先介绍``NodePort``的方式

```shell
# 创建一个service来发布Pod
# 参数介绍：
	#deployment web：对哪个deployment生效 
	#--port=80集群内部端口 
	#--target-port=8080应用端口 
	#--type=NodePort K8S给该应用创建一个对外随机端口
k8s@yf-k8s-160005:~$ kubectl expose deployment web --port=80 --target-port=8080 --type=NodePort
service/web exposed

#查看service  刚刚创建的service对外暴露端口为30263
k8s@yf-k8s-160005:~$ kubectl get svc
NAME         TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)        AGE
kubernetes   ClusterIP   10.0.0.1     <none>        443/TCP        6d6h
web          NodePort    10.0.0.90    <none>        80:30263/TCP   37s


#通过浏览器访问暴露的应用  ip为任意node节点的ip 端口为30263 这个端口就是上面命令--type=NodePort给创建一个对外随机端口
```

![image-20210506154714590](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506154714590.png)



# 三. YAML资源编排

通过上述方式我们通过``kubectl``命令去创建Pod然后还得通过命令去创建Service去发布服务，非常麻烦，如果去部署一个微服务或者较为复杂的应用，就显得非常的难受。所以我们可以使用YAML来进行资源的编排

## 3.1 YAML文件格式说明

YAML是一种简介的非标记语言。

**语法格式**：

* 缩进表示层级关系
* 不支持制表符``tab``缩进，必须使用空格缩进
* 通常开头缩进2个空格
* 字符后缩进一个空格，如冒号、逗号等
* ``---``表示YAML格式，一个新文件的开始
* ``#``注释



## 3.2 YAML文件创建资源对象

### 3.2.1 YAML文件示例

![image-20210506155512958](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506155512958.png)

### 3.2.2 YAML文件字段简介

![image-20210506155443716](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506155443716.png)

### 3.2.3 YAML字段太多记不住

**使用run命令生成**

比如现在我们想要写一个YAML资源清单去创建一个Nginx应用，那么我们可以通过如下命令去生成一个最基本的YAML文件，然后根据该文件再去详细修改

```shell
#通过nginx使用deployment控制器创建一个web-nginx资源Pod 并通过-o yaml命令导出yaml文件，也就是创建它的yaml模板，--dry-run代表只是尝试 不会再K8S真实创建
# 通过nginx镜像去创建一个名为web-nginx的pod并由deployment控制器管理
#-o yaml命令：导出创建该pod的yaml文件
#--dry-run命令：代表只是尝试 不会再K8S真实创建
#>> nginx.yaml：将生成的yaml文件保存到当前目录下的nginx.yaml文件中
k8s@yf-k8s-160005:~$ kubectl create deployment web-nginx --image=nginx -o yaml --dry-run  >> nginx.yaml
#我们通过如上命令就可以获得一个模板，然后我们改改就能用了
apiVersion: apps/v1
kind: Deployment
metadata:
  creationTimestamp: null #这个时间戳字段一般删除
  labels:
    app: web-nginx
  name: web-nginx
spec:
  replicas: 1
  selector:
    matchLabels:
      app: web-nginx
  strategy: {}
  template:
    metadata:
      creationTimestamp: null #这个时间戳字段一般删除
      labels:
        app: web-nginx
    spec:
      containers:
      - image: nginx
        name: nginx
        resources: {}
status: {} #这个状态字段一般删除
```



**使用get命令导出某Pod的YAML**

比如你部署的项目和已在线上的项目大致相同，那么就可以导出它的YAML然后修改

```shell
#查看已部署的deployment
k8s@yf-k8s-160005:~$ kubectl get deploy
NAME   READY   UP-TO-DATE   AVAILABLE   AGE
web    1/1     1            1           122m

#导出这个web的yaml到某文件中
k8s@yf-k8s-160005:~$ kubectl get deploy web -o yaml --export > web2.yaml
Flag --export has been deprecated, This flag is deprecated and will be removed in future.
k8s@yf-k8s-160005:~$ ll
-rw-r--r-- 1 root root 970 Mar 31 06:11 web2.yaml
```



### 3.2.3 使用YAML部署一个Pod

你可以使用如上两种方式生成一个YAML文件，比如我们使用第一种方式生成了一个``nginx.yaml``文件，我们可以通过如下命令去部署一个Pod

```shell
#部署
k8s@yf-k8s-160005:~$ kubectl apply -f nginx.yaml 
deployment.apps/web-nginx created

#查看Pod
k8s@yf-k8s-160005:~$ kubectl get pod
NAME                       READY   STATUS    RESTARTS   AGE
web-767bbdb558-mvr6j       1/1     Running   0          29m
web-nginx-5b57788b-4xrvb   1/1     Running   0          60s
```



# 四. 深入理解Pod

## 4.1 Pod基本概念

- 最小部署单元
- 一个或一组容器的集合
- 一个Pod中的容器共享网络
- 一个Pod中的容器共享存储
- Pod的生命周期是短暂的



## 4.2 Pod实现机制

**如何实现共享网络？**

在一个Pod中有一个对我们不可见的``infra``容器，每个Pod都有自己独立的ip，这个ip就配置在该i``nfra``容器中，并且该Pod中的其他容器都会关联到这个``infra``网络命名空间中，也就意味着每个容器都可以从``infra``容器中查看ip。



**如何实现共享存储？**

在一个Pod中会有一个Volume数据卷，同一个Pod中的容器都会挂载该数据卷

如下YAML创建pod，pod中包含两个容器,write和read，他们都将容器中的``/data``目录进行挂载

挂载在宿主机默认的目录为``/var/lib/kubelet/pods/32f5419a-55a2-40dd-adc3-cdb56b0a4dac/volumes``（32f5419a-55a2-40dd-adc3-cdb56b0a4dac为docker容器ID）

![image-20210506161313254](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506161313254.png)

我们可以应用如上的YAML清单，然后进入到``read``容器中查看是否读取到``write``容器写入到``/data/hello``文件中的内容，如果可以读取到，则说明Pod是可以共享存储的。



## 4.3 Pod资源限制

一般都会做Pod的资源限制，分为：``requests``和``limits``

- requests：调度时去使用的，调度选择Node节点部署时根据requests定义的限制和调度Node节点剩余利用率去对比，选择Node节点剩余利用率大于等于requests定义的使用资源大小
- limits：限制已部署的应用使用的资源，如一个项目已经部署上线，那么该应用使用的内存等资源不能大于limits的限制

```shell
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: web-nginx
  name: web-nginx
spec:
  replicas: 1
  selector:
    matchLabels:
      app: web-nginx
  template:
    metadata:
      labels:
        app: web-nginx
    spec:
      containers:
      - image: nginx
        name: nginx
        resources:
            requests:
              memory: "2048Mi" # 2gb
              cpu: "2000m" # 2cpu
            limits:
              memory: "4096Mi" # 4gb
              cpu: "4000m" # 4cpu
```

> 一般limits设置为requests的两倍，requests最好不要太大，不然机器剩余的资源较少的话就得不到利用



## 4.4 Pod重启策略

- Always：当容器退出后，总是重启容器，默认策略
- OnFailure：当容器异常退出（退出状态码非0,0为正常,非0就是不正常），才会重启容器
- Never：当容器中止退出，从不重启容器

```shell
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: web-nginx
  name: web-nginx
spec:
  replicas: 1
  selector:
    matchLabels:
      app: web-nginx
  template:
    metadata:
      labels:
        app: web-nginx
    spec:
      containers:
      - image: nginx
        name: nginx
      restartPolicy: Always # 重启策略
```



## 4.5 Pod健康检查

k8s的健康检查是通过新建一个``Probe``容器来对目标容器进行检测的。

**Probe有两种类型**

- livenessProbe：如果检查失败，将容器杀死，根据Pod的``restartPolicy``重启策略来进行重启
- readinessProbe：如果检查失败，kubernetes会把Pod从``service endpoints``中剔除

**Probe支持三种检查方法：**

- httpGet：发送HTTP请求，返回200-400范围状态码为成功
- exec：执行shell命令返回状态码是0为成功
- tcpSocket：发起TCP Socket建立成功

**举例**

![image-20210506163847817](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506163847817.png)

> 通过上面的YAML创建一个Pod容器，改用其使用``livedessProbe``的``exec``方式进行探测``/tmp/healthy``文件，如果该文件存在则返回0，如果不存在则返回其他，如果为其他则状态异常执行目标容器的重启策略



## 4.6 Pod调度策略

K8S默认是通过Scheduler来进行计算，选择合适的Node来进行部署，但是我们可以修改他的调度策略

- nodeName：用于将Pod调度到指定的Node节点（不会走Scheduler）
- nodeSelector：用于将Pod调度到匹配的Label的Node上（通过设置的Node标签来进行调度，会走Scheduler）

### 4.6.1 nodeName方式

```shell
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: web-nginx
  name: web-nginx
spec:
  replicas: 1
  selector:
    matchLabels:
      app: web-nginx
  template:
    metadata:
      labels:
        app: web-nginx
    spec:
      nodeName: k8s-node1 #选择调度的节点名称
      containers:
      - image: nginx
        name: nginx
```



### 4.6.2 nodeSelector方式

**如何给Node设置Label标签**

```shell
#查看所有Node标签
kubectl get nodes --show-labels
#查看Pods标签
kubectl get pods --show-labels
#给Node设置标签  使用主机名或ip都可以 主机名的话得绑定hosts
kubectl label nodes k8s-node1 team=a
kubectl label nodes k8s-node2 team=b
```

```shell
#使用标签的方式指定调度到某Node
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: web-nginx
  name: web-nginx
spec:
  replicas: 1
  selector:
    matchLabels:
      app: web-nginx
  template:
    metadata:
      labels:
        app: web-nginx
    spec:
      nodeSelector:
        team: a #选择调度的节点的标签
      containers:
      - image: nginx
        name: nginx
```



## 4.7 Pod故障排查

![image-20210506165016127](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506165016127.png)

```shell
#查看pod的事件【master节点】
kubectl describe pod pod名称
#查看pod日志
kubectl logs pod名称 -f
#进入容器查看
kubectl exec -it pod名称 bash
```



# 五. Pod升级、回滚、删除、扩容

## 5.1 升级

无非就是动态的升级镜像

```shell
#查看现有Pod
k8s@yf-k8s-160005:~$ kubectl get pods
NAME                   READY   STATUS    RESTARTS   AGE
web-767bbdb558-mvr6j   1/1     Running   0          78m

#这里有一个java应用，我们现在将这个java应用的镜像更换成Nginx镜像来体现pod升级的过程（如有多个副本，默认滚动更新）
k8s@yf-k8s-160005:~$ kubectl set image deployment web java-demo=nginx
```

> web：修改要升级的Pod对应的deployment的控制器，控制器名字为web
>
> java-demo：要升级Pod的容器名称，可以通过``kubectl edit pod Pod名称``命令实时查看该Pod的YAML文件，从文件中查看容器名称
>
> nginx：新的镜像名称

```shell
#查看升级状态，也就是查看升级应用所使用的deployment的状态  deployment是用的上面控制器  web是控制器名称
k8s@yf-k8s-160005:~$ kubectl rollout status deployment/web
deployment "web" successfully rolled out
```



## 5.2 回滚

```shell
#查看发布历史  deployment应用使用的控制器    web是控制器名称
k8s@yf-k8s-160005:~$ kubectl rollout history deployment web
deployment.apps/web 
REVISION  CHANGE-CAUSE
1         <none>
3         <none>
4         <none>
5         <none>

#指定回滚版本
kubectl rollout undo deployment web --version=2

#回滚上一版本【生产环境一般都只回滚到上一版本即可】  deployment应用使用的控制器    web是控制器名称
k8s@yf-k8s-160005:~$ kubectl rollout undo deployment web
deployment.apps/web rolled back
```



## 5.3 删除

如果不想要某个pod了需要删除该应用，直接删除pod是不可行的，deployment会继续帮你拉起pod。

```shell
#删除pod测试 会不会重新拉起
kubectl delete pod pod名称
#肯定是会重新拉取的

#那么我们该如何删除？
#我们得删除该应用的控制器   web-nginx控制器名称
kubectl delete deploy/web

#如果该pod还有service的话还得删除对应的service  web-nginx是service名称
kubectl delete svc/web-nginx

#如果你是通过YAML文件创建的Pod，可以使用如下命令直接删除  web.yaml为Pod的资源配置清单
kubectl delete -f web.yaml
```



## 5.4 扩容

```shell
#动态扩容副本 deployment应用使用的控制器    web是控制器名称
kubectl scale deployment web --replicas=5
#查看pod
k8s@yf-k8s-160005:~$ kubectl get pod
NAME                   READY   STATUS              RESTARTS   AGE
web-67bfc85867-4p5tp   1/1     Running             0          50s
web-67bfc85867-7d2f8   1/1     Running             0          50s
web-67bfc85867-jqh4l   1/1     Running             0          50s
web-67bfc85867-qblhs   1/1     Running             0          18m
web-67bfc85867-qtllj   1/1     Running             0          50s

#如果想要减少副本数 只需要 减少 --replicas= 后面的参数就可以了
```



# 六. 深入理解Service

## 6.1 Service存在的意义

- 防止Pod失联（服务发现）：Pod是短暂的，每个Pod拥有自己的IP地址，如果这个Pod镜像滚动升级 Ip是会变的，所以通过service来关联一组Pod，通过Service来访问Pod
- 定义一组Pod的访问策略（负载均衡）

## 6.2 Pod与Service的关系

Service主要就是用来暴露应用程序的 也就是Pod

![image-20210506172714359](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506172714359.png)

那Service也得有IP，这个IP就是虚拟IP。其他人想要访问这个Pod就访问Service的虚拟IP，由Service来进行负载均衡

## 6.3 Service三种类型

- ClusterIP：集群内部使用：默认，分配一个稳定的IP，即VIP，只能在集群内访问
- NodePort：对外暴露应用：在每个节点上启动一个端口来暴露服务，可以在集群外部访问。也会分配一个稳定内部集群地址。访问地址：NodeIp:NodePort
- LoadBalancer：对外暴露应用，适用公有云

## 6.4 Service两种负载均衡模式（代理模式）

![image-20210506172819159](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506172819159.png)



# 七. Ingress对外暴露应用

![image-20210506172856865](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506172856865.png)

## 7.1 Ingress-Nginx

![image-20210506172954585](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210506172954585.png)



## 7.2 部署高可用Ingress-Nginx

生产环境中，建议把ingress-controller通过DaemonSet的方式部署集群中的每一个node节点

```shell
#创建Ingress-Nginx的资源配置清单
vim ingress-nginx.yaml
#Ingress-Nginx的资源配置清单如下
apiVersion: v1
kind: Namespace
metadata:
  name: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx

---

kind: ConfigMap
apiVersion: v1
metadata:
  name: nginx-configuration
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
data:
  log-format-upstream: '{"time": "$time_iso8601","remote_addr": "$remote_addr","x-forward-for":
    "$proxy_add_x_forwarded_for","request_id": "$req_id","remote_user": "$remote_user","bytes_sent":
    "$bytes_sent","request_time": "$request_time","status": "$status","vhost": "$host","request_proto":
    "$server_protocol","path": "$uri","request_query": "$args","request_length": "$request_length","duration":
    "$request_time","method": "$request_method","http_referrer": "$http_referer","respTime":"$upstream_response_time","headerTime":
    "$upstream_header_time","connectTime":"$upstream_connect_time"}'
---
kind: ConfigMap
apiVersion: v1
metadata:
  name: tcp-services
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
---
kind: ConfigMap
apiVersion: v1
metadata:
  name: udp-services
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx

---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: nginx-ingress-serviceaccount
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx

---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: nginx-ingress-clusterrole
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
rules:
  - apiGroups:
      - ""
    resources:
      - configmaps
      - endpoints
      - nodes
      - pods
      - secrets
    verbs:
      - list
      - watch
  - apiGroups:
      - ""
    resources:
      - nodes
    verbs:
      - get
  - apiGroups:
      - ""
    resources:
      - services
    verbs:
      - get
      - list
      - watch
  - apiGroups:
      - ""
    resources:
      - events
    verbs:
      - create
      - patch
  - apiGroups:
      - "extensions"
      - "networking.k8s.io"
    resources:
      - ingresses
    verbs:
      - get
      - list
      - watch
  - apiGroups:
      - "extensions"
      - "networking.k8s.io"
    resources:
      - ingresses/status
    verbs:
      - update

---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: nginx-ingress-role
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
rules:
  - apiGroups:
      - ""
    resources:
      - configmaps
      - pods
      - secrets
      - namespaces
    verbs:
      - get
  - apiGroups:
      - ""
    resources:
      - configmaps
    resourceNames:
      # Defaults to "<election-id>-<ingress-class>"
      # Here: "<ingress-controller-leader>-<nginx>"
      # This has to be adapted if you change either parameter
      # when launching the nginx-ingress-controller.
      - "ingress-controller-leader-nginx"
    verbs:
      - get
      - update
  - apiGroups:
      - ""
    resources:
      - configmaps
    verbs:
      - create
  - apiGroups:
      - ""
    resources:
      - endpoints
    verbs:
      - get

---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: nginx-ingress-role-nisa-binding
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: nginx-ingress-role
subjects:
  - kind: ServiceAccount
    name: nginx-ingress-serviceaccount
    namespace: ingress-nginx

---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: nginx-ingress-clusterrole-nisa-binding
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: nginx-ingress-clusterrole
subjects:
  - kind: ServiceAccount
    name: nginx-ingress-serviceaccount
    namespace: ingress-nginx

---

apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: nginx-ingress-controller
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
spec:
  #replicas: 1
  selector:
    matchLabels:
      app.kubernetes.io/name: ingress-nginx
      app.kubernetes.io/part-of: ingress-nginx
  template:
    metadata:
      labels:
        app.kubernetes.io/name: ingress-nginx
        app.kubernetes.io/part-of: ingress-nginx
      annotations:
        prometheus.io/port: "10254"
        prometheus.io/scrape: "true"
    spec:
      # wait up to five minutes for the drain of connections
      terminationGracePeriodSeconds: 300
      serviceAccountName: nginx-ingress-serviceaccount
      nodeSelector:
        kubernetes.io/os: linux
      hostNetwork: true
      dnsPolicy: ClusterFirstWithHostNet
      containers:
        - name: nginx-ingress-controller
          image: lizhenliang/nginx-ingress-controller:0.30.0
          args:
            - /nginx-ingress-controller
            - --configmap=$(POD_NAMESPACE)/nginx-configuration
            - --tcp-services-configmap=$(POD_NAMESPACE)/tcp-services
            - --udp-services-configmap=$(POD_NAMESPACE)/udp-services
            - --publish-service=$(POD_NAMESPACE)/ingress-nginx
            - --annotations-prefix=nginx.ingress.kubernetes.io
          securityContext:
            allowPrivilegeEscalation: true
            capabilities:
              drop:
                - ALL
              add:
                - NET_BIND_SERVICE
            # www-data -> 101
            runAsUser: 101
          env:
            - name: POD_NAME
              valueFrom:
                fieldRef:
                  fieldPath: metadata.name
            - name: POD_NAMESPACE
              valueFrom:
                fieldRef:
                  fieldPath: metadata.namespace
          ports:
            - name: http
              containerPort: 80
              protocol: TCP
            - name: https
              containerPort: 443
              protocol: TCP
          livenessProbe:
            failureThreshold: 3
            httpGet:
              path: /healthz
              port: 10254
              scheme: HTTP
            initialDelaySeconds: 10
            periodSeconds: 10
            successThreshold: 1
            timeoutSeconds: 10
          readinessProbe:
            failureThreshold: 3
            httpGet:
              path: /healthz
              port: 10254
              scheme: HTTP
            periodSeconds: 10
            successThreshold: 1
            timeoutSeconds: 10
          lifecycle:
            preStop:
              exec:
                command:
                  - /wait-shutdown

---

apiVersion: v1
kind: LimitRange
metadata:
  name: ingress-nginx
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
spec:
  limits:
  - min:
      memory: 90Mi
      cpu: 100m
    type: Container
```

**为Ingress-Nginx创建一个Service**

```shell
apiVersion: v1
kind: Service
metadata:
  name: ingress-nginx
  namespace: ingress-nginx
spec:
  type: ClusterIP
  ports:
  - name: http
    port: 80
    targetPort: 80
    protocol: TCP
  - name: https
    port: 443
    targetPort: 443
    protocol: TCP
  selector:
    app: ingress-nginx
    
#应用
kubectl apply -f ingress-nginx-svc.yaml
```

**应用**

```shell
#应用ingress-nginx
kubectl apply -f ingress-nginx.yaml

#查看ingress-nginx的Pod 
kubectl get pod -n ingress-nginx
NAME                             READY   STATUS    RESTARTS   AGE
nginx-ingress-controller-bvspb   1/1     Running   0          4m51s
nginx-ingress-controller-j76c9   1/1     Running   0          4m51s
nginx-ingress-controller-mwbhz   1/1     Running   0          4m51s
```



# 八. 部署Mertucs-Server

Metrics Server：是一个数据聚合器，从kubelet收集资源指标，并通 过Metrics API在Kubernetes apiserver暴露，以供HPA使用。

简单地说它可以收集到我们K8s集群中个节点的状态（CPU、内存...）以及Pod、Node...的的指标监控。

比如后面我们需要部署的UI组件Kuboard，它聚合了对K8s集群的监控功能就是依赖于Metrics-server来实现的。

最重要的就是对于Pod的自动扩容/缩容也是基于Metrics-Server收集资源指标来实现的。所以还是很重要的。

## 8.1 开启ApiServer聚合层

想要部署Metrics-Server必须先开启APIServer的聚合层（Kubeadm部署的K8s集群默认是已经实现了，所以可以跳过这一步，如果是二进制部署的，必须开启聚合层）

在 Kubernetes 1.7 版本引入了聚合层，允许第三方应用程序通过将自己注册到 kube-apiserver上，仍然通过 API Server 的 HTTP URL 对新的 API 进行访问和 操作。为了实现这个机制，Kubernetes 在 kube-apiserver 服务中引入了一个 API 聚合层（API Aggregation Layer），用于将扩展 API 的访问请求转发到用 户服务的功能。

**修改所有Master节点的kube-apiserver.conf配置文件开启聚合层**

```shell
#修改所有Master节点的Kube-apiserver.conf配置文件
#在原有的配置文件末尾加上如下配置开启聚合层
--requestheader-client-ca-file=/opt/kubernetes/ssl/ca.pem \
--proxy-client-cert-file=/opt/kubernetes/ssl/server.pem \
--proxy-client-key-file=/opt/kubernetes/ssl/server-key.pem \
--requestheader-allowed-names=kubernetes \
--requestheader-extra-headers-prefix=X-Remote-Extra- \
--requestheader-group-headers=X-Remote-Group \
--requestheader-username-headers=X-Remote-User \
--enable-aggregator-routing=true"
```

**注意**：

kube-apiserver.conf配置文件中的参数是通过双引号包起来的，所以添加如上配置之后，需要将原来的双引号移动到``enable-aggregator-routing=true``的后面（PS：这个坑我整整踩了两天）

如上配置引入的证书地址也需要注意下

**如修改之前的样子如下**

注意看红框的双引号

![image-20210419125850284](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210419125850284.png)

**如修改之后的样子如下**

注意看黄框的双引号

![image-20210419125958327](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210419125958327.png)

**重启所有Master节点的kube-apiserver**

```shell
#重启所有Master节点的kube-apiserver
systemctl restart kube-apiserver
```



## 8.2 所有Master节点部署kube-proxy、kubelet、CNI

其实就是所有Master节点部署Node节点上的所有组件。这一步就不具体演示了，大家直接按照 [``七. Work Node扩容``](https://github.com/EayonLee/JavaGod/tree/main/9%E9%98%B6%E6%AE%B5%EF%BC%9ADocker%E5%AE%B9%E5%99%A8%20%26%20CICD%20%26%20DevOps%20%26apm/02%E6%A8%A1%E5%9D%97%EF%BC%9AK8s%E5%AE%B9%E5%99%A8%E7%BC%96%E6%8E%92%E7%B3%BB%E7%BB%9F/01.%20Kubernetes%E4%BA%8C%E8%BF%9B%E5%88%B6%E9%AB%98%E5%8F%AF%E7%94%A8%E9%83%A8%E7%BD%B2) 那一章节去操作即可。

（kubeadm部署的可以省略该步骤，二进制部署的不可以省略）



## 8.3 创建Mertrics-Server资源清单

随便在一个Master节点的任意目录创建即可，但最好将其K8s所有基础组件的YAML文件进行统一的归档

```shell
#创建metrics-server资源文件
vim metrics-server.yaml
#文件内容如下
apiVersion: v1
kind: ServiceAccount
metadata:
  labels:
    k8s-app: metrics-server
  name: metrics-server
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  labels:
    k8s-app: metrics-server
    rbac.authorization.k8s.io/aggregate-to-admin: "true"
    rbac.authorization.k8s.io/aggregate-to-edit: "true"
    rbac.authorization.k8s.io/aggregate-to-view: "true"
  name: system:aggregated-metrics-reader
rules:
- apiGroups:
  - metrics.k8s.io
  resources:
  - pods
  - nodes
  verbs:
  - get
  - list
  - watch
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  labels:
    k8s-app: metrics-server
  name: system:metrics-server
rules:
- apiGroups:
  - ""
  resources:
  - pods
  - nodes
  - nodes/stats
  - namespaces
  - configmaps
  verbs:
  - get
  - list
  - watch
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  labels:
    k8s-app: metrics-server
  name: metrics-server-auth-reader
  namespace: kube-system
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: extension-apiserver-authentication-reader
subjects:
- kind: ServiceAccount
  name: metrics-server
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  labels:
    k8s-app: metrics-server
  name: metrics-server:system:auth-delegator
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: system:auth-delegator
subjects:
- kind: ServiceAccount
  name: metrics-server
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  labels:
    k8s-app: metrics-server
  name: system:metrics-server
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: system:metrics-server
subjects:
- kind: ServiceAccount
  name: metrics-server
  namespace: kube-system
---
apiVersion: v1
kind: Service
metadata:
  labels:
    k8s-app: metrics-server
  name: metrics-server
  namespace: kube-system
spec:
  ports:
  - name: https
    port: 443
    protocol: TCP
    targetPort: https
  selector:
    k8s-app: metrics-server
---
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    k8s-app: metrics-server
  name: metrics-server
  namespace: kube-system
spec:
  selector:
    matchLabels:
      k8s-app: metrics-server
  strategy:
    rollingUpdate:
      maxUnavailable: 0
  template:
    metadata:
      labels:
        k8s-app: metrics-server
    spec:
      containers:
      - args:
        - --cert-dir=/tmp
        - --secure-port=4443
        - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
        - --kubelet-use-node-status-port
        - --kubelet-insecure-tls
        image: lizhenliang/metrics-server:v0.4.1
        imagePullPolicy: IfNotPresent
        livenessProbe:
          failureThreshold: 3
          httpGet:
            path: /livez
            port: https
            scheme: HTTPS
          periodSeconds: 10
        name: metrics-server
        ports:
        - containerPort: 4443
          name: https
          protocol: TCP
        readinessProbe:
          failureThreshold: 3
          httpGet:
            path: /readyz
            port: https
            scheme: HTTPS
          periodSeconds: 10
        securityContext:
          readOnlyRootFilesystem: true
          runAsNonRoot: true
          runAsUser: 1000
        volumeMounts:
        - mountPath: /tmp
          name: tmp-dir
      nodeSelector:
        kubernetes.io/os: linux
      priorityClassName: system-cluster-critical
      serviceAccountName: metrics-server
      volumes:
      - emptyDir: {}
        name: tmp-dir
---
apiVersion: apiregistration.k8s.io/v1
kind: APIService
metadata:
  labels:
    k8s-app: metrics-server
  name: v1beta1.metrics.k8s.io
spec:
  group: metrics.k8s.io
  groupPriorityMinimum: 100
  insecureSkipTLSVerify: true
  service:
    name: metrics-server
    namespace: kube-system
  version: v1beta1
  versionPriority: 100
```

> --kubelet-insecure-tls #表示不验证客户端的证书
>
> --kubelet-preferred-address-types=InternalIP #metrics-server是通过主机名来区分主机的,所以说必须要配置 host解析，metrics-server才能正确的采集到目标，而DNS我们没有配置，所以这里要通过IP访问

**应用**

```shell
kubectl apply -f metrics-server.yaml
```



**测试**

```shell
#查看Node
k8s@yf-k8s-160005:~$ kubectl top node
NAME            CPU(cores)   CPU%   MEMORY(bytes)   MEMORY%   
yf-k8s-160005   511m         0%     5767Mi          4%        
yf-k8s-160006   372m         0%     5591Mi          4%        
yf-k8s-160007   408m         0%     10168Mi         7%     

#查看Pod
k8s@yf-k8s-160005:~$ kubectl top pod
NAME                   CPU(cores)   MEMORY(bytes)   
busybox                0m           0Mi             
nginx                  0m           6Mi             
web-5dcb957ccc-mnqvs   0m           6Mi
```





**多说一嘴**：

> **本章相关代码及笔记地址：**[**飞机票🚀**](https://github.com/EayonLee/JavaGod/tree/main/9%E9%98%B6%E6%AE%B5%EF%BC%9ADocker%E5%AE%B9%E5%99%A8%20%26%20CICD%20%26%20DevOps%20%26apm/02%E6%A8%A1%E5%9D%97%EF%BC%9AK8s%E5%AE%B9%E5%99%A8%E7%BC%96%E6%8E%92%E7%B3%BB%E7%BB%9F/02.%20Kubernetes%E5%BF%AB%E9%80%9F%E5%AE%9E%E6%88%98)
>
> 🌍Github：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://github.com/EayonLee/JavaGod)
> 🪐CSDN：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://blog.csdn.net/qq_20492277/article/details/114269863)







