package ece;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.codehaus.jackson.map.ObjectMapper;

import java.awt.event.InputEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ScratchForArduinoController {
    @FXML private ToggleGroup BlockToggleGroup;
    @FXML private Pane drawingPane;
    @FXML private RadioButton controlsButton,operatorsButton,arduinoButton;
    @FXML private AnchorPane blockPane;
    @FXML private TextArea codeArea;
    enum BlockClass{CONTROLS,OPERATORS,ARDUINO}
    private BlockClass currentBlockClass = BlockClass.CONTROLS;
    private VBox selectedOperatorsPane, selectedArduinoPane,selectedControlsPane;
    private java.awt.Robot robot;
    public void initialize(){
        controlsButton.setUserData(BlockClass.CONTROLS);
        operatorsButton.setUserData(BlockClass.OPERATORS);
        arduinoButton.setUserData(BlockClass.ARDUINO);
        try {
            robot = new java.awt.Robot();
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
//        codeArea.setEditable(false);
        codeArea.setFont(new Font("consolas",17));
        initializeBlocks();     //add blocks to left pane
        drawingPane.setOnMouseReleased(mouseEvent -> refreshCode());
        drawingPane.setOnKeyReleased(keyEvent -> refreshCode());
        //finish interface initialize

        Head block1 = new Head(blockSpecBuilder("Arduino Program","headBlock"),drawingPane);
        block1.setLayoutX(100);
        block1.setLayoutY(50);

        drawingPane.getChildren().add(block1);
    }
    @FXML
    private void typeClicked(ActionEvent e){
        currentBlockClass=(BlockClass) BlockToggleGroup.getSelectedToggle().getUserData();
        selectedOperatorsPane.setVisible(currentBlockClass==BlockClass.OPERATORS);
        selectedControlsPane.setVisible(currentBlockClass==BlockClass.CONTROLS);
        selectedArduinoPane.setVisible(currentBlockClass==BlockClass.ARDUINO);
    }

    private void initializeBlocks(){
        //operators blocks
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<BlockSpec> blockSpecList = Arrays.asList(objectMapper.readValue(new File(getClass().getResource("json/operatorBlocks.json").toURI()), BlockSpec[].class));
            for(BlockSpec blockSpec : blockSpecList) {
                System.out.println(blockSpec);
                Block newBlock = null;
                switch(blockSpec.type){
                    case "r":
                        newBlock = new ValueBlock(blockSpec, blockPane) {
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
                        break;
                    case "b":
                        newBlock = new BooleanBlock(blockSpec, blockPane) {
                            @Override
                            public void onMousePressed(MouseEvent mouseEvent) {
                                //super.onMousePressed(mouseEvent);
                                System.out.println("Mouse Entered on Click Me Two");
                                BooleanBlock valueBlock1 = new BooleanBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
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
                        break;
                }
                selectedOperatorsPane.getChildren().add(newBlock);
            }
        }catch(Exception e){e.printStackTrace();}
        //statements blocks
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<BlockSpec> blockSpecList = Arrays.asList(objectMapper.readValue(new File(getClass().getResource("json/arduinoBlocks.json").toURI()), BlockSpec[].class));
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
        //control blocks
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<BlockSpec> blockSpecList = Arrays.asList(objectMapper.readValue(new File(getClass().getResource("json/controlBlocks.json").toURI()), BlockSpec[].class));
            for(BlockSpec blockSpec : blockSpecList) {
                System.out.println(blockSpec);
                Block newBlock = null;
                switch(blockSpec.type){
                    case "one block":
                        newBlock = new ControlBlock(blockSpec, blockPane) {
                            @Override
                            public void onMousePressed(MouseEvent mouseEvent) {
                                //super.onMousePressed(mouseEvent);
                                System.out.println("Mouse Entered on Click Me Two");
                                ControlBlock controlBlock1= new ControlBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                                ScratchForArduinoController.this.drawingPane.getChildren().add(controlBlock1);
                                Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
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
                        break;
                    case "loop":
                        newBlock = new ForeverLoopBlock(blockSpec,drawingPane){
                            @Override
                            public void onMousePressed(MouseEvent mouseEvent) {
                                //super.onMousePressed(mouseEvent);
                                System.out.println("Mouse Entered on Click Me Two");
                                ForeverLoopBlock controlBlock1= new ForeverLoopBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                                ScratchForArduinoController.this.drawingPane.getChildren().add(controlBlock1);
                                Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
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
                        break;
                    case "two block":
                        newBlock = new IfandElseBlock(blockSpec,drawingPane){
                            @Override
                            public void onMousePressed(MouseEvent mouseEvent) {
                                //super.onMousePressed(mouseEvent);
                                System.out.println("Mouse Entered on Click Me Two");
                                IfandElseBlock controlBlock1= new IfandElseBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                                ScratchForArduinoController.this.drawingPane.getChildren().add(controlBlock1);
                                Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
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
                        break;
                }
                selectedControlsPane.getChildren().add(newBlock);
            }
        }catch(Exception e){e.printStackTrace();}


    }

    private BlockSpec blockSpecBuilder(String title,String name){
        BlockSpec blockSpec = new BlockSpec();
        blockSpec.title = title;
        blockSpec.name = name;
        blockSpec.type = "";
        blockSpec.field = new String[0];
        blockSpec.code = new BlockSpec.BlockSpecCode();
        blockSpec.code.inc = "";
        blockSpec.code.def = "";
        blockSpec.code.setup = "";
        blockSpec.code.work = "";
        blockSpec.code.loop = "";
        return blockSpec;
    }

    private void refreshCode(){
        Code code = new Code();
        String loop="",workInSetup;
        StringBuilder setup = new StringBuilder();
        HashSet<String> setupSet = new HashSet<>();
        Head headBlock = null;
        for(Node node : drawingPane.getChildren())
            if(node instanceof Head){
                headBlock = (Head)node;
                break;
            }
        if(headBlock==null)return;
        headBlock.generateCode(code);
        setupSet.addAll(code.setup);
        workInSetup = code.code.toString();

        //find foreverloopblock
        ForeverLoopBlock foreverLoopBlock = null;
        BlockWithPlug iterator = headBlock;
        while(iterator!=null){
            if(iterator instanceof ForeverLoopBlock){
                foreverLoopBlock = (ForeverLoopBlock)iterator;
                break;
            }
            iterator = iterator.plugs.get(0).getBlock();
        }
        if(foreverLoopBlock!=null){
            if(code.code.length()>0)
                code.code.delete(0,code.code.length());
            foreverLoopBlock.generateCode(code);
            loop = code.code.toString();
        }
        setupSet.addAll(code.setup);
        for(String s:setupSet)
            setup.append(s);
        setup.append(workInSetup);

        StringBuilder str = new StringBuilder();
        str.append("#include <Arduino.h>\n" +
                "#include <Wire.h>\n" +
                "#include <SoftwareSerial.h>\n");
//        for(String s : code.include)
//            str.append(s);
//        for(String s : code.define)
//            str.append(s);
        str.append("\nvoid setup(){\n");
        str.append(setup);
        str.append("}\n\nvoid loop(){\n");
        str.append(loop);
        str.append("}\n");

        //indent
        StringBuilder result = new StringBuilder();
        int currentIndent = 0;
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)=='{'){
                result.append("{");
                currentIndent+=4;
            }else if(str.charAt(i)=='\n'){
                if(i<str.length()-1&&str.charAt(i+1)=='}')currentIndent-=4;
                result.append("\n");
                result.append(" ".repeat(Math.max(0, currentIndent)));
            }else{
                result.append(str.charAt(i));
            }
        }

        codeArea.setText(result.toString());
        System.out.println("Refresh Code");
    }
}
