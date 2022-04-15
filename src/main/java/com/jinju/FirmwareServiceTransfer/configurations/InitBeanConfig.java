package com.jinju.FirmwareServiceTransfer.configurations;

import com.jinju.FirmwareServiceTransfer.beans.DiskInfo;
//import com.jinju.FirmwareServiceTransfer.lifeCycle.BeanPost;
import com.jinju.FirmwareServiceTransfer.utils.FlowLimiter;
import com.jinju.FirmwareServiceTransfer.utils.Util;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Configuration
public class InitBeanConfig {

    @Autowired
    Util util;

    @Value("${basePath}")
    String basePath;

    @Value("${versionInfoFileName}")
    String versionInfoFileName;

    @Value("${firmwarePath}")
    String firmwarePath;

    @Value("${firmwareFilePath}")
    String firmwareFilePath;

    @Value("${configCapacity}")
    int flowLimiterCapacity;

    @Value("${configRate}")
    int flowLimiterRate;

    @Value("${zkHost}")
    String zkHost;

    @Value("${configVersion}")
    String configVersion;

    @Autowired
    ApplicationArguments applicationArguments;


    @Bean(name = "versionInfoMap")
    public Map<String, Object> versionInfoMap() throws FileNotFoundException {
//        String path = System.getProperty("user.dir");
//        System.out.println("this project's path is :" + path + "------");
        InputStream input = new FileInputStream(basePath + "/" + versionInfoFileName);
        Yaml yaml = new Yaml();
        Map<String, Object> object = yaml.load(input);
        if (object == null) {
            object = new LinkedHashMap<>();
            object.put("none", "none");
        }
        return object;
    }

    @Bean(name = "yaml")
    public Yaml yaml() {
        return new Yaml();
    }

    @Bean(name = "zk")
    public ZooKeeper zooKeeper() {
        try {
            System.err.println("Connecting to zookeeper...");
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ZooKeeper zk = new ZooKeeper(this.zkHost, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            System.err.println("Zookeeper connected!");

            // init root
            try {
                zk.create("/root", "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (Exception ignored) {}

            // registry
            zk.create("/root/node", "1".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.err.println("Success to registry on zookeeper!");

            // get or create version node
            if(null == zk.exists("/root/version", false)){
                zk.create("/root/version", (System.currentTimeMillis()+"").getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }else{
                Watcher versionWatcher = new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if(event.getType() == Event.EventType.NodeDataChanged){
//                                byte[] version = zk.getData("/root/version", this, new Stat());
//                                configVersion = Arrays.toString(version);
                                System.err.println("detect changed...");
                                util.refreshDiskInfo();
                        }
                    }
                };
                byte[] version = zk.getData("/root/version", versionWatcher , new Stat());
//                configVersion = new String(version);
                System.err.println("init version");
            }
            System.err.println("Success to sync with zookeeper on version!");

            return zk;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean(name = "diskInfo")
    public DiskInfo diskInfo() {
        return util.refreshDiskInfo();
    }

    @Bean(name = "flowLimiter")
    public FlowLimiter flowLimiter() {
        int argCapacity = -1;
        int argRate = -1;
        try {
            argCapacity = Integer.parseInt(checkOutArg(applicationArguments, "flowLimiterCapacity"));
        } catch (Exception e) {
        }
        try {
            argRate = Integer.parseInt(checkOutArg(applicationArguments, "flowLimiterRate"));
        } catch (Exception e) {
        }
        int initCapacity = argCapacity == -1 ? this.flowLimiterCapacity : argCapacity;
        int initRate = argRate == -1 ? flowLimiterRate : argRate;
        System.err.println("init flow limitation --> capacity : " + initCapacity + ", rate: " + initRate);
        return new FlowLimiter(initCapacity, initRate);
    }

    String checkOutArg(ApplicationArguments applicationArguments, String argName) {
        String[] sourceArgs = applicationArguments.getSourceArgs();
        for (int i = 0; i < sourceArgs.length; i++) {
            if (sourceArgs[i].equals("-" + argName)) {
                if (i < sourceArgs.length) {
                    return sourceArgs[i + 1];
                }
                break;
            }
        }
        return "";
    }
}
