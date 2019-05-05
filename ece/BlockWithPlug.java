package ece;

import ece.block.Block;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.util.ArrayList;

public abstract class BlockWithPlug extends Block {
    public ArrayList<PointBlockPair<BlockWithSlotAndPlug>> plugs = new ArrayList<>();
    public BlockWithPlug(String arg, String blockName, Pane drawingPane,int plugCount){
        super(arg, blockName,drawingPane);
        for(int i=0;i<plugCount;i++)
            plugs.add(new PointBlockPair<BlockWithSlotAndPlug>(new Point2D(0,0),null));
        this.layoutXProperty().addListener(((observableValue, oldValue, newValue) -> reAllocate()));
        this.layoutYProperty().addListener(((observableValue, oldValue, newValue) -> reAllocate()));

    }
    @Override
    public void onMouseReleased(MouseEvent mouseEvent) {
        super.onMouseReleased(mouseEvent);
        for(int i=0;i<plugs.size();i++){        //find slots
            for(Node node : drawingPane.getChildren()){
//                System.out.println(node);
                if(node instanceof BlockWithSlotAndPlug){

                    BlockWithSlotAndPlug blockWithSlotAndPlug = (BlockWithSlotAndPlug)node;
//                    System.out.println("blockWithSlotAndPlot: " + blockWithSlotAndPlug);
                    Point2D slotPoint2D = blockWithSlotAndPlug.localToScene(25,0);
//                    System.out.println("Plug Pos: " + plugs.get(i).getKey());
                    if(this.localToScene(plugs.get(i).getPoint2D()).distance(slotPoint2D)<15){
                        System.out.println("Found");
                        plugs.get(i).setBlock(blockWithSlotAndPlug);
//                        plugs.set(i,new Pair<Point2D, BlockWithSlotAndPlug>(plugs.get(i).getKey(),blockWithSlotAndPlug));

                        blockWithSlotAndPlug.slot.setBlock(this);
                                //new Pair<Point2D, BlockWithPlug>(blockWithSlotAndPlug.slot.getKey(),this);
                        this.setLayoutX(blockWithSlotAndPlug.getLayoutX());
                        this.setLayoutY(blockWithSlotAndPlug.getLayoutY()-this.getHeight());
                        break;
                    }
                }
            }
        }
    }
//
//    @Override
//    public void onMouseDragged(MouseEvent mouseEvent) {
//        super.onMouseDragged(mouseEvent);
//
//    }

    public void reAllocate(){
        for(PointBlockPair<BlockWithSlotAndPlug> plug : plugs){
            BlockWithSlotAndPlug blockWithSlotAndPlug = plug.getBlock();
            if(blockWithSlotAndPlug!=null) {

                blockWithSlotAndPlug.setLayoutX(this.localToParent(plug.getPoint2D()).getX()-25);
                blockWithSlotAndPlug.setLayoutY(this.localToParent(plug.getPoint2D()).getY());
            }
        }
    }

    @Override
    public void reShape() {
        reAllocate();
    }

    @Override
    public void onMouseDragged(MouseEvent mouseEvent) {
        super.onMouseDragged(mouseEvent);
        System.out.println("plug dragged " + plugs.get(0).getBlock());
    }
}
