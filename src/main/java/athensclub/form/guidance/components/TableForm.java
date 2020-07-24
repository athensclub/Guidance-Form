package athensclub.form.guidance.components;

import athensclub.form.guidance.Main;
import com.grack.nanojson.JsonArray;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;

public class TableForm extends TableView<TableForm.FormData> {

    private Map<Integer, ToggleGroup> toggleGroups;

    private ObservableList<FormData> items;

    private NumberBinding totalScore;

    public TableForm() {
        toggleGroups = new HashMap<>();

        JsonArray data = Main.DATA.getArray("behavior");
        items = FXCollections.observableArrayList();
        for (int i = 0; i < data.size(); i++)
            items.add(new FormData((String) data.get(i)));

        setEditable(false);

        TableColumn<FormData, String> infoColumn = new TableColumn("รายการประเมิน");
        infoColumn.setCellValueFactory(
                new PropertyValueFactory<>("info"));

        TableColumn<FormData, Integer> highColumn = new TableColumn("มาก");
        highColumn.setCellFactory((param) -> new RadioButtonCell(toggleGroups,
                (item, selected) -> {
                    item.setScore(selected ? SelectType.HIGH.score : 0);
                    item.type = SelectType.HIGH;
                }));
        highColumn.setMinWidth(150);

        TableColumn<FormData, Integer> mediumColumn = new TableColumn("ปานกลาง");
        mediumColumn.setCellFactory((param) -> new RadioButtonCell(toggleGroups,
                (item, selected) -> {
                    item.setScore(selected ? SelectType.MEDIUM.score : 0);
                    item.type = SelectType.MEDIUM;
                }));
        mediumColumn.setMinWidth(150);

        TableColumn<FormData, Integer> lowColumn = new TableColumn("น้อย");
        lowColumn.setCellFactory((param) -> new RadioButtonCell(toggleGroups,
                (item, selected) -> {
                    item.setScore(selected ? SelectType.LOW.score : 0);
                    item.type = SelectType.LOW;
                }));
        lowColumn.setMinWidth(150);

        totalScore = items.get(0).score.add(items.get(1).score);
        for (int i = 2; i < items.size(); i++)
            totalScore = totalScore.add(items.get(i).score);

        getColumns().addAll(infoColumn, highColumn, mediumColumn, lowColumn);
        setItems(items);
    }

    public NumberBinding totalScoreProperty() {
        return totalScore;
    }

    public void addToDocument(Document doc) {
        Table table = new Table(4);

        Phrase header1 = new Phrase("รายการประเมิน");
        header1.setFont(Main.BOLD_FONT);
        table.addCell(header1);
        Phrase header2 = new Phrase("มาก");
        header2.setFont(Main.BOLD_FONT);
        table.addCell(header2);
        Phrase header3 = new Phrase("ปานกลาง");
        header3.setFont(Main.BOLD_FONT);
        table.addCell(header3);
        Phrase header4 = new Phrase("น้อย");
        header4.setFont(Main.BOLD_FONT);
        table.addCell(header4);

        for (FormData f : items) {
            Phrase body1 = new Phrase(f.getInfo());
            body1.setFont(Main.FONT);
            table.addCell(body1);
            Phrase body2 = new Phrase(f.type == SelectType.HIGH ? "√" : "");
            body2.setFont(Main.FONT);
            table.addCell(body2);
            Phrase body3 = new Phrase(f.type == SelectType.MEDIUM ? "√" : "");
            body3.setFont(Main.FONT);
            table.addCell(body3);
            Phrase body4 = new Phrase(f.type == SelectType.LOW ? "√" : "");
            body4.setFont(Main.FONT);
            table.addCell(body4);
        }

        doc.add(table);
    }

    public static enum SelectType {
        HIGH(3), MEDIUM(2), LOW(1);

        private int score;

        private SelectType(int score) {
            this.score = score;
        }
    }

    @FunctionalInterface
    private static interface Setter {

        public void set(FormData data, boolean selected);

    }

    public static class FormData {

        private SimpleStringProperty info;

        private SimpleIntegerProperty score;

        private SelectType type;

        public FormData(String text) {
            info = new SimpleStringProperty(text);
            score = new SimpleIntegerProperty();
        }

        public void setInfo(String i) {
            info.set(i);
        }

        public String getInfo() {
            return info.get();
        }

        public void setScore(int score) {
            this.score.set(score);
        }

        public int getScore() {
            return score.get();
        }

    }

    public static class RadioButtonCell<FormType> extends TableCell<FormData, Integer> {

        private Map<Integer, ToggleGroup> group;

        private Setter setter;

        public RadioButtonCell(Map<Integer, ToggleGroup> group, Setter setter) {
            this.group = group;
            this.setter = setter;
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                if (getTableRow() == null)
                    return;

                RadioButton radioButton = new RadioButton();

                HBox container = new HBox();
                container.setAlignment(Pos.CENTER);
                container.getChildren().add(radioButton);

                if (!group.containsKey(getTableRow().getIndex()))
                    group.put(getTableRow().getIndex(), new ToggleGroup());
                ToggleGroup g = group.get(getTableRow().getIndex());
                radioButton.setToggleGroup(g);

                setter.set(getTableRow().getItem(), false);
                radioButton.selectedProperty().addListener((prop, oldv, newv) -> setter.set(getTableRow().getItem(), newv));
                setGraphic(container);
            }
        }
    }
}