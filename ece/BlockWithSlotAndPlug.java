package ece;

import ece.block.Block;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.util.ArrayList;

public abstract class BlockWithSlotAndPlug extends BlockWithPlug {
    public Pair<Point2D,BlockWithPlug> slot = new Pair<>(new Point2D(25,0),null);

    public BlockWithSlotAndPlug(String arg, String blockName, Pane drawingPane, int plugCount){
        super(arg, blockName,drawingPane,plugCount);
    }
    @Override
    public void onMouseDragged(MouseEvent mouseEvent) {
        super.onMouseDragged(mouseEvent);

    }
}