package ece;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ScratchForArduinoController {
    @FXML private ToggleGroup BlockToggleGroup;
    @FXML private Pane drawingPane;

    public void initialize(){
        System.out.println("TEST");
        Head block1 = new Head();
        block1.setLayoutX(100);
        block1.setLayoutY(100);
        Head head2 = new Head();
        head2.setLayoutX(100);
        head2.setLayoutY(100);
        drawingPane.getChildren().add(block1);
        drawingPane.getChildren().add(head2);
        drawingPane.getChildren().add(new Rectangle(100,100, Color.BLUE));

    }
}
