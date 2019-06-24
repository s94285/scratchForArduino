package ece;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;

public class ForeverLoopBlock extends BlockWithSlotAndPlug {
    public ForeverLoopBlock(BlockSpec blockSpec, Pane drawingPane) {
        super(blockSpec, drawingPane, 1);
        this.setAlignment(Pos.TOP_LEFT);
        reShape();
        this.setBackground(new Background(new BackgroundFill(Color.rgb(225, 169, 26), CornerRadii.EMPTY, Insets.EMPTY)));
    }
    @Override
    public void onMouseReleased(MouseEvent mouseEvent) {
        if(mouseEvent.getButton() == MouseButton.SECONDARY)return;
        super.onMouseReleased(mouseEvent);
        reShape();      //for reshape after plug to others
    }

    @Override
    public void reShape() {
        int containedBlockHeight = 0;
        BlockWithSlotAndPlug blockWithSlotAndPlug = this.plugs.get(0).getBlock();
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
        System.out.println("new: " + containedBlockHeight + " , " + getHeight());
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
        LineTo lineTo14 = new LineTo(getWidth(),getHeight());
        LineTo lineTo15 = new LineTo(0,getHeight());

        path.getElements().addAll(moveTo,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5,lineTo6,lineTo7,lineTo8,lineTo9,lineTo10,lineTo11,lineTo12,lineTo13,lineTo14,lineTo15,new ClosePath());
        this.setShape(path);

        //this.plugs.get(0).setPoint2D(new Point2D(25,getHeight()));      //lowest plug
        this.plugs.get(0).setPoint2D(new Point2D(43,getHeight()-32-containedBlockHeight));      //inner plug
        super.reShape();
    }

    @Override
    public void generateCode(Code code) {
        ArrayList<StackPane> allStackPanes = new ArrayList<>();
        //get all stackPanes
        for(Node node:titlePane.getChildren())
            if(node instanceof StackPane)
                allStackPanes.add((StackPane)node);
        String[] workString = this.blockSpec.code.work.split("\\{[0-9]+}");
        code.code.append(workString[0]);
        for(int i=1;i<workString.length;i++){
            StackPane stackPane = allStackPanes.get(i-1);
            if(stackPane.getChildren().size()>1){
                //has inner block
                ((Block)stackPane.getChildren().get(1)).generateCode(code);
            }else{
                code.code.append(((TextField)stackPane.getChildren().get(0)).getText());
            }
            code.code.append(workString[i]);
        }
        if(this.plugs.get(0).getBlock()!=null){
            this.plugs.get(0).getBlock().generateCode(code);
        }
    }


}
