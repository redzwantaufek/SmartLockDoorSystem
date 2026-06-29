module com.group6 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.group6 to javafx.fxml;
    exports com.group6;
}
