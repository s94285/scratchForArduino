package ece;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueBlock extends Block {
    protected StackPane selectedStackPane = null;
    public ValueBlock(BlockSpec blockSpec,Pane drawingPane) {
        super(blockSpec,drawingPane);
        this.setAlignment(Pos.CENTER_LEFT);

        reShape();

        this.setBackground(new Background(new BackgroundFill(Color.rgb(92, 183, 18), CornerRadii.EMPTY, Insets.EMPTY)));
    }
    @Override
    public void reShape() {
        autosize();
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
        this.setShape(path);
    }

    @Override
    public void generateCode(Code code) {
        ArrayList<StackPane> allStackPanes = new ArrayList<>();
        //get all stackPanes
        for(Node node:titlePane.getChildren())
            if(node instanceof StackPane)
                allStackPanes.add((StackPane)node);

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
        System.out.println("setupString.length = "+setupString.length);
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
        code.setup.add(setup.toString());
        code.include.add(blockSpec.code.inc);
    }

    private ArrayList<StackPane> getStackPanes(Pane pane){
        ArrayList<StackPane> list = new ArrayList<>();
        for(Node nodeInPane : pane.getChildren()){
            if(nodeInPane == this)continue;
            if(nodeInPane instanceof Block){
                Block block = (Block)nodeInPane;
                for(Node nodeInTitlePane : block.getTitlePane().getChildren()){
                    if(nodeInTitlePane instanceof StackPane){
                        StackPane stackPane = (StackPane)nodeInTitlePane;
                        if(stackPane.getChildren().size()>1){
                            list.addAll(getStackPanes(stackPane));
                        }else{
                            if(((TextField)stackPane.getChildren().get(0)).editableProperty().getValue())
                                list.add(stackPane);
                        }
                    }
                }

            }
        }
        return list;
    }

    @Override
    public void onMousePressed(MouseEvent mouseEvent) {
        super.onMousePressed(mouseEvent);
        Node node = (Node)mouseEvent.getTarget();
        while(node != drawingPane){
            if(node instanceof Block)break;
            node = node.getParent();
        }
        if(node != this)return;
        if(this.getParent()!=drawingPane){
            Point2D blockScenePoint = this.localToScene(0,0);
            //System.out.println("test");
            //System.out.println(blockScenePoint);
            drawingPane.getChildren().add(this);
            this.relocate(drawingPane.sceneToLocal(blockScenePoint).getX(),drawingPane.sceneToLocal(blockScenePoint).getY());
            System.out.println(this.getLayoutX() + " " + this.getLayoutY());
        }
        super.onMousePressed(mouseEvent);
        mouseEvent.consume();
    }

    @Override
    public void onMouseDragged(MouseEvent mouseEvent) {
        super.onMouseDragged(mouseEvent);
//        System.out.println(this + " " + mouseEvent.getTarget());

        Transform blockTransformToScene = this.getLocalToSceneTransform();
        Point2D blockScenePoint = blockTransformToScene.transform(0,0);
        ArrayList<StackPane> stackPanes = getStackPanes(drawingPane);
        for(StackPane stackPane : stackPanes) {
            Transform stackPaneTransformToScene = stackPane.getLocalToSceneTransform();
            Point2D stackPaneScenePoint = stackPaneTransformToScene.transform(0, stackPane.getHeight()/2.0);
            if (stackPaneScenePoint.distance(blockScenePoint) < 15) {
                stackPane.setEffect(new DropShadow(10, Color.BLACK));
                selectedStackPane = stackPane;
                return;
            } else {

                stackPane.setEffect(null);
                selectedStackPane = null;
            }
        }
    }

    @Override
    public void onMouseReleased(MouseEvent mouseEvent) {
        super.onMouseReleased(mouseEvent);
        if(selectedStackPane != null){
//            System.out.println("selectedStackPane");
            try {

                selectedStackPane.getChildren().add(this);
                selectedStackPane.setEffect(null);
            }catch (Exception e){
                //do nothing;
            }
            selectedStackPane = null;
        }
    }
}