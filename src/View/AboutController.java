package View;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {

    @FXML
    private TextFlow textFlow;
    @FXML
    private ImageView imageView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Text text1 = new Text("The game was created by Navit Branin and Dana Hohenstein, students at Ben-Gurion University in the Department of Information Systems Engineering.\n" +
                "Funjoya is a music festival in Eilat that includes three days of parties for students! Here is a link to the Funjoya website -\n"
        + "https://web.funjoya.co.il/");
        text1.setFill(Color.BLACK);
        text1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 20));
        imageView.setImage(new Image("/Images/danavit.png"));
        textFlow.getChildren().add(text1);

    }
}
