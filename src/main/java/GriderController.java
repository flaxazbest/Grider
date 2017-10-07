import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GriderController {

    @FXML
    Pane pane;

    @FXML
    Canvas canvas;

    @FXML
    Button button1;
    @FXML
    Button button2;
    @FXML
    Button button3;

    private final double multyplierScaling = 1.05;

    private GraphicsContext gc = null;
    private Field field = null;
    private double cornerX;
    private double scale = 1.0;
    private double deltaCornerX = 0;
    private double deltaMoveX = 0;
    private double cornerY;
    private double deltaCornerY = 0;
    private double deltaMoveY = 0;
    private double sizeSquare;
    private boolean isDragged = false;

    public void initialize() {
        canvas.setFocusTraversable(true);
        field = new Field(1,1);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.widthProperty().addListener(observable->redraw());
        canvas.heightProperty().addListener(observable->redraw());
        gc = canvas.getGraphicsContext2D();

    }

    private void redraw() {
        gc.setFill(Color.GREY);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        drawGridForField(field);
    }

    private void drawGridForField(Field f) {
        double size = Math.min(pane.getHeight(), pane.getWidth());
        sizeSquare = size/(f.getSize()+3) * scale;
        double sizeGrid = sizeSquare*(f.getSize()+3);
        cornerX = (pane.getWidth() - sizeGrid)/2 + sizeSquare/2;
        cornerY = (pane.getHeight() - sizeGrid)/2 + sizeSquare/2;

        gc.setStroke(Color.GREEN);
        gc.setLineWidth(1);

        for (int i=0; i<field.getSize()+3; i++) {
            gc.strokeLine(cornerX+deltaCornerX+deltaMoveX+i*sizeSquare, cornerY+deltaCornerY+deltaMoveY,
                          cornerX+deltaCornerX+deltaMoveX+i*sizeSquare, cornerY+deltaCornerY+deltaMoveY+sizeSquare*(field.getSize()+2));
            gc.strokeLine(cornerX+deltaCornerX+deltaMoveX, cornerY+deltaCornerY+i*sizeSquare,
                          cornerX+deltaCornerX+deltaMoveX+sizeSquare*(field.getSize()+2), cornerY+deltaCornerY+deltaMoveY+i*sizeSquare);
        }


    }

    private double oldX = 0;
    private double oldY = 0;

    public void mouseCanvas(MouseEvent mouseEvent) {

        if (mouseEvent.isControlDown()) {
            deltaMoveX = (mouseEvent.getX() - oldX);
            deltaMoveY = (mouseEvent.getX() - oldY);
            redraw();
            System.out.println("here " + oldX);
        }

        button1.setText(mouseEvent.getX() + " " + mouseEvent.getY());
        if (mouseEvent.getX() > cornerX+deltaCornerX && mouseEvent.getX() < cornerX+deltaCornerX+sizeSquare*(field.getSize()+2)
            && mouseEvent.getY() > cornerY+deltaCornerY && mouseEvent.getY() < cornerY+deltaCornerY+sizeSquare*(field.getSize()+2) ) {
            button2.setText(String.valueOf((int) ((mouseEvent.getX() - cornerX-deltaCornerX) / sizeSquare ))
                            + " " +
                            String.valueOf((int) ((mouseEvent.getY() - cornerY-deltaCornerY) / sizeSquare ))
            );
        }
        else {
            button2.setText("Out of range");
        }
    }

    public void onScrollCanvas(ScrollEvent scrollEvent) {
        //button3.setText("scroll");

        if (scrollEvent.isControlDown()) {
            //deltaCornerX += scrollEvent.getDeltaY();
            if (scrollEvent.getDeltaY() > 0)
                scale *= multyplierScaling;
            else
                scale /= multyplierScaling;
        }
        else
            field.setWidth(field.getSize() - (int)(scrollEvent.getDeltaY() / scrollEvent.getMultiplierY()));
        redraw();
    }

    public void onCanvasPress(KeyEvent keyEvent) {
        if (keyEvent.isControlDown()) {
            canvas.setCursor(Cursor.MOVE);
            if (keyEvent.getCode()== KeyCode.NUMPAD0) {
                scale = 1.0;
                deltaCornerX = 0.0;
                deltaCornerY = 0.0;
                redraw();
            }
        }
    }

    public void onCanvasReleased(KeyEvent keyEvent) {
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void onCanvasMousePress(MouseEvent mouseEvent) {
        oldX = mouseEvent.getX();
        oldY = mouseEvent.getY();
        isDragged = true;
        System.out.println("D true");
    }

    public void onCanvasMouseRelease(MouseEvent mouseEvent) {
        isDragged = false;
        deltaMoveX = 0.0;
        deltaMoveY = 0.0;
        System.out.println("D false");
    }
}
