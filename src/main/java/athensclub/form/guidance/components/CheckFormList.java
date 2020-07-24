package athensclub.form.guidance.components;

import athensclub.form.guidance.Main;
import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.lowagie.text.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckFormList extends BorderPane {

    @FXML
    private Label title;

    @FXML
    private VBox checks;

    @FXML
    private Text score;

    @FXML
    private Label other;

    @FXML
    private TextArea otherInput;

    private String type;

    private ReadOnlyIntegerWrapper scoreCount;

    private JsonObject data;

    private List<CheckForm> forms;

    public CheckFormList(String type) throws IOException {
        this.type = type;
        scoreCount = new ReadOnlyIntegerWrapper();
        forms = new ArrayList<>();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("check_form_list.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        data = Main.DATA.getObject("types").getObject(type);
        title.setText(data.getString("name"));
        other.setText(data.getString("other"));

        JsonArray questions = data.getArray("checks");
        for (int i = 0; i < questions.size(); i++) {
            CheckForm form = new CheckForm(questions.getString(i));
            form.getCheckbox().selectedProperty().addListener((prop, oldv, newv) -> {
                if (newv)
                    scoreCount.set(scoreCount.get() + 1);
                else
                    scoreCount.set(scoreCount.get() - 1);
            });
            checks.getChildren().add(form);
            forms.add(form);
        }

        score.textProperty().bind(Bindings.concat("คะแนนรวม: ", scoreCount.asString()));
    }

    public ReadOnlyIntegerProperty scoreProperty() {
        return scoreCount.getReadOnlyProperty();
    }

    /**
     * Take this node's element and convert it and add it to the document
     *
     * @param doc
     */
    public void addToDocument(Document doc) {
        Paragraph pTitle = new Paragraph(data.getString("name"));
        pTitle.setAlignment(Element.ALIGN_CENTER);
        pTitle.setFont(Main.BOLD_UNDERLINE_FONT);
        doc.add(pTitle);

        Phrase body = new Phrase();
        for (CheckForm f : forms)
            f.addToPhrase(body);
        doc.add(body);

        Phrase pOther = new Phrase();
        Chunk frontOther = new Chunk(data.getString("other") + ": ");
        frontOther.setFont(Main.BOLD_FONT);
        Chunk bodyOther = new Chunk(otherInput.getText().isBlank() ? "-\n" : otherInput.getText() + "\n");
        bodyOther.setFont(Main.FONT);
        pOther.add(frontOther);
        pOther.add(bodyOther);
        doc.add(pOther);
    }

    /**
     * @return The type of question that this form is asking
     */
    public String getType() {
        return type;
    }

}
