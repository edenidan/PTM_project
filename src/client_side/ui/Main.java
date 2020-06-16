package client_side.ui;

import client_side.ui.models.Model;
import client_side.ui.models.ModelImpl;
import client_side.ui.view_models.MainWindowViewModel;
import client_side.ui.view_models.MainWindowViewModelImpl;
import client_side.ui.views.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/mainWindow.fxml"));
        Parent root = loader.load();

        MainWindowController viewController = loader.getController();
        Model model = new ModelImpl();
        MainWindowViewModelImpl viewModel = new MainWindowViewModelImpl(model);
        viewController.setViewModel(viewModel);

        primaryStage.setTitle("PTM Project");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
