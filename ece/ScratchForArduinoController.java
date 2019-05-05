package ece;

import ece.block.arduino.Head;
import ece.block.operators.ValueBlock;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class ScratchForArduinoController {
    @FXML private ToggleGroup BlockToggleGroup;
    @FXML private Pane drawingPane;
    @FXML private RadioButton controlsButton,operatorsButton,arduinoButton;
    enum BlockClass{CONTROLS,OPERATORS,ARDUINO}
    private BlockClass currentBlockClass = BlockClass.CONTROLS;
    public void initialize(){
        controlsButton.setUserData(BlockClass.CONTROLS);
        operatorsButton.setUserData(BlockClass.OPERATORS);
        arduinoButton.setUserData(BlockClass.ARDUINO);
        System.out.println("TEST");
        Head block1 = new Head("Arduino Program","headBlock",drawingPane);
        block1.setLayoutX(100);
        block1.setLayoutY(100);
        Head head2 = new Head("Arduino Program","headBlock",drawingPane);
        head2.setLayoutX(100);
        head2.setLayoutY(100);

        drawingPane.getChildren().add(block1);
        drawingPane.getChildren().add(head2);
        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
        drawingPane.getChildren().add(new ValueBlock("%n - %n + %n","valueAdd",drawingPane));
        drawingPane.getChildren().add(new ValueBlock("abc","valueAdd",drawingPane));

        //drawingPane.getChildren().add(new Rectangle(100,100, Color.BLUE));

    }
    @FXML
    private void typeClicked(ActionEvent e){
        currentBlockClass=(BlockClass) BlockToggleGroup.getSelectedToggle().getUserData();
    }
}
