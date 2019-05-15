package ece;
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
    private  boolean flag=false;
    public Block(String arg,String blockName,Pane drawingPane){
       // this.setPrefWidth(USE_COMPUTED_SIZE);
        //this.setPrefHeight(USE_COMPUTED_SIZE);
        this.drawingPane = drawingPane;
        this.blockName = blockName;
        String args[] = arg.split(" ");
        StringBuilder tmp = new StringBuilder();
        for (String str : args) {
            if (str.charAt(0) == '%' && str.length() > 1) {
                if (tmp.length() > 0) {
                    Label label = new Label(tmp.toString());
                    label.setFont(new Font(17));
                    label.setTextFill(Color.WHITE);
                    this.titlePane.getChildren().add(label);
                    tmp = new StringBuilder();
                }
                StackPane stackPane = new StackPane();
                switch (str.charAt(1)) {
                    case 'm':
                        break;
                    case 'd':
                        break;
                    case 'n': {
                        TextField textField = new TextField("0");
                        textField.setAlignment(Pos.CENTER_LEFT);
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
            } else {
                if(tmp.length() == 0)tmp.append(" ");
                tmp.append(str).append(" ");
            }
        }
        if(tmp.length()>0){
            Label label = new Label(tmp.toString());
            label.setFont(new Font(17));
            label.setTextFill(Color.WHITE);
            this.titlePane.getChildren().add(label);
        }
//        this.setHgap(8);
        //rec=new Rectangle(200,200);
        //rec.setFill(BLUE);
        //Circle c=new Circle(50);
        //this.setBackground(new Background(new BackgroundFill(YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        //this.setShape(c);
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
        this.heightProperty().addListener((observableValue, oldValue, newValue) -> reShape());
        this.widthProperty().addListener((observableValue, oldValue, newValue) -> reShape());
    }
    public  void onMousePressed(MouseEvent mouseEvent){

        Pane myParent = (Pane) this.getParent();
        myParent.getChildren().remove(this);        //put self to the front
        myParent.getChildren().add(this);
        nowlayoutx=this.getLayoutX();
        nowlayouty=this.getLayoutY();
        pressedx=mouseEvent.getSceneX();
        pressedy=mouseEvent.getSceneY();
        System.out.println("Mouse Pressed");
    }
    public void onMouseDragged(MouseEvent mouseEvent){
        this.setLayoutX(nowlayoutx+(mouseEvent.getSceneX()-pressedx));
        this.setLayoutY(nowlayouty+(mouseEvent.getSceneY()-pressedy));
        pressedx=mouseEvent.getSceneX();
        pressedy=mouseEvent.getSceneY();
        nowlayoutx=this.getLayoutX();
        nowlayouty=this.getLayoutY();
        System.out.println("Mouse Dragged");
    }
    public void onMouseReleased(MouseEvent mouseEvent){
        //Do nothing
    }
    public void setBlockName(String blockName){this.blockName = blockName;}
    public String getBlockName(){return blockName;}
    public HBox getTitlePane(){return titlePane;}
    public abstract void reShape();
}
