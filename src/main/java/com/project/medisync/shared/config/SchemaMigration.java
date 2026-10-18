package com.project.medisync.shared.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class SchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        addColumnIfMissing("medecin", "supprime", "BOOLEAN NOT NULL DEFAULT false");
    }

    private void addColumnIfMissing(String table, String column, String definition) {
        try {
            boolean exists = Boolean.TRUE.equals(jdbc.queryForObject(
                    "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = ? AND column_name = ?)",
                    Boolean.class, table, column));
            if (!exists) {
                jdbc.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
                log.info("[Migration] Colonne {}.{} créée.", table, column);
            }
        } catch (Exception e) {
            log.warn("[Migration] Impossible d'ajouter {}.{} : {}", table, column, e.getMessage());
        }
    }
}
