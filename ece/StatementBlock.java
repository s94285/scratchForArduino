package ece;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementBlock extends BlockWithSlotAndPlug {
    public StatementBlock(BlockSpec blockSpec, Pane drawingPane) {
        super(blockSpec, drawingPane,1);
        reShape();
        this.setBackground(new Background(new BackgroundFill(Color.rgb(10, 134, 152), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @Override
    public void reShape() {
        autosize();
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

    @Override
    public void generateCode(Code code) {
        ArrayList<StackPane> allStackPanes = new ArrayList<>();
        //get all stackPanes
        for(Node node:titlePane.getChildren())
            if(node instanceof StackPane)
                allStackPanes.add((StackPane)node);
        //extract argument index from provided string e.g. {0} => 0
        String rule = "(?<=\\{)[0-9]+(?=})";    //look behind and look ahead
        Pattern pattern = Pattern.compile(rule);

        Matcher matcherSetup = pattern.matcher(this.blockSpec.code.setup);
        ArrayList<Integer> argCountSetup = new ArrayList<>();
        while(matcherSetup.find())
            argCountSetup.add(Integer.valueOf(matcherSetup.group()));

        Matcher matcherDefine = pattern.matcher(this.blockSpec.code.def);
        ArrayList<Integer> argCountDefine = new ArrayList<>();
        while(matcherDefine.find())
            argCountDefine.add(Integer.valueOf(matcherDefine.group()));

        Matcher matcherWork = pattern.matcher(this.blockSpec.code.work);
        ArrayList<Integer> argCountWork = new ArrayList<>();
        while(matcherWork.find())
            argCountWork.add(Integer.valueOf(matcherWork.group()));

        String[] workString = this.blockSpec.code.work.split("\\{[0-9]+}");
        code.code.append(workString[0]);
        for(int i=1;i<workString.length;i++){
            StackPane stackPane = allStackPanes.get(argCountWork.get(i-1));
            if(stackPane.getChildren().size()>1){
                //has inner block
                ((Block)stackPane.getChildren().get(1)).generateCode(code);
            }else{
                code.code.append(((TextField)stackPane.getChildren().get(0)).getText());
            }
            code.code.append(workString[i]);
        }


        String[] setupString = this.blockSpec.code.setup.split("\\{[0-9]+}");
        StringBuilder setup = new StringBuilder();
        setup.append(setupString[0]);
        for(int i=1;i<setupString.length;i++){
            StackPane stackPane = allStackPanes.get(argCountSetup.get(i-1));
            if(stackPane.getChildren().size()>1){
                //has inner block
                Code tmpCode = new Code();
                ((Block)stackPane.getChildren().get(1)).generateCode(tmpCode);
                setup.append(tmpCode.code);
            }else{
                setup.append(((TextField)stackPane.getChildren().get(0)).getText());
            }
            setup.append(setupString[i]);
        }

        String[] defineString = this.blockSpec.code.def.split("\\{[0-9]+}");
        StringBuilder define = new StringBuilder();
        define.append(defineString[0]);
        for(int i=1;i<defineString.length;i++){
            StackPane stackPane = allStackPanes.get(argCountDefine.get(i-1));
            if(stackPane.getChildren().size()>1){
                //has inner block
                Code tmpCode = new Code();
                ((Block)stackPane.getChildren().get(1)).generateCode(tmpCode);
                define.append(tmpCode.code);
            }else{
                define.append(((TextField)stackPane.getChildren().get(0)).getText());
            }
            define.append(defineString[i]);
        }

        code.define.add(define.toString());
        code.include.add(blockSpec.code.inc);
        code.setup.add(setup.toString());
        if(this.plugs.get(0).getBlock()!=null && !(this.plugs.get(0).getBlock() instanceof ForeverLoopBlock)){
            this.plugs.get(0).getBlock().generateCode(code);
        }
    }
}
