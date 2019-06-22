package ece;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.codehaus.jackson.map.ObjectMapper;

import java.awt.event.InputEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import ece.FunctionDialogController.ArgumentType;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.type.TypeReference;
import org.fxmisc.flowless.VirtualizedScrollPane;

import static ece.FunctionBlock.FUNCTION_COLOR;

public class ScratchForArduinoController {
    @FXML private ToggleGroup BlockToggleGroup;
    @FXML private Pane drawingPane;
    @FXML private RadioButton controlsButton,operatorsButton,arduinoButton,userDefinedButton;
    @FXML private AnchorPane blockPane;
    @FXML private AnchorPane codeAreaPane;
    @FXML private TextArea serialTextArea;
    @FXML private TextField serialInputTextField;
    @FXML private ComboBox<String> boardComboBox,portComboBox;
    @FXML private ToggleButton serialConnectButton;
    private Properties configs = new Properties();
    private CustomCodeArea codeArea = new CustomCodeArea();
    private String boardName="";
    private boolean serialListening = false;
    private SerialPort comPort;


    enum BlockClass{CONTROLS,OPERATORS,ARDUINO,USER_DEFINED}
    private BlockClass currentBlockClass = BlockClass.ARDUINO;
    private VBox selectedOperatorsPane, selectedArduinoPane,selectedControlsPane,selectedUserDefinedPane,variablePane,functionPane;
    private final Button makeVariableButton = new Button("Make a Variable");
    private final Button makeFunctionButton = new Button("Make a Function");
    private java.awt.Robot robot;
    public void initialize(){
        //setup property file
        try {
            Path pathToConfig = Paths.get("configs.properties");
            if (Files.exists(pathToConfig))
                configs.load(new FileReader(pathToConfig.toString()));
        }catch (Exception e){e.printStackTrace();}
        controlsButton.setUserData(BlockClass.CONTROLS);
        operatorsButton.setUserData(BlockClass.OPERATORS);
        arduinoButton.setUserData(BlockClass.ARDUINO);
        userDefinedButton.setUserData(BlockClass.USER_DEFINED);
        VirtualizedScrollPane codeScrollPane = new VirtualizedScrollPane<>(codeArea);
        codeAreaPane.getChildren().add(codeScrollPane);
        codeAreaPane.getStylesheets().add(getClass().getResource("keywords.css").toExternalForm());
        AnchorPane.setTopAnchor(codeScrollPane,0.0);
        AnchorPane.setLeftAnchor(codeScrollPane,0.0);
        AnchorPane.setRightAnchor(codeScrollPane,0.0);
        AnchorPane.setBottomAnchor(codeScrollPane,0.0);
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
        makeVariableButton.setOnAction(this::makeVariableButtonOnAction);
        variablePane.getChildren().add(makeVariableButton);
        functionPane.setSpacing(10);
        functionPane.setPadding(new Insets(10,10,10,10));
        makeFunctionButton.setOnAction(this::makeFunctionButtonOnAction);
        functionPane.getChildren().add(makeFunctionButton);
        selectedUserDefinedPane.getChildren().addAll(variablePane,functionPane);
        blockPane.getChildren().addAll(selectedControlsPane,selectedOperatorsPane, selectedArduinoPane,selectedUserDefinedPane);
//        codeArea.setEditable(false);
//        codeArea.setFont(new Font("consolas",17));
        initializeBlocks();     //add blocks to left pane
        drawingPane.setOnMouseReleased(mouseEvent -> refreshCode());
        drawingPane.setOnKeyReleased(keyEvent -> refreshCode());
        selectedOperatorsPane.setVisible(currentBlockClass==BlockClass.OPERATORS);
        selectedControlsPane.setVisible(currentBlockClass==BlockClass.CONTROLS);
        selectedArduinoPane.setVisible(currentBlockClass==BlockClass.ARDUINO);
        selectedUserDefinedPane.setVisible(currentBlockClass==BlockClass.USER_DEFINED);
//        drawingPane.setViewOrder(5);
//        drawingPane.toFront();
        boardComboBox.setItems(FXCollections.observableList(Arrays.asList("Uno","Mega")));
        boardComboBox.setOnAction(event -> {
            switch(boardComboBox.getValue()){
                case "Uno":boardName = "arduino:avr:uno";break;
                case "Mega":boardName = "arduino:avr:mega";break;
            }
        });
        portComboBox.setOnShowing(this::portComboBoxOnShowing);
        serialConnectButton.selectedProperty().addListener(this::serialConnectButtonChanged);
        //finish interface initialize

        Head block1 = new Head(blockSpecBuilder("Arduino Program","headBlock"),drawingPane);
        block1.setLayoutX(100);
        block1.setLayoutY(50);

        drawingPane.getChildren().add(block1);
        refreshCode();
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
        filePanePairs.add(new Pair<>("json/variableBlocks.json",variablePane));
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
                                        robot.delay(20);
                                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                                    }
                                }
                                @Override
                                public void onMouseDragged(MouseEvent mouseEvent) {}
                            };
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
                                        robot.delay(20);
                                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                                    }
                                }
                                @Override
                                public void onMouseDragged(MouseEvent mouseEvent) {}
                            };
                            break;
                        case "w":
                            newBlock = new StatementBlock(blockSpec, blockPane) {
                                @Override
                                public void onMousePressed(MouseEvent mouseEvent) {
                                    //super.onMousePressed(mouseEvent);
                                    System.out.println("Mouse Entered on Click Me Two");
                                    StatementBlock valueBlock1 = new StatementBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                                    if(stringPanePair.getValue() == variablePane)
                                        valueBlock1.setBackground(new Background(new BackgroundFill(Color.rgb(238,125,22), CornerRadii.EMPTY, Insets.EMPTY)));
                                    ScratchForArduinoController.this.drawingPane.getChildren().add(valueBlock1);
                                    Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
                                    valueBlock1.setLayoutY(scenePoint.getY() - mouseEvent.getY());
                                    valueBlock1.setLayoutX(scenePoint.getX() - mouseEvent.getX());
                                    if (robot != null) {
                                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                                        robot.mouseMove((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
                                        robot.delay(20);
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
                                        robot.delay(20);
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
                                        robot.delay(20);
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
                                        robot.delay(20);
                                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                                    }
                                }
                                @Override
                                public void onMouseDragged(MouseEvent mouseEvent) {}
                            };
                            break;
                    }
                    if(newBlock != null && stringPanePair.getValue() == selectedArduinoPane)
                        newBlock.setBackground(new Background(new BackgroundFill(Color.rgb(10,134,152), CornerRadii.EMPTY, Insets.EMPTY)));
                    if(newBlock != null && stringPanePair.getValue() == variablePane)
                        newBlock.setBackground(new Background(new BackgroundFill(Color.rgb(238,125,22), CornerRadii.EMPTY, Insets.EMPTY)));
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
        blockSpec.field = new ArrayList<>();
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
        ArrayList<StringBuilder> functions = new ArrayList<>();
//        HashSet<String> setupSet = new HashSet<>();
        Head headBlock = null;
        for(Node node : drawingPane.getChildren())
            if(node instanceof Head && !(node instanceof FunctionBlock)){
                headBlock = (Head)node;
                break;
            }
        if(headBlock==null)return;
        headBlock.generateCode(code);
//        setupSet.addAll(code.setup);
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
//        setupSet.addAll();
        for(Node node : drawingPane.getChildren())
            if((node instanceof FunctionBlock)){
                FunctionBlock functionBlock = (FunctionBlock)node;
                StringBuilder functionCode = new StringBuilder();
                if(code.code.length()>0)
                    code.code.delete(0,code.code.length());
                //prototype
                code.define.add(functionBlock.getFunctionPrototype()+";\n");
                functionBlock.generateCode(code);
                functionCode.append(functionBlock.getFunctionPrototype()).append("{\n");
                functionCode.append(code.code.toString()).append("}\n");
                functions.add(functionCode);
            }
        for(String s:code.setup)
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
        for(StringBuilder s:functions)
            str.append(s.toString());

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
        codeArea.clear();
        codeArea.replaceText(result.toString());
        System.out.println("Refresh Code");
    }

    @FXML
    public void onVerifyButtonClicked(ActionEvent event){
        //TODO: finish this
        if(configs.isEmpty()||!Files.exists(Paths.get(configs.getProperty("arduinoPath")))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid arduino path");
            alert.setContentText("Current Path: "+configs.getProperty("arduinoPath")+"\nPlease setup correct path in File/Arduino Path");
            alert.showAndWait();
            return;
        }
        try {
            Path folder = Paths.get("./inoTmp");
            if(!Files.exists(folder)){
                Files.createDirectories(folder);
            }
            Path file = Paths.get(folder.toString(),"tmp.ino");
            Files.write(file,Arrays.asList(codeArea.getText()) , StandardCharsets.UTF_8);
            final Process p = Runtime.getRuntime().exec(String.format("%s\\arduino_debug --verify --board %s --port %s --verbose --preserve-temp-files \"%s\"",configs.getProperty("arduinoPath"),boardName,portComboBox.getValue(),file.toAbsolutePath()));

            Thread update = new Thread(new Runnable() {
                public void run() {
                    InputStreamReader isr = new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8);
                    BufferedReader input = new BufferedReader(isr);
                    String line = null;

                    try {
                        while ((line = input.readLine()) != null){
                            System.out.println(line);
                            String finalLine = line;
                            //update UI from the FX thread to prevent errors
                            Platform.runLater(() -> serialTextArea.appendText(finalLine +"\n"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> serialTextArea.appendText("Verify Finished\n"));
                }
            });
            update.setDaemon(true);
            update.start();
        }catch (Exception e){e.printStackTrace();}
    }

    @FXML
    public void onUploadButtonClicked(ActionEvent event){
        //TODO: not finished yet
        serialConnectButton.setSelected(false);
        if(configs.isEmpty()||!Files.exists(Paths.get(configs.getProperty("arduinoPath")))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid arduino path");
            alert.setContentText("Current Path: "+configs.getProperty("arduinoPath")+"\nPlease setup correct path in File/Arduino Path");
            alert.showAndWait();
            return;
        }
        try {
            Path folder = Paths.get("./inoTmp");
            if(!Files.exists(folder)){
                Files.createDirectories(folder);
            }
            Path file = Paths.get(folder.toString(),"tmp.ino");
            Files.write(file,Arrays.asList(codeArea.getText()) , StandardCharsets.UTF_8);
            final Process p = Runtime.getRuntime().exec(String.format("%s\\arduino_debug --upload --board %s --port %s --verbose --preserve-temp-files \"%s\"",configs.getProperty("arduinoPath"),boardName,portComboBox.getValue(),file.toAbsolutePath()));

            Thread update = new Thread(new Runnable() {
                public void run() {
                    InputStreamReader isr = new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8);
                    BufferedReader input = new BufferedReader(isr);
                    String line = null;

                    try {
                        while ((line = input.readLine()) != null){
                            System.out.println(line);
                            String finalLine = line;
                            Platform.runLater(new Runnable() {      //update UI from the FX thread to prevent errors
                                @Override
                                public void run() {
                                    serialTextArea.appendText(finalLine +"\n");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> serialTextArea.appendText("Upload Finished\n"));
                }
            });
            update.setDaemon(true);
            update.start();
        }catch (Exception e){e.printStackTrace();}
    }

    @FXML
    public void onSendButtonClicked(ActionEvent event){
        if(comPort!=null  && comPort.isOpen()){
            byte[] data = serialInputTextField.getText().getBytes();
            comPort.writeBytes(data,data.length);
//            serialTextArea.appendText(serialInputTextField.getText()+"\n");
        }
    }

    @FXML
    public void onSerialClearButtonClicked(ActionEvent event){
        serialTextArea.clear();
    }

    @FXML
    public void menuSetupArduinoPath(ActionEvent event){
        TextInputDialog textInputDialog = new TextInputDialog(configs.getProperty("arduinoPath"));  //default value
        textInputDialog.setTitle("Setup Arduino Path");
        textInputDialog.setHeaderText("Input full arduino path below without \\ at last\neg: "+"C:\\Program Files (x86)\\Arduino");
        textInputDialog.setContentText("Path: ");
        textInputDialog.showAndWait();
        String str = textInputDialog.getResult();
        if(str==null)return;
        System.out.println(str);
        if(!Files.exists(Paths.get(str))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid arduino path");
            alert.setContentText("Current Path: "+str+"\nPlease setup correct path");
            alert.showAndWait();
            return;
        }
        configs.setProperty("arduinoPath",str);
        try {
            Path pathToConfig = Paths.get("configs.properties");
            configs.store(new FileWriter(pathToConfig.toString()),"Arduino Path");
        }catch(Exception e){e.printStackTrace();}
    }

    private void serialConnectButtonChanged(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        if(newValue){
            //open port
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        comPort = SerialPort.getCommPort(portComboBox.getValue());
                    }catch (Exception e){
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Invalid port");
                            alert.setContentText("Please check selected port");
                            alert.showAndWait();
                            serialConnectButton.setSelected(false);
                        });
                        return;
                    }
                    serialListening = true;
                    comPort.setBaudRate(115200);
                    comPort.openPort();
                    try {
                        while (serialListening)
                        {
                            while (serialListening&&comPort.bytesAvailable() == 0)
                                Thread.sleep(20);

                            byte[] readBuffer = new byte[comPort.bytesAvailable()];
                            int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                            System.out.println("Read " + numRead + " bytes.");
                            Platform.runLater(() -> serialTextArea.appendText(new String(readBuffer,StandardCharsets.UTF_8)));
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    comPort.closePort();
                }
            });
            thread.setDaemon(true);
            thread.start();
        }else{
            //close port
            serialListening = false;
        }
    }

    private void portComboBoxOnShowing(Event event) {
        SerialPort[] ports = SerialPort.getCommPorts();
        ArrayList<String> portNames = new ArrayList<>();
        for(SerialPort port : ports)
            portNames.add(port.getSystemPortName());
        portComboBox.setItems(FXCollections.observableList(portNames));
    }

    private void makeVariableButtonOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Make a Variable");
        GridPane gridPane = new GridPane();
        Label labelName = new Label("Variable Name");
        Label labelType = new Label("Variable Type");
        TextField textFieldName = new TextField();
        textFieldName.setPrefColumnCount(10);
        String[] types = {"double", "boolean"};
        ComboBox<String> comboBoxType = new ComboBox<>(FXCollections.observableArrayList(types));
        comboBoxType.setValue("double");
        gridPane.add(labelName, 0, 0);
        gridPane.add(labelType, 0, 1);
        gridPane.add(textFieldName, 1, 0);
        gridPane.add(comboBoxType, 1, 1);
        gridPane.setHgap(10);
        gridPane.setVgap(15);
        alert.getDialogPane().setContent(gridPane);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.CANCEL) return;
        String varName = textFieldName.getText();
        if (varName.length() == 0 || varName.contains(" ")) {
            Alert alert1 = new Alert(Alert.AlertType.WARNING);
            alert1.setTitle("Invalid variable name");
            alert1.setContentText((varName.contains(" ")) ? "Variable name cannot contain space" : ((varName.length() == 0) ? "Variable name cannot be empty" : "Invalid variable name"));
            alert1.showAndWait();
            return;
        }
        if (comboBoxType.getValue().equals("double")) {
            BlockSpec blockSpec = blockSpecBuilder(varName, varName);
            blockSpec.code.work = varName;
            blockSpec.code.def = "double " + varName + ";\n";
            ValueBlock valueBlock = new ValueBlock(blockSpec, variablePane) {
                @Override
                public void onMousePressed(MouseEvent mouseEvent) {
                    //super.onMousePressed(mouseEvent);
                    System.out.println("Mouse Entered on Click Me Two");
                    ValueBlock valueBlock1 = new ValueBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                    valueBlock1.setBackground(new Background(new BackgroundFill(Color.rgb(238, 125, 22), CornerRadii.EMPTY, Insets.EMPTY)));
                    valueBlock1.setPadding(new Insets(-5, 2, -2, 2));
                    ScratchForArduinoController.this.drawingPane.getChildren().add(valueBlock1);
                    Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
                    valueBlock1.setLayoutY(scenePoint.getY() - mouseEvent.getY());
                    valueBlock1.setLayoutX(scenePoint.getX() - mouseEvent.getX());
                    if (robot != null) {
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseMove((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
                        robot.delay(20);
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    }
                }

                @Override
                public void onMouseDragged(MouseEvent mouseEvent) {
                }
            };
            valueBlock.setBackground(new Background(new BackgroundFill(Color.rgb(238, 125, 22), CornerRadii.EMPTY, Insets.EMPTY)));
            valueBlock.setPadding(new Insets(-5, 2, -2, 2));
            variablePane.getChildren().add(valueBlock);
        }
    }

    private void makeFunctionButtonOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("newFunctionDialog.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
//                stage.setResizable(false);
            stage.sizeToScene();
            stage.setTitle("Make a Function");
            stage.initModality(Modality.APPLICATION_MODAL);
            AtomicBoolean isClosed = new AtomicBoolean(false);
            stage.setOnCloseRequest(windowEvent -> isClosed.set(true));
            stage.showAndWait();
            ArrayList<MyPair<ArgumentType, String>> funcSpec = fxmlLoader.<FunctionDialogController>getController().getArgumentList();
            System.out.println(funcSpec);
            if (funcSpec != null && !isClosed.get()) {
                FunctionBlock functionBlock = new FunctionBlock(blockSpecBuilder("define ", funcSpec.get(0).getValue()), drawingPane, funcSpec, functionPane);
                functionBlock.setLayoutX(50);
                functionBlock.setLayoutY(50);
                drawingPane.getChildren().add(functionBlock);
                BlockSpec blockSpec = FunctionBlock.getStatementBlockSpec(funcSpec);
                StatementBlock statementBlock = new StatementBlock(blockSpec, functionPane) {
                    @Override
                    public void onMousePressed(MouseEvent mouseEvent) {
                        //super.onMousePressed(mouseEvent);
                        System.out.println("Mouse Entered on Click Me Two");
                        StatementBlock valueBlock1 = new StatementBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                        valueBlock1.setBackground(new Background(new BackgroundFill(FUNCTION_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
                        ScratchForArduinoController.this.drawingPane.getChildren().add(valueBlock1);
                        Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
                        valueBlock1.setLayoutY(scenePoint.getY() - mouseEvent.getY());
                        valueBlock1.setLayoutX(scenePoint.getX() - mouseEvent.getX());
                        if (robot != null) {
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseMove((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
                            robot.delay(20);
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        }
                    }

                    @Override
                    public void onMouseDragged(MouseEvent mouseEvent) {
                    }
                };
                statementBlock.setBackground(new Background(new BackgroundFill(FUNCTION_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
                functionPane.getChildren().add(statementBlock);
            } else
                System.out.println("Not valid function block");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openFile(ActionEvent event){
//        System.out.println("open pressed");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ScratchForAndroid File","*.sfa"));
        File file = fileChooser.showOpenDialog(drawingPane.getScene().getWindow());
        if(file != null){
            System.out.println(file);
            drawingPane.getChildren().clear();
            for(int i=0;i<variablePane.getChildren().size();i++){
                Node node = variablePane.getChildren().get(i);
                if(node instanceof ValueBlock || node instanceof BooleanBlock) {
                    variablePane.getChildren().remove(node);
                    i--;
                }
            }
            for(int i=0;i<functionPane.getChildren().size();i++){
                Node node = functionPane.getChildren().get(i);
                if(node instanceof StatementBlock) {
                    functionPane.getChildren().remove(node);
                    i--;
                }
            }
            try {
                ObjectMapper mapper = new ObjectMapper();
                LinkedHashMap<String,ArrayList<BlockMap>> fileMap = mapper.readValue(file, new TypeReference<LinkedHashMap<String,ArrayList<BlockMap>>>(){});
                ArrayList<BlockMap> blockMaps = fileMap.get("Blocks");
                System.out.println(blockMaps);
                for(BlockMap blockMap : blockMaps){
                    drawingPane.getChildren().add(getBlockFromBlockMap(blockMap,null,drawingPane));
                }
                ArrayList<BlockMap> variableMaps = fileMap.get("Variables");
                for(BlockMap blockMap : variableMaps){
                    ValueBlock valueBlock = new ValueBlock(blockMap.blockSpec, variablePane) {
                        @Override
                        public void onMousePressed(MouseEvent mouseEvent) {
                            //super.onMousePressed(mouseEvent);
                            System.out.println("Mouse Entered on Click Me Two");
                            ValueBlock valueBlock1 = new ValueBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                            valueBlock1.setBackground(new Background(new BackgroundFill(Color.rgb(238, 125, 22), CornerRadii.EMPTY, Insets.EMPTY)));
                            valueBlock1.setPadding(new Insets(-5, 2, -2, 2));
                            ScratchForArduinoController.this.drawingPane.getChildren().add(valueBlock1);
                            Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
                            valueBlock1.setLayoutY(scenePoint.getY() - mouseEvent.getY());
                            valueBlock1.setLayoutX(scenePoint.getX() - mouseEvent.getX());
                            if (robot != null) {
                                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                                robot.mouseMove((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
                                robot.delay(20);
                                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            }
                        }

                        @Override
                        public void onMouseDragged(MouseEvent mouseEvent) {
                        }
                    };
                    valueBlock.setBackground(new Background(new BackgroundFill(Color.rgb(238, 125, 22), CornerRadii.EMPTY, Insets.EMPTY)));
                    valueBlock.setPadding(new Insets(-5, 2, -2, 2));
                    variablePane.getChildren().add(valueBlock);
                }
                ArrayList<BlockMap> functionMaps = fileMap.get("Functions");
                for(BlockMap blockMap : functionMaps){
                    StatementBlock statementBlock = new StatementBlock(blockMap.blockSpec, functionPane) {
                        @Override
                        public void onMousePressed(MouseEvent mouseEvent) {
                            //super.onMousePressed(mouseEvent);
                            System.out.println("Mouse Entered on Click Me Two");
                            StatementBlock valueBlock1 = new StatementBlock(blockSpec, ScratchForArduinoController.this.drawingPane);
                            valueBlock1.setBackground(new Background(new BackgroundFill(FUNCTION_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
                            ScratchForArduinoController.this.drawingPane.getChildren().add(valueBlock1);
                            Point2D scenePoint = ScratchForArduinoController.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
                            valueBlock1.setLayoutY(scenePoint.getY() - mouseEvent.getY());
                            valueBlock1.setLayoutX(scenePoint.getX() - mouseEvent.getX());
                            if (robot != null) {
                                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                                robot.mouseMove((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
                                robot.delay(20);
                                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            }
                        }

                        @Override
                        public void onMouseDragged(MouseEvent mouseEvent) {
                        }
                    };
                    statementBlock.setBackground(new Background(new BackgroundFill(FUNCTION_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
                    functionPane.getChildren().add(statementBlock);
                }
                System.out.println("The Object  was successfully read from a file\n"+fileMap);
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Open Failed");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void saveFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ScratchForAndroid File","*.sfa"));
        File file = fileChooser.showSaveDialog(drawingPane.getScene().getWindow());
        if(file != null){
            System.out.println(file);
            try {
                ObjectMapper mapper = new ObjectMapper();

                ObjectWriter writer = mapper.defaultPrettyPrintingWriter();
                LinkedHashMap<String,Object> fileMap = new LinkedHashMap<>();
                ArrayList<BlockMap> blockMaps = new ArrayList<>();
                for(Node node : drawingPane.getChildren()){
                    if(node instanceof Block){
                        //if not top block, skip
                        if(node instanceof BlockWithSlotAndPlug && ((BlockWithSlotAndPlug) node).slot.getBlock()!=null)continue;
                        blockMaps.add(((Block) node).getBlockMap());
                    }
                }
                ArrayList<BlockMap> variableMaps = new ArrayList<>();
                for(Node node : variablePane.getChildren()){
                    if(node instanceof ValueBlock){
                        variableMaps.add(((Block) node).getBlockMap());
                    }
                }
                ArrayList<BlockMap> functionMaps = new ArrayList<>();
                for(Node node : functionPane.getChildren()){
                    if(node instanceof Block){
                        functionMaps.add(((Block) node).getBlockMap());
                    }
                }
                fileMap.put("Blocks",blockMaps);
                fileMap.put("Variables",variableMaps);
                fileMap.put("Functions",functionMaps);
                writer.writeValue(file,fileMap);

                System.out.println("The Object  was successfully written to a file");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("File Saved");
                alert.setContentText("File Saved");
                alert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save Failed");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }
    }

    /**
     * New a block from blockMap.
     * @param blockMap Block constructs according to information in it.
     * @param parentBlock If the block is a succeeding block, provide parent block to point to in its slot.
     * @param containedPane The pane which the block is going to be in.
     * @return The new block's reference.
     */
    public Block getBlockFromBlockMap(BlockMap blockMap,BlockWithPlug parentBlock,Pane containedPane){
        BlockSpec blockSpec = blockMap.blockSpec;
        Block newBlock = null;
        System.out.println("Type="+blockSpec.type);
        switch(blockSpec.type){
            case "h":
                newBlock = new Head(blockSpec, containedPane);
                break;
            case "r":
                newBlock = new ValueBlock(blockSpec, containedPane);
                break;
            case "b":
                newBlock = new BooleanBlock(blockSpec, containedPane);
                break;
            case "w":
                newBlock = new StatementBlock(blockSpec, containedPane);
                break;
            case "one block":
                newBlock = new ControlBlock(blockSpec, containedPane);
                break;
            case "loop":
                newBlock = new ForeverLoopBlock(blockSpec,containedPane);
                break;
            case "two block":
                newBlock = new IfandElseBlock(blockSpec,containedPane);
                break;
            case "FunctionBlock":
                newBlock = new FunctionBlock(blockSpec,containedPane,blockMap.functionSpec,functionPane);
                break;
        }
        if(newBlock==null)return null;
        //process titlePane
        Iterator<BlockMap.TitleField> titleFieldIterator = blockMap.titleFields.iterator(); //iterator points in front of first element
        for(Node node : newBlock.titlePane.getChildren()){
            if(node instanceof StackPane){
                BlockMap.TitleField titleField = titleFieldIterator.next();
                Node textComponent = ((StackPane) node).getChildren().get(0);
                if(textComponent instanceof TextField) {
                    TextField textField = ((TextField) textComponent);
                    textField.setText(titleField.value);
                }else if(textComponent instanceof ComboBox && ((ComboBox) textComponent).getValue() instanceof String)
                    ((ComboBox<String>) textComponent).setValue(titleField.value);
                if(titleField.block != null){
                    textComponent.setManaged(false);
                    textComponent.setVisible(false);
                    ((StackPane) node).getChildren().add(getBlockFromBlockMap(titleField.block,null,containedPane));
                }
            }
        }
        //process with slot connection
        if(parentBlock != null && newBlock instanceof BlockWithSlotAndPlug)
            ((BlockWithSlotAndPlug) newBlock).slot.setBlock(parentBlock);
        //process with succeeding blocks
        if(newBlock instanceof BlockWithPlug){
            BlockWithPlug blockWithPlug = (BlockWithPlug) newBlock;
            for(int i=0;i<blockWithPlug.plugs.size();i++){
                if(blockMap.plugs[i]!=null){
                    BlockWithSlotAndPlug blockWithSlotAndPlug = (BlockWithSlotAndPlug)getBlockFromBlockMap(blockMap.plugs[i],(BlockWithPlug)newBlock,containedPane);
                    blockWithPlug.plugs.get(i).setBlock(blockWithSlotAndPlug);
                    drawingPane.getChildren().add(blockWithSlotAndPlug);
                }
            }
        }
        newBlock.setBackground(new Background(new BackgroundFill(Paint.valueOf(blockMap.color), CornerRadii.EMPTY, Insets.EMPTY)));
        newBlock.setLayoutX(blockMap.layoutX);
        newBlock.setLayoutY(blockMap.layoutY);
        return newBlock;
    }
}
