package ece;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;

import java.util.concurrent.Flow;

public class Head extends Block {
    private Label textLabel;
    private TextField textField;
    private FlowPane titlePane = new FlowPane();
    private double height,width;

    public  Head(){
//        comboBox=new ComboBox();


        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        moveTo.setX(0);
        moveTo.setY(16);
        ArcTo arcTo1 = new ArcTo();
        arcTo1.setX(100);
        arcTo1.setY(16);
        arcTo1.setRadiusX(50);
        arcTo1.setRadiusY(16);
        arcTo1.setSweepFlag(true);
        LineTo lineTo1 = new LineTo(148,16);
        LineTo lineTo2 = new LineTo(148,45);
        LineTo lineTo3 = new LineTo(34,45);
        LineTo lineTo4 = new LineTo(30,48);
        LineTo lineTo5 = new LineTo(20,48);
        LineTo lineTo6 = new LineTo(16,45);
        LineTo lineTo7 = new LineTo(0,45);
        path.getElements().addAll(moveTo,arcTo1,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5,lineTo6,lineTo7,new ClosePath());
        //this.setHeight(50);
        //this.setWidth(150);
        this.setShape(path);
        this.setBackground(new Background(new BackgroundFill(Color.rgb(10,134,152), CornerRadii.EMPTY, Insets.EMPTY)));

//        comboBox.setItems(FXCollections.observableArrayList("A", "B", new Separator(), "C", "D"));
        textLabel=new Label();
        textLabel.setText("Arduino Program");
        textLabel.setFont(new Font(17));
        textLabel.setTextFill(Color.WHITE);
        //textLabel.setBackground(new Background(new BackgroundFill(Color.BROWN,CornerRadii.EMPTY,Insets.EMPTY)));
        titlePane.setLayoutX(5);
        titlePane.setLayoutY(20);
        titlePane.setPrefWrapLength(USE_COMPUTED_SIZE);
        //textLabel.setLayoutX(this.getLayoutX()+8);
        //textLabel.setLayoutY(this.getLayoutY()+2);
        titlePane.getChildren().add(textLabel);
        //titlePane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setPrefWidth(148);
        this.setPrefHeight(48);
        this.getChildren().add(titlePane);
//        this.getChildren().add(comboBox);
    }

}
