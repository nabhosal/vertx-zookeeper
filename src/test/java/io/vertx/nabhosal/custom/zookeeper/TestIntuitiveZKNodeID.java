package io.vertx.nabhosal.custom.zookeeper;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;

public class TestIntuitiveZKNodeID {

  public void basicWorking(){
    JsonObject zkConfig = new JsonObject();
    zkConfig.put("zookeeperHosts", "127.0.0.1");
    zkConfig.put("rootPath", "io.vertx");
    zkConfig.put("retry", new JsonObject()
      .put("initialSleepTime", 3000)
      .put("maxTimes", 3));

    ClusterManager mgr = new ZookeeperClusterManager(zkConfig);
    VertxOptions vertxOptions = new VertxOptions().setClusterManager(mgr);

    Vertx.clusteredVertx(vertxOptions).setHandler(handler -> {
      if (handler.succeeded()){
        System.out.println("http started");
        handler.result().createHttpServer()
          .requestHandler(req -> {
            req.response().end("hello !");
          })
          .listen(8080);
      }else if(handler.failed()){
        System.out.println("http failed");
      }
    });
  }

  public static void main(String... args) {

    System.setProperty("vertx.zookeeper.node.prefix", "servicePrefix");
    TestIntuitiveZKNodeID intuitiveZKNodeID = new TestIntuitiveZKNodeID();
    intuitiveZKNodeID.basicWorking();
  }
  /**
   * =============== On Zookeeper Server ====================
   * nilesh@CVHYD084:~/zookeeper/apache-zookeeper-3.5.5-bin$ bin/zkCli.sh -server 127.0.0.1:2181
   * /usr/bin/java
   * Connecting to 127.0.0.1:2181
   *
   * [zk: 127.0.0.1:2181(CONNECTED) 32] ls /io.vertx/cluster/nodes
   * [127.0.1.1_857]
   * [zk: 127.0.0.1:2181(CONNECTED) 33] ls /io.vertx/cluster/nodes
   * [127.0.1.1_857, servicePrefix_127.0.1.1_207]
   * [zk: 127.0.0.1:2181(CONNECTED) 34]
   *
   */
}
