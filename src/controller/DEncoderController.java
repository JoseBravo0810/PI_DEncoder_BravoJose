/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
       inputFile.setPrText("input");
       outputFile.setPrText("output");
       
       order.selectedToggleProperty().addListener(new listenerRadioGroup());
    }
    
    public void setBtnText() {
        // Se comprueba 
    }
    
    public class listenerRadioGroup implements ChangeListener<Toggle> {

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
