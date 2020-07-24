package athensclub.form.guidance.components;

import athensclub.form.guidance.Main;
import athensclub.form.guidance.paging.PageList;
import com.lowagie.text.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class BehaviorTest extends BorderPane implements PageList<BehaviorTest> {

    @FXML
    private TableForm table;

    @FXML
    private Text score, level;

    private ObservableList<BehaviorTest> pages;

    private IntegerProperty currentPage;

    private FirstExercise firstExercise;

    public BehaviorTest(FirstExercise firstExercise) throws IOException {
        pages = FXCollections.observableArrayList();
        pages.add(this);
        this.firstExercise = firstExercise;

        currentPage = new SimpleIntegerProperty();
        currentPage.addListener((prop, oldv, newv) -> {
            if (newv.intValue() != 0)
                throw new IllegalStateException("MatchBehavior current page must always be 0 (itself)");
        });

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("behavior_test.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();

        score.textProperty().bind(Bindings.concat("คะแนนรวม: ", table.totalScoreProperty()));
        level.textProperty().bind(Bindings.concat("เกณฑ์: ",
                new When(table.totalScoreProperty().lessThanOrEqualTo(7))
                        .then("ปรับปรุง")
                        .otherwise(new When(table.totalScoreProperty().lessThanOrEqualTo(14))
                                .then("พอใช้").otherwise("ดี"))));
    }

    /**
     * Take this node's element and convert it and add it to the document
     *
     * @param doc
     */
    public void addToDocument(Document doc) {
        doc.newPage();

        Paragraph pTitle = new Paragraph("แบประเมินพฤติกรรมนักเรียน");
        pTitle.setAlignment(Element.ALIGN_CENTER);
        pTitle.setFont(Main.BOLD_UNDERLINE_FONT);
        doc.add(pTitle);

        Phrase pInfo = new Phrase("ชื่อ " + firstExercise.getName().getText() +
                " ชั้น " + firstExercise.getStudentClass().getText() +
                " เลขที่ " + firstExercise.getNumber().getText() +
                " โรงเรียน " + firstExercise.getSchool().getText() + "\n");
        pInfo.setFont(Main.FONT);
        doc.add(pInfo);

        Phrase command = new Phrase();
        Chunk frontCommand = new Chunk("คำชี้แจง");
        frontCommand.setFont(Main.BOLD_FONT);
        Chunk bodyCommand = new Chunk(" ให้นักเรียนใส่เครื่องหมาย  √  ลงในช่องที่ตรงกับความคิดเห็นตามพฤติกรรมที่นักเรียนแสดงออกมามากที่สุด\n");
        command.add(frontCommand);
        command.add(bodyCommand);
        doc.add(command);

        table.addToDocument(doc);

        Chunk scoreInfo = new Chunk(score.getText() + "\n" + level.getText());
        doc.add(scoreInfo);

    }

    @Override
    public ObservableList<BehaviorTest> pagesProperty() {
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
