package com.barinek.gnip;

import java.sql.SQLException;

public class SchemaCreator {
    public static void main(String[] args) throws SQLException {
        KeywordDAO.createAndSeedTable();
    }
}
