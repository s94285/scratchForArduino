package ece;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;

public class FunctionDialogController {
    @FXML private HBox hbox;
    @FXML private Button numberButton,stringButton,booleanButton,labelButton;
    public enum ArgumentType{TEXT,STRING,NUMBER,BOOLEAN}
    private int stringCnt = 1,booleanCnt = 1,numberCnt = 1;

    public void initialize(){
        Ellipse ellipse = new Ellipse(15,7.5);
        ellipse.setFill(Color.DARKGRAY);
        numberButton.setGraphic(ellipse);
        Rectangle rectangle = new Rectangle(30, 15);
        rectangle.setFill(Color.DARKGRAY);
        stringButton.setGraphic(rectangle);
        Polygon polygon = new Polygon(25, 0, 30, 7.5, 25, 15, 5, 15, 0, 7.5, 5, 0);
        polygon.setFill(Color.DARKGRAY);
        booleanButton.setGraphic(polygon);
        hbox.widthProperty().addListener((observableValue, number, t1) -> hbox.getScene().getWindow().sizeToScene());
        hbox.getChildren().add(new LabelString("fun"));
    }

    @FXML
    private void onAddButtonClicked(ActionEvent e){
        if(hbox.getChildren().size()<1 ||!(hbox.getChildren().get(0) instanceof LabelString)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Format incorrect");
            alert.setContentText("Label has to be in the first place, representing function's name.");
            alert.showAndWait();
        }else{
            ((Stage)((Button)e.getSource()).getScene().getWindow()).close();
        }
    }
    @FXML
    private void onCancelButtonClicked(ActionEvent e){
        hbox.getChildren().removeAll();
        ((Stage)((Button)e.getSource()).getScene().getWindow()).close();
    }
    @FXML
    private void onStringButtonClicked(ActionEvent e){
        StringArg stringArg = new StringArg("string"+String.valueOf(stringCnt++));
        hbox.getChildren().add(stringArg);
    }
    @FXML
    private void onNumberButtonClicked(ActionEvent e){
        NumberArg numberArg = new NumberArg("number"+String.valueOf(numberCnt++));
        hbox.getChildren().add(numberArg);
    }
    @FXML
    private void onBooleanButtonClicked(ActionEvent e){
        BooleanArg booleanArg = new BooleanArg("boolean"+String.valueOf(booleanCnt++));
        hbox.getChildren().add(booleanArg);
    }
    @FXML
    private void onLabelButtonClicked(ActionEvent e){
        LabelString labelString = new LabelString("label");
        hbox.getChildren().add(labelString);
    }

    public ArrayList<Pair<ArgumentType,String>> getArgumentList(){
        ArrayList<Pair<ArgumentType,String>> list = new ArrayList<>();
        for(Node node : hbox.getChildren()){
            if(node instanceof LabelString){
                list.add(new Pair<>(ArgumentType.TEXT,((TextField)node).getText()));
            }else if(node instanceof NumberArg){
                list.add(new Pair<>(ArgumentType.NUMBER,((TextField)node).getText()));
            }else if(node instanceof StringArg){
                list.add(new Pair<>(ArgumentType.STRING,((TextField)node).getText()));
            }else if(node instanceof BooleanArg){
                list.add(new Pair<>(ArgumentType.BOOLEAN,((TextField)node).getText()));
            }
        }
        if(list.size()<1||!(list.get(0).getKey()==ArgumentType.TEXT)){
            return null;
        }
        return list;
    }

    private class LabelString extends TextField{
        public LabelString(String name){
            super();
            this.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL,null,null)));
            TextField textField = this;
            textField.setFont(new Font(14));
            textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
