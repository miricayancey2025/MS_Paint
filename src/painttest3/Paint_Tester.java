/*
 *Mirica Yancey
 */
package painttest3;

//THE ONE THAT WORKS
import java.io.File;
import java.util.Optional;
import java.util.Stack;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 *
 * @author miric
 */
public class Paint_Tester extends Application {

    private Stage stage;

    private Line currentLine;
    private Rectangle rect;
    private Rectangle roundrect;
    private Path path;
    private Path erase_path;
    private Circle circ;
    private Ellipse elps;
    private Text text;
    private Shape currentShape;

    private Slider slider;
    private double startX;
    private double startY;

    private final ColorUtil cutie = new ColorUtil();
    private final FileHandler handle = new FileHandler();
    private final ShapeHandler shaper = new ShapeHandler();

    BorderPane pane = new BorderPane();
    AnchorPane anchor_pane = new AnchorPane();
    ImageView view = new ImageView();
    Image image;

    Stack<Shape> undoHistory;
    Stack<Shape> redoHistory;

    private File save_file;
    private final boolean saved = false;
    private boolean changesMade;

    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;

    private Rectangle selection = new Rectangle();
    final Light.Point anchor = new Light.Point();
    private double rectW = 0;
    private double rectH = 0;
    private Rectangle fill_rect;
    private PixelReader reader;
    boolean frontboy = true;
    boolean rectSet = false;
    Image nImage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setOnCloseRequest(confirmCloseEventHandler);
        Button closeButton = new Button("Close Application");
        closeButton.setOnAction((e) -> stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));

        image = new Image("https://i0.wp.com/cdepatie.pnyhost.com/wp-content/uploads/2017/04/white-background-2.jpg");
        view.setImage(image);

        undoHistory = new Stack();
        redoHistory = new Stack();

        MenuBar menu = new MenuBar();
        Menu menu_file = new Menu("File");
        MenuItem itemNew = new MenuItem("New");
        MenuItem saveAs = new MenuItem("Save As");
        MenuItem menu_save = new MenuItem("Save");
        MenuItem itemClose = new MenuItem("Close");
        MenuItem menu_open = new MenuItem("Open");

        ColorPicker tool_color = new ColorPicker(Color.BLACK);
        ColorPicker fill_color = new ColorPicker(Color.TRANSPARENT);
        Label line_color_label = new Label("Line Color");
        Label fill_color_label = new Label("Fill Color");
        Label tool_color_label = new Label();
        Label fill_label = new Label();
        Label line_width = new Label("Line Width");

        /*Button Set up*/
        Button undo = new Button("Undo");
        Button redo = new Button("Redo");
        Button save = new Button("Save");
        Button open = new Button("Open");

        /*button array to apply the styles*/
        Button[] basicArr = {undo, redo, save, open};
        for (Button btn : basicArr) {
            btn.setMinWidth(90);
            btn.setCursor(Cursor.HAND);
            btn.setTextFill(Color.WHITE);
            btn.setStyle("-fx-background-color: #666;");
        }
        save.setStyle("-fx-background-color: #F26666;");
        open.setStyle("-fx-background-color: #F26666;");

        TextArea textarea = new TextArea();
        textarea.setPrefRowCount(1);

        /*Slider stuff*/
        slider = new Slider(1, 100, 5);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        ToggleButton drawbtn = new ToggleButton("Draw");
        ToggleButton erasebtn = new ToggleButton("Eraser");
        ToggleButton dropperbtn = new ToggleButton("Dropper");
        ToggleButton linebtn = new ToggleButton("Line");
        ToggleButton rectbtn = new ToggleButton("Rectange");
        ToggleButton circbtn = new ToggleButton("Circle");
        ToggleButton elpsbtn = new ToggleButton("Ellipse");
        ToggleButton tribtn = new ToggleButton("Triangle");
        ToggleButton roundrectbtn = new ToggleButton("Round Rectangle");
        ToggleButton textbtn = new ToggleButton("Text");
        ToggleButton selectbtn = new ToggleButton("Select");
        ToggleButton[] toolsArr = {drawbtn, erasebtn, dropperbtn, selectbtn, linebtn, rectbtn, circbtn, elpsbtn, roundrectbtn, tribtn, textbtn};
        ToggleGroup tools = new ToggleGroup();
        for (ToggleButton tool : toolsArr) {
            tool.setMinWidth(90);
            tool.setToggleGroup(tools);
            tool.setCursor(Cursor.HAND);
        }
        tool_color.setOnAction(e -> {
            Color c = tool_color.getValue();
            java.awt.Color awtColor = new java.awt.Color((float) c.getRed(), (float) c.getGreen(), (float) c.getBlue());
            String color_label = cutie.getColorNameFromColor(awtColor);
            tool_color_label.setText(color_label);
        });
        fill_color.setOnAction(e -> {
            Color c = fill_color.getValue();
            java.awt.Color awtColor = new java.awt.Color((float) c.getRed(), (float) c.getGreen(), (float) c.getBlue());
            String color_labelA = cutie.getColorNameFromColor(awtColor);
            fill_label.setText(color_labelA);
        });
        /* on press set location and erase stroke */
        EventHandler<MouseEvent> SelectionPressed
                = (MouseEvent t) -> {
                    orgSceneX = t.getSceneX();
                    orgSceneY = t.getSceneY();
                    orgTranslateX = ((Rectangle) (t.getSource())).getTranslateX();
                    orgTranslateY = ((Rectangle) (t.getSource())).getTranslateY();
                };
        /*drags selected shape*/
        EventHandler<MouseEvent> SelectionDragged
                = (MouseEvent t) -> {
                    double offsetX = t.getSceneX() - orgSceneX;
                    double offsetY = t.getSceneY() - orgSceneY;
                    double newTranslateX = orgTranslateX + offsetX;
                    double newTranslateY = orgTranslateY + offsetY;

                    ((Rectangle) (t.getSource())).setTranslateX(newTranslateX);
                    ((Rectangle) (t.getSource())).setTranslateY(newTranslateY);
                };

        /* on click pick up color and set tool and fill colors to that value
        also create text
         */

        /*Creates the shapes and adds them to the pane*/
        pane.setOnMousePressed(e -> {
            startX = e.getX();
            startY = e.getY();
            if (dropperbtn.isSelected()) {
                Color c = Pixel.getColor(pane, e.getX(), e.getY());
                tool_color.setValue(c);
                fill_color.setValue(c);
            }
            if (drawbtn.isSelected()) {
                path = new Path();
                path.getElements().add(new MoveTo(e.getX(), e.getY()));
                pane.getChildren().add(path);
                currentShape = path;
            }
            if (erasebtn.isSelected()) {
                erase_path = new Path();
                erase_path.getElements().add(new MoveTo(e.getX(), e.getY()));
                pane.getChildren().add(erase_path);
                currentShape = erase_path;
            }
            if (linebtn.isSelected()) {
                currentLine = new Line(e.getX(), e.getY(), e.getX(), e.getY());
                pane.getChildren().add(currentLine);
                currentShape = currentLine;
            }
            if (rectbtn.isSelected()) {
                rect = new Rectangle();
                pane.getChildren().add(rect);
                currentShape = rect;
            }
            if (roundrectbtn.isSelected()) {
                roundrect = new Rectangle();
                pane.getChildren().add(roundrect);
                currentShape = roundrect;
            }
            if (circbtn.isSelected()) {
                circ = new Circle();
                pane.getChildren().add(circ);
                currentShape = circ;
            }
            if (elpsbtn.isSelected()) {
                elps = new Ellipse();
                pane.getChildren().add(elps);
                currentShape = elps;
            }
            if (selectbtn.isSelected()) {
                fill_rect = new Rectangle();
                pane.getChildren().add(fill_rect);
                fill_rect.setFill(Color.WHITE);
                selection = new Rectangle();
                handleSelection(e, selection);
                pane.getChildren().add(selection);
            }
            if (textbtn.isSelected()) {
                text = new Text(textarea.getText());
                text.setFill(fill_color.getValue());
                text.setStroke(tool_color.getValue());
                text.setFont(Font.font(slider.getValue()));
                shaper.createShape(text, e.getX(), e.getY(), startX, startY);
                currentShape = text;
                pane.getChildren().add(text);
                undoHistory.push(text);
                changesMade = true;
            }});
        /* on drag draw shapes*/
        pane.setOnMouseDragged((MouseEvent e) -> {
           
            if (drawbtn.isSelected()) {
                path.getElements().add(new LineTo(e.getX(), e.getY()));
                shaper.setAttributes(path, tool_color, fill_color, slider);
            }
            if (erasebtn.isSelected()) {
                erase_path.getElements().add(new LineTo(e.getX(), e.getY()));
                erase_path.setFillRule(FillRule.NON_ZERO);
                erase_path.setStroke(Color.WHITE);
                erase_path.setFill(Color.WHITE);
                erase_path.setStrokeWidth(slider.getValue());
            }
            if (linebtn.isSelected()) {
                shaper.createShape(currentLine, e.getX(), e.getY(), startX, startY);
                shaper.setAttributes(currentLine, tool_color, fill_color, slider);
            }
            if (rectbtn.isSelected()) {
                rect.setX(startX);
                rect.setY(startY);
                shaper.createShape(rect, e.getX(), e.getY(), startX, startY);
                shaper.setAttributes(rect, tool_color, fill_color, slider);
            }
            if (roundrectbtn.isSelected()) {
                roundrect.setX(startX);
                roundrect.setY(startY);
                shaper.createShape(roundrect, e.getX(), e.getY(), startX, startY);
                roundrect.setArcWidth(roundrect.getWidth() / 5);
                roundrect.setArcHeight(roundrect.getHeight() / 5);
                shaper.setAttributes(roundrect, tool_color, fill_color, slider);
            }
            if (circbtn.isSelected()) {
                shaper.createShape(circ, e.getX(), e.getY(), startX, startY);
                shaper.setAttributes(circ, tool_color, fill_color, slider);
            }
            if (elpsbtn.isSelected()) {
                shaper.createShape(elps, e.getX(), e.getY(), startX, startY);
                shaper.setAttributes(elps, tool_color, fill_color, slider);
            }
            if (selectbtn.isSelected()) {
                if (frontboy == true) {
                    setSelection(e, selection);
                    selection.setOnMousePressed(SelectionPressed);
                    selection.setOnMouseDragged(SelectionDragged);
                }
            }
        });

        pane.setOnMouseReleased((MouseEvent e) -> {
            changesMade = true;
            if (selectbtn.isSelected()) {
                if (image != null) {
                    /*sets the empty fill rect that's white*/
                    setRect(fill_rect);
                    /* checs if the rect has been set and if true creates a new image and cuts
                    then pushes to the undo history and unselectes the select button
                    */
                    if (frontboy == true) {
                        if (rectSet == false) {
                            rectSet = true;
                        }
                        int width = (int) rectW;
                        int height = (int) rectH;
                        reader = image.getPixelReader();
                        nImage = new WritableImage(reader, (int) startX, (int) startY, width, height);
                        cutSelection(nImage);
                        undoHistory.push(selection);
                        selection.setStroke(Color.TRANSPARENT); // border
                        selectbtn.setSelected(false);
                    }
                } else {
                    handle.makeAlert("No Image!", "There is no image to cut!");
                }
            }
            else if (linebtn.isSelected()) {
                undoHistory.push(currentLine);
            } else if (erasebtn.isSelected()) {
                undoHistory.push(erase_path);
            } else if (drawbtn.isSelected()) {
                undoHistory.push(path);
            } else if (elpsbtn.isSelected()) {
                undoHistory.push(elps);
            } else if (circbtn.isSelected()) {
                undoHistory.push(circ);
            } else if (rectbtn.isSelected()) {
                undoHistory.push(rect);
            } else if (roundrectbtn.isSelected()) {
                undoHistory.push(roundrect);
            }
        });
        /*sets the undo and redo on action */
        undo.setOnAction(e -> {
            undo();
        });
        redo.setOnAction(e -> {
            redo();
        });
        /* opens pic
         */
        open.setOnAction((e) -> {
            image = handle.uploadPic(changesMade, stage, view);
        });

        menu_open.setOnAction((e) -> {
            image = handle.uploadPic(changesMade, stage, view);
        });
        saveAs.setOnAction((e) -> {
            handle.save(pane, save_file, saved, changesMade);
        });
        menu_save.setOnAction((e) -> {
            if (saved == true) {
                handle.quickSave(pane, changesMade, save_file);
            } else {
                handle.save(pane, save_file, saved, changesMade);
            }
        });
        save.setOnAction((e) -> {
            if (saved == true) {
                handle.quickSave(pane, changesMade, save_file);
            } else {
                handle.save(pane, save_file, saved, changesMade);
            }
        });

        /*save key combination*/
        final KeyCombination saveK;
        saveK = new KeyCodeCombination(KeyCode.S,
                KeyCombination.CONTROL_DOWN);
        stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (saveK.match(event)) {
                if (saved == true) {
                    handle.quickSave(pane, changesMade, save_file);
                } else {
                    handle.save(pane, save_file, saved, changesMade);
                }
            }
        });
        /*open key combination*/
        final KeyCombination openK;
        openK = new KeyCodeCombination(KeyCode.O,
                KeyCombination.CONTROL_DOWN);
        stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (!openK.match(event)) {
            } else {
                handle.uploadPic(changesMade, stage, view);
            }
        });

        VBox btns = new VBox(10);
        btns.getChildren().addAll(drawbtn, erasebtn, dropperbtn, selectbtn, linebtn, rectbtn, roundrectbtn, circbtn, elpsbtn, line_color_label, tool_color_label, tool_color, fill_color_label, fill_label, fill_color, textbtn, textarea, line_width, slider, undo, redo, open, save);
        btns.setPadding(new Insets(5));
        btns.setStyle("-fx-background-color: #ffb399");
        btns.setPrefWidth(100);

        menu_file.getItems().addAll(itemNew, menu_open, saveAs, menu_save, itemClose);
        menu.getMenus().add(menu_file);

        AnchorPane.setTopAnchor(menu, 0.0);
        AnchorPane.setRightAnchor(btns, 0.0);
        AnchorPane.setTopAnchor(pane, 25.0);

        anchor_pane.getChildren().addAll(pane, menu, btns);
        pane.getChildren().add(view);

        drawbtn.setSelected(true);
        Scene scene = new Scene(anchor_pane, 1200, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Gets the beginning coordinates for selection and outlines the area
     *
     * @param event gets the event form the mouse handler to set the first coordinate set for the selection rectangle
     * @param select the selection rectangle of which we're cutting the picture
     */
    public void handleSelection(MouseEvent event, Rectangle select) {
        anchor.setX(event.getX());
        anchor.setY(event.getY());
        select.setX(event.getX());
        select.setY(event.getY());
        select.setFill(null);
        select.setStroke(Color.BLACK);
        select.getStrokeDashArray().add(10.0);
    }

    /**
     * Sets the selection width, height, x, y
     *
     * @param event the mouse pressed event to get the coordinates
     * @param select the rectangle area you are setting
     */
    public void setSelection(MouseEvent event, Rectangle select) {
        select.setWidth(Math.abs(event.getX() - anchor.getX()));
        select.setHeight(Math.abs(event.getY() - anchor.getY()));
        select.setX(Math.min(anchor.getX(), event.getX()));
        select.setY(Math.min(anchor.getY(), event.getY()));
        rectW = select.getWidth();
        rectH = select.getHeight();
        startX = select.getX();
        startY = select.getY();
    }

    /**
     * cuts selection and fills with image from that coordinate and sets it to
     * the front of the view
     *
     * @param img the image you are filling the selection with
     */
    public void cutSelection(Image img) {
        selection.setFill(new ImagePattern(img));
        selection.toFront();
    }

    /**
     * sets the x, y, width, and height of a rectangle
     *
     * @param rect the rectangle you need to set the coordinates for (fill
     * rectangle)
     */
   public void setRect(Rectangle rect) {
        rect.setX(startX);
        rect.setY(startY);
        rect.setWidth(rectW);
        rect.setHeight(rectH);
    }
    /**
     * finds the removed shape and pushes to the redo history then removes it
     * from pane
     */
    public void undo() {
        if (!undoHistory.empty()) {
            Shape removedShape = undoHistory.lastElement();
            if (removedShape.getClass() == Line.class) {
                Line tempLine = (Line) removedShape;
                redoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
                pane.getChildren().remove(tempLine);

            } else if (removedShape.getClass() == Rectangle.class) {
                Rectangle tempRect = (Rectangle) removedShape;
                redoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                pane.getChildren().remove(tempRect);

            } else if (removedShape.getClass() == Circle.class) {
                Circle tempCirc = (Circle) removedShape;
                redoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                pane.getChildren().remove(tempCirc);

            } else if (removedShape.getClass() == Ellipse.class) {
                Ellipse tempElps = (Ellipse) removedShape;
                redoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                pane.getChildren().remove(tempElps);

            } else if (removedShape.getClass() == Path.class) {
                Path tempPath = (Path) removedShape;
                redoHistory.push(new Path(tempPath.getElements()));
                pane.getChildren().remove(tempPath);

            } else if (removedShape.getClass() == Text.class) {
                Text tempText = (Text) removedShape;
                redoHistory.push(new Text(tempText.getX(), tempText.getY(), tempText.getText()));
                pane.getChildren().remove(tempText);
            }
            Shape lastRedo = redoHistory.lastElement();
            lastRedo.setFill(removedShape.getFill());
            lastRedo.setStroke(removedShape.getStroke());
            lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
            undoHistory.pop();
        } else {
            System.out.println("there is no action to undo");
        }
    }

    /**
     * removes shape from redoHistory adds it back to the undoHostory and then
     * adds it to the pane
     */
   public void redo() {
        if (!redoHistory.empty()) {
            /* if not empty get the last element set the width, storke and fill the thing pop the thing*/
            Shape removedShape = redoHistory.lastElement();
            redoHistory.pop();
            /*find out what kind of shape it is and based on that do redraw the thin and push it back onto undo*/
            if (removedShape.getClass() == Line.class) {
                Line tempLine = (Line) removedShape;
                undoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
                pane.getChildren().add(tempLine);

            } else if (removedShape.getClass() == Rectangle.class) {
                Rectangle tempRect = (Rectangle) removedShape;
                undoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                pane.getChildren().add(tempRect);

            } else if (removedShape.getClass() == Circle.class) {
                Circle tempCirc = (Circle) removedShape;
                undoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                pane.getChildren().add(tempCirc);

            } else if (removedShape.getClass() == Ellipse.class) {
                Ellipse tempElps = (Ellipse) removedShape;
                undoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                pane.getChildren().add(tempElps);

            } else if (removedShape.getClass() == Path.class) {
                Path tempPath = (Path) removedShape;
                undoHistory.push(new Path(tempPath.getElements()));
                pane.getChildren().add(tempPath);

            } else if (removedShape.getClass() == Text.class) {
                Text tempText = (Text) removedShape;
                undoHistory.push(new Text(tempText.getX(), tempText.getY(), tempText.getText()));
                pane.getChildren().add(tempText);
            }
            Shape lastUndo = undoHistory.lastElement();
        } else {
            System.out.println("there is no action to redo");
        }
    }
   

    /**
     *Handles asking the user if they want to close
     */
    public final EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
        if (changesMade == true) {
            Alert closeConfirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");  //make alert box
            Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(ButtonType.OK);  //make it so ya gotta confirm that ya wanna close
            exitButton.setText("Exit");
            closeConfirmation.setHeaderText("File Not Saved!");
            closeConfirmation.initModality(Modality.APPLICATION_MODAL);
            closeConfirmation.initOwner(stage);
            Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();  ///give ya the options and wait
            if (!ButtonType.OK.equals(closeResponse.get())) {  //if not okay cancel the close
                event.consume();
            }
        } else {
            Platform.exit();
        }
    };

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
