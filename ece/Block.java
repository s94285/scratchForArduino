package ece;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.event.* ;

import static javafx.scene.paint.Color.*;

public class Block extends FlowPane {
    private Rectangle rec;
    private double nowlayoutx,nowlayouty;
    private double pressedx,pressedy;
   /* private enum Type{
        HEAD(1);
        CONTAINER(2);
        STATEMENT(3);
        VALUE(4);

    }*/
    public Block(){

        this.setHgap(16);
        rec=new Rectangle(200,200);
        rec.setFill(BLUE);
        Circle c=new Circle(50);
        this.setBackground(new Background(new BackgroundFill(YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setShape(c);

        this.setOnMousePressed(mouseEvent -> {
            Pane myParent = (Pane) this.getParent();
            myParent.getChildren().remove(this);        //put self to the front
            myParent.getChildren().add(this);
            pressed(mouseEvent.getSceneX(),mouseEvent.getSceneY());
        });
        this.setOnMouseDragged(mouseEvent -> {
            dragged(mouseEvent.getSceneX(),mouseEvent.getSceneY());
        });
        //this.setPrefWidth(U);

        //this.getChildren().add(rec);
    }
    public  void pressed(double mousex,double mousey){
        nowlayoutx=this.getLayoutX();
        nowlayouty=this.getLayoutY();
        pressedx=mousex;
        pressedy=mousey;
        //System.out.print("777");
    }
    public void dragged(double mousex,double mousey){
        this.setLayoutX(nowlayoutx+(mousex-pressedx));
        this.setLayoutY(nowlayouty+(mousey-pressedy));
        pressedx=mousex;
        pressedy=mousey;
        nowlayoutx=this.getLayoutX();
        nowlayouty=this.getLayoutY();
        //System.out.print("666");
    }
}
