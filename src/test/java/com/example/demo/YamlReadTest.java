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
import java.util.List;

public class YamlReadTest {


    @Test
    public void testReadYaml() {
        final String path = "/Users/Yang/workstation/sharding-demo/src/main/resources/datasource-sharding.yaml";
        YamlRootConfiguration unmarshal;
        try {
            unmarshal = YamlEngine.unmarshal(new File(path), YamlRootConfiguration.class);
            System.out.println(unmarshal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testp() {
        String path = "/Users/Yang/workstation/sharding-demo/src/test/resources/prcatice.yaml";
        Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(Car.class, "!car"));
        Yaml yaml = new Yaml(constructor);
        try (FileInputStream in = new FileInputStream(path)) {
            Object load = yaml.load(in);
            System.out.println(load);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class Car {

    public String plate;
    public List<Wheel> wheels;

    @Override
    public String toString() {
        return "Car{" +
                "plate='" + plate + '\'' +
                ", wheel=" + wheels +
                '}';
    }
}

class Wheel {
    public Integer id;

    public Dice dice;

    @Override
    public String toString() {
        return "Wheel{" +
                "id=" + id +
                ", dice=" + dice +
                '}';
    }
}

class Dice {
    public Integer a;
    public Integer b;

    @Override
    public String toString() {
        return "Dice{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}

class ShardingSphereYamlConstructor extends Constructor {

    public ShardingSphereYamlConstructor(Class theRoot) throws ClassNotFoundException {
        super(theRoot, new LoaderOptions() {
            @Override
            public void setCodePointLimit(int codePointLimit) {
                super.setCodePointLimit(Integer.MAX_VALUE);
            }
        });
        TypeDescription typeDescription = new TypeDescription(YamlReadwriteSplittingRuleConfiguration.class, "!READWRITE_SPLITTING");
        typeDescription.addPropertyParameters("dataSources", YamlReadwriteSplittingDataSourceRuleConfiguration.class, Object.class);
        typeDescription.addPropertyParameters("staticStrategy", YamlStaticReadwriteSplittingStrategyConfiguration.class, Object.class);
        addTypeDescription(typeDescription);
//        setPropertyUtils(PropertyUtils);
    }
}
