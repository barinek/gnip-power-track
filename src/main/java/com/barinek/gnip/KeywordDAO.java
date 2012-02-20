package com.barinek.gnip;

import com.google.common.collect.Lists;

import java.sql.*;
import java.util.List;

public class KeywordDAO {
    private static String databaseUrl;

    static {
        databaseUrl = System.getenv("DATABASE_URL");
        databaseUrl = databaseUrl.replaceAll("postgres://(.*):(.*)@(.*)", "jdbc:postgresql://$3?user=$1&password=$2");
    }

    public List<Keyword> getAll() throws SQLException {
        List<Keyword> keywords = Lists.newArrayList();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrl);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id, keyword, count, created_at, updated_at from keywords order by keyword asc");
            while (resultSet.next()) {
                keywords.add(new Keyword(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getTimestamp(4), resultSet.getTimestamp(5)));
            }
        } finally {
            if (connection != null) connection.close();
        }
        return keywords;
    }

    public List<Keyword> getTop10() throws SQLException {
        List<Keyword> keywords = Lists.newArrayList();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrl);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id, keyword, count, created_at, updated_at from keywords order by count desc limit 10");
            while (resultSet.next()) {
                keywords.add(new Keyword(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getTimestamp(4), resultSet.getTimestamp(5)));
            }
        } finally {
            if (connection != null) connection.close();
        }
        return keywords;
    }

    public void incrementCount(String keyword) throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrl);
            Statement statement = connection.createStatement();
            statement.executeUpdate("update keywords set count = count + 1, updated_at = now() where keyword = '" + keyword + "'");
        } finally {
            if (connection != null) connection.close();
        }
    }

    public static void createAndSeedTable() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrl);

            Statement dropStatement = connection.createStatement();
            dropStatement.executeUpdate("drop table if exists keywords");

            Statement createStatement = connection.createStatement();
            createStatement.executeUpdate("create table keywords (id serial primary key, keyword character varying(255), count integer, created_at timestamp, updated_at timestamp)");
            createStatement.executeUpdate("create unique index keyword_idx on keywords (keyword)");

            for (String knownCity : WebApplication.knownCities) {
                Statement insertStatement = connection.createStatement();
                insertStatement.executeUpdate("insert into keywords (keyword, count, created_at, updated_at) values ('" + knownCity + "', 0, now(), now())");
            }
        } finally {
            if (connection != null) connection.close();
        }
    }
}
