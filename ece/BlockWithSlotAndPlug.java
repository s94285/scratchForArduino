package ece;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public abstract class BlockWithSlotAndPlug extends BlockWithPlug {
    public PointBlockPair<BlockWithPlug> slot = new PointBlockPair<>(new Point2D(25,0),null);

    public BlockWithSlotAndPlug(String arg, String blockName, Pane drawingPane, int plugCount){
        super(arg, blockName,drawingPane,plugCount);
        this.layoutXProperty().addListener(((observableValue, oldValue, newValue) -> reAllocate()));
        this.layoutYProperty().addListener(((observableValue, oldValue, newValue) -> reAllocate()));

    }
    @Override
    public void onMouseDragged(MouseEvent mouseEvent) {
        super.onMouseDragged(mouseEvent);
        System.out.println("slot dragged" + slot.getBlock());
        if(slot.getBlock()!=null){

            BlockWithPlug blockWithPlug = slot.getBlock();
            for(int i=0;i<blockWithPlug.plugs.size();i++){
                if(blockWithPlug.plugs.get(i).getBlock()==this){
                    blockWithPlug.plugs.get(i).setBlock(null);
                    this.slot.setBlock(null);
                    break;
                }
            }
        }
    }

    @Override
    public void onMouseReleased(MouseEvent mouseEvent) {
        super.onMouseReleased(mouseEvent);
             //find plug for slot

            for(Node node : drawingPane.getChildren()){
//                System.out.println(node);
                if(node instanceof BlockWithPlug){

                    BlockWithPlug blockWithPlug = (BlockWithPlug)node;
//                    System.out.println("blockWithSlotAndPlot: " + blockWithSlotAndPlug);
                    Point2D slotPoint2D = blockWithPlug.localToScene(25,((BlockWithPlug) node).getHeight());
//                    System.out.println("Plug Pos: " + plugs.get(i).getKey());
                    if(this.localToScene(slot.getPoint2D()).distance(slotPoint2D)<15){
                        System.out.println("Found");
                        //plugs.get(0).setBlock(blockWithSlotAndPlug);
//                        plugs.set(i,new Pair<Point2D, BlockWithSlotAndPlug>(plugs.get(i).getKey(),blockWithSlotAndPlug));
                        this.slot.setBlock(blockWithPlug);
                        blockWithPlug.plugs.get(0).setBlock(this);
                        //new Pair<Point2D, BlockWithPlug>(blockWithSlotAndPlug.slot.getKey(),this);
                        this.setLayoutX(blockWithPlug.getLayoutX());
                        this.reAllocate();
                        this.setLayoutY(blockWithPlug.getLayoutY()+blockWithPlug.getHeight());
                    }
                }
            }


    }
}