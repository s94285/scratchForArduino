package ece;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class ControlBlock extends  BlockWithSlotAndPlug {
    public ControlBlock(BlockSpec blockSpec, Pane drawingPane) {
        super(blockSpec, drawingPane, 2);
        this.setAlignment(Pos.TOP_LEFT);
        reShape();
        this.setBackground(new Background(new BackgroundFill(Color.rgb(225, 169, 26), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @Override
    public void onMouseReleased(MouseEvent mouseEvent) {
        super.onMouseReleased(mouseEvent);
        reShape();      //for reshape after plug to others
    }

    @Override
    public void reShape() {
        int containedBlockHeight = 0;
        BlockWithSlotAndPlug blockWithSlotAndPlug = this.plugs.get(1).getBlock();
        while(blockWithSlotAndPlug!=null){
            containedBlockHeight+=blockWithSlotAndPlug.getHeight();
            if(blockWithSlotAndPlug instanceof ForeverLoopBlock)break;
            blockWithSlotAndPlug = blockWithSlotAndPlug.plugs.get(0).getBlock();
        }
        if(containedBlockHeight!=0)
            containedBlockHeight-=5;
//        System.out.println("old: " + containedBlockHeight + " , " + getHeight());
        this.setPadding(new Insets(0,0,36+containedBlockHeight,5));
        this.autosize();        //force height computation
//        System.out.println("new: " + containedBlockHeight + " , " + getHeight());
        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        moveTo.setX(0);
        moveTo.setY(0);
        LineTo lineTo1 = new LineTo(16,0);
        LineTo lineTo2 = new LineTo(20,3);
        LineTo lineTo3 = new LineTo(30,3);
        LineTo lineTo4 = new LineTo(34,0);
        LineTo lineTo5 = new LineTo(getWidth(),0);
        LineTo lineTo6 = new LineTo(getWidth(),getHeight()-36-containedBlockHeight);
        LineTo lineTo7 = new LineTo(52,getHeight()-36-containedBlockHeight);
        LineTo lineTo8 = new LineTo(48,getHeight()-33-containedBlockHeight);
        LineTo lineTo9 = new LineTo(38,getHeight()-33-containedBlockHeight);
        LineTo lineTo10 = new LineTo(34,getHeight()-36-containedBlockHeight);
        LineTo lineTo11 = new LineTo(16,getHeight()-36-containedBlockHeight);
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

        this.plugs.get(0).setPoint2D(new Point2D(25,getHeight()));      //lowest plug
        this.plugs.get(1).setPoint2D(new Point2D(43,getHeight()-32-containedBlockHeight));      //inner plug
        super.reShape();
    }
}
