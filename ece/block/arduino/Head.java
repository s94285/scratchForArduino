package ece.block.arduino;
import ece.block.Block;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;

public class Head extends Block {
    private Label textLabel;
    private TextField textField;
    private  ComboBox comboBox;
    private double height,width;

    public  Head(String arg, String blockName,Pane drawingPane){
        super(arg,blockName,drawingPane);
        comboBox=new ComboBox();


//        Path path = new Path();
//        MoveTo moveTo = new MoveTo();
//        moveTo.setX(0);
//        moveTo.setY(16);
//        ArcTo arcTo1 = new ArcTo();
//        arcTo1.setX(100);
//        arcTo1.setY(16);
//        arcTo1.setRadiusX(50);
//        arcTo1.setRadiusY(16);
//        arcTo1.setSweepFlag(true);
//        LineTo lineTo1 = new LineTo(148,16);
//        LineTo lineTo2 = new LineTo(148,45);
//        LineTo lineTo3 = new LineTo(34,45);
//        LineTo lineTo4 = new LineTo(30,48);
//        LineTo lineTo5 = new LineTo(20,48);
//        LineTo lineTo6 = new LineTo(16,45);
//        LineTo lineTo7 = new LineTo(0,45);
//        path.getElements().addAll(moveTo,arcTo1,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5,lineTo6,lineTo7,new ClosePath());
//        //this.setHeight(50);
//        //this.setWidth(150);
//        this.setShape(path);
        //this.setBorder(new Border(new BorderStroke(Color.WHITE,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
        reShape();
        this.setBackground(new Background(new BackgroundFill(Color.rgb(10,134,152), CornerRadii.EMPTY, Insets.EMPTY)));

        comboBox.setItems(FXCollections.observableArrayList("A", "B", new Separator(), "C", "D"));
        textLabel=new Label();
        textLabel.setText("Arduino Program");
        textLabel.setFont(new Font(17));
        textLabel.setTextFill(Color.WHITE);
        //textLabel.setBackground(new Background(new BackgroundFill(Color.BROWN,CornerRadii.EMPTY,Insets.EMPTY)));
//        titlePane.setLayoutX(5);
//        titlePane.setLayoutY(this.getHeight()/2);
//        titlePane.setAlignment(Pos.CENTER_LEFT);
//        titlePane.getChildren().add(textLabel);
//        this.setPrefHeight(USE_COMPUTED_SIZE);
        titlePane.setPadding(new Insets(15,5,5,5));
//        this.setEffect(new DropShadow(5,Color.BLACK));



    }
    @Override
    public void reShape(){
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
        LineTo lineTo1 = new LineTo(getWidth(),16);
        LineTo lineTo2 = new LineTo(getWidth(),getHeight()-3);
        LineTo lineTo3 = new LineTo(34,getHeight()-3);
        LineTo lineTo4 = new LineTo(30,getHeight());
        LineTo lineTo5 = new LineTo(20,getHeight());
        LineTo lineTo6 = new LineTo(16,getHeight()-3);
        LineTo lineTo7 = new LineTo(0,getHeight()-3);
        path.getElements().addAll(moveTo,arcTo1,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5,lineTo6,lineTo7,new ClosePath());
        this.setShape(path);
    }
}
