module com.example.whatever {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.whatever to javafx.fxml;
    exports com.example.whatever;
}