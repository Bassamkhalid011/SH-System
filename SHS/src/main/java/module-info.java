module com.example.shs {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;


    opens com.example.shs to javafx.fxml;
    exports com.example.shs;
}