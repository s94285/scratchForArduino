package ece;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.ArrayList;


public abstract class Block extends VBox {
    protected final Pane drawingPane;
    private double nowlayoutx,nowlayouty;
    private double pressedx,pressedy;
    protected HBox titlePane = new HBox();
    protected String blockName;
    protected BlockSpec blockSpec;
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
                            if(!newValue.matches("\\d*"))
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
                            if(!newValue.matches("\\d*")){
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
                            LineTo lineTo1 = new LineTo(getWidth()-12, 0);
                            ArcTo arcTo2 = new ArcTo(12, getHeight()/2, 0,getWidth()-12 , getHeight(), false, true);
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

        Pane myParent = (Pane) this.getParent();
        myParent.getChildren().remove(this);        //put self to the front
        myParent.getChildren().add(this);
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
}
