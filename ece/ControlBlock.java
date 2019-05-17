package ece;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class ControlBlock extends  BlockWithSlotAndPlug {
    public ControlBlock(String arg, String blockName, Pane drawingPane) {
        super(arg, blockName, drawingPane, 2);
        this.setAlignment(Pos.TOP_LEFT);
        reShape();
        this.setBackground(new Background(new BackgroundFill(Color.rgb(225, 169, 26), CornerRadii.EMPTY, Insets.EMPTY)));
    }
    @Override
    public void reShape() {
        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        moveTo.setX(0);
        moveTo.setY(0);
        LineTo lineTo1 = new LineTo(16,0);
        LineTo lineTo2 = new LineTo(20,3);
        LineTo lineTo3 = new LineTo(30,3);
        LineTo lineTo4 = new LineTo(34,0);
        LineTo lineTo5 = new LineTo(getWidth(),0);
        LineTo lineTo6 = new LineTo(getWidth(),getHeight()-36);
        LineTo lineTo7 = new LineTo(52,getHeight()-36);
        LineTo lineTo8 = new LineTo(48,getHeight()-33);
        LineTo lineTo9 = new LineTo(38,getHeight()-33);
        LineTo lineTo10 = new LineTo(34,getHeight()-36);
        LineTo lineTo11 = new LineTo(16,getHeight()-36);
        LineTo lineTo12 = new LineTo(16,getHeight()-24);
        LineTo lineTo13 = new LineTo(getWidth(),getHeight()-24);
        LineTo lineTo14 = new LineTo(getWidth(),getHeight()-3);
        LineTo lineTo15 = new LineTo(34,getHeight()-3);
        LineTo lineTo16 = new LineTo(30,getHeight());
        LineTo lineTo17 = new LineTo(20,getHeight());
        LineTo lineTo18 = new LineTo(16,getHeight()-3);
        LineTo lineTo19 = new LineTo(0,getHeight()-3);
        path.getElements().addAll(moveTo,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5,lineTo6,lineTo7,lineTo8,lineTo9,lineTo10,lineTo11,lineTo12,lineTo13,lineTo14,lineTo15,lineTo16,
                lineTo17,lineTo18,lineTo19,new ClosePath());
        this.setShape(path);
        this.setPadding(new Insets(0,0,36,5));
        this.plugs.get(0).setPoint2D(new Point2D(25,getHeight()));
//        this.plugs.set(0,new Pair<>(new Point2D(25,getHeight()),this.plugs.get(0).getValue()));
        super.reShape();
    }
}
