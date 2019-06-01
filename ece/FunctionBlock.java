package ece;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.awt.event.InputEvent;
import java.util.ArrayList;
import ece.FunctionDialogController.ArgumentType;

public class FunctionBlock extends Head {
    public static final Color FUNCTION_COLOR =  Color.rgb(99, 141, 217);
    public static final Color ARGUMENT_COLOR =  Color.rgb(89, 71, 177);
    private ArrayList<Pair<ArgumentType,String>> functionSpec;
    private java.awt.Robot robot;
    public FunctionBlock(BlockSpec blockSpec, Pane drawingPane, ArrayList<Pair<ArgumentType,String>> functionSpec) {
        super(blockSpec, drawingPane);
        try {
            robot = new java.awt.Robot();
        }catch (Exception e) {
            e.printStackTrace();
        }
        this.functionSpec = functionSpec;
        this.setBackground(new Background(new BackgroundFill(FUNCTION_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        titlePane.getChildren().removeAll();
        for(Pair<ArgumentType,String> pair : functionSpec){
            BlockSpec newBlockSpec = ScratchForArduinoController.blockSpecBuilder(pair.getValue(),pair.getValue());
            newBlockSpec.code.work = pair.getValue();
            switch(pair.getKey()){
                case TEXT: {
                    Label label = new Label(pair.getValue());
                    label.setFont(new Font(17));
                    label.setTextFill(Color.WHITE);
                    this.titlePane.getChildren().add(label);
                    break;
                }

                case NUMBER:
                case STRING: {
                    ValueBlock newBlock = new ValueBlock(newBlockSpec, titlePane) {
                        private ValueBlock valueBlock1;
                        private Point2D pressPos;
                        @Override
                        public void onMousePressed(MouseEvent mouseEvent) {
                            //super.onMousePressed(mouseEvent);
                            System.out.println("Mouse Entered on Click Me Two");
                            valueBlock1 = new ValueBlock(newBlockSpec, FunctionBlock.this.drawingPane);
                            valueBlock1.setBackground(new Background(new BackgroundFill(ARGUMENT_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
                            FunctionBlock.this.drawingPane.getChildren().add(valueBlock1);
                            Point2D scenePoint = FunctionBlock.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
                            valueBlock1.setLayoutY(scenePoint.getY() - mouseEvent.getY());
                            valueBlock1.setLayoutX(scenePoint.getX() - mouseEvent.getX());
                            if (robot != null) {
                                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                                robot.mouseMove((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
                                robot.delay(50);
                                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            }
                            pressPos = new Point2D(mouseEvent.getSceneX(),mouseEvent.getSceneY());
                            mouseEvent.consume();
                        }
                        @Override
                        public void onMouseDragged(MouseEvent mouseEvent) {}
//                        @Override
//                        public void onMouseReleased(MouseEvent mouseEvent) {
//                            if(pressPos.distance(mouseEvent.getSceneX(),mouseEvent.getSceneY())<2){
//                                FunctionBlock.this.drawingPane.getChildren().remove(valueBlock1);
//                            }
//                        }
                    };
                    newBlock.setBackground(new Background(new BackgroundFill(ARGUMENT_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
                    titlePane.getChildren().add(newBlock);
                    break;
                }
                case BOOLEAN: {
                    BooleanBlock newBlock = new BooleanBlock(newBlockSpec, titlePane) {
                        private BooleanBlock booleanBlock1;
                        private Point2D pressPos;
                        @Override
                        public void onMousePressed(MouseEvent mouseEvent) {
                            //super.onMousePressed(mouseEvent);
                            System.out.println("Mouse Entered on Click Me Two");
                            booleanBlock1 = new BooleanBlock(newBlockSpec, FunctionBlock.this.drawingPane);
                            booleanBlock1.setBackground(new Background(new BackgroundFill(ARGUMENT_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
                            FunctionBlock.this.drawingPane.getChildren().add(booleanBlock1);
                            Point2D scenePoint = FunctionBlock.this.drawingPane.sceneToLocal(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
                            booleanBlock1.setLayoutY(scenePoint.getY() - mouseEvent.getY());
                            booleanBlock1.setLayoutX(scenePoint.getX() - mouseEvent.getX());
                            if (robot != null) {
                                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                                robot.mouseMove((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
                                robot.delay(50);
                                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            }
                            pressPos = new Point2D(mouseEvent.getSceneX(),mouseEvent.getSceneY());
                            mouseEvent.consume();
                        }
                        @Override
                        public void onMouseDragged(MouseEvent mouseEvent) {}
//                        @Override
//                        public void onMouseReleased(MouseEvent mouseEvent) {
//                            if(pressPos.distance(mouseEvent.getSceneX(),mouseEvent.getSceneY())<2){
//                                FunctionBlock.this.drawingPane.getChildren().remove(valueBlock1);
//                            }
//                        }
                    };
                    newBlock.setBackground(new Background(new BackgroundFill(ARGUMENT_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
                    titlePane.getChildren().add(newBlock);
                    break;
                }
            }
        }
    }
}
