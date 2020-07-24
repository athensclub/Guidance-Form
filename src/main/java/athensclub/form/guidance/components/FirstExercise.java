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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class FirstExercise extends VBox implements PageList<FirstExercise> {

    @FXML
    private TextFlow bestMatch, skill, job;

    @FXML
    private TextArea hopingJob;

    @FXML
    private TextField name, studentClass, number, school;

    private ObservableList<FirstExercise> pages;

    private IntegerProperty currentPage;

    private CheckFormListPages checks;

    public FirstExercise(CheckFormListPages checks) throws IOException {
        pages = FXCollections.observableArrayList();
        pages.add(this);
        this.checks = checks;

        currentPage = new SimpleIntegerProperty();
        currentPage.addListener((prop, oldv, newv) -> {
            if (newv.intValue() != 0)
                throw new IllegalStateException("MatchBehavior current page must always be 0 (itself)");
        });

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("first_exercise.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        checks.bestMatchProperty().addListener((ListChangeListener<? super CheckFormList>) change -> {
            while (change.next()) {
                bestMatch.getChildren().clear();
                skill.getChildren().clear();
                job.getChildren().clear();
                for (int i = 0; i < change.getList().size(); i++) {
                    CheckFormList l = change.getList().get(i);
                    JsonObject data = Main.DATA.getObject("types").getObject(l.getType());
                    bestMatch.getChildren().add(new Text((i == 0 ? "" : "\n") + data.getString("name")));
                    skill.getChildren().add(new Text((i == 0 ? "" : "\n") + data.getString("skills")));
                    job.getChildren().add(new Text((i == 0 ? "" : "\n") + data.getString("jobs")));
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
        Paragraph pTitle = new Paragraph("ใบงานที่ 1");
        pTitle.setAlignment(Element.ALIGN_CENTER);
        pTitle.setFont(Main.BOLD_UNDERLINE_FONT);
        doc.add(pTitle);

        Phrase pInfo = new Phrase("ชื่อ " + name.getText() +
                " ชั้น " + studentClass.getText() +
                " เลขที่ " + number.getText() +
                " โรงเรียน " + school.getText() + "\n");
        pInfo.setFont(Main.FONT);
        doc.add(pInfo);

        Paragraph match = new Paragraph();
        Chunk frontMatch = new Chunk("จากแบบสำรวจพหุปัญญา  พบว่า  ปัญญาเด่น  คือ\nตอบ");
        frontMatch.setFont(Main.BOLD_FONT);
        match.add(frontMatch);

        Paragraph skill = new Paragraph();
        Chunk frontSkill = new Chunk("ต้องใช้ทักษะอาชีพดังนี้\nตอบ");
        frontSkill.setFont(Main.BOLD_FONT);
        skill.add(frontSkill);

        Paragraph job = new Paragraph();
        Chunk frontJob = new Chunk("อาชีพที่เหมาะสม  คือ\nตอบ");
        frontJob.setFont(Main.BOLD_FONT);
        job.add(frontJob);
        for (int i = 0; i < checks.bestMatchProperty().size(); i++) {
            CheckFormList l = checks.bestMatchProperty().get(i);
            JsonObject data = Main.DATA.getObject("types").getObject(l.getType());
            Chunk bodyMatch = new Chunk("\t" + data.getString("name"));
            bodyMatch.setFont(Main.FONT);
            match.add(bodyMatch);

            Chunk bodySkill = new Chunk("\t" + data.getString("skills"));
            bodySkill.setFont(Main.FONT);
            skill.add(bodySkill);

            Chunk bodyJob = new Chunk("\t" + data.getString("jobs"));
            bodyJob.setFont(Main.FONT);
            job.add(bodyJob);
        }
        doc.add(match);
        doc.add(skill);
        doc.add(job);

        Paragraph hoping = new Paragraph();
        Chunk frontHoping = new Chunk("อาชีพในอนาคตที่คาดหวัง  คือ\nตอบ");
        frontHoping.setFont(Main.BOLD_FONT);
        hoping.add(frontHoping);
        Chunk bodyHoping = new Chunk(hopingJob.getText());
        bodyHoping.setFont(Main.FONT);
        hoping.add(bodyHoping);
        doc.add(hoping);
    }

    public TextField getName() {
        return name;
    }

    public TextField getNumber() {
        return number;
    }

    public TextField getStudentClass() {
        return studentClass;
    }

    public TextField getSchool() {
        return school;
    }

    @Override
    public ObservableList pagesProperty() {
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
