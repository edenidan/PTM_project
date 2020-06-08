package client_side.ui;

import client_side.interpreter.Interpreter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class MainWindowController {
    @FXML
    TextArea inputTextArea;
    @FXML
    Label resultLabel;


    StringProperty script = new SimpleStringProperty();

    public void run() {
        resultLabel.setText(new Interpreter().interpret(inputTextArea.getText()) + "");
    }
}
