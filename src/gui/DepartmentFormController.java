package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController implements Initializable{

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Button btnCancel;
	@FXML
	private Button btnSave;
	@FXML
	private Label lbMessage;
	@FXML
	public void onBtnCancelAction() {
		
	}
	
	@FXML
	public void onBtnSaveAction() {
		
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		intializeNodes();
		
	}

	public void intializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
}
