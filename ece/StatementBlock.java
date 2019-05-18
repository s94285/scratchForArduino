package ece;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class StatementBlock extends BlockWithSlotAndPlug {
    public StatementBlock(BlockSpec blockSpec, Pane drawingPane) {
        super(blockSpec, drawingPane,1);
        reShape();
        this.setBackground(new Background(new BackgroundFill(Color.rgb(10, 134, 152), CornerRadii.EMPTY, Insets.EMPTY)));
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
        LineTo lineTo6 = new LineTo(getWidth(),getHeight()-3);
        LineTo lineTo7 = new LineTo(34,getHeight()-3);
        LineTo lineTo8 = new LineTo(30,getHeight());
        LineTo lineTo9 = new LineTo(20,getHeight());
        LineTo lineTo10 = new LineTo(16,getHeight()-3);
        LineTo lineTo11 = new LineTo(0,getHeight()-3);
        path.getElements().addAll(moveTo,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5,lineTo6,lineTo7,lineTo8,lineTo9,lineTo10,lineTo11,new ClosePath());
        this.setShape(path);
        this.plugs.get(0).setPoint2D(new Point2D(25,getHeight()));
        super.reShape();
    }

}
