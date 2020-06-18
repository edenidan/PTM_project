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
import server_side.MySerialServer;
import server_side.problems.graph_searching.MyClientHandler;

import java.io.*;
import java.net.Socket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        boolean stop = true;
        MySerialServer server = new MySerialServer();
        server.start(1234, new MyClientHandler());

//        Socket s = new Socket("127.0.0.1", 1234);
//        PrintWriter out = new PrintWriter(new BufferedOutputStream(s.getOutputStream()));
//        out.println("1,23,45");
//        out.println("1,1,45");
//        out.println("444,1,45");
//        out.println("end");
//
//        out.println("0,0");
//        out.println("2,1");
//        out.flush();
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(s.getInputStream())));
//        System.out.println(in.readLine());
//
//        if (stop)
//            return;

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
