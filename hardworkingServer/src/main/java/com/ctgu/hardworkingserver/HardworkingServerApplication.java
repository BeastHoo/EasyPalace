package com.ctgu.hardworkingserver;

import com.ctgu.hardworkingserver.utils.Node;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EnableScheduling
public class HardworkingServerApplication {

    public static void main(String[] args) throws Exception {
//        ConfigurableApplicationContext run =
        SpringApplication.run(HardworkingServerApplication.class, args);
        System.setProperty("sun.jnu.encoding","utf-8");

        System.out.println("FileEcoding:  "+System.getProperty("file.encoding"));
        System.out.println("Encoding:::: "+System.getProperty("sun.jnu.encoding"));
//        String [] names=run.getBeanDefinitionNames();
//        for (String name : names) {
//            System.out.println(name);
//        }
//
//        boolean user = run.containsBean("TreeRoot");
//        Node treeRoot = (Node) run.getBean("TreeRoot");
//        System.out.println("nODE组件："+user + " " +treeRoot.toString());
    }

}
