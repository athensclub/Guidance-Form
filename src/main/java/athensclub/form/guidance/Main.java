package athensclub.form.guidance;

import athensclub.form.guidance.components.MainPane;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.lowagie.text.rtf.style.RtfFont;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

public class Main extends Application {

    public static final JsonObject DATA;

    public static final RtfFont BOLD_UNDERLINE_FONT = new RtfFont(RtfFont.DEFAULT_FONT, 16, RtfFont.BOLD | RtfFont.UNDERLINE);
    public static final RtfFont BOLD_FONT = new RtfFont(RtfFont.DEFAULT_FONT, 16, RtfFont.BOLD);
    public static final RtfFont FONT = new RtfFont(RtfFont.DEFAULT_FONT, 16);

    public static Stage PRIMARY_STAGE;

    static {
        JsonObject data = null;
        try {
            data = JsonParser.object().from(Main.class.getClassLoader().getResource("datas.json"));
        } catch (JsonParserException e) {
            e.printStackTrace();
        }
        DATA = data;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PRIMARY_STAGE = primaryStage;

        MainPane mainPane = new MainPane();
        Scene scene = new Scene(mainPane);

        JMetro metro = new JMetro(Style.LIGHT);
        metro.setScene(scene);
        mainPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Guidance Form - แบบทดสอบพหุปัญญา");
        primaryStage.sizeToScene();
        primaryStage.show();
    }

}
