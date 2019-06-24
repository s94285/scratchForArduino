package ece;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooleanBlock extends Block {
    protected StackPane selectedStackPane = null;
    private ArrayList<StackPane> currentStackPanes;

    public BooleanBlock(BlockSpec blockSpec,Pane drawingPane) {
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
        moveTo.setX(0);
        moveTo.setY(getHeight()/2);
        LineTo lineTo1 = new LineTo(8, 0);
        LineTo lineTo2 = new LineTo(getWidth()-8, 0);
        LineTo lineTo3 = new LineTo(getWidth(), getHeight()/2);
        LineTo lineTo4 = new LineTo(getWidth()-8, getHeight());
        LineTo lineTo5 = new LineTo(8, getHeight());
        path.getElements().addAll(moveTo,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5, new ClosePath());
        this.setShape(path);
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

        //check for comboBox first time
        String newWorkString = this.blockSpec.code.work;
        for(int i=0;i<allStackPanes.size();i++){
            StackPane stackPane = allStackPanes.get(i);
            if(stackPane.getChildren().size()==1){
                Node node = stackPane.getChildren().get(0);
                if(node instanceof ComboBox && ((ComboBox)node).getValue() instanceof String) {
                    String keyString = ((ComboBox<String>) stackPane.getChildren().get(0)).getValue();
                    if(blockSpec.value != null && blockSpec.value.containsKey(keyString))keyString = blockSpec.value.get(keyString);
                    newWorkString = newWorkString.replaceAll("\\{"+i+"}",keyString);
                }
            }
        }

        Matcher matcherWork = pattern.matcher(newWorkString);
        ArrayList<Integer> argCountWork = new ArrayList<>();
        while(matcherWork.find())
            argCountWork.add(Integer.valueOf(matcherWork.group()));

        String[] workString = newWorkString.split("\\{[0-9]+}");
        if(workString.length>0){
            code.code.append(workString[0]);
            for(int i=1;i<workString.length;i++){
                StackPane stackPane = allStackPanes.get(argCountWork.get(i-1));
                if(stackPane.getChildren().size()>1){
                    //has inner block
                    ((Block)stackPane.getChildren().get(1)).generateCode(code);
                }else{
                    Node node = stackPane.getChildren().get(0);
                    if(node instanceof TextField) {
                        if (node.getUserData().equals("string"))
                            code.code.append('"');
                        code.code.append(((TextField) stackPane.getChildren().get(0)).getText());
                        if (node.getUserData().equals("string"))
                            code.code.append('"');
                    }else if(node instanceof ComboBox && ((ComboBox)node).getValue() instanceof String) {
                        String keyString = ((ComboBox<String>) stackPane.getChildren().get(0)).getValue();
                        if(blockSpec.value != null && blockSpec.value.containsKey(keyString))keyString = blockSpec.value.get(keyString);
                        code.code.append(keyString);
                    }
                }
                code.code.append(workString[i]);
            }
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
                Node node = stackPane.getChildren().get(0);
                if(node instanceof TextField) {
                    if (node.getUserData().equals("string"))
                        setup.append('"');
                    setup.append(((TextField) stackPane.getChildren().get(0)).getText());
                    if (node.getUserData().equals("string"))
                        setup.append('"');
                }else if(node instanceof ComboBox && ((ComboBox)node).getValue() instanceof String){
                    String keyString = ((ComboBox<String>) stackPane.getChildren().get(0)).getValue();
                    if(blockSpec.value != null && blockSpec.value.containsKey(keyString))keyString = blockSpec.value.get(keyString);
                    setup.append(keyString);
                }
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
                Node node = stackPane.getChildren().get(0);
                if(node instanceof TextField) {
                    if (node.getUserData().equals("string"))
                        define.append('"');
                    define.append(((TextField) stackPane.getChildren().get(0)).getText());
                    if (node.getUserData().equals("string"))
                        define.append('"');
                }else if(node instanceof ComboBox && ((ComboBox)node).getValue() instanceof String){
                    String keyString = ((ComboBox<String>) stackPane.getChildren().get(0)).getValue();
                    if(blockSpec.value != null && blockSpec.value.containsKey(keyString))keyString = blockSpec.value.get(keyString);
                    define.append(keyString);
                }
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
        if(mouseEvent.getButton() == MouseButton.SECONDARY){
            this.duplicate();
            mouseEvent.consume();
            return;
        }

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
            ((Pane)this.getParent()).getChildren().get(0).setManaged(true);
            ((Pane)this.getParent()).getChildren().get(0).setVisible(true);
            drawingPane.getChildren().add(this);
            this.relocate(drawingPane.sceneToLocal(blockScenePoint).getX(),drawingPane.sceneToLocal(blockScenePoint).getY());
            System.out.println(this.getLayoutX() + " " + this.getLayoutY());
        }
        currentStackPanes = getStackPanes(drawingPane);
        super.onMousePressed(mouseEvent);
        mouseEvent.consume();
    }

    @Override
    public void onMouseDragged(MouseEvent mouseEvent) {
        if(mouseEvent.getButton() == MouseButton.SECONDARY)return;
        super.onMouseDragged(mouseEvent);
//        System.out.println(this + " " + mouseEvent.getTarget());

        Transform blockTransformToScene = this.getLocalToSceneTransform();
        Point2D blockScenePoint = blockTransformToScene.transform(0,0);
        for(StackPane stackPane : currentStackPanes) {
            Transform stackPaneTransformToScene = stackPane.getLocalToSceneTransform();
            Point2D stackPaneScenePoint = stackPaneTransformToScene.transform(0, stackPane.getHeight()/2.0);
            if (stackPaneScenePoint.distance(blockScenePoint) < 15) {
                stackPane.setEffect(new DropShadow(10, Color.BLACK));
                selectedStackPane = stackPane;
                return;
            } else {

                stackPane.setEffect(null);
//                stackPane.getChildren().get(0).setVisible(true);
                selectedStackPane = null;
            }
        }
    }

    @Override
    public void onMouseReleased(MouseEvent mouseEvent) {
        if(mouseEvent.getButton() == MouseButton.SECONDARY)return;
        super.onMouseReleased(mouseEvent);
        if(selectedStackPane != null){
//            System.out.println("selectedStackPane");
            try {

                selectedStackPane.getChildren().add(this);
                selectedStackPane.getChildren().get(0).setManaged(false);
                selectedStackPane.getChildren().get(0).setVisible(false);
                selectedStackPane.setEffect(null);
            }catch (Exception e){
                //do nothing;
            }
            selectedStackPane = null;
        }
    }
}
