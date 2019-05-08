package ece;

import ece.block.arduino.Head;
import ece.block.arduino.StatementBlock;
import ece.block.operators.ValueBlock;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;

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
        block1.setLayoutY(50);

        drawingPane.getChildren().add(block1);
        for(int i=0;i<10;i++){
            ValueBlock valueBlock=new ValueBlock("%n + %n","valueAdd",drawingPane);
            drawingPane.getChildren().add(valueBlock);
            valueBlock.setLayoutX(10);
            valueBlock.setLayoutY(10+i*50);

        }
        for(int i=0;i<5;i++){
            StatementBlock statementBlock=new StatementBlock("abc %n cde %n","statementblock",drawingPane);
            drawingPane.getChildren().add(statementBlock);
            statementBlock.setLayoutX(100);
            statementBlock.setLayoutY(100+i*50);
        }
//        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
//        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
//        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
//        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
//        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
//        drawingPane.getChildren().add(new ValueBlock("%n + %n","valueAdd",drawingPane));
//        drawingPane.getChildren().add(new ValueBlock("%n - %n + %n","valueAdd",drawingPane));
//        drawingPane.getChildren().add(new ValueBlock("abc","valueAdd",drawingPane));

//        drawingPane.getChildren().add(new StatementBlock("abc %n cde %n","statementblock",drawingPane));
//        drawingPane.getChildren().add(new StatementBlock("abc %n cde %n","statementblock",drawingPane));

    }
    @FXML
    private void typeClicked(ActionEvent e){
        currentBlockClass=(BlockClass) BlockToggleGroup.getSelectedToggle().getUserData();
    }
}
