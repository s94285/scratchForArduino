package ece;

import javafx.geometry.Point2D;

public class PointBlockPair<BlockType> {
    private Point2D point2D;
    private BlockType block;
    public PointBlockPair(Point2D point2D,BlockType block) {
        this.point2D = point2D;
        this.block = block;
    }

    public void setBlock(BlockType block) {
        this.block = block;
    }

    public BlockType getBlock() {
        return block;
    }

    public void setPoint2D(Point2D point2D) {
        this.point2D = point2D;
    }

    public Point2D getPoint2D() {
        return point2D;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
