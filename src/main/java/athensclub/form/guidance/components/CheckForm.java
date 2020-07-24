package athensclub.form.guidance.components;

import athensclub.form.guidance.Main;
import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class CheckForm extends HBox {

    @FXML
    private CheckBox checkbox;

    @FXML
    private Text text;

    public CheckForm(String info) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("check_form.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        text.setText(info);
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }

    /**
     * Take this node's element and convert it and add it to the document
     *
     * @param pg
     */
    public void addToPhrase(Phrase pg) {
        Phrase p = new Phrase();

        Chunk front = new Chunk(getCheckbox().isSelected() ? "√ " : "x ");
        front.setFont(Main.BOLD_FONT);

        Chunk body = new Chunk(text.getText() + "\n");
        body.setFont(Main.FONT);

        p.add(front);
        p.add(body);

        pg.add(p);
    }

}
