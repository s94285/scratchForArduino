package ece;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.*;
import javafx.scene.Parent;

import java.awt.*;
import java.awt.event.InputEvent;

public class ScratchForArduinoController {
    @FXML private ToggleGroup BlockToggleGroup;
    @FXML private Pane drawingPane;
    @FXML private RadioButton controlsButton,operatorsButton,arduinoButton;
    @FXML private AnchorPane blockPane;
    enum BlockClass{CONTROLS,OPERATORS,ARDUINO}
    private BlockClass currentBlockClass = BlockClass.CONTROLS;
    private  Pane selectedOperatorsPane,selectedRobotPane;
    private Robot robot;
    public void initialize(){
        controlsButton.setUserData(BlockClass.CONTROLS);
        operatorsButton.setUserData(BlockClass.OPERATORS);
        arduinoButton.setUserData(BlockClass.ARDUINO);
        try {
            robot = new Robot();
        }catch (AWTException e) {
            e.printStackTrace();
        }
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
        ControlBlock controlBlock=new ControlBlock("if %n then","controlblock",drawingPane);
        drawingPane.getChildren().add(controlBlock);
        controlBlock.setLayoutX(200);
        controlBlock.setLayoutY(200);
        selectedOperatorsPane=new Pane();
        ValueBlock valueBlock=new ValueBlock("%n + %n","valueAdd",blockPane){
            @Override
            public void onMousePressed(MouseEvent mouseEvent) {
                //super.onMousePressed(mouseEvent);
                System.out.println("Mouse Entered on Click Me Two");
                ValueBlock valueBlock1=new ValueBlock("%n + %n","valueAdd",ScratchForArduinoController.this.drawingPane);
                ScratchForArduinoController.this.drawingPane.getChildren().add(valueBlock1);
                Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(),mouseEvent.getSceneY()));
//                System.out.println("scene"+mouseEvent.getSceneX()+mouseEvent.getSceneY());
//                System.out.println("scenetoLocal"+scenePoint.getX()+scenePoint.getY());
//                System.out.println("mouse"+mouseEvent.getX()+mouseEvent.getY());
                valueBlock1.setLayoutY(scenePoint.getY()-mouseEvent.getY());
                valueBlock1.setLayoutX(scenePoint.getX()-mouseEvent.getX());

                if(robot!=null){

                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseMove((int)mouseEvent.getScreenX(),(int)mouseEvent.getScreenY());
                    robot.delay(50);
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                }

            }
            @Override
            public void onMouseDragged(MouseEvent mouseEvent){

            }

        };
        selectedOperatorsPane.getChildren().add(valueBlock);
        blockPane.getChildren().add(selectedOperatorsPane);
        valueBlock.setLayoutY(10);
        valueBlock.setLayoutX(10);
        selectedOperatorsPane.setVisible(false);

        selectedRobotPane=new Pane();
        StatementBlock statementBlock=new StatementBlock("abc %n cde %n","statementblock",blockPane);
        statementBlock.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent e){
                System.out.println("Mouse Entered on Click Me ");
            }
        });
        statementBlock.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent e){

                System.out.println("Mouse Entered on Click Me ");
            }
        });
        selectedRobotPane.getChildren().add(statementBlock);
        blockPane.getChildren().add(selectedRobotPane);
        statementBlock.setLayoutX(10);
        statementBlock.setLayoutY(10);
        selectedRobotPane.setVisible(false);
        selectedBlock(currentBlockClass);
    }
    @FXML
    private void typeClicked(ActionEvent e){
        currentBlockClass=(BlockClass) BlockToggleGroup.getSelectedToggle().getUserData();
        selectedBlock(currentBlockClass);
    }
    private  void selectedBlock(BlockClass currentBlockClass){
        if(currentBlockClass==BlockClass.CONTROLS){
            System.out.println("clicked 2 Controls");
            selectedRobotPane.setVisible(false);
            selectedOperatorsPane.setVisible(true);
        }
        else if(currentBlockClass==BlockClass.OPERATORS){

        }
        else if(currentBlockClass==BlockClass.ARDUINO){
            System.out.println("clicked 2 Arduino");
            selectedOperatorsPane.setVisible(false);
            selectedRobotPane.setVisible(true);
        }

    }

}
