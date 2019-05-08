package ece.block.operators;

import ece.block.Block;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Transform;

import java.util.ArrayList;

public class ValueBlock extends Block {
    protected StackPane selectedStackPane = null;
    public ValueBlock(String arg, String blockName,Pane drawingPane) {
        super(arg,blockName,drawingPane);
        this.setAlignment(Pos.CENTER_LEFT);

        reShape();
        this.setBackground(new Background(new BackgroundFill(Color.rgb(92, 183, 18), CornerRadii.EMPTY, Insets.EMPTY)));
    }
    @Override
    public void reShape() {
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
    private ArrayList<StackPane> getStackPanes(Pane pane){
        ArrayList<StackPane> list = new ArrayList<StackPane>();
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

            System.out.println(blockScenePoint);
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
        System.out.println(this + " " + mouseEvent.getTarget());

        Transform blockTransformToScene = this.getLocalToSceneTransform();
        Point2D blockScenePoint = blockTransformToScene.transform(0,0);
        ArrayList<StackPane> stackPanes = getStackPanes(drawingPane);
        for(StackPane stackPane : stackPanes) {
            Transform stackPaneTransformToScene = stackPane.getLocalToSceneTransform();
            Point2D stackPaneScenePoint = stackPaneTransformToScene.transform(0, 0);
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
            System.out.println("selectedStackPane");
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