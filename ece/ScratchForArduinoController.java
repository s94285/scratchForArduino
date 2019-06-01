package ece;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.codehaus.jackson.map.ObjectMapper;

import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import ece.FunctionDialogController.ArgumentType;

public class ScratchForArduinoController {
    @FXML private ToggleGroup BlockToggleGroup;
    @FXML private Pane drawingPane;
    @FXML private RadioButton controlsButton,operatorsButton,arduinoButton,userDefinedButton;
    @FXML private AnchorPane blockPane;
    @FXML private TextArea codeArea;
    enum BlockClass{CONTROLS,OPERATORS,ARDUINO,USER_DEFINED}
    private BlockClass currentBlockClass = BlockClass.ARDUINO;
    private VBox selectedOperatorsPane, selectedArduinoPane,selectedControlsPane,selectedUserDefinedPane,variablePane,functionPane;
    private final Button makeVariableButton = new Button("Make a Variable");
    private final Button makeFunctionButton = new Button("Make a Function");
    private java.awt.Robot robot;
    public void initialize(){
        controlsButton.setUserData(BlockClass.CONTROLS);
        operatorsButton.setUserData(BlockClass.OPERATORS);
        arduinoButton.setUserData(BlockClass.ARDUINO);
        userDefinedButton.setUserData(BlockClass.USER_DEFINED);
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
        selectedUserDefinedPane = new VBox();
        variablePane = new VBox();
        functionPane = new VBox();
        selectedUserDefinedPane.setSpacing(10);
        selectedUserDefinedPane.setPadding(new Insets(10,10,10,10));
        variablePane.setSpacing(10);
        variablePane.setPadding(new Insets(10,10,10,10));
        makeVariableButton.setOnAction(actionEvent -> {
           //TODO: call dialog
        });
        variablePane.getChildren().add(makeVariableButton);
        functionPane.setSpacing(10);
        functionPane.setPadding(new Insets(10,10,10,10));
        makeFunctionButton.setOnAction(actionEvent -> {
            //call newFunctionDialog
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("newFunctionDialog.fxml"));
                Parent parent = fxmlLoader.load();
                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                stage.setScene(scene);
//                stage.setResizable(false);
                stage.sizeToScene();
                stage.setTitle("Make a Function");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                ArrayList<Pair<ArgumentType,String>> funcSpec = fxmlLoader.<FunctionDialogController>getController().getArgumentList();
                System.out.println(funcSpec);
                if(funcSpec!=null){
                    FunctionBlock functionBlock = new FunctionBlock(blockSpecBuilder("define ","function"),drawingPane,funcSpec);
                    functionBlock.setLayoutX(50);
                    functionBlock.setLayoutY(50);
                    drawingPane.getChildren().add(functionBlock);
                }else
                    System.out.println("Not valid function block");
            }catch (IOException e){
                e.printStackTrace();
            }
        });
        functionPane.getChildren().add(makeFunctionButton);
        selectedUserDefinedPane.getChildren().addAll(variablePane,functionPane);
        blockPane.getChildren().addAll(selectedControlsPane,selectedOperatorsPane, selectedArduinoPane,selectedUserDefinedPane);
