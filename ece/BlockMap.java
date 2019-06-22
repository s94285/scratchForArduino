package ece;

import javafx.util.Pair;

import java.util.ArrayList;

public class BlockMap {
    public String color;
    public double layoutX;
    public double layoutY;
    public BlockSpec blockSpec;
    public ArrayList<TitleField> titleFields;
    public BlockMap[] plugs = new BlockMap[3];
    public ArrayList<MyPair<FunctionDialogController.ArgumentType,String>> functionSpec;

    static class TitleField{
        public String value;
        public BlockMap block;
    }
}
