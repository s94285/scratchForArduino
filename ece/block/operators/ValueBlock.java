package ece.block.operators;

import ece.block.Block;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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

        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        moveTo.setX(12);
        moveTo.setY(29);
        ArcTo arcTo1 = new ArcTo();
        arcTo1.setX(12);
        arcTo1.setY(0);
        arcTo1.setRadiusX(12);
        arcTo1.setRadiusY(15);
        arcTo1.setSweepFlag(true);
        LineTo lineTo1 = new LineTo(62, 0);
        ArcTo arcTo2 = new ArcTo(12, 15, 0, 62, 29, false, true);
        path.getElements().addAll(moveTo, arcTo1, lineTo1, arcTo2, new ClosePath());
        this.setShape(path);
        this.setBackground(new Background(new BackgroundFill(Color.rgb(92, 183, 18), CornerRadii.EMPTY, Insets.EMPTY)));


//        this.heightProperty().addListener((observableValue, oldValue, newValue) -> reShape());
//        this.widthProperty().addListener((observableValue, oldValue, newValue) -> reShape());

        //overriding dragging event
//        this.setOnMousePressed(mouseEvent -> {
//            onMousePressed(mouseEvent);
//
//            onMousePressed(mouseEvent);
//            mouseEvent.consume();
//        });
//        this.setOnMouseDragged(mouseEvent -> {
//
////            Node node = (Node)mouseEvent.getTarget();
////            while(node != drawingPane){
////                if(node instanceof Block)break;
////                node = node.getParent();
////            }
////            if(node != this)return;
//
//        });
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
//                ((Pane)this.getParent()).getChildren().remove(this);
//                this.setLayoutX();
//
//                this.getParent().getParent().getParent().setLayoutX(500);
//                this.getParent().getParent().getParent().setLayoutY(500);
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
//                                System.out.println("block" + blockScenePoint);
//                                System.out.println("stackPane" + stackPaneScenePoint);
                stackPane.setEffect(new DropShadow(2, Color.RED));
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
//                ((Pane)this.getParent()).getChildren().remove(this);
            try {

                selectedStackPane.getChildren().add(this);

            }catch (Exception e){
                //do nothing;
            }
            selectedStackPane = null;
        }
    }
}