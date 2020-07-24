package athensclub.form.guidance.components;

import athensclub.form.guidance.Main;
import athensclub.form.guidance.paging.PageList;
import com.grack.nanojson.JsonObject;
import com.lowagie.text.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class MatchBehavior extends BorderPane implements PageList<MatchBehavior> {

    @FXML
    private Label title;

    @FXML
    private TextFlow text;

    private IntegerProperty currentPage;

    private ObservableList<MatchBehavior> pages;

    private CheckFormListPages checks;

    public MatchBehavior(CheckFormListPages checks) throws IOException {
        this.checks = checks;
        pages = FXCollections.observableArrayList();
        pages.add(this);

        currentPage = new SimpleIntegerProperty();
        currentPage.addListener((prop, oldv, newv) -> {
            if (newv.intValue() != 0)
                throw new IllegalStateException("MatchBehavior current page must always be 0 (itself)");
        });

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("match_behavior.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();

        checks.bestMatchProperty().addListener((ListChangeListener<? super CheckFormList>) change -> {
            while (change.next()) {
                text.getChildren().clear();
                for (CheckFormList l : change.getList()) {
                    JsonObject data = Main.DATA.getObject("types").getObject(l.getType());
                    Text header = new Text(data.getString("name") + " (" + checks.maxScoreProperty().get() + " คะแนน)\n");
                    header.setStyle("-fx-font-weight: bold;");

                    Text frontSkill = new Text("\tทักษะในอาชีพ");
                    frontSkill.setStyle("-fx-font-weight: bold;");
                    Text skills = new Text(" " + data.getString("skills") + "\n");

                    Text frontJob = new Text("\tตัวอย่างอาชีพ");
                    frontJob.setStyle("-fx-font-weight: bold;");
                    Text jobs = new Text(" " + data.getString("jobs") + "\n\n");
                    text.getChildren().addAll(header, frontSkill, skills, frontJob, jobs);
                }
            }
        });
    }

    /**
     * Take this node's element and convert it and add it to the document
     *
     * @param doc
     */
    public void addToDocument(Document doc) {
        doc.newPage();
        Paragraph pTitle = new Paragraph("ด้านที่มีคะแนนมากที่สุด\n");
        pTitle.setAlignment(Element.ALIGN_CENTER);
        pTitle.setFont(Main.BOLD_UNDERLINE_FONT);
        doc.add(pTitle);

        Phrase body = new Phrase();
        for (CheckFormList l : checks.bestMatchProperty()) {
            JsonObject data = Main.DATA.getObject("types").getObject(l.getType());
            Phrase current = new Phrase();

            Chunk header = new Chunk(data.getString("name") + " (" + checks.maxScoreProperty().get() + " คะแนน)\n");
            header.setFont(Main.BOLD_FONT);
            current.add(header);

            Phrase pSkill = new Phrase();
            Chunk frontSkill = new Chunk("\tทักษะในอาชีพ");
            frontSkill.setFont(Main.BOLD_FONT);
            Chunk bodySkill = new Chunk(" " + data.getString("skills") + "\n");
            bodySkill.setFont(Main.FONT);
            pSkill.add(frontSkill);
            pSkill.add(bodySkill);
            current.add(pSkill);

            Phrase pJob = new Phrase();
            Chunk frontJob = new Chunk("\tตัวอย่างอาชีพ");
            frontJob.setFont(Main.BOLD_FONT);
            Chunk bodyJob = new Chunk(" " + data.getString("jobs") + "\n");
            bodyJob.setFont(Main.FONT);
            pJob.add(frontJob);
            pJob.add(bodyJob);
            current.add(pJob);

            body.add(current);
        }
        doc.add(body);
    }

    @Override
    public ObservableList<MatchBehavior> pagesProperty() {
        return pages;
    }

    @Override
    public IntegerProperty currentPageProperty() {
        return currentPage;
    }

    @Override
    public Node asNode() {
        return this;
    }
}
