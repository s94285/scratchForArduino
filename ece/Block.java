package ece;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.codehaus.jackson.map.ObjectMapper;
import org.reactfx.value.Val;

import java.util.ArrayList;
import java.util.Iterator;


public abstract class Block extends VBox {
    protected final Pane drawingPane;
    private double nowlayoutx,nowlayouty;
    private double pressedx,pressedy;
    protected HBox titlePane = new HBox();
    protected String blockName;
    protected BlockSpec blockSpec;
    protected static final ObjectMapper jsonMapper = new ObjectMapper();
    protected ChangeListener<?super Number> sizeChangeListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            System.out.println("called in block height changed");
            reShape();
        }
    };
    private  boolean flag=false;
    public Block(BlockSpec blockSpec,Pane drawingPane){
        this.blockSpec = blockSpec;
        String arg = blockSpec.title;
        String blockName = blockSpec.name;
        this.drawingPane = drawingPane;
        this.blockName = blockName;
        StringBuilder tmp = new StringBuilder();
        int fieldCount = 0;
        for(int i=0;i<arg.length();i++){
            if(arg.charAt(i)=='%'){
                if (tmp.length() > 0) {
                    Label label = new Label(tmp.toString());
                    label.setPadding(new Insets(0,5,0,5));
                    label.setFont(new Font(17));
                    label.setTextFill(Color.WHITE);
                    this.titlePane.getChildren().add(label);
                    tmp = new StringBuilder();
                }
                StackPane stackPane = new StackPane();
                switch (arg.charAt(++i)) {      //read next character
                    case 'm':{
                        StringBuilder menuLabel = new StringBuilder();
                        if(arg.charAt(++i)=='.'){
                            while(i<arg.length()-1&&arg.charAt(++i)!=' ')
                                menuLabel.append(arg.charAt(i));
                        }
                        ArrayList<String> menuArray = blockSpec.menu.get(menuLabel.toString());

                        final ComboBox<String> combobox1 = new ComboBox<>();
                        if(menuArray!=null)
                            combobox1.setItems(FXCollections.observableArrayList(menuArray));
                        combobox1.setPromptText("Editable");
                        combobox1.setVisibleRowCount(5);
                        combobox1.setEditable(true);
                        combobox1.setValue(blockSpec.field.get(fieldCount++));
                        combobox1.autosize();
 //Cell Factory
                        combobox1.setCellFactory(
                                new Callback<ListView<String>, ListCell<String>>() {
                                    @Override
                                    public ListCell<String> call(ListView<String> param) {

                                        return new ListCell<String>()
                                        {
                                            {
                                                super.setPrefWidth(60);
                                            }
                                            @Override
                                            protected void updateItem(String item, boolean empty) {
                                                super.updateItem(item, empty);

                                                if (item != null) {
                                                    setText(item);

                                                    if (item.contains("Item 0"))
                                                        setTextFill(Color.RED);
                                                    else if (item.contains("Item 1"))
                                                        setTextFill(Color.GREEN);
                                                }
                                                else {
                                                    setText(null);
                                                }
                                            }
                                        };
                                    }
                                });
                        combobox1.autosize();
                        combobox1.getEditor().textProperty().addListener((observableValue,oldValue,newValue) -> {
                            combobox1.setValue(newValue);
                            Text text = new Text(combobox1.getValue());
                            double width = text.getLayoutBounds().getWidth()*1.17 +combobox1.getPadding().getLeft() + combobox1.getPadding().getRight() + 35d;
                            combobox1.setPrefWidth(width);
                            if(drawingPane.getOnMouseReleased()!=null)drawingPane.getOnMouseReleased().handle(null);
                        });
                        if(menuArray!=null)
                            combobox1.setPrefWidth(menuArray.get(0).length()*14);
                        stackPane.getChildren().add(combobox1);
                        break;
                    }
                    case 'd':{
                        StringBuilder menuLabel = new StringBuilder();
                        if(arg.charAt(++i)=='.'){
                            while(i<arg.length()-1&&arg.charAt(++i)!=' ')
                                menuLabel.append(arg.charAt(i));
                        }
                        ArrayList<String> menuArray = blockSpec.menu.get(menuLabel.toString());

                        final ComboBox<String> combobox1 = new ComboBox<>();
                        if(menuArray!=null)
                            combobox1.setItems(FXCollections.observableArrayList(menuArray));
                        combobox1.setPromptText("Editable");
                        combobox1.setVisibleRowCount(5);
                        combobox1.setEditable(true);
                        combobox1.setValue(blockSpec.field.get(fieldCount++));
                        combobox1.autosize();
                        //Cell Factory
                        combobox1.setCellFactory(
                                new Callback<ListView<String>, ListCell<String>>() {
                                    @Override
                                    public ListCell<String> call(ListView<String> param) {

                                        return new ListCell<String>()
                                        {
                                            {
                                                super.setPrefWidth(60);
                                            }
                                            @Override
                                            protected void updateItem(String item, boolean empty) {
                                                super.updateItem(item, empty);

                                                if (item != null) {
                                                    setText(item);

                                                    if (item.contains("Item 0"))
                                                        setTextFill(Color.RED);
                                                    else if (item.contains("Item 1"))
                                                        setTextFill(Color.GREEN);
                                                }
                                                else {
                                                    setText(null);
                                                }
                                            }
                                        };
                                    }
                                });
                        combobox1.autosize();
                        combobox1.getEditor().textProperty().addListener((observableValue,oldValue,newValue) -> {
                            if(!newValue.matches("([\\d*.])*"))
                                combobox1.getEditor().setText(oldValue);
                            else
                                combobox1.setValue(newValue);
                            Text text = new Text(combobox1.getValue());
                            double width = text.getLayoutBounds().getWidth()*1.17 +combobox1.getPadding().getLeft() + combobox1.getPadding().getRight() + 35d;
                            combobox1.setPrefWidth(width);
                            if(drawingPane.getOnMouseReleased()!=null)drawingPane.getOnMouseReleased().handle(null);
                        });
                        if(menuArray!=null)
                            combobox1.setPrefWidth(menuArray.get(0).length()*14);
                        stackPane.getChildren().add(combobox1);
                        break;
                    }
                    case 'n': {

                        TextField textField = new TextField(blockSpec.field.get(fieldCount++));
                        textField.setUserData("number");
                        textField.setAlignment(Pos.CENTER);
                        textField.setPrefColumnCount(1);
                        textField.setFont(new Font(14));
                        textField.setPadding(new Insets(0,2,0,2));
                        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
                            System.out.println(newValue.length());
                            Text text = new Text(newValue);
                            double width = text.getLayoutBounds().getWidth()*1.17 // This big is the Text in the TextField
                                    + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                                    + 2d; // Add some spacing
                            textField.setPrefWidth(width+1); // Set the width
                            textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
                            if(!newValue.matches("([\\d*.])*")){
                                textField.setText(oldValue);
                            }
                            Path path = new Path();
                            MoveTo moveTo = new MoveTo();
                            moveTo.setX(12);
                            moveTo.setY(getHeight());
                            ArcTo arcTo1 = new ArcTo();
                            arcTo1.setX(12);
                            arcTo1.setY(0);
                            arcTo1.setRadiusX(12);
                            arcTo1.setRadiusY(getHeight()/2);
                            arcTo1.setSweepFlag(true);
                            LineTo lineTo1 = new LineTo((width+3)-12, 0);
                            ArcTo arcTo2 = new ArcTo(12, getHeight()/2, 0,(width+3)-12 , getHeight(), false, true);
                            path.getElements().addAll(moveTo, arcTo1, lineTo1, arcTo2, new ClosePath());
                            textField.setShape(path);
                        });

                        textField.setShape(new Circle(5));
                        stackPane.getChildren().add(textField);
                        break;
                    }
                    case 's':{
                        TextField textField = new TextField();
                        textField.setUserData("string");
                        textField.setAlignment(Pos.CENTER);
                        textField.setPrefColumnCount(1);
                        textField.setFont(new Font(14));

                        textField.setPadding(new Insets(0,2,0,2));
                        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
                            System.out.println(newValue.length());
                            Text text = new Text(newValue);
                            double width = text.getLayoutBounds().getWidth()*1.17 // This big is the Text in the TextField
                                    + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                                    + 2d; // Add some spacing
                            textField.setPrefWidth(width+1); // Set the width
                            textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
                        });
                        textField.setText(blockSpec.field.get(fieldCount++));
                        stackPane.getChildren().add(textField);
                        break;
                    }
                    case 'b':{
                        TextField textField = new TextField(blockSpec.field.get(fieldCount++));
                        textField.setUserData("number");
                        textField.setAlignment(Pos.CENTER);
                        textField.setPrefColumnCount(1);
                        textField.setFont(new Font(14));
                        textField.setPadding(new Insets(0,2,0,2));
                        textField.setShape(new Polygon(0,6,6,0,30,0,36,6,30,12,6,12));
                        textField.setEditable(false);
                        textField.setFocusTraversable(false);
                        textField.setMouseTransparent(true);
                        stackPane.getChildren().add(textField);
                        break;
                    }
                }
                this.titlePane.getChildren().add(stackPane);
            }else{
//                if(tmp.length() == 0)tmp.append(" ");
                tmp.append(arg.charAt(i));
            }
        }
        if(tmp.length()>0){
            Label label = new Label(tmp.toString());
            label.setFont(new Font(17));
            label.setTextFill(Color.WHITE);
            this.titlePane.getChildren().add(label);
        }
        titlePane.setAlignment(Pos.CENTER_LEFT);
        titlePane.setPadding(new Insets(5,5,5,5));
        this.setEffect(new DropShadow(5,Color.BLACK));
        this.setPrefWidth(USE_COMPUTED_SIZE);
        this.setPrefHeight(USE_COMPUTED_SIZE);
        this.getChildren().add(titlePane);
        this.setOnMousePressed(mouseEvent -> onMousePressed(mouseEvent));
        this.setOnMouseDragged(mouseEvent -> onMouseDragged(mouseEvent));
        this.setOnMouseReleased(mouseEvent -> onMouseReleased(mouseEvent));

        //this.setPrefWidth(U);

        //this.getChildren().add(rec);
        this.heightProperty().addListener(sizeChangeListener);
        this.widthProperty().addListener(sizeChangeListener);
    }
    public  void onMousePressed(MouseEvent mouseEvent){

        this.toFront();
        nowlayoutx=this.getLayoutX();
        nowlayouty=this.getLayoutY();
        pressedx=mouseEvent.getSceneX();
        pressedy=mouseEvent.getSceneY();
//        System.out.println("Mouse Pressed");
    }
    public void onMouseDragged(MouseEvent mouseEvent){
        this.setLayoutX(nowlayoutx+(mouseEvent.getSceneX()-pressedx));
        this.setLayoutY(nowlayouty+(mouseEvent.getSceneY()-pressedy));
        pressedx=mouseEvent.getSceneX();
        pressedy=mouseEvent.getSceneY();
        nowlayoutx=this.getLayoutX();
        nowlayouty=this.getLayoutY();
//        System.out.println("Mouse Dragged x:"+nowlayoutx+"y"+nowlayouty);
    }
    public void onMouseReleased(MouseEvent mouseEvent){
        //Remove this block if not head
        if(this.getLayoutX()+mouseEvent.getX()<0 && !(this instanceof Head)){
            Pane myParent = (Pane) this.getParent();
            myParent.getChildren().remove(this);
        }
    }
    public void setBlockName(String blockName){this.blockName = blockName;}
    public String getBlockName(){return blockName;}
    public HBox getTitlePane(){return titlePane;}
    public abstract void reShape();
    public abstract void generateCode(Code code);
    public BlockSpec getBlockSpec() {
        return blockSpec;
    }
    public BlockMap getBlockMap(){       //return a LinkedHashMap of this block
        BlockMap blockMap = new BlockMap();
        //fill type in spec if forgot
        if(this.blockSpec.type == null || this.blockSpec.type.equals("")){
            if(this instanceof StatementBlock)
                this.blockSpec.type = "w";
            else if(this instanceof Head)
                this.blockSpec.type = "h";
            else if(this instanceof ValueBlock)
                this.blockSpec.type = "r";
            else if(this instanceof BooleanBlock)
                this.blockSpec.type = "b";
        }
        blockMap.blockSpec = this.blockSpec;
        blockMap.color = this.getBackground().getFills().get(0).getFill().toString();
        blockMap.layoutX = this.getLayoutX();
        blockMap.layoutY = this.getLayoutY();
        blockMap.titleFields = new ArrayList<>();
        for(Node node : titlePane.getChildren()){
            if(node instanceof StackPane){
                Node textComponent = ((StackPane) node).getChildren().get(0);
                BlockMap.TitleField titleField = new BlockMap.TitleField();
                if(textComponent instanceof TextField)
                    titleField.value = ((TextField) textComponent).getText();
                else if(textComponent instanceof ComboBox)
                    titleField.value = (String)((ComboBox) textComponent).getValue();
                if(((StackPane) node).getChildren().size()>1){
                    titleField.block = ((Block)(((StackPane) node).getChildren().get(1))).getBlockMap();
                }
                blockMap.titleFields.add(titleField);
            }
        }
        return blockMap;
    }

    public Block duplicate(){
        BlockMap blockMap = this.getBlockMap();
        Block block =  getBlockFromBlockMap(blockMap,null,drawingPane);
        if(block!=null) {
            block.setLayoutX(block.getLayoutX() + 20);
            block.setLayoutY(block.getLayoutY() + 20);
            drawingPane.getChildren().add(block);
        }
        return block;
    }

    private Block getBlockFromBlockMap(BlockMap blockMap,BlockWithPlug parentBlock,Pane containedPane){
        BlockSpec blockSpec = blockMap.blockSpec;
        Block newBlock = null;
        System.out.println("Type="+blockSpec.type);
        switch(blockSpec.type){
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
