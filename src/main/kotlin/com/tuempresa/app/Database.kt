package com.tuempresa.app

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object Database {

    private val config = HikariConfig().apply {

        jdbcUrl = "jdbc:sqlserver://localhost:1433;databaseName=FormularioDB;encrypt=false"
        username = "sa"
        password = "12345"

        driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        maximumPoolSize = 5
    }

    private val ds = HikariDataSource(config)

    fun getConnection(): Connection {
        return ds.connection
    }
}
