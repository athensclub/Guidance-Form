package athensclub.form.guidance.paging;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface PageList<T extends Node> {

    public ObservableList<T> pagesProperty();

    public IntegerProperty currentPageProperty();

    /**
     * Return {@code this} itself as a Node instance
     *
     * @return
     */
    public Node asNode();

    /**
     * Check whether the page that come before the current page exists (If currentPage index > 0)
     */
    public default BooleanBinding canGoPrevious() {
        return currentPageProperty().greaterThan(0);
    }

    /**
     * Check whether the page that com after the current page exists (If currentPage < pages.size() - 1)
     */
    public default BooleanBinding canGoNext() {
        return currentPageProperty().lessThan(Bindings.size(pagesProperty()).subtract(1));
    }

    /**
     * Go to the next page
     */
    public default void goNext() {
        currentPageProperty().set(currentPageProperty().get() + 1);
    }

    /**
     * Go to the previous page
     */
    public default void goPrevious() {
        currentPageProperty().set(currentPageProperty().get() - 1);
    }

}
