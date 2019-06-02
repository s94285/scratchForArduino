package ece;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BlockSpec {
    public String name;
    public String type;
    public String title;
    public ArrayList<String> field;      //default value
    public LinkedHashMap<String,ArrayList<String>> menu;
    public LinkedHashMap<String,String> value;
    public BlockSpecCode code;

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(name).append("\n");
        str.append(type).append("\n");
        str.append(title).append("\n");
        for(String s:field)
            str.append(s).append("\n");
        str.append(menu).append("\n");
        str.append(value).append("\n");
        str.append(code).append("\n");
        return str.toString();
    }

    static class BlockSpecCode {
        public String inc;
        public String def;
        public String setup;
        public String work;
        public String loop;

        public String toString(){
            StringBuilder str = new StringBuilder();
            str.append(inc).append("\n");
            str.append(def).append("\n");
            str.append(setup).append("\n");
            str.append(work).append("\n");
            str.append(loop).append("\n");
            return str.toString();
        }
    }
}
