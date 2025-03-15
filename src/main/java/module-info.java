module org.example.socialnetworkfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // if you are using SQL
    requires org.jgrapht.core; // if you are using JGraphT
    requires org.postgresql.jdbc; // if you are using PostgreSQL

    opens org.example.socialnetworkfx to javafx.fxml;
    exports org.example.socialnetworkfx;
    exports org.example.socialnetworkfx.controller;
    opens org.example.socialnetworkfx.controller to javafx.fxml;
}