package taskDao.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource("/taskDao/view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 1200, 720);
        stage.setTitle("Менеджер игр");
        stage.setScene(scene);
        stage.show();
    }

}