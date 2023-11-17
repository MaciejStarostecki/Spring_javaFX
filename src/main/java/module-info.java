module pl.strefakursow.spring_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens pl.strefakursow.spring_javafx to javafx.fxml;
    exports pl.strefakursow.spring_javafx;
}