package athensclub.form.guidance.components;

import athensclub.form.guidance.Main;
import com.lowagie.text.*;
import com.lowagie.text.rtf.RtfWriter2;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainPane extends BorderPane {

    @FXML
    private Pane pageBody;

    @FXML
    private Button previousPage;

    @FXML
    private Button nextPage;

    @FXML
    private Button export;

    @FXML
    private MergedPage body;

    private CheckFormListPages listPages;
    private MatchBehavior matchBehavior;
    private FirstExercise firstExercise;
    private BehaviorTest behaviorTest;

    public MainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main_pane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        listPages = new CheckFormListPages();
        matchBehavior = new MatchBehavior(listPages);
        firstExercise = new FirstExercise(listPages);
        behaviorTest = new BehaviorTest(firstExercise);

        listPages.fireBestMatchEvent();

        body.listsProperty().addAll(listPages, matchBehavior, firstExercise, behaviorTest);
        previousPage.disableProperty().bind(body.canGoPrevious().not());
        nextPage.disableProperty().bind(body.canGoNext().not());
    }

    private void export(File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            Document document = new Document(PageSize.A4);
            RtfWriter2 rtf = RtfWriter2.getInstance(document, outputStream);
            document.open();
            document.setMargins(50, 50, 50, 50);

            Paragraph pTitle = new Paragraph("แบบสำรวจพหุปัญญา  (Multiple  Intelligences)");
            pTitle.setAlignment(Element.ALIGN_CENTER);
            pTitle.setFont(Main.BOLD_UNDERLINE_FONT);
            document.add(pTitle);

            Phrase command = new Phrase();
            Chunk frontCommand = new Chunk("คำสั่ง");
            frontCommand.setFont(Main.BOLD_FONT);
            Chunk bodyCommand = new Chunk(" โปรดทำเครื่องหมาย  √  หน้าข้อความที่ตรงกับลักษณะของท่าน");
            command.add(frontCommand);
            command.add(bodyCommand);
            document.add(command);

            listPages.addToDocument(document);
            matchBehavior.addToDocument(document);
            firstExercise.addToDocument(document);
            behaviorTest.addToDocument(document);

            rtf.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Completed");
            alert.setHeaderText("Export Completed!");

            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onPreviousPage() {
        body.goPrevious();
    }

    @FXML
    public void onNextPage() {
        body.goNext();
    }

    @FXML
    public void onExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Location to Export...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Rich Text Format (*.rtf)", "*.rtf"));
        File selectedFile = fileChooser.showSaveDialog(Main.PRIMARY_STAGE);
        if (selectedFile.exists())
            selectedFile.delete();
        export(selectedFile);
    }

}
