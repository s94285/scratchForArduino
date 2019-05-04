package ece;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class ScratchForArduinoController {
    @FXML private ToggleGroup BlockToggleGroup;
    @FXML private Pane drawingPane;
    @FXML private RadioButton controlsButton,operatorsButton,arduinoButton;
    enum BlockClass{CONTROLS,OPERATORS,ARDUINO}
    private BlockClass currentBlockClass = BlockClass.CONTROLS;
    public void initialize(){
        controlsButton.setUserData(BlockClass.CONTROLS);
        operatorsButton.setUserData(BlockClass.OPERATORS);
        arduinoButton.setUserData(BlockClass.ARDUINO);
        System.out.println("TEST");
        Head block1 = new Head();
        block1.setLayoutX(100);
        block1.setLayoutY(100);
        Head head2 = new Head();
        head2.setLayoutX(100);
        head2.setLayoutY(100);
        Path path = new Path();

        MoveTo moveTo = new MoveTo();
        moveTo.setX(0.0f);
        moveTo.setY(0.0f);

        HLineTo hLineTo = new HLineTo();
        hLineTo.setX(70.0f);

        QuadCurveTo quadCurveTo = new QuadCurveTo();
        quadCurveTo.setX(120.0f);
        quadCurveTo.setY(60.0f);
        quadCurveTo.setControlX(100.0f);
        quadCurveTo.setControlY(0.0f);

        LineTo lineTo = new LineTo();
        lineTo.setX(175.0f);
        lineTo.setY(55.0f);

        ArcTo arcTo = new ArcTo();
        arcTo.setX(50.0f);
        arcTo.setY(50.0f);
        arcTo.setRadiusX(50.0f);
        arcTo.setRadiusY(50.0f);

        path.getElements().add(moveTo);
        path.getElements().add(hLineTo);
       // path.getElements().add(quadCurveTo);
        //path.getElements().add(lineTo);
        path.getElements().add(arcTo);
        path.getElements().add(new ClosePath());
        path.setFill(Color.BLUE);
        drawingPane.getChildren().add(path);
        drawingPane.getChildren().add(block1);
        drawingPane.getChildren().add(head2);
        //drawingPane.getChildren().add(new Rectangle(100,100, Color.BLUE));

    }
    @FXML
    private void typeClicked(ActionEvent e){
        currentBlockClass=(BlockClass) BlockToggleGroup.getSelectedToggle().getUserData();
    }
}
