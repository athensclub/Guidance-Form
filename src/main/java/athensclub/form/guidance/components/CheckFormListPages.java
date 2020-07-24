package athensclub.form.guidance.components;

import athensclub.form.guidance.Main;
import athensclub.form.guidance.paging.PageList;
import com.grack.nanojson.JsonObject;
import com.lowagie.text.Document;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.stream.Collectors;

public class CheckFormListPages extends BorderPane implements PageList<CheckFormList> {

    public static final CheckFormList DUMMY;

    static {
        CheckFormList dummy = null;
        try {
            dummy = new CheckFormList("language");
        } catch (IOException e) {
            e.printStackTrace();
        }
        DUMMY = dummy;
    }

    private ObservableList<CheckFormList> pages;

    private IntegerProperty currentPage;

    private ObservableList<CheckFormList> bestMatch;

    private ReadOnlyIntegerWrapper maxScore;

    public CheckFormListPages() throws IOException {
        pages = FXCollections.observableArrayList();
        bestMatch = FXCollections.observableArrayList();

        maxScore = new ReadOnlyIntegerWrapper();

        currentPage = new SimpleIntegerProperty();
        currentPage.addListener((prop, oldv, newv) -> setPage(newv.intValue()));

        JsonObject data = Main.DATA.getObject("types");
        for (String type : data.keySet()) {
            CheckFormList list = new CheckFormList(type);
            list.scoreProperty().addListener((prop, oldv, newv) -> refind());
            pages.add(list);
        }

        setPage(0);
    }

    private void refind() {
        int best = pages.stream().mapToInt(l -> l.scoreProperty().get()).max().orElse(0);
        maxScore.set(best);
        bestMatch.setAll(pages
                .stream()
                .filter(l -> l.scoreProperty().get() == best).collect(Collectors.toList()));
    }

    /**
     * Take this node's element and convert it and add it to the document
     *
     * @param doc
     */
    public void addToDocument(Document doc) {
        for (CheckFormList page : pages)
            page.addToDocument(doc);
    }

    /**
     * Fire change event for best match for the first time. This is required as no user input has
     * been made to update the value yet.
     */
    public void fireBestMatchEvent() {
        refind();
    }

    /**
     * Get a read-only list of the best match of knowledge type that has the most score for the user.
     *
     * @return
     */
    public ObservableList<CheckFormList> bestMatchProperty() {
        return bestMatch;
    }

    /**
     * Get a read-only value of the max score that the user get in any knowledge type.
     *
     * @return
     */
    public ReadOnlyIntegerProperty maxScoreProperty() {
        return maxScore.getReadOnlyProperty();
    }

    public ObservableList<CheckFormList> pagesProperty() {
        return pages;
    }

    public IntegerProperty currentPageProperty() {
        return currentPage;
    }

    @Override
    public Node asNode() {
        return this;
    }

    private void setPage(int idx) {
        getChildren().clear();
        setCenter(pages.get(idx));
    }

}
