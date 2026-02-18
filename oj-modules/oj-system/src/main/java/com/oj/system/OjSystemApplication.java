package com.oj.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.oj.**.mapper")
/*@ComponentScan(basePackages = {
        "com.oj.system",       // 你的业务包
        "com.oj.common"        // 新增：RedisService 所在的上层包
})*/
public class OjSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(OjSystemApplication.class,args);
    }
}
