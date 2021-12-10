package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;
	private ObservableList<Seller> obsList;
	@FXML
	private Button btnNew;
	@FXML
	private TableView<Seller> tableViewSeller;
	@FXML
	private TableColumn<Seller, Integer> tableColumId;
	@FXML
	private TableColumn<Seller, String> tableColumName;
	@FXML
	private TableColumn<Seller, String> tableColumEmail;
	@FXML
	private TableColumn<Seller, Date> tableColumBirthDate;
	@FXML
	private TableColumn<Seller, Double> tableColumBaseSalary;
	@FXML
	private TableColumn<Seller, Seller> tableColumEDIT;
	@FXML
	private TableColumn<Seller, Seller> tableColumREMOVE;

	public void onBtnNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml/", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle bs) {
		initializeNodes();

	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	private void initializeNodes() {
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumBirthDate, "dd/MM/yyyy");
		tableColumBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumBaseSalary, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		
		  try { FXMLLoader loader = new
		  FXMLLoader(getClass().getResource(absoluteName)); Pane pane = loader.load();
		  
		  SellerFormController controller = loader.getController();
		  controller.setSeller(obj); controller.setServices(new SellerService(), new DepartmentService());
		  controller.subscribeDataChangeListener(this); 
		  controller.updateFormData();
		  controller.loadAssociateObjects();
		  Stage dialogStage = new Stage(); dialogStage.setTitle("Enter Seller data");
		  dialogStage.setScene(new Scene(pane)); dialogStage.setResizable(false);
		  dialogStage.initOwner(parentStage);
		  dialogStage.initModality(Modality.WINDOW_MODAL); dialogStage.showAndWait(); 
		  }
		  catch (IOException e){ 
			  e.printStackTrace();
			  Alerts.showAlert("IO Exception Error loading view",null, e.getMessage(), AlertType.ERROR);
		  }
		 
	}

	@Override
	public void onChangeData() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	public void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch(DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
