package painttest3;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * @author miric
 * handles setting the attributes for shapes and creating the shapes
 */
public class ShapeHandler {

    /**
     * sets the coordinates, width, height, and radius for a given shape
     * @param s the shape you are creating
     * @param xVal the event double value for the end of your shape
     * @param yVal the event y double value for the end of your shape
     * @param startX the start x coordinate
     * @param startY  the start y coordinate
     */
   public void createShape(Shape s, double xVal, double yVal, double startX, double startY) {
        if (s.getClass() == Line.class) {
            Line temp = (Line) s;
            temp.setEndX(xVal);
            temp.setEndY(yVal);
        } else if (s.getClass() == Rectangle.class) {
            Rectangle temp = (Rectangle) s;
            temp.setX(startX);
            temp.setY(startY);
            temp.setWidth(Math.abs((xVal - temp.getX())));
            temp.setHeight(Math.abs((yVal - temp.getY())));
            if (temp.getX() > xVal) {
                temp.setX(xVal);
            }
            if (temp.getY() > yVal) {
                temp.setY(yVal);
            }
        } else if (s.getClass() == Circle.class) {
            Circle temp = (Circle) s;
            temp.setCenterX(startX);
            temp.setCenterY(startY);
            temp.setRadius((Math.abs(xVal - temp.getCenterX()) + Math.abs(yVal - temp.getCenterY())));

        } else if (s.getClass() == Ellipse.class) {
            Ellipse temp = (Ellipse) s;
            temp.setCenterX(startX);
            temp.setCenterY(startY);
            temp.setRadiusX(Math.abs(xVal - temp.getCenterX()));
            temp.setRadiusY(Math.abs(yVal - temp.getCenterY()));
            
        } else if (s.getClass() == Path.class) {
            Path temp = (Path) s;
            temp.getElements().add(new LineTo(xVal, yVal));

        } else if (s.getClass() == Text.class) {
            Text temp = (Text) s;
            temp.setTranslateX(xVal);
            temp.setTranslateY(yVal);

        }
    }
    /**
     * sets the stroke, fill and width attributes for any given shape
     * @param s the shape you're setting
     * @param tool_color the outline color
     * @param fill_color the fill color
     * @param slider  the slider you're deriving a width from
     */
       public void setAttributes(Shape s, ColorPicker tool_color, ColorPicker fill_color, Slider slider){
        s.setStroke(tool_color.getValue());
        s.setFill(fill_color.getValue());
        s.setStrokeWidth(slider.getValue());
    }   
}
