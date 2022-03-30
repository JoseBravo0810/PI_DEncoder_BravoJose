/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import textfieldfilechooser.TextFieldFileChooser;

/**
 * FXML Controller class
 *
 * @author jose
 */
public class DEncoderController implements Initializable {

    @FXML
    private MenuItem help;
    @FXML
    private Menu Contact;
    @FXML
    private MenuItem joseBravo;
    @FXML
    private TextFieldFileChooser inputFile;
    @FXML
    private RadioButton rbEncode;
    @FXML
    private ToggleGroup order;
    @FXML
    private RadioButton rbDencode;
    @FXML
    private TextFieldFileChooser outputFile;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnDEncode;
    @FXML
    private TextField password;

    // Variable para almacenar el escenario principal en el controlador (necesario para el FileChooser)
    private Stage mainStage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       // Establecemos el promptText de los componentes customizados
       inputFile.setPrText("input");
       outputFile.setPrText("output");
       
       // Establecemos listener para cambiar el nombre del boton al seleccionar codificar o decodificar como orden
       order.selectedToggleProperty().addListener(new listenerChangeNameButton());
       
       inputFile.setOnAciont(new searchButtonInput());
       outputFile.setOnAciont(new searchButtonOutput());
    }
    
    // Metodo para establecer el escenario principal
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }
    
    
    
    // Metodo para reestablecer los campos
    public void clearFields(ActionEvent event) {
        // Vaciamos path de entrada y salida y quitamos estilo de error
        inputFile.setPath("");
        inputFile.removeClassStyle("tfError");
        outputFile.setPath("");
        outputFile.removeClassStyle("tfError");
        // Marcamos boton Encode
        rbEncode.setSelected(true);
        // Desmarcamos boton Decode
        rbDencode.setSelected(false);
        // Vaciamos campo contraseña
        password.setText("");
    }
    
    public class searchButtonInput implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // Creamos el selector de fichero
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Input file selector"); // Establecemos titulo para entrada
            
            // Establecemos filtro de archivos que se muestran (all files)
            fileChooser.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
            
            // Recogemos el fichero seleccionado
            File selectedFile = fileChooser.showOpenDialog(mainStage);
            
            // Si lo que recoge el fileChooser es null, se ha cancelado la seleccion de fichero
            if(selectedFile == null) {
                inputFile.setClassStyle("tfError");
                System.out.println("13");
            } else {
                inputFile.setPath(selectedFile.getPath());
                inputFile.removeClassStyle("tfError");
            }
        }

    }
    
    public class searchButtonOutput implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // Creamos el selector de fichero
            FileChooser fileChooser = new FileChooser();
            // Establecemos titulo al selector
            fileChooser.setTitle("Output file selector");
            
            // Establecemos filtro de archivos que se muestran (all files)
            fileChooser.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
            
            // Recogemos el fichero seleccionado
            File selectedFile = fileChooser.showOpenDialog(mainStage);
            
            // Si lo que recoge el fileChooser es null, se ha cancelado la seleccion de fichero
            if(selectedFile == null) {
                outputFile.setClassStyle("tfError");
            } else {
                outputFile.setPath(selectedFile.getPath());
                outputFile.removeClassStyle("tfError");
            }
        }

    }
    
    public class listenerChangeNameButton implements ChangeListener<Toggle> {

        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            // Comprobamos cual es el radio button seleccionado
            if(rbEncode.isSelected()) { // Si es el de codificar
                btnDEncode.setText("Encode"); // Establecemos nombre del boton
            } else { // Si no (es decodificar)
                btnDEncode.setText("Decode"); // Establecemos nombre del boton
            }
        }
        
    }
    
}
