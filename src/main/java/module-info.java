module athensclub.form.guidance {
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.github.librepdf.openpdf;
    requires javafx.controls;
    requires openrtf;
    requires com.grack.nanojson;
    requires org.jfxtras.styles.jmetro;

    opens  athensclub.form.guidance.components to javafx.base, javafx.fxml;
    exports athensclub.form.guidance;
}