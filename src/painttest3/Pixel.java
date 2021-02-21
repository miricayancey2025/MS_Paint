package painttest3;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * @author miric
 * handles converting pixels to colors
 */
 public class Pixel
{
    private static final SnapshotParameters SP = new SnapshotParameters();
    private static final WritableImage WI = new WritableImage(1, 1);
    private static final PixelReader PR = WI.getPixelReader();
/**
 * gets the Arbg value from a snapshot
 * @param n the node you're snapshotting from
 * @param x the x value of the location
 * @param y the y value of the location
 * @return  the RGB value of that pixel
 */
    public static int getArgb(Node n, double x, double y)
    {
        synchronized (WI)
        {
            Rectangle2D r = new Rectangle2D(x, y, 1, 1);
            SP.setViewport(r);
            n.snapshot(SP, WI);
           return PR.getArgb(0, 0);
        }
    }
/**
 * gets the color from a pixel
 * @param n the node you're snapshotting from
 * @param x the x value of the location
 * @param y the y value of the location
 * @return the color of the pixel at that location
 */
    public static Color getColor(Node n, double x, double y)
    {
        synchronized (WI)
        {
            Rectangle2D r = new Rectangle2D(x, y, 1, 1);
            SP.setViewport(r);
            n.snapshot(SP, WI);
            return PR.getColor(0, 0);
        }
    }
}