//        codeArea.setEditable(false);
        codeArea.setFont(new Font("consolas",17));
        initializeBlocks();     //add blocks to left pane
        drawingPane.setOnMouseReleased(mouseEvent -> refreshCode());
        drawingPane.setOnKeyReleased(keyEvent -> refreshCode());
        selectedOperatorsPane.setVisible(currentBlockClass==BlockClass.OPERATORS);
        selectedControlsPane.setVisible(currentBlockClass==BlockClass.CONTROLS);
        selectedArduinoPane.setVisible(currentBlockClass==BlockClass.ARDUINO);
        selectedUserDefinedPane.setVisible(currentBlockClass==BlockClass.USER_DEFINED);
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
        selectedUserDefinedPane.setVisible(currentBlockClass==BlockClass.USER_DEFINED);
    }

    private void initializeBlocks(){
        ArrayList<Pair<String,Pane>> filePanePairs = new ArrayList<>();
        filePanePairs.add(new Pair<>("json/operatorBlocks.json",selectedOperatorsPane));
        filePanePairs.add(new Pair<>("json/arduinoBlocks.json",selectedArduinoPane));
        filePanePairs.add(new Pair<>("json/controlBlocks.json",selectedControlsPane));
        for(Pair<String,Pane> stringPanePair : filePanePairs){
            try{
                ObjectMapper objectMapper = new ObjectMapper();
                List<BlockSpec> blockSpecList = Arrays.asList(objectMapper.readValue(new File(getClass().getResource(stringPanePair.getKey()).toURI()), BlockSpec[].class));
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
                                    if(stringPanePair.getValue() == selectedArduinoPane)
                                        valueBlock1.setBackground(new Background(new BackgroundFill(Color.rgb(10,134,152), CornerRadii.EMPTY, Insets.EMPTY)));
                                    ScratchForArduinoController.this.drawingPane.getChildren().add(valueBlock1);
                                    Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
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
                                public void onMouseDragged(MouseEvent mouseEvent) {}
                            };
                            if(stringPanePair.getValue() == selectedArduinoPane)
                                newBlock.setBackground(new Background(new BackgroundFill(Color.rgb(10,134,152), CornerRadii.EMPTY, Insets.EMPTY)));
                            break;
                        case "b":
                            newBlock = new BooleanBlock(blockSpec, blockPane) {
                                @Override
                                public void onMousePressed(MouseEvent mouseEvent) {
                                    //super.onMousePressed(mouseEvent);
                                    System.out.println("Mouse Entered on Click Me Two");
                                    BooleanBlock valueBlock1 = new BooleanBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                                    if(stringPanePair.getValue() == selectedArduinoPane)
                                        valueBlock1.setBackground(new Background(new BackgroundFill(Color.rgb(10,134,152), CornerRadii.EMPTY, Insets.EMPTY)));
                                    ScratchForArduinoController.this.drawingPane.getChildren().add(valueBlock1);
                                    Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
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
                                public void onMouseDragged(MouseEvent mouseEvent) {}
                            };
                            if(stringPanePair.getValue() == selectedArduinoPane)
                                newBlock.setBackground(new Background(new BackgroundFill(Color.rgb(10,134,152), CornerRadii.EMPTY, Insets.EMPTY)));
                            break;
                        case "w":
                            newBlock = new StatementBlock(blockSpec, blockPane) {
                                @Override
                                public void onMousePressed(MouseEvent mouseEvent) {
                                    //super.onMousePressed(mouseEvent);
                                    System.out.println("Mouse Entered on Click Me Two");
                                    StatementBlock valueBlock1 = new StatementBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                                    ScratchForArduinoController.this.drawingPane.getChildren().add(valueBlock1);
                                    Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
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
                                public void onMouseDragged(MouseEvent mouseEvent) {}
                            };
                            break;
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
                                public void onMouseDragged(MouseEvent mouseEvent) {}
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
                                public void onMouseDragged(MouseEvent mouseEvent) {}
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
                                public void onMouseDragged(MouseEvent mouseEvent) {}
                            };
                            break;
                    }
                    stringPanePair.getValue().getChildren().add(newBlock);
                }
            }catch(Exception e){e.printStackTrace();}
        }
    }

    public static BlockSpec blockSpecBuilder(String title,String name){
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
        for(String s : code.include)
            str.append(s);
        for(String s : code.define)
            str.append(s);
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

    @FXML
    public void openFile(ActionEvent event){
//        System.out.println("open pressed");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*"));
        File file = fileChooser.showSaveDialog(drawingPane.getScene().getWindow());
        if(file != null){
            System.out.println(file);

        }
    }

    @FXML
    public void saveFile(ActionEvent event){
//        System.out.println("save pressed");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*"));
        File file = fileChooser.showSaveDialog(drawingPane.getScene().getWindow());
        if(file != null){
            System.out.println(file);
            try {
                ObjectMapper mapper = new ObjectMapper();

                String jsonInString = mapper.writeValueAsString(drawingPane.getChildren().get(0));


                System.out.println("The Object  was succesfully written to a file\n"+jsonInString);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