//                System.out.println(newValue.length());
                Text text = new Text(newValue);
                double width = text.getLayoutBounds().getWidth() * 1 // This big is the Text in the TextField
                        + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                        ; // Add some spacing
                textField.setPrefWidth(width + 1); // Set the width
                textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
            });
            this.setText(name);
            Text text = new Text(name);
            double width = text.getLayoutBounds().getWidth() * 1.17 // This big is the Text in the TextField
                    + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                    + 18d; // Add some spacing
            textField.setPrefWidth(width + 1); // Set the width
            textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
        }
    }

    private class NumberArg extends TextField{
        public NumberArg(String name){
            super();
            TextField textField = this;
            textField.setAlignment(Pos.CENTER);
            textField.setPrefColumnCount(1);
            textField.setFont(new Font(14));
            textField.setPadding(new Insets(0,2,0,2));
            textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
//                System.out.println(newValue.length());
                Text text = new Text(newValue);
                double width = text.getLayoutBounds().getWidth()*1.17 // This big is the Text in the TextField
                        + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                        + 2d; // Add some spacing
                textField.setPrefWidth(width+1); // Set the width
                textField.autosize();
                textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
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
            this.setText(name);
            Text text = new Text(name);
            double width = text.getLayoutBounds().getWidth()*1.17 // This big is the Text in the TextField
                    + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                    + 2d; // Add some spacing
            double height = text.getLayoutBounds().getHeight()*1.8 + textField.getPadding().getTop() + textField.getPadding().getBottom() +2d;
            textField.setPrefWidth(width+1); // Set the width
            textField.setPrefHeight(height);
            textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
            Path path = new Path();
            MoveTo moveTo = new MoveTo();
            moveTo.setX(12);
            moveTo.setY(height);
            ArcTo arcTo1 = new ArcTo();
            arcTo1.setX(12);
            arcTo1.setY(0);
            arcTo1.setRadiusX(12);
            arcTo1.setRadiusY(height/2);
            arcTo1.setSweepFlag(true);
            LineTo lineTo1 = new LineTo(width-12, 0);
            ArcTo arcTo2 = new ArcTo(12, height/2, 0,width-12 , height, false, true);
            path.getElements().addAll(moveTo, arcTo1, lineTo1, arcTo2, new ClosePath());
            textField.setShape(path);
        }
    }

    private class BooleanArg extends TextField{
        public BooleanArg(String name){
            super();
            TextField textField = this;
            textField.setAlignment(Pos.CENTER);
            textField.setPrefColumnCount(1);
            textField.setFont(new Font(14));
            textField.setPadding(new Insets(0,2,0,2));
            textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
//                System.out.println(newValue.length());
                Text text = new Text(newValue);
                double width = text.getLayoutBounds().getWidth()*1.2 // This big is the Text in the TextField
                        + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                        + 2d; // Add some spacing
                textField.setPrefWidth(width+1); // Set the width
                textField.autosize();
                textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
                Path path = new Path();
                MoveTo moveTo = new MoveTo();
                moveTo.setX(0);
                moveTo.setY(getHeight()/2);
                LineTo lineTo1 = new LineTo(8, 0);
                LineTo lineTo2 = new LineTo(getWidth()-8, 0);
                LineTo lineTo3 = new LineTo(getWidth(), getHeight()/2);
                LineTo lineTo4 = new LineTo(getWidth()-8, getHeight());
                LineTo lineTo5 = new LineTo(8, getHeight());
                path.getElements().addAll(moveTo,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5, new ClosePath());
                this.setShape(path);
            });
            this.setText(name);
            Text text = new Text(name);
            double width = text.getLayoutBounds().getWidth()*1.2 // This big is the Text in the TextField
                    + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                    + 2d; // Add some spacing
            double height = text.getLayoutBounds().getHeight()*1.8 + textField.getPadding().getTop() + textField.getPadding().getBottom() +2d;
            textField.setPrefWidth(width+1); // Set the width
            textField.setPrefHeight(height);
            textField.autosize();
            textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
            Path path = new Path();
            MoveTo moveTo = new MoveTo();
            moveTo.setX(0);
            moveTo.setY(height/2);
            LineTo lineTo1 = new LineTo(8, 0);
            LineTo lineTo2 = new LineTo(width-8, 0);
            LineTo lineTo3 = new LineTo(width, height/2);
            LineTo lineTo4 = new LineTo(width-8, height);
            LineTo lineTo5 = new LineTo(8, height);
            path.getElements().addAll(moveTo,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5, new ClosePath());
            this.setShape(path);
        }
    }

    private class StringArg extends TextField{
        public StringArg(String name){
            super();
            TextField textField = this;
            textField.setFont(new Font(14));
            textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
//                System.out.println(newValue.length());
                Text text = new Text(newValue);
                double width = text.getLayoutBounds().getWidth() * 1 // This big is the Text in the TextField
                        + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                        ; // Add some spacing
                textField.setPrefWidth(width + 1); // Set the width
                textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
            });
            this.setText(name);
            Text text = new Text(name);
            double width = text.getLayoutBounds().getWidth() * 1.17 // This big is the Text in the TextField
                    + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                    + 18d; // Add some spacing
            textField.setPrefWidth(width + 1); // Set the width
            textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
        }
    }

    @FXML
    private void onDeleteButtonClicked(ActionEvent e){
        if(hbox.getChildren().size()>0)
            hbox.getChildren().remove(hbox.getChildren().size()-1);
    }
}
