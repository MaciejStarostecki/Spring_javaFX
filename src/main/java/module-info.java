module pl.strefakursow.spring_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires lombok;
    requires spring.web;

    opens pl.strefakursow.spring_javafx to javafx.fxml;
    exports pl.strefakursow.spring_javafx;
    exports pl.strefakursow.spring_javafx.controller;
    opens pl.strefakursow.spring_javafx.controller to javafx.fxml;
    exports pl.strefakursow.spring_javafx.dto;
    opens pl.strefakursow.spring_javafx.dto to javafx.fxml;
    exports pl.strefakursow.spring_javafx.table;
    opens pl.strefakursow.spring_javafx.table to javafx.fxml;
    exports pl.strefakursow.spring_javafx.rest;
    opens pl.strefakursow.spring_javafx.rest to javafx.fxml;
}