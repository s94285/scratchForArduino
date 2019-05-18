package ece;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public abstract class Block extends VBox {
    protected final Pane drawingPane;
    private double nowlayoutx,nowlayouty;
    private double pressedx,pressedy;
    protected HBox titlePane = new HBox();
    protected String blockName;
    protected ChangeListener<?super Number> sizeChangeListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            System.out.println("called in block height changed");
            reShape();
        }
    };
    private  boolean flag=false;
    public Block(String arg,String blockName,Pane drawingPane){
        this.drawingPane = drawingPane;
        this.blockName = blockName;
        StringBuilder tmp = new StringBuilder();
        for(int i=0;i<arg.length();i++){
            if(arg.charAt(i)=='%'){
                if (tmp.length() > 0) {
                    Label label = new Label(tmp.toString());
                    label.setFont(new Font(17));
                    label.setTextFill(Color.WHITE);
                    this.titlePane.getChildren().add(label);
                    tmp = new StringBuilder();
                }
                StackPane stackPane = new StackPane();
                switch (arg.charAt(++i)) {      //read next character
                    case 'm':
                        break;
                    case 'd':
                        break;
                    case 'n': {
                        TextField textField = new TextField("0");
                        textField.setAlignment(Pos.CENTER);
                        textField.setPrefColumnCount(1);
                        textField.setFont(new Font(14));
                        textField.setPadding(new Insets(0,2,0,2));
                        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
                            System.out.println(newValue.length());
                            Text text = new Text(newValue);
                            double width = text.getLayoutBounds().getWidth()*1.17 // This big is the Text in the TextField
                                    + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                                    + 2d; // Add some spacing
                            textField.setPrefWidth(width+1); // Set the width
                            textField.positionCaret(textField.getCaretPosition()); // If you remove this line, it flashes a little bit
                        });

                        textField.setShape(new Circle(5));
                        textField.textProperty().addListener((observableValue, oldValue, newValue)->{
                            if(!newValue.matches("\\d*")){
                                textField.setText(oldValue);
                            }
                            Path path = new Path();
                            MoveTo moveTo = new MoveTo();
                            moveTo.setX(12);
                            moveTo.setY(getHeight());
                            ArcTo arcTo1 = new ArcTo();
                            arcTo1.setX(12);
                            arcTo1.setY(0);
                            arcTo1.setRadiusX(12);
                            arcTo1.setRadiusY(getHeight()/2);
                            arcTo1.setSweepFlag(true);
                            LineTo lineTo1 = new LineTo(getWidth()-12, 0);
                            ArcTo arcTo2 = new ArcTo(12, getHeight()/2, 0,getWidth()-12 , getHeight(), false, true);
                            path.getElements().addAll(moveTo, arcTo1, lineTo1, arcTo2, new ClosePath());
                            textField.setShape(path);
                        });
                        stackPane.getChildren().add(textField);
                        break;
                    }
                    case 's':
                        break;
                    case 'b':
                        break;
                }
                this.titlePane.getChildren().add(stackPane);
            }else{
//                if(tmp.length() == 0)tmp.append(" ");
                tmp.append(arg.charAt(i));
            }
        }
        if(tmp.length()>0){
            Label label = new Label(tmp.toString());
            label.setFont(new Font(17));
            label.setTextFill(Color.WHITE);
            this.titlePane.getChildren().add(label);
        }
        titlePane.setAlignment(Pos.CENTER_LEFT);
        titlePane.setPadding(new Insets(5,5,5,5));
        this.setEffect(new DropShadow(5,Color.BLACK));
        this.setPrefWidth(USE_COMPUTED_SIZE);
        this.setPrefHeight(USE_COMPUTED_SIZE);
        this.getChildren().add(titlePane);
        this.setOnMousePressed(mouseEvent -> onMousePressed(mouseEvent));
        this.setOnMouseDragged(mouseEvent -> onMouseDragged(mouseEvent));
        this.setOnMouseReleased(mouseEvent -> onMouseReleased(mouseEvent));

        //this.setPrefWidth(U);

        //this.getChildren().add(rec);
        this.heightProperty().addListener(sizeChangeListener);
        this.widthProperty().addListener(sizeChangeListener);
    }
    public  void onMousePressed(MouseEvent mouseEvent){

        Pane myParent = (Pane) this.getParent();
        myParent.getChildren().remove(this);        //put self to the front
        myParent.getChildren().add(this);
        nowlayoutx=this.getLayoutX();
        nowlayouty=this.getLayoutY();
        pressedx=mouseEvent.getSceneX();
        pressedy=mouseEvent.getSceneY();
//        System.out.println("Mouse Pressed");
    }
    public void onMouseDragged(MouseEvent mouseEvent){
        this.setLayoutX(nowlayoutx+(mouseEvent.getSceneX()-pressedx));
        this.setLayoutY(nowlayouty+(mouseEvent.getSceneY()-pressedy));
        pressedx=mouseEvent.getSceneX();
        pressedy=mouseEvent.getSceneY();
        nowlayoutx=this.getLayoutX();
        nowlayouty=this.getLayoutY();
//        System.out.println("Mouse Dragged x:"+nowlayoutx+"y"+nowlayouty);
    }
    public void onMouseReleased(MouseEvent mouseEvent){
        //Do nothing
        if(this.getLayoutX()+mouseEvent.getX()<0){
            Pane myParent = (Pane) this.getParent();
            myParent.getChildren().remove(this);
        }

    }
    public void setBlockName(String blockName){this.blockName = blockName;}
    public String getBlockName(){return blockName;}
    public HBox getTitlePane(){return titlePane;}
    public abstract void reShape();
}
