package ece;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public abstract class BlockWithPlug extends Block {
    public ArrayList<PointBlockPair<BlockWithSlotAndPlug>> plugs = new ArrayList<>();

    public BlockWithPlug(BlockSpec blockSpec, Pane drawingPane, int plugCount) {
        super(blockSpec, drawingPane);
        for (int i = 0; i < plugCount; i++)
            plugs.add(new PointBlockPair<BlockWithSlotAndPlug>(new Point2D(0, 0), null));
        this.layoutXProperty().addListener(((observableValue, oldValue, newValue) -> reAllocate()));
        this.layoutYProperty().addListener(((observableValue, oldValue, newValue) -> reAllocate()));
    }

    @Override
    public void onMouseReleased(MouseEvent mouseEvent) {
        super.onMouseReleased(mouseEvent);
        if (this.getLayoutX() + mouseEvent.getX() < 0) {
            //delete succeeding blocks
            deleteSubBlocks(this);
            return;
        }
        for (PointBlockPair<BlockWithSlotAndPlug> plug : plugs) {        //find slots for each plug
            //obtain lowest block's plug
            BlockWithPlug lowest = this;
            while (plug.getBlock() != null) {
                lowest = plug.getBlock();
                plug = plug.getBlock().plugs.get(0);
            }
            for (Node node : drawingPane.getChildren()) {
//                System.out.println(node);
                if (node instanceof BlockWithSlotAndPlug) {

                    BlockWithSlotAndPlug blockWithSlotAndPlug = (BlockWithSlotAndPlug) node;
//                    System.out.println("blockWithSlotAndPlot: " + blockWithSlotAndPlug);
                    Point2D slotPoint2D = blockWithSlotAndPlug.localToScene(25, 0);
//                    System.out.println("Plug Pos: " + plugs.get(i).getKey());
                    if (lowest.localToScene(plug.getPoint2D()).distance(slotPoint2D) < 15) {
                        System.out.println("Found");
                        if (blockWithSlotAndPlug.slot.getBlock() == null) {
                            plug.setBlock(blockWithSlotAndPlug);

                            blockWithSlotAndPlug.slot.setBlock(lowest);

                            lowest.reAllocate();
                            break;
                        }


                    }
                }
            }
        }

        bringToFront(this);
    }

    private void deleteSubBlocks(BlockWithPlug blockWithPlug) {     //delete blocks by recursive method
        if(blockWithPlug!=this)drawingPane.getChildren().remove(blockWithPlug);     //this has been delete in Block.java
        for (PointBlockPair<BlockWithSlotAndPlug> plug : blockWithPlug.plugs){
            BlockWithSlotAndPlug block = plug.getBlock();
            if(block!=null){
                plug.setBlock(null);
                block.slot.setBlock(null);
                deleteSubBlocks(block);
            }
        }
    }


    public void reAllocate() {
        for (PointBlockPair<BlockWithSlotAndPlug> plug : plugs) {
            BlockWithSlotAndPlug blockWithSlotAndPlug = plug.getBlock();
            if (blockWithSlotAndPlug != null) {

                blockWithSlotAndPlug.setLayoutX(this.localToParent(plug.getPoint2D()).getX() - 25);
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
//        System.out.println("plug dragged " + plugs.get(0).getBlock());
    }

    @Override
    public void onMousePressed(MouseEvent mouseEvent) {
        super.onMousePressed(mouseEvent);
        bringToFront(this);
    }

    private void bringToFront(BlockWithPlug blockWithPlug) {
        if (blockWithPlug != this) {        //this already done in Block
            drawingPane.getChildren().remove(blockWithPlug);
            drawingPane.getChildren().add(blockWithPlug);
        }
        for (PointBlockPair<BlockWithSlotAndPlug> pair : blockWithPlug.plugs) {
            if (pair.getBlock() != null)
                bringToFront(pair.getBlock());
        }

    }
}
