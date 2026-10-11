package com.coffeekartel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class CoffeeKartelApplication {

    public static void main(String[] args) {
        normalizeDbUrl();
        loadDotEnv();
        SpringApplication.run(CoffeeKartelApplication.class, args);
    }

    private static void normalizeDbUrl() {
        String envDbUrl = System.getenv("DB_URL");
        if (envDbUrl != null && envDbUrl.startsWith("mysql://")) {
            System.setProperty("DB_URL", "jdbc:" + envDbUrl);
        }
    }

    private static void loadDotEnv() {
        File[] candidates = {
            new File(".env"),
            new File("backend/.env"),
            new File("../backend/.env")
        };

        for (File file : candidates) {
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String value = line.substring(eqIdx + 1).trim();
                            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                                (value.startsWith("'") && value.endsWith("'"))) {
                                value = value.substring(1, value.length() - 1);
                            }
                            if (System.getProperty(key) == null && System.getenv(key) == null) {
                                System.setProperty(key, value);
                            }
                        }
                    }
                } catch (IOException ignored) {
                }
                break;
            }
        }
    }
}

