package ece;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.util.ArrayList;

public abstract class BlockWithSlotAndPlug extends BlockWithPlug {
    public PointBlockPair<BlockWithPlug> slot = new PointBlockPair<>(new Point2D(25,0),null);

    public BlockWithSlotAndPlug(BlockSpec blockSpec, Pane drawingPane, int plugCount){
        super(blockSpec,drawingPane,plugCount);
        this.heightProperty().removeListener(this.sizeChangeListener);
        this.heightProperty().addListener((observableValue, number, t1) -> {
            reShape();
            BlockWithPlug blockWithPlug1 = this;
            while(blockWithPlug1!=null){
                if(blockWithPlug1 instanceof BlockWithSlotAndPlug){
                    if((blockWithPlug1 instanceof ControlBlock || blockWithPlug1 instanceof IfandElseBlock|| blockWithPlug1 instanceof ForeverLoopBlock) && blockWithPlug1 != this){
                        blockWithPlug1.reShape();
                    }
                    blockWithPlug1 = ((BlockWithSlotAndPlug)blockWithPlug1).slot.getBlock();
                }else{
                    blockWithPlug1 = null;
                }
            }
        });

    }
    @Override
    public void onMouseDragged(MouseEvent mouseEvent) {
        if(mouseEvent.getButton() == MouseButton.SECONDARY)return;
        super.onMouseDragged(mouseEvent);
//        System.out.println("slot dragged" + slot.getBlock());
        if(slot.getBlock()!=null){

            BlockWithPlug blockWithPlug = slot.getBlock();
            for(int i=0;i<blockWithPlug.plugs.size();i++){
                if(blockWithPlug.plugs.get(i).getBlock()==this){
                    blockWithPlug.plugs.get(i).setBlock(null);
                    BlockWithPlug blockWithPlug1 = blockWithPlug;
                    while(blockWithPlug1!=null){
                        if(blockWithPlug1 instanceof BlockWithSlotAndPlug){
                            if((blockWithPlug1 instanceof ControlBlock || blockWithPlug1 instanceof IfandElseBlock|| blockWithPlug1 instanceof ForeverLoopBlock))blockWithPlug1.reShape();
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
        if(mouseEvent.getButton() == MouseButton.SECONDARY)return;
        super.onMouseReleased(mouseEvent);
        //find plug for slot

        for(Node node : drawingPane.getChildren()){
            if(node instanceof javafx.scene.shape.Rectangle)continue;
//                System.out.println(node);
            if(node instanceof BlockWithPlug){

                BlockWithPlug blockWithPlug = (BlockWithPlug)node;
//                    System.out.println("blockWithSlotAndPlot: " + blockWithSlotAndPlug);
                for(PointBlockPair<BlockWithSlotAndPlug> plug : blockWithPlug.plugs){
                    Point2D slotPoint2D = blockWithPlug.localToScene(plug.getPoint2D());
//                    System.out.println("Plug Pos: " + plugs.get(i).getKey());
                    if(this.localToScene(slot.getPoint2D()).distance(slotPoint2D)<15){
                        System.out.println("Found");
                        if(plug.getBlock()!=null && plug.getBlock()!=this){
                            //get my lowest block's plug
                            BlockWithPlug lowest = this;
                            while(lowest.plugs.get(0).getBlock()!=null){
                                lowest = lowest.plugs.get(0).getBlock();
                            }
                            //insert this and lower blocks in between
                            lowest.plugs.get(0).setBlock(plug.getBlock());
                            plug.getBlock().slot.setBlock(lowest);
                            this.slot.setBlock(blockWithPlug);
                            plug.setBlock(this);
                        }else {
                            this.slot.setBlock(blockWithPlug);
                            plug.setBlock(this);
                        }
                        //reshape
                        BlockWithPlug blockWithPlug1 = blockWithPlug;
                        while(blockWithPlug1!=null){
                            if(blockWithPlug1 instanceof BlockWithSlotAndPlug){
                                if((blockWithPlug1 instanceof ControlBlock || blockWithPlug1 instanceof IfandElseBlock|| blockWithPlug1 instanceof ForeverLoopBlock))blockWithPlug1.reShape();
                                blockWithPlug1 = ((BlockWithSlotAndPlug)blockWithPlug1).slot.getBlock();
                            }else{
                                blockWithPlug1 = null;
                            }
                        }
                        //move current blockWithSlot to plug's location
                        this.setLayoutX(blockWithPlug.localToParent(plug.getPoint2D()).getX()-slot.getPoint2D().getX());
                        this.setLayoutY(blockWithPlug.localToParent(plug.getPoint2D()).getY()-slot.getPoint2D().getY());
                        this.reAllocate();
                    }
                }

            }
        }
    }
}