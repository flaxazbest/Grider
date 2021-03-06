import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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
    Canvas layer;
    @FXML
    Canvas card1;
    @FXML
    Canvas card2;

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
    private double oldX = 0;
    private double cornerY;
    private double deltaCornerY = 0;
    private double deltaMoveY = 0;
    private double oldY = 0;
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


        layer.getGraphicsContext2D().setFill(Color.GREY);
        layer.getGraphicsContext2D().setLineWidth(2);
        layer.getGraphicsContext2D().setStroke(Color.YELLOW);
        //pane.getChildren().add(layer);
        layer.toFront();

        Image im = new Image("Card2T.png");
        card1.getGraphicsContext2D().drawImage(im, 0, 0, card1.getWidth(), card1.getHeight());
        card2.getGraphicsContext2D().drawImage(im, 0, 0, card1.getWidth(), card1.getHeight());
    }

    private void redraw() {
        gc.setFill(Color.GREY);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

        drawGridForField(field);

        drawSelection(1,1);
    }

    private void drawSelection(int col, int row) {
        layer.setWidth(sizeSquare);
        layer.setHeight(sizeSquare);
        layer.getGraphicsContext2D().fillRect(0,0,sizeSquare,sizeSquare);
        if (cornerX+deltaCornerX+sizeSquare*col < 0 || cornerX+deltaCornerX+sizeSquare*col > pane.getWidth()-sizeSquare
         || cornerY+deltaCornerY+sizeSquare*row < 0 || cornerY+deltaCornerY+sizeSquare*row > pane.getHeight()-sizeSquare )
            layer.setVisible(false);
        layer.setLayoutX(cornerX+deltaCornerX+sizeSquare*col);
        layer.setLayoutY(cornerY+deltaCornerY+sizeSquare*row);
        layer.getGraphicsContext2D().strokeRect(0,0,sizeSquare-2, sizeSquare-2);
        layer.toFront();
    }

    private void drawGridForField(Field f) {
        double size = Math.min(pane.getHeight(), pane.getWidth());
        sizeSquare = size/(f.getSize()+3) * scale;
        double sizeGrid = sizeSquare*(f.getSize()+3);
        cornerX = (pane.getWidth() - sizeGrid)/2 + sizeSquare/2;
        cornerY = (pane.getHeight() - sizeGrid)/2 + sizeSquare/2;

        //*
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(1);

        for (int i=0; i<field.getSize()+3; i++) {
            gc.strokeLine(cornerX+deltaCornerX+deltaMoveX+i*sizeSquare, cornerY+deltaCornerY+deltaMoveY,
                          cornerX+deltaCornerX+deltaMoveX+i*sizeSquare, cornerY+deltaCornerY+deltaMoveY+sizeSquare*(field.getSize()+2));
            gc.strokeLine(cornerX+deltaCornerX+deltaMoveX, cornerY+deltaCornerY+deltaMoveY+i*sizeSquare,
                          cornerX+deltaCornerX+deltaMoveX+sizeSquare*(field.getSize()+2), cornerY+deltaCornerY+deltaMoveY+i*sizeSquare);
        }
        //*/
    }

    public void mouseCanvas(MouseEvent mouseEvent) {
        if (!layer.isVisible())
            layer.setVisible(true);
        if (!mouseEvent.isControlDown()) {
            if (mouseEvent.getX() > cornerX+deltaCornerX && mouseEvent.getX() < cornerX+deltaCornerX+sizeSquare*(field.getSize()+2)
                && mouseEvent.getY() > cornerY+deltaCornerY && mouseEvent.getY() < cornerY+deltaCornerY+sizeSquare*(field.getSize()+2) ) {

                int x = (int) ((mouseEvent.getX() - cornerX-deltaCornerX) / sizeSquare );
                int y = (int) ((mouseEvent.getY() - cornerY-deltaCornerY) / sizeSquare );

                drawSelection(x, y);

                button2.setText(x + " " + y);
            }
            else {
                button2.setText("Out of range");
            }
        }
    }

    public void onScrollCanvas(ScrollEvent scrollEvent) {
        //button3.setText("scroll");
        deltaMoveX = 0.0;
        deltaMoveY = 0.0;
        if (scrollEvent.isControlDown()) {
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
            layer.toBack();
            canvas.setCursor(Cursor.MOVE);
            if (keyEvent.getCode()== KeyCode.NUMPAD0) {
                scale = 1.0;
                deltaCornerX = 0.0;
                deltaCornerY = 0.0;
                deltaMoveX = 0.0;
                deltaMoveY = 0.0;
                redraw();
            }
        }
    }

    public void onCanvasReleased(KeyEvent keyEvent) {
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void onCanvasMousePress(MouseEvent mouseEvent) {
        canvas.setCursor(Cursor.CLOSED_HAND);
        oldX = mouseEvent.getSceneX();
        oldY = mouseEvent.getSceneY();
        deltaMoveX = ((Canvas)(mouseEvent.getSource())).getTranslateX();
        deltaMoveY = ((Canvas)(mouseEvent.getSource())).getTranslateY();
        layer.setVisible(false);
    }

    public void onCanvasMouseRelease(MouseEvent mouseEvent) {
        deltaCornerX += deltaMoveX;
        deltaCornerY += deltaMoveY;
        if (mouseEvent.isControlDown())
            canvas.setCursor(Cursor.MOVE);
        else
            canvas.setCursor(Cursor.DEFAULT);
        //layer.setVisible(true);
    }

    public void onDrag(MouseEvent mouseEvent) {
        if (mouseEvent.isControlDown()) {
            double offsetX = mouseEvent.getSceneX() - oldX;
            double offsetY = mouseEvent.getSceneY() - oldY;
            deltaMoveX = offsetX;
            deltaMoveY = offsetY;
            redraw();
        }
    }

    public void onCLayerMouseRelease(MouseEvent mouseEvent) {
        if (mouseEvent.isControlDown())
            canvas.setCursor(Cursor.MOVE);
        else
            canvas.setCursor(Cursor.DEFAULT);
        layer.setVisible(true);
    }

    private double oldCardX, oldCardY, traslateCardX, traslateCardY;

    public void cardRelease(MouseEvent mouseEvent) {
        ((Canvas)(mouseEvent.getSource())).setCursor(Cursor.DEFAULT);
    }

    public void cardPress(MouseEvent mouseEvent) {
        ((Canvas)(mouseEvent.getSource())).setCursor(Cursor.CLOSED_HAND);
        oldCardX = mouseEvent.getSceneX();
        oldCardY = mouseEvent.getSceneY();
        traslateCardX = ((Canvas)(mouseEvent.getSource())).getTranslateX();
        traslateCardY = ((Canvas)(mouseEvent.getSource())).getTranslateY();
    }

    public void cardDrag(MouseEvent mouseEvent) {
        double offsetX = mouseEvent.getSceneX() - oldCardX;
        double offsetY = mouseEvent.getSceneY() - oldCardY;
        double newTranslateX = traslateCardX + offsetX;
        double newTranslateY = traslateCardY + offsetY;
        ((Canvas)(mouseEvent.getSource())).setTranslateX(newTranslateX);
        ((Canvas)(mouseEvent.getSource())).setTranslateY(newTranslateY);
    }
}
