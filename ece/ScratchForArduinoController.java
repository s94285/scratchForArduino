package ece;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.codehaus.jackson.map.ObjectMapper;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ScratchForArduinoController {
    @FXML private ToggleGroup BlockToggleGroup;
    @FXML private Pane drawingPane;
    @FXML private RadioButton controlsButton,operatorsButton,arduinoButton;
    @FXML private AnchorPane blockPane;
    enum BlockClass{CONTROLS,OPERATORS,ARDUINO}
    private BlockClass currentBlockClass = BlockClass.CONTROLS;
    private VBox selectedOperatorsPane, selectedArduinoPane,selectedControlsPane;
    private Robot robot;
    public void initialize(){
        controlsButton.setUserData(BlockClass.CONTROLS);
        operatorsButton.setUserData(BlockClass.OPERATORS);
        arduinoButton.setUserData(BlockClass.ARDUINO);
        try {
            robot = new Robot();
        }catch (Exception e) {
            e.printStackTrace();
        }
        selectedOperatorsPane = new VBox();
        selectedArduinoPane = new VBox();
        selectedControlsPane = new VBox();
        selectedOperatorsPane.setSpacing(10);
        selectedOperatorsPane.setPadding(new Insets(10,10,10,10));
        selectedArduinoPane.setSpacing(10);
        selectedArduinoPane.setPadding(new Insets(10,10,10,10));
        selectedControlsPane.setSpacing(10);
        selectedControlsPane.setPadding(new Insets(10,10,10,10));
        selectedOperatorsPane.setVisible(false);
        selectedArduinoPane.setVisible(true);
        selectedControlsPane.setVisible(false);
        blockPane.getChildren().addAll(selectedControlsPane,selectedOperatorsPane, selectedArduinoPane);
        initializeBlocks();     //add blocks to left pane
        //finish interface initialize

        Head block1 = new Head(blockSpecBuilder("Arduino Program","headBlock"),drawingPane);
        block1.setLayoutX(100);
        block1.setLayoutY(50);

        drawingPane.getChildren().add(block1);
        for(int i=0;i<10;i++){
            ValueBlock valueBlock=new ValueBlock(blockSpecBuilder("%n + %n","valueAdd"),drawingPane);
            drawingPane.getChildren().add(valueBlock);
            valueBlock.setLayoutX(10);
            valueBlock.setLayoutY(10+i*50);

        }
        for(int i=0;i<5;i++){
            StatementBlock statementBlock=new StatementBlock(blockSpecBuilder("abc %n cde %n","statementblock"),drawingPane);
            drawingPane.getChildren().add(statementBlock);
            statementBlock.setLayoutX(100);
            statementBlock.setLayoutY(100+i*50);
        }
        ControlBlock controlBlock=new ControlBlock(blockSpecBuilder("if     %n     then","controlblock"),drawingPane);
        drawingPane.getChildren().add(controlBlock);
        controlBlock.setLayoutX(200);
        controlBlock.setLayoutY(200);
        ControlBlock controlBlock1=new ControlBlock(blockSpecBuilder("if     %n     then","controlblock"),drawingPane);
        drawingPane.getChildren().add(controlBlock1);
        controlBlock1.setLayoutX(250);
        controlBlock1.setLayoutY(250);

        IfandElseBlock ifandElseBlock=new IfandElseBlock(blockSpecBuilder("if     %n     then","statementblock"),drawingPane);
        drawingPane.getChildren().add(ifandElseBlock);
        ifandElseBlock.setLayoutX(300);
        ifandElseBlock.setLayoutY(300);

        ForeverLoopBlock foreverLoopBlock=new ForeverLoopBlock(blockSpecBuilder("Forever           ","statementblock"),drawingPane);
        drawingPane.getChildren().add(foreverLoopBlock);
        foreverLoopBlock.setLayoutX(350);
        foreverLoopBlock.setLayoutY(350);

        BooleanBlock booleanBlock=new BooleanBlock(blockSpecBuilder("%n + %n","valueAdd"),drawingPane);
        drawingPane.getChildren().add(booleanBlock);
        booleanBlock.setLayoutX(400);
        booleanBlock.setLayoutY(400);
        /*ValueBlock valueBlock=new ValueBlock(blockSpecBuilder("%n + %n","valueAdd"),blockPane){
            @Override
            public void onMousePressed(MouseEvent mouseEvent) {
                //super.onMousePressed(mouseEvent);
                System.out.println("Mouse Entered on Click Me Two");
                ValueBlock valueBlock1=new ValueBlock(blockSpecBuilder("%n + %n","valueAdd"),ScratchForArduinoController.this.drawingPane);
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
        selectedOperatorsPane.getChildren().add(valueBlock);*/

//        StatementBlock statementBlock=new StatementBlock(blockSpecBuilder("abc %n cde %n","statementblock"),blockPane);
//        statementBlock.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
//            @Override
//            public void handle(MouseEvent e){
//                System.out.println("Mouse Entered on Click Me ");
//            }
//        });
//        statementBlock.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){
//            @Override
//            public void handle(MouseEvent e){
//
//                System.out.println("Mouse Entered on Click Me ");
//            }
//        });
//        selectedArduinoPane.getChildren().add(statementBlock);
    }
    @FXML
    private void typeClicked(ActionEvent e){
        currentBlockClass=(BlockClass) BlockToggleGroup.getSelectedToggle().getUserData();
        selectedOperatorsPane.setVisible(currentBlockClass==BlockClass.OPERATORS);
        selectedControlsPane.setVisible(currentBlockClass==BlockClass.CONTROLS);
        selectedArduinoPane.setVisible(currentBlockClass==BlockClass.ARDUINO);
    }

    private void initializeBlocks(){
        //value blocks
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<BlockSpec> blockSpecList = Arrays.asList(objectMapper.readValue(new File(getClass().getResource("json/valueBlocks.json").toURI()), BlockSpec[].class));
            for(BlockSpec blockSpec : blockSpecList) {
                System.out.println(blockSpec);
                ValueBlock valueBlock = new ValueBlock(blockSpec, blockPane) {
                    @Override
                    public void onMousePressed(MouseEvent mouseEvent) {
                        //super.onMousePressed(mouseEvent);
                        System.out.println("Mouse Entered on Click Me Two");
                        ValueBlock valueBlock1 = new ValueBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                        ScratchForArduinoController.this.drawingPane.getChildren().add(valueBlock1);
                        Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
//                System.out.println("scene"+mouseEvent.getSceneX()+mouseEvent.getSceneY());
//                System.out.println("scenetoLocal"+scenePoint.getX()+scenePoint.getY());
//                System.out.println("mouse"+mouseEvent.getX()+mouseEvent.getY());
                        valueBlock1.setLayoutY(scenePoint.getY() - mouseEvent.getY());
                        valueBlock1.setLayoutX(scenePoint.getX() - mouseEvent.getX());

                        if (robot != null) {

                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseMove((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
                            robot.delay(50);
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        }

                    }

                    @Override
                    public void onMouseDragged(MouseEvent mouseEvent) {

                    }

                };
                selectedOperatorsPane.getChildren().add(valueBlock);
            }
        }catch(Exception e){e.printStackTrace();}

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<BlockSpec> blockSpecList = Arrays.asList(objectMapper.readValue(new File(getClass().getResource("json/statementBlocks.json").toURI()), BlockSpec[].class));
            for(BlockSpec blockSpec : blockSpecList) {
                System.out.println(blockSpec);
                StatementBlock statementBlock = new StatementBlock(blockSpec, blockPane) {
                    @Override
                    public void onMousePressed(MouseEvent mouseEvent) {
                        //super.onMousePressed(mouseEvent);
                        System.out.println("Mouse Entered on Click Me Two");
                        StatementBlock statementBlock1= new StatementBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                        ScratchForArduinoController.this.drawingPane.getChildren().add(statementBlock1);
                        Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
//                System.out.println("scene"+mouseEvent.getSceneX()+mouseEvent.getSceneY());
//                System.out.println("scenetoLocal"+scenePoint.getX()+scenePoint.getY());
//                System.out.println("mouse"+mouseEvent.getX()+mouseEvent.getY());
                        statementBlock1.setLayoutY(scenePoint.getY() - mouseEvent.getY());
                        statementBlock1.setLayoutX(scenePoint.getX() - mouseEvent.getX());

                        if (robot != null) {

                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseMove((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
                            robot.delay(50);
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        }

                    }

                    @Override
                    public void onMouseDragged(MouseEvent mouseEvent) {

                    }

                };
                selectedArduinoPane.getChildren().add(statementBlock);
            }
        }catch(Exception e){e.printStackTrace();}
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<BlockSpec> blockSpecList = Arrays.asList(objectMapper.readValue(new File(getClass().getResource("json/controlBlocks.json").toURI()), BlockSpec[].class));
            for(BlockSpec blockSpec : blockSpecList) {
                System.out.println(blockSpec);
                ControlBlock controlBlock = new ControlBlock(blockSpec, blockPane) {
                    @Override
                    public void onMousePressed(MouseEvent mouseEvent) {
                        //super.onMousePressed(mouseEvent);
                        System.out.println("Mouse Entered on Click Me Two");
                        ControlBlock controlBlock1= new ControlBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                        ScratchForArduinoController.this.drawingPane.getChildren().add(controlBlock1);
                        Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
//                System.out.println("scene"+mouseEvent.getSceneX()+mouseEvent.getSceneY());
//                System.out.println("scenetoLocal"+scenePoint.getX()+scenePoint.getY());
//                System.out.println("mouse"+mouseEvent.getX()+mouseEvent.getY());
                        controlBlock1.setLayoutY(scenePoint.getY() - mouseEvent.getY());
                        controlBlock1.setLayoutX(scenePoint.getX() - mouseEvent.getX());

                        if (robot != null) {

                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseMove((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
                            robot.delay(50);
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        }

                    }

                    @Override
                    public void onMouseDragged(MouseEvent mouseEvent) {

                    }

                };
                selectedControlsPane.getChildren().add(controlBlock);
            }
        }catch(Exception e){e.printStackTrace();}


    }

    private BlockSpec blockSpecBuilder(String title,String name){
        BlockSpec blockSpec = new BlockSpec();
        blockSpec.title = title;
        blockSpec.name = name;
        blockSpec.type = "";
        blockSpec.field = new String[0
                ];
        blockSpec.code = new BlockSpec.BlockSpecCode();
        blockSpec.code.inc = "";
        blockSpec.code.def = "";
        blockSpec.code.setup = "";
        blockSpec.code.work = "";
        blockSpec.code.loop = "";
        return blockSpec;
    }
}
