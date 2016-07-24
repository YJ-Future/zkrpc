# zkrpc
zkrpc是一个基于 ZooKeeper的 RPC 框架
##目标实现功能：
*1.基于Netty实现rpc通信
*2.基于Zookeeper的服务注册和服务发现
*3.基于Curator的zookeeper重连
*4.provider端(server)的重连重新注册服务
*5.consumer端(client)服务列表缓存，同步服务提供者上线和下线，更新本地列表缓存



