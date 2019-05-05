package ece.block;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;


public class Block extends Pane {
    protected final Pane drawingPane;
    private double nowlayoutx,nowlayouty;
    private double pressedx,pressedy;
    protected HBox titlePane = new HBox();
    protected String blockName;
    private  boolean flag=false;
    public Block(Pane drawingPane){
        this.drawingPane = drawingPane;
//        this.setHgap(8);
        //rec=new Rectangle(200,200);
        //rec.setFill(BLUE);
        //Circle c=new Circle(50);
        //this.setBackground(new Background(new BackgroundFill(YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        //this.setShape(c);

        this.setOnMousePressed(mouseEvent -> {
            pressed(mouseEvent.getSceneX(),mouseEvent.getSceneY(),mouseEvent);
        });
        this.setOnMouseDragged(mouseEvent -> {
            dragged(mouseEvent.getSceneX(),mouseEvent.getSceneY());
        });
        //this.setPrefWidth(U);

        //this.getChildren().add(rec);
    }
    public  void pressed(double mousex, double mousey, MouseEvent mouseEvent){

        Pane myParent = (Pane) this.getParent();
        myParent.getChildren().remove(this);        //put self to the front
        myParent.getChildren().add(this);
        nowlayoutx=this.getLayoutX();
        nowlayouty=this.getLayoutY();
        pressedx=mousex;
        pressedy=mousey;
    }
    public void dragged(double mousex,double mousey){

        this.setLayoutX(nowlayoutx+(mousex-pressedx));
        this.setLayoutY(nowlayouty+(mousey-pressedy));
        pressedx=mousex;
        pressedy=mousey;
        nowlayoutx=this.getLayoutX();
        nowlayouty=this.getLayoutY();
    }
    public void setBlockName(String blockName){this.blockName = blockName;}
    public String getBlockName(){return blockName;}
    public HBox getTitlePane(){return titlePane;}
}
