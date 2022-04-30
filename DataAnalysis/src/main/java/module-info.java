module badvision.dataanalysis {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires com.google.gson;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens badvision.dataanalysis to javafx.fxml;
    opens badvision.dataanalysis.model to com.google.gson, javafx.base;
    exports badvision.dataanalysis;    
}
