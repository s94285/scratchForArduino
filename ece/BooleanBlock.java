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

public class BooleanBlock extends Block {
    protected StackPane selectedStackPane = null;
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
        String[] setupString = this.blockSpec.code.setup.split("\\{[0-9]+}");
        StringBuilder setup = new StringBuilder();
        setup.append(setupString[0]);
        for(int i=1;i<setupString.length;i++){
            StackPane stackPane = allStackPanes.get(i-1);
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
        code.setup.add(setup.toString());
        code.define.add(blockSpec.code.def);
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
            this.getParent().getChildrenUnmodifiable().get(0).setVisible(true);
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
//                stackPane.getChildren().get(0).setVisible(true);
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
                selectedStackPane.getChildren().get(0).setVisible(false);
                selectedStackPane.getChildren().add(this);
                selectedStackPane.setEffect(null);
            }catch (Exception e){
                //do nothing;
            }
            selectedStackPane = null;
        }
    }
}
