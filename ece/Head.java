package ece;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;

public class Head extends BlockWithPlug {
    private Label textLabel;
    private TextField textField;
    private  ComboBox comboBox;
    private double height,width;

    public  Head(BlockSpec blockSpec,Pane drawingPane){
        super(blockSpec,drawingPane,1);
        comboBox=new ComboBox();

        reShape();
        this.setBackground(new Background(new BackgroundFill(Color.rgb(10,134,152), CornerRadii.EMPTY, Insets.EMPTY)));

        comboBox.setItems(FXCollections.observableArrayList("A", "B", new Separator(), "C", "D"));
        textLabel=new Label();
        textLabel.setText("Arduino Program");
        textLabel.setFont(new Font(17));
        textLabel.setTextFill(Color.WHITE);

        titlePane.setPadding(new Insets(15,5,5,5));



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
        this.plugs.get(0).setPoint2D(new Point2D(25,getHeight()));
//        this.plugs.set(0,new Pair<>(new Point2D(25,getHeight()),this.plugs.get(0).getValue()));
        super.reShape();
    }

    @Override
    public void generateCode(Code code) {
        if(this.plugs.get(0).getBlock()!=null && !(this.plugs.get(0).getBlock() instanceof ForeverLoopBlock)){
            this.plugs.get(0).getBlock().generateCode(code);
        }
    }
}
