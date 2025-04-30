module com.example.gerenteapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
        requires java.desktop;
    requires org.json;
        requires org.apache.commons.net;
    requires net.sf.jasperreports.core;

    opens com.example.gerenteapp to javafx.fxml;
    exports com.example.gerenteapp;
}