package pl.cafebabe.jug.hadoop.hbase;

import static pl.cafebabe.jug.hadoop.commons.SQLUtils.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class HBasePhoenixSample {

    public static void main(String[] args) throws SQLException {
        // Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        try (
                Connection connection = DriverManager.getConnection("jdbc:phoenix:sandbox.hortonworks.com:/hbase-unsecure");
                Statement stmt = connection.createStatement()) {

            // https://stackoverflow.com/questions/39974877/create-view-in-apache-phoenix-error-505
            /**
             * Poniższy zapytanie
             * CREATE VIEW odczyty (pk VARCHAR PRIMARY KEY, cf.nrl VARCHAR, cf.zuzycie VARCHAR)
             * to odpowiednik
             * CREATE VIEW ODCZYTY (PK VARCHAR PRIMARY KEY, CF.NRL VARCHAR, CF.ZUZYCIE VARCHAR)
             * i
             * create 'ODCZYTY', 'CF'
             *
             * Dla
             * create 'odczyty', 'cf'
             * musimy wykonać
             * CREATE VIEW "odczyty" (pk VARCHAR PRIMARY KEY, "cf"."nrl" VARCHAR, "cf"."zuzycie" VARCHAR)
             * i
             * SELECT count(*) FROM "odczyty"
             */

            // http://phoenix.apache.org/language/
            // https://phoenix.apache.org/language/functions.html

            // DDL
            // utworzenie widoku na istniejącej tabeli HBase
            //stmt.execute("DROP VIEW IF EXISTS odczyty");
            stmt.executeUpdate("CREATE VIEW IF NOT EXISTS odczyty (pk VARCHAR PRIMARY KEY, cf.nrl VARCHAR, cf.zuzycie VARCHAR)");

            // DML
            // a na tabeli moglibyśmy wykonać INSERT/UPDATE
            // stmt.execute(String.format("UPSERT INTO odczyty VALUES ('%s', '%s', '%s')", UUID.randomUUID().toString(), "nr 17", "64"));
            print(stmt.executeQuery("SELECT count(*) FROM odczyty"));
            print(stmt.executeQuery("SELECT * FROM odczyty WHERE nrl = 'nr 17'"));
            print(stmt.executeQuery("SELECT sum(to_number(zuzycie)) AS zuzycie FROM odczyty WHERE nrl = 'nr 17'"));
        }
    }

}