package ece;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public abstract class BlockWithSlotAndPlug extends BlockWithPlug {
    public PointBlockPair<BlockWithPlug> slot = new PointBlockPair<>(new Point2D(25,0),null);

    public BlockWithSlotAndPlug(String arg, String blockName, Pane drawingPane, int plugCount){
        super(arg, blockName,drawingPane,plugCount);
        this.layoutXProperty().addListener(((observableValue, oldValue, newValue) -> {
            reAllocate();
            BlockWithPlug blockWithPlug1 = BlockWithSlotAndPlug.this;
            while(blockWithPlug1!=null){
                if(blockWithPlug1 instanceof BlockWithSlotAndPlug){
                    if(blockWithPlug1 instanceof ControlBlock)blockWithPlug1.reShape();
                    blockWithPlug1 = ((BlockWithSlotAndPlug)blockWithPlug1).slot.getBlock();
                }else{
                    blockWithPlug1 = null;
                }
            }
        }));
        this.layoutYProperty().addListener(((observableValue, oldValue, newValue) -> {
            reAllocate();
            BlockWithPlug blockWithPlug1 = BlockWithSlotAndPlug.this;
            while(blockWithPlug1!=null){
                if(blockWithPlug1 instanceof BlockWithSlotAndPlug){
                    if(blockWithPlug1 instanceof ControlBlock)blockWithPlug1.reShape();
                    blockWithPlug1 = ((BlockWithSlotAndPlug)blockWithPlug1).slot.getBlock();
                }else{
                    blockWithPlug1 = null;
                }
            }
        }));

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
                    BlockWithPlug blockWithPlug1 = blockWithPlug;
                    while(blockWithPlug1!=null){
                        if(blockWithPlug1 instanceof BlockWithSlotAndPlug){
                            if(blockWithPlug1 instanceof ControlBlock)blockWithPlug1.reShape();
                            blockWithPlug1 = ((BlockWithSlotAndPlug)blockWithPlug1).slot.getBlock();
                        }else{
                            blockWithPlug1 = null;
                        }
                    }

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
                    for(PointBlockPair<BlockWithSlotAndPlug> plug : ((BlockWithPlug) node).plugs){
                        if(plug.getBlock()!=null)continue;
                        Point2D slotPoint2D = blockWithPlug.localToScene(plug.getPoint2D());
//                    System.out.println("Plug Pos: " + plugs.get(i).getKey());
                        if(this.localToScene(slot.getPoint2D()).distance(slotPoint2D)<15){
                            System.out.println("Found");

                            this.slot.setBlock(blockWithPlug);
                            plug.setBlock(this);
                            //reshape
                            BlockWithPlug blockWithPlug1 = blockWithPlug;
                            while(blockWithPlug1!=null){
                                if(blockWithPlug1 instanceof BlockWithSlotAndPlug){
                                    if(blockWithPlug1 instanceof ControlBlock)blockWithPlug1.reShape();
                                    blockWithPlug1 = ((BlockWithSlotAndPlug)blockWithPlug1).slot.getBlock();
                                }else{
                                    blockWithPlug1 = null;
                                }
                            }
                            //move current blockWithSlot to plug's location
                            this.setLayoutX(blockWithPlug.localToParent(plug.getPoint2D()).getX()-slot.getPoint2D().getX());
                            this.reAllocate();
                            this.setLayoutY(blockWithPlug.localToParent(plug.getPoint2D()).getY()-slot.getPoint2D().getY());
                        }
                    }

                }
            }


    }

    @Override
    public void reAllocate() {
        super.reAllocate();

    }
}