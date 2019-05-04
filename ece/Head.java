package ece;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class Head extends Block {
    private Label textLabel;
    private TextField textField;
    private ComboBox comboBox;
    public  Head(){
        comboBox=new ComboBox();

        comboBox.setItems(FXCollections.observableArrayList("A", "B", new Separator(), "C", "D"));
        textField=new TextField();
        textLabel=new Label();
        textLabel.setText("Main");
        //textLabel.setLayoutX(this.getLayoutX()+8);
        //textLabel.setLayoutY(this.getLayoutY()+2);
        this.getChildren().add(textLabel);
        this.getChildren().add(textField);
        this.getChildren().add(comboBox);
    }

}
