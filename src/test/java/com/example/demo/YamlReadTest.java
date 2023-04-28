package com.example.demo;

import org.apache.shardingsphere.infra.util.yaml.YamlEngine;
import org.apache.shardingsphere.infra.yaml.config.pojo.YamlRootConfiguration;
import org.apache.shardingsphere.readwritesplitting.yaml.config.YamlReadwriteSplittingRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.yaml.config.rule.YamlReadwriteSplittingDataSourceRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.yaml.config.strategy.YamlStaticReadwriteSplittingStrategyConfiguration;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class YamlReadTest {


    @Test
    public void testReadYaml() {
        final String path = "/Users/Yang/workstation/sharding-demo/src/main/resources/datasource.yaml";

        YamlRootConfiguration unmarshal;
        try {
            unmarshal = YamlEngine.unmarshal(new File(path), YamlRootConfiguration.class);
            System.out.println(unmarshal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testYamlReading() {
        final String path = "/Users/Yang/workstation/sharding-demo/src/main/resources/datasource.yaml";
        Yaml yaml = null;
        try {
            yaml = new Yaml(new ShardingSphereYamlConstructor(YamlRootConfiguration.class));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (FileInputStream in = new FileInputStream(path)) {
            Object load = yaml.load(in);
            System.out.println(load);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}

class ShardingSphereYamlConstructor extends Constructor {

    public ShardingSphereYamlConstructor(Class theRoot) throws ClassNotFoundException {
        TypeDescription root = new TypeDescription(theRoot);
        TypeDescription typeDescription = new TypeDescription(YamlReadwriteSplittingRuleConfiguration.class, "!READWRITE_SPLITTING");
//        root.addPropertyParameters("!READWRITE_SPLITTING", YamlReadwriteSplittingRuleConfiguration.class);
//        TypeDescription typeDescription = new TypeDescription(YamlReadwriteSplittingRuleConfiguration.class, "!READWRITE_SPLITTING");
//        typeDescription.addPropertyParameters("dataSources", YamlReadwriteSplittingDataSourceRuleConfiguration.class);
//        typeDescription.addPropertyParameters("staticStrategy", YamlStaticReadwriteSplittingStrategyConfiguration.class);
        addTypeDescription(root);
        addTypeDescription(typeDescription);
    }
}
