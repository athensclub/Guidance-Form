package athensclub.form.guidance.components;

import athensclub.form.guidance.paging.PageList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;


public class MergedPage<T extends Node> extends BorderPane implements PageList<T> {

    private final ObservableList<PageList<T>> lists;

    private final IntegerProperty currentPage;

    private final ReadOnlyIntegerWrapper pageCount;

    private final PagesWrapper asPageList;

    private static class PagesWrapper<U extends Node> extends ObservableListBase<U> {

        private MergedPage<U> pane;

        public PagesWrapper(MergedPage<U> pane) {
            this.pane = pane;
        }

        @Override
        public U get(int index) {
            return pane.getPageAt(index);
        }

        @Override
        public int size() {
            return pane.pageCount.get();
        }
    }

    public MergedPage() {
        lists = FXCollections.observableArrayList();
        pageCount = new ReadOnlyIntegerWrapper();
        asPageList = new PagesWrapper(this);

        currentPage = new SimpleIntegerProperty();
        currentPage.addListener((prop, oldv, newv) -> {
            setPage(newv.intValue());
        });

        ListChangeListener<T> recount = change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    pageCount.set(pageCount.get() + change.getAddedSize());
                    if (pageCountTo((ObservableList<T>) change.getList()) + change.getFrom() < currentPage.get()) {
                        //shift right to go to the "current page"
                        currentPage.set(currentPage.get() + change.getAddedSize());
                    }
                } else if (change.wasRemoved()) {
                    pageCount.set(pageCount.get() - change.getRemovedSize());
                    if (pageCountTo((ObservableList<T>) change.getList()) + change.getFrom() < currentPage.get()) {
                        //shift left to go to the "current page"
                        currentPage.set(currentPage.get() - change.getRemovedSize());
                    }
                }
            }
        };
        lists.addListener((ListChangeListener<? super PageList<T>>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    boolean setPage = false;
                    if (pageCount.get() == 0)
                        setPage = true;

                    pageCount.set(pageCount.get() + change.getAddedSubList()
                            .stream()
                            .mapToInt(l -> l.pagesProperty().size()).sum());
                    for (PageList<T> pl : change.getAddedSubList())
                        pl.pagesProperty().addListener(recount);

                    if (setPage)
                        setPage(0);
                } else if (change.wasRemoved()) {
                    pageCount.set(pageCount.get() - change.getRemoved()
                            .stream()
                            .mapToInt(l -> l.pagesProperty().size()).sum());
                    for (PageList<T> pl : change.getRemoved())
                        pl.pagesProperty().removeListener(recount);
                }
            }
        });
    }

    /**
     * Get amount of pages (or index) that you have to traverse to all the list before
     * finding the given list
     *
     * @return
     */
    private int pageCountTo(ObservableList<T> list) {
        int sum = 0;
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).pagesProperty() == list) { //I can check for reference here right?
                return sum;
            }
            sum += lists.get(i).pagesProperty().size();
        }
        return -1;
    }

    private T getPageAt(int idx) {
        int current = 0;
        while (idx >= lists.get(current).pagesProperty().size()) {
            idx -= lists.get(current).pagesProperty().size();
            current++;
        }
        return lists.get(current).pagesProperty().get(idx);
    }

    private void setPage(int idx) {
        getChildren().clear();
        setCenter(getPageAt(idx));
    }

    /**
     * Do not use this to mutate the list. Instead use {@link MergedPage#listsProperty()} to
     * mutate the list instead
     *
     * @return
     */
    @Override
    public ObservableList<T> pagesProperty() {
        return asPageList;
    }

    @Override
    public IntegerProperty currentPageProperty() {
        return currentPage;
    }

    @Override
    public Node asNode() {
        return this;
    }

    public ObservableList<PageList<T>> listsProperty() {
        return lists;
    }

    public ReadOnlyIntegerProperty pageCountProperty() {
        return pageCount.getReadOnlyProperty();
    }

}
