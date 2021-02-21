package painttest3;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * @author miric
 * 
 */
public class FileHandler {
    private Image image;

    /**
     *Handles uploading and saving files
     */
    public FileHandler() {
        this.image = null;
    }

    /**
     * sets the image extensions that you can upload form
     * @param chooser FileChooser
     */
    public void setExtFilters(FileChooser chooser) {
        chooser.getExtensionFilters().addAll( //calls the method extension filts and adds all
                new FileChooser.ExtensionFilter("All Images", "*.*"), //all image filters
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif")
        );
    }
    /**
     * uploads file to the view
     * @param changesMade boolean changes made to tell the program you've made a change
     * @param stage stage to open the dialog to
     * @param view the imageView to set the image onto
     * @return the image you wish to upload
     */
     public Image uploadPic(boolean changesMade, Stage stage, ImageView view){
             FileChooser openFile = new FileChooser();
            setExtFilters(openFile); // call method on filechooser
            File file2 = openFile.showOpenDialog(stage);
            if (file2 != null) {
                try {
                    InputStream io = new FileInputStream(file2);   //put into the stream this file that we're saving
                     image = new Image(io);  //make this image the new file
                    view.setImage(image);
                    changesMade = true;
                }catch (IOException ex) {
                    makeAlert("Upload Failed", "Unable to Upload!");
                }}
            return image;
     }
     /**
      * saves file
      * @param pane the pane you're snapshotting from
      * @param save_file the file you're saving too
      * @param saved boolean value to tell if you've saved
      * @param changesMade  boolean value to tell if changes have been made
      */
      public void save(BorderPane pane, File save_file, boolean saved, boolean changesMade){
        FileChooser savefile = new FileChooser();
            savefile.setTitle("Save File");
            setExtFilters(savefile);
            File file3 = savefile.showSaveDialog(null);
            if (file3 != null) {
                try {
                    WritableImage writableImage = new WritableImage(1200, 800);  //makes an empty image to be filled with anything
                    pane.snapshot(null, writableImage);  //take a 'pic' of the canvas and sets the empty to the snap
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);   //render the image, so make it
                    ImageIO.write(renderedImage, "png",file3);   //write the image to the file
                    save_file = file3; //set variable save to image (for future quicksave
                    saved = true;  //set saved to true so you know its been saved once
                    changesMade = false;
                }catch (IOException ex) {
                    makeAlert("Save Incomplete!", "Unable to Save!");
                }}}
    /**
     * quick save for when you have already set the filters and have a save file
     * @param pane pane you're snapshotitng from
     * @param changesMade the boolean changes made to tell if changes have been made to the file
     * @param save_file  the save file you're saving to
     */
    public void quickSave(BorderPane pane, boolean changesMade, File save_file){
        try { /* same as top boy */
                 WritableImage writableImage = new WritableImage(1200, 800);
                    pane.snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", save_file);
                    changesMade = false;
        } catch (IOException ex) {
                    makeAlert("Save Incomplete!", "Unable to Save!");
                }}
    /**
     * creates an alert with a given title and content
     * @param title the title of your alert
     * @param content  the content of your alert
     */
     public void makeAlert(String title, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
    

}
