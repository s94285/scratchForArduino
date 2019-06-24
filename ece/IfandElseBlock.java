package ece;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class IfandElseBlock extends BlockWithSlotAndPlug {
    private Pane blankPane;
    private Label labelElse = new Label("else");
    private static final int TITLEPANE_TO_LABELELSE = 10;
    public IfandElseBlock(BlockSpec blockSpec, Pane drawingPane) {
        super(blockSpec, drawingPane, 3);
        this.setAlignment(Pos.TOP_LEFT);
        blankPane = new Pane();
        labelElse.setPrefSize(60,22);
        labelElse.setFont(new Font(17));
        labelElse.setTextFill(Color.WHITE);
        this.getChildren().add(blankPane);
        this.getChildren().add(labelElse);
        reShape();
        this.setBackground(new Background(new BackgroundFill(Color.rgb(225, 169, 26), CornerRadii.EMPTY, Insets.EMPTY)));
    }
    @Override
    public void onMouseReleased(MouseEvent mouseEvent) {
        if(mouseEvent.getButton() == MouseButton.SECONDARY)return;
        super.onMouseReleased(mouseEvent);
        reShape();  //for reshape after plug to others
    }

    @Override
    public void reShape() {
        //plug 0:bottom 1:center 2:top
        int containedBlockHeight2 = 0,containedBlockHeight1=0;
        BlockWithSlotAndPlug blockWithSlotAndPlug = this.plugs.get(1).getBlock();
        while(blockWithSlotAndPlug!=null){
            containedBlockHeight1+=blockWithSlotAndPlug.getHeight();
            if(blockWithSlotAndPlug instanceof ForeverLoopBlock)break;
            blockWithSlotAndPlug = blockWithSlotAndPlug.plugs.get(0).getBlock();
        }
        blockWithSlotAndPlug=this.plugs.get(2).getBlock();
        while(blockWithSlotAndPlug!=null){
            containedBlockHeight2+=blockWithSlotAndPlug.getHeight();
            if(blockWithSlotAndPlug instanceof ForeverLoopBlock)break;
            blockWithSlotAndPlug = blockWithSlotAndPlug.plugs.get(0).getBlock();
        }
        if(containedBlockHeight1!=0)
            containedBlockHeight1-=5;
        if(containedBlockHeight2!=0)
            containedBlockHeight2-=5;
        blankPane.setPrefHeight(containedBlockHeight2+TITLEPANE_TO_LABELELSE);
        blankPane.setPrefWidth(10);
//        System.out.println("old: " + containedBlockHeight2 + " , " + getHeight());
        this.setPadding(new Insets(0,0,70+containedBlockHeight1-22-TITLEPANE_TO_LABELELSE,5));
        this.autosize();
//        System.out.println("new: " + containedBlockHeight1 + " , " + containedBlockHeight2);
        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        moveTo.setX(0);
        moveTo.setY(0);
        LineTo lineTo1 = new LineTo(16,0);
        LineTo lineTo2 = new LineTo(20,3);
        LineTo lineTo3 = new LineTo(30,3);
        LineTo lineTo4 = new LineTo(34,0);
        LineTo lineTo5 = new LineTo(getWidth(),0);
        LineTo lineTo6 = new LineTo(getWidth(),getHeight()-70-containedBlockHeight2-containedBlockHeight1);
        LineTo lineTo7 = new LineTo(52,getHeight()-70-containedBlockHeight2-containedBlockHeight1);
        LineTo lineTo8 = new LineTo(48,getHeight()-67-containedBlockHeight2-containedBlockHeight1);
        LineTo lineTo9 = new LineTo(38,getHeight()-67-containedBlockHeight2-containedBlockHeight1);
        LineTo lineTo10 = new LineTo(34,getHeight()-70-containedBlockHeight2-containedBlockHeight1);
        LineTo lineTo11 = new LineTo(16,getHeight()-70-containedBlockHeight2-containedBlockHeight1);
        LineTo lineTo12 = new LineTo(16,getHeight()-58-containedBlockHeight1);
        LineTo lineTo13 = new LineTo(getWidth(),getHeight()-58-containedBlockHeight1);
        LineTo lineTo14 = new LineTo(getWidth(),getHeight()-36-containedBlockHeight1);
        LineTo lineTo15 = new LineTo(52,getHeight()-36-containedBlockHeight1);
        LineTo lineTo16 = new LineTo(48,getHeight()-33-containedBlockHeight1);
        LineTo lineTo17 = new LineTo(38,getHeight()-33-containedBlockHeight1);
        LineTo lineTo18 = new LineTo(34,getHeight()-36-containedBlockHeight1);
        LineTo lineTo19 = new LineTo(16,getHeight()-36-containedBlockHeight1);
        LineTo lineTo20 = new LineTo(16,getHeight()-24);
        LineTo lineTo21 = new LineTo(getWidth(),getHeight()-24);
        LineTo lineTo22 = new LineTo(getWidth(),getHeight()-3);
        LineTo lineTo23 = new LineTo(34,getHeight()-3);
        LineTo lineTo24 = new LineTo(30,getHeight());
        LineTo lineTo25 = new LineTo(20,getHeight());
        LineTo lineTo26 = new LineTo(16,getHeight()-3);
        LineTo lineTo27 = new LineTo(0,getHeight()-3);
        path.getElements().addAll(moveTo,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5,lineTo6,lineTo7,lineTo8,lineTo9,lineTo10,lineTo11,lineTo12,lineTo13,lineTo14,lineTo15,lineTo16,
                lineTo17,lineTo18,lineTo19,lineTo20,lineTo21,lineTo22,lineTo23,lineTo24,lineTo25,lineTo26,lineTo27,new ClosePath());
        this.setShape(path);

        this.plugs.get(0).setPoint2D(new Point2D(25,getHeight()));      //lowest plug
        this.plugs.get(1).setPoint2D(new Point2D(43,getHeight()-32-containedBlockHeight1));      //inner plug
        this.plugs.get(2).setPoint2D(new Point2D(43,getHeight()-66-containedBlockHeight2-containedBlockHeight1));
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
        code.define.add(blockSpec.code.def);
        code.include.add(blockSpec.code.inc);
        code.code.append("{\n");
        if(this.plugs.get(2).getBlock()!=null){
            this.plugs.get(2).getBlock().generateCode(code);
        }
        code.code.append("}else{\n");
        if(this.plugs.get(1).getBlock()!=null){
            this.plugs.get(1).getBlock().generateCode(code);
        }
        code.code.append("}\n");
        if(this.plugs.get(0).getBlock()!=null && !(this.plugs.get(0).getBlock() instanceof ForeverLoopBlock)){
            this.plugs.get(0).getBlock().generateCode(code);
        }
    }


}
