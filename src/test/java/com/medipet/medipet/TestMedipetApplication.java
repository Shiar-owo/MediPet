package com.medipet.medipet;

import org.springframework.boot.SpringApplication;

public class TestMedipetApplication {

    public static void main(String[] args) {
        SpringApplication.from(MedipetApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
