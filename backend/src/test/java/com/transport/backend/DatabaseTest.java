package com.transport.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class DatabaseTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void testVersionPostgre(){
        String response = jdbcTemplate.queryForObject("select version()", String.class);
        assertThat(response).startsWith("PostgreSQL 18");
    }
}
