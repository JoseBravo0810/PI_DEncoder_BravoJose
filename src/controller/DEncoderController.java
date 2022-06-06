/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import pi_dencoder_bravojose.Browser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/* Paquetes propios */
import textfieldfilechooser.TextFieldFileChooser;
import Resources.passwordfieldskin.VisiblePasswordFieldSkin;
import com.sun.glass.ui.SystemClipboard;
import java.awt.Toolkit;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

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
    private PasswordField password;
    @FXML
    private Label outputLabel;

    // Variable para almacenar el escenario principal en el controlador (necesario para el FileChooser)
    private Stage mainStage;
    
    // Variable para almacenar el nombre del fichero seleccionado
    private String fileName;

    /**
     * Initializes the controller class.
     * Metodo que se ejecuta al cargar la clase (esta clase se carga desde el FXML de la aplicacion)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Establecemos tooltips
        inputFile.setTextFieldTooltip("Write here the path to your file"); // Tooltip para el campo de texto del fichero de entrada
        inputFile.setButtonTooltip("Click here to find your file"); // Tooltip para el boton del fichero de entrada
        rbEncode.setTooltip(new Tooltip("Select this option to encode your file.")); // Tooltip para la opcion encode en order
        rbDencode.setTooltip(new Tooltip("Select this option to decode your file.")); // Tooltip para la opcion decode en order
        password.setTooltip(new Tooltip("Type your password here, remember that it must have exactly 8 characters.")); // Tooltip del campo de texto para la contraseña
        outputFile.setTextFieldTooltip("Write here the output directory"); // Tooltip del campo de texto del fichero de salida
        outputFile.setButtonTooltip("Click here to select the output directory"); // Tooltip del boton del fichero de salida
        
        
        outputFile.setClassStyle("tfError"); // Establecemos el estilo de error de inicio al fichero de salida, para atraer la atencion del usuario cuando lo hagamos visible
        outputFile.setVisible(false); // Hacemos no visible el componente
        outputLabel.setVisible(false); // Hacemos no visible la etiqueta

        // Establecemos el promptText de los componentes customizados
        inputFile.setPrText("Enter input file"); // Fichero de entrada
        outputFile.setPrText("Don't leave this field empty"); // Fichero de salida

        // Establecemos el enmascaramiento que seguirá el campo de texto (para añadir funcionalidad de hacer visible/no visible la contraseña)
        password.setSkin(new VisiblePasswordFieldSkin(password));
        // Listener para hacer que el campo de texto de la contraseña tenga el estilo de error si no tiene 8 caracteres exactamente
        password.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                // Quitamos el estilo de error para evitar duplicados
                password.getStyleClass().remove("tfError");

                // Si el valor nuevo (la cadena actual de la contraseña) tiene una longitud distinta de 8 caracteres, si esta vacia se gestiona al quitarle el foco
                if(newValue.length() != 8)
                {
                    password.getStyleClass().add("tfError"); // Añadimos el estilo de error al campo de texto
                }
            }
        });
        // Establecemos listener para gestionar la perdida del foco en el campo de la contraseña
        password.focusedProperty().addListener(new listenerChangeFocusOnPasswordField());

        // Establecemos listener para cambiar el nombre del boton al seleccionar codificar o decodificar como orden
        order.selectedToggleProperty().addListener(new listenerChangeNameButton());

        // Establecemos handlers para gestionar la accion a realizar al pulsar el boton de los componentes personalizados
        inputFile.setOnAciont(new searchButtonInput());
        outputFile.setOnAciont(new searchButtonOutput());
        // Establecemos los listeners para desplegar error cuando se deja el campo vacio y quitamos el foco del campo
        inputFile.setOnLostFocusListener(new listenerChangeFocusOnInputTextField());
        outputFile.setOnLostFocusListener(new listenerChangeFocusOnOutputTextField());
    }
    
    // Metodo para establecer el escenario principal (se establece desde la clase main
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }
    
    // Metodo para reestablecer los campos -> Boton clear
    @FXML
    public void clearFields(ActionEvent event) {
        
        // Devolvemos todo al estado inicial
        
        // Componente para seleccionar el fichero de entrada
        inputFile.setPath(""); // Vaciamos el fichero de entrada
        inputFile.removeClassStyle("tfError"); // Quitamos estilo de error si lo tiene
        inputFile.setPrText("Enter input file");
        
        // Componente para seleccionar el fichero de salida
        outputFile.setPath(""); // Vaciamos el campo de texto
        outputFile.removeClassStyle("tfError"); // Quitamos estilo de error (para asegurar que no lo tiene [evitamos duplicar el estilo en la lista observable])
        outputFile.setClassStyle("tfError"); // Le damos el estilo de error para el enfasis inicial para el usuario
        outputFile.setVisible(false); // Hacemos que no se vea el componente
        outputLabel.setVisible(false); // Hacemos que no se vea la etiqueta
        
        // Marcamos boton Encode
        rbEncode.setSelected(true);
        // Desmarcamos boton Decode
        rbDencode.setSelected(false);
        
        // Vaciamos campo contraseña
        password.setText("");
        // Quitamos estilo de error al campo de la contraseña si lo tiene
        password.getStyleClass().remove("tfError");
        password.setPromptText("Password");
    }
    
    // MEtodo que se ejecuta al hacer click en el boton Encode - Decode
    @FXML
    public void dencode(ActionEvent event) {
        
        String osName = System.getProperty("os.name"); // Extraemos el nombre del sistema operativo
        
        // Variable que almacenará el estado de salida del programa DEncode
        int estado;
        
        // Declaramos alert
        Alert alert = new Alert(AlertType.INFORMATION);
        
        
        // Si los datos son validos
        if(validateFields()) {
            try {
                if(rbEncode.isSelected()) {
                    if(osName.contains("Windows")) { // SO Windows -> Usa backslash
                        // Construimos un nuevo proceso y lo lanzamos
                        Process p = new ProcessBuilder("DEncoder/DEncoder.exe", inputFile.getPath(), outputFile.getPath(), "c", password.getText()).start();
                        // Esperamos el estado que nos devuelve
                        estado = p.waitFor();
                    } else { // SOs UNIX -> Usa slash
                        // Construimos un nuevo proceso y lo lanzamos
                        Process p = new ProcessBuilder("DEncoder/DEncoder", inputFile.getPath(), outputFile.getPath(), "c", password.getText()).start();
                        // Esperamos el estado que nos devuelve
                        estado = p.waitFor();
                    }
                } else {
                    if(osName.contains("Windows")) { // SO Windows -> Usa backslash
                        // Construimos un nuevo proceso y lo lanzamos
                        Process p = new ProcessBuilder("DEncoder/DEncoder.exe", inputFile.getPath(), outputFile.getPath(), "d", password.getText()).start();
                        // Esperamos el estado que nos devuelve
                        estado = p.waitFor();
                    } else { // SOs UNIX -> Usa slash
                        // Construimos un nuevo proceso y lo lanzamos
                        Process p = new ProcessBuilder("DEncoder/DEncoder", inputFile.getPath(), outputFile.getPath(), "d", password.getText()).start();
                        // Esperamos el estado que nos devuelve
                        estado = p.waitFor();
                    }
                }
                
                System.out.println(estado + "");
                switch(estado) {
                    case 0: 
                        alert = new Alert(AlertType.INFORMATION); // Establecemos alert de tipo informativo
                        if(rbEncode.isSelected()) { // Si la orden es codificar
                            alert.setHeaderText("File encode success");
                        } else { // Si la orden es decodificar
                            alert.setHeaderText("Fila decode success");
                        }
                        
                        alert.setContentText("File saved in: " + new File(outputFile.getPath()).getAbsolutePath() + "\n\nYou got the path to your file in your clipboard");
                        // Establecemos el alto minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        // Establecemos el ancho minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
                        // Mostramos alerta y esperamos que el usuario haaga click en el boton OK
                        alert.showAndWait();
                        
                        // Instancia del portapapeles
                        Clipboard clpbrd = Clipboard.getSystemClipboard();
                        // Instancia del contenido
                        ClipboardContent content = new ClipboardContent();
                        // Establecemos la cadena que será la ruta
                        content.putString(new File(outputFile.getPath()).getAbsolutePath());
                        // Establecemos este contenido, como el contenido del portapapeles
                        clpbrd.setContent(content);
                        
                        break;
                    // Teoricamente, como las comprobaciones las realizamos antes de ejecutar la orden, nunca deberia salir algo distinto a 0
                    // pero contemplamos las distintas salidas de error del programa en C
                    case -1: // Error en el numero de argumentos
                        alert = new Alert(AlertType.ERROR); // Alert de tipo error
                        alert.setHeaderText("INTERNAL ERROR -1"); // Añadimos cabecera
                        alert.setContentText("Error, no arguments specified\nClose the app, and open it again"); // Establecemos contenido
                        // Establecemos el alto minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        // Establecemos el ancho minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
                        // Mostramos alerta y esperamos que el usuario haaga click en el boton OK
                        alert.showAndWait();
                    case -2: // Error en el fichero de entrada
                        alert = new Alert(AlertType.ERROR); // Alert de tipo error
                        alert.setHeaderText("INTERNAL ERROR -2"); // Añadimos cabecera
                        alert.setContentText("Error, no input file\nClose the app, and open it again"); // Establecemos contenido
                        // Establecemos el alto minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        // Establecemos el ancho minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
                        // Mostramos alerta y esperamos que el usuario haaga click en el boton OK
                        alert.showAndWait();
                    case -3: // Error en el fichero de salida
                        alert = new Alert(AlertType.ERROR); // Alert de tipo error
                        alert.setHeaderText("INTERNAL ERROR -3"); // Añadimos cabecera
                        alert.setContentText("Error, no output file\nClose the app, and open it again"); // Establecemos contenido
                        // Establecemos el alto minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        // Establecemos el ancho minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
                        // Mostramos alerta y esperamos que el usuario haaga click en el boton OK
                        alert.showAndWait();
                    case -4: // Error, orden no especificada
                        alert = new Alert(AlertType.ERROR); // Alert de tipo error
                        alert.setHeaderText("INTERNAL ERROR -4"); // Añadimos cabecera
                        alert.setContentText("Error, no selected order\nClose the app, and open it again"); // Establecemos contenido
                        // Establecemos el alto minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        // Establecemos el ancho minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
                        // Mostramos alerta y esperamos que el usuario haaga click en el boton OK
                        alert.showAndWait();
                    default: // Error directorio de saida inexistente
                        alert = new Alert(AlertType.ERROR); // Alert de tipo error
                        alert.setHeaderText("INTERNAL ERROR"); // Añadimos cabecera
                        alert.setContentText("Error, output directory does not exist.\nYou have to specify an existing output directory."); // Establecemos contenido
                        // Establecemos el alto minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        // Establecemos el ancho minimo del alert para que se ajuste al contenido
                        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
                        // Mostramos alerta y esperamos que el usuario haaga click en el boton OK
                        alert.showAndWait();
                        outputFile.setClassStyle("tfError");
                        System.out.println("Other");
                }
            } catch (IOException ioex) {Logger.getLogger(DEncoderController.class.getName()).log(Level.SEVERE, null, ioex);
            } catch (InterruptedException ex) {Logger.getLogger(DEncoderController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    // Metodo que se ejecuta al hacer click en el boton contacto del menu "Jose Bravo"
    @FXML
    public void joseBravo(ActionEvent event) {
        // Declaramos alert de tipo error, con la cadena formada dependiendo de los errores cometidos
        Alert alert = new Alert(AlertType.INFORMATION);
        // Establecemos header del alert
        alert.setHeaderText("Contact information");
        alert.setContentText("Jose Bravo Castillo - jobravcas@gmail.com");
        // Establecemos el alto minimo del alert para que se ajuste al contenido
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        // Establecemos el ancho minimo del alert para que se ajuste al contenido
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        // Mostramos alerta y esperamos que el usuario haaga click en el boton OK
        alert.showAndWait();
    }
    
    // Metodo que se ejecuta al hacer click en el boton help del menu "Help"
    @FXML
    public void help(ActionEvent event) {
        Scene browserScene;
        Stage browserStage = new Stage();
    
        // Hacemos la ventana de tipo modal respecto a la aplicación
        browserStage.initModality(Modality.APPLICATION_MODAL);
        
        // Añadimos la escena al stage y la hacemos visible
        browserStage.setTitle("DEncoder user´s manual");
        browserScene = new Scene(new Browser(browserStage), 750, 500, Color.web("#666970"));
        browserStage.setScene(browserScene);
        //scene.getStylesheets().add(AppAyuda.class.getResource("BrowserToolbar.css").toExternalForm());
        browserStage.show();  
    }
    
    // Metodo para validar los campos
    public boolean validateFields() {
        
        // Creamos booleanos que nos dicen los campos validos e invalidos
        boolean input = false, pswd = false, output = false;
        // Creamos la cadena que se mostrá en el alert en caso de que algun campo sea invalido
        String cadena = "";
        
        // Comprobamos que haya contenido en el fichero de entrada
        if(!inputFile.getPath().isEmpty()) { // Si hay contenido (no está vacío)
            // Cargamos el fichero
            File inFile = new File(new File(inputFile.getPath()).getAbsolutePath());

            if(inFile.exists()) { // Si el fichero existe
                if(inFile.isFile()) { // Y es un fichero, no un directorio
                    input = true; // El campo de l fichero de entrada es valido
                } else { // Si no es un fichero
                    cadena += "- Specified input file is a directory.\n\n"; // Mensaje de erro para el alert
                    inputFile.setPrText("Specify a file, not a directory"); // Establecemos prompt text especificando el error
                }
            } else { // Si no existe
                cadena += "- Specified input file does not exist.\n\n"; // Mensaje de error para el alert
                inputFile.setPrText("Specify an existing file"); // Establecemos prompt text especificando el error
            }
        } else { // Si esta vacio
            cadena += "- You have to specify an input file to encode or decode.\n\n"; // Mensaje de error para el alert
            inputFile.setPrText("Don't leave this field empty"); // Establecemos prompt text especificando el error
        }
        
        
        // Comprobamos que la contraseña sea valida -> Exactamente 8 caracteres
        if(password.getText().length() == 8) {
            pswd = true; // Validamos contraseña
        } else { // Si la contraseña no tiene 8 caracteres exactamente
            cadena += "- Password must have exactly 8 characters.\n\n"; // Mensaje de error para el alert
            
            if(password.getText().isEmpty()) { // Si esta vacia
                password.setPromptText("Don't leave this field empty"); // Establecemos prompt text especificando el error
            } else { // Si no
                password.setPromptText("Password must have exactly 8 characters"); // Establecemos prompt text especificando el error
            }
        }
        
        // Comprobamos que el fichero de salida no esté vacío
        if(!outputFile.getPath().isEmpty()) {
            // Comprobamos que el fichero de salida no exista
            File outFile = new File(new File(outputFile.getPath()).getAbsolutePath());
            // Si el fichero no existe
            if(!outFile.exists()) {
                output = true; // Validamos fichero de salida
            } else { // Si existe
                cadena += "- Specified output file already exist, delete file, specify other output directory, or other name.\n\n"; // Mensaje de error para el alert
                outputFile.setPrText("Specify a non-existing file"); // Establecemos prompt text especificando el error
            }
        } else { // Si el campo esta vacio
            cadena += "- You have to specify an output file to save your encode or decode.\n\n"; // Mensaje de error para el alert
            outputFile.setPrText("Don't leave this field empty"); // Establecemos prompt text especificando el error
        }
        
        // Si todos los campos estan validados
        if(input && pswd && output) {
            inputFile.removeClassStyle("tfError"); // Establecemos estilo de error
            password.getStyleClass().remove("tfError"); // Establecemos estilo de error
            outputFile.removeClassStyle("tfError"); // Establecemos estilo de error
            return true; // Devolvemos true
        } else {
            // Declaramos alert de tipo error, con la cadena formada dependiendo de los errores cometidos
            Alert alert = new Alert(AlertType.ERROR, cadena);
            // Establecemos header del alert
            alert.setHeaderText("Resolve following problems");
            // Establecemos el alto minimo del alert para que se ajuste al contenido
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            // Establecemos el ancho minimo del alert para que se ajuste al contenido
            alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
            // Mostramos alerta y esperamos que el usuario haaga click en el boton OK
            alert.showAndWait();
            
            // Si no es valido el campo del cihero de entrada
            if(!input)
            {
                inputFile.setClassStyle("tfError"); // Establecemos estilo de error
            }
            
            // Si no es valido el campo de la contraseña
            if(!pswd) {
                password.getStyleClass().add("tfError"); // Establecemos estilo de error
            }
            
            // Si no es valido el campo del fichero de salida
            if(!output) {
                outputFile.setClassStyle("tfError"); // Establecemos estilo de error
            }
            
            // Devolvemos false
            return false;
        }
        
    }
    
    // Metodo para extraer el nombre del fichero
    public String getFileName(String s) {
        String osName = System.getProperty("os.name"); // Extraemos el nombre del sistema operativo
        int startIndex; // Variable para la ultima ocurrencia del slash o el backslash
        
        if(osName.contains("Windows")) { // SO Windows -> Usa backslash
            startIndex = s.lastIndexOf("\\"); // Buscamos la ultima ocurrencia del backslash (donde empieza el nombre del fichero)
        } else { // SOs UNIX -> Usa slash
            startIndex = s.lastIndexOf("/"); // Buscamos la ultima ocurrencia del slash (donde empieza el nombre del fichero)
        }
        
        if(startIndex == -1) { // No hay slash ni backslash
            return s; // Devolvemos la cadena
        } else { // Si lo hay
            return s.substring(startIndex + 1); // Devolvemos la cadena desde la posicion siguiente a la ultima ocurrencia del slash o backslash, hasta el final
        }
    }
    
    // Clase para manejar la pulsacion del boton del componente fichero de entrada (inputFile)
    public class searchButtonInput implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // Creamos el selector de fichero
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Input file selector"); // Establecemos titulo para la ventana del selector
            
            // El directorio que se abrira para seleccionar el fichero será el home del usuario
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // Con "user.dir" -> La ubicacion donde se ejecuta el programa
            
            // Establecemos filtro de archivos que se muestran (all files)
            fileChooser.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
            
            // Recogemos el fichero seleccionado
            File selectedFile = fileChooser.showOpenDialog(mainStage);
            
            // Si lo que recoge el fileChooser es null, se ha cancelado la seleccion de fichero
            // Si es distinto de null, se ha seleccionado un fichero existente
            if(selectedFile != null) {
                inputFile.setPath(selectedFile.getPath()); // Establecemos el contenido del campo de texto
                outputFile.setVisible(true); // Hacemos visible el componente del fichero de salida
                outputLabel.setVisible(true); // 
            }
        }

    }
    
    // Clase para manejar la pulsacion del boton del componente fichero de salida (outputFile)
    public class searchButtonOutput implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // Creamos el selector de directorio
            DirectoryChooser directoryChooser = new DirectoryChooser();
            // Establecemos titulo al selector
            directoryChooser.setTitle("Output directory selector");
            
            // El directorio que se abrira para seleccionar será el home del usuario para facilitar la navegacion
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // Con "user.dir" -> La ubicacion donde se ejecuta el programa
            
            // Recogemos el directorio seleccionado
            File selectedFile = directoryChooser.showDialog(mainStage);
            
            // Si lo que recoge el directoryChooser es null, se ha cancelado la seleccion de fichero
            if(selectedFile != null) {
                // Si el fichero seleccionado es un directorio (no puede ser otra cosa con el selector de directorios)
                if(selectedFile.isDirectory())
                {
                    // Establecemos el path del directorio recogido + separador de nodos en un path* + nombre del fichero seleccionado en el inputfile + extensión .cif
                    outputFile.setPath(selectedFile.getPath() + System.getProperty("file.separator") + getFileName(inputFile.getPath()) + ".DEncode");
                    // * Con la instruccion System.getProperty("file.separator") obtenemos el separador que usa el sistema operativo
                    //   devolverá "/" en sistemas UNIX, y "\" en sistemas Windows
                }
            }
        }
    }
    
    // Clase que sera el listener que cambia el contenido del boton D-Encode en funcion de la orden seleccionada
    public class listenerChangeNameButton implements ChangeListener<Toggle> {

        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            // Comprobamos cual es el radio button seleccionado
            if(rbEncode.isSelected()) { // Si es el de codificar
                btnDEncode.setText("Encode"); // Establecemos nombre del boton
                btnDEncode.setTooltip(new Tooltip("Press me to encode your file"));// Cambiamos el tooltip
            } else { // Si no (es decodificar)
                btnDEncode.setText("Decode"); // Establecemos nombre del boton
                btnDEncode.setTooltip(new Tooltip("Press me to decode your file")); // Cambiamos el tooltip
            }
        }
    }
    
    // Clase que será el listener que se ejecuta al cambiar el foco del componente para seleccionar el fichero de entrada
    public class listenerChangeFocusOnInputTextField implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            // Evitamos que se duplique la clase en la lista de clases del componente por si se añade mas de una vez (reiteracion de obtener y perder el foco con el campo vacio)
            inputFile.removeClassStyle("tfError");
            
            // Si pierde el foco
            if(!newValue)
            {
                // Y no hay ruta escrita en el campo de texto
                if(inputFile.getPath().isEmpty()){
                    // Cambios del componente del fichero de entrada
                    inputFile.setClassStyle("tfError"); // Asignamos clase de error de campo de texto
                    inputFile.setPrText("Don't leave this field empty"); // Cambiamos el prompt text
                    
                    // Cambios en el fichero de salida
                    outputFile.setPath(""); // Vaciamos el campo de texto del directorio de salida (por si no está vacio, caso de escribir un fichero de salida y borrar la ruta del fichero de entrada)
                    outputFile.setVisible(false); // Volvemos no visible el componente del output
                    outputLabel.setVisible(false); // Volvemos no visible la etiqueta
                    
                } else { // Si tiene algo escrito
                    inputFile.removeClassStyle("tfError"); // Eliminamos la clase de error (si no la tiene no hace nada)
                    inputFile.setPrText("Enter input file"); // CAmbiamos el prompt text (no se verá)
                    outputFile.setVisible(true); // Hacemos visible componente del fichero de salida
                    outputLabel.setVisible(true); // Hacemos visible la etiqueta del directorio de salida
                }
            } else { // Si gana el foco
                inputFile.removeClassStyle("tfError"); // Quitamos la clase de error
                inputFile.setPrText("Enter an input file"); // Cambiamos el prompt text (aunque no se vera debido a que tiene el foco)
            }
        }
        
    }
    
    // Clase que sera el listener que controla el cambio de foco en el componente selector del fichero de salida
    public class listenerChangeFocusOnOutputTextField implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            // Evitamos que se duplique la clase en la lista de clases
            outputFile.removeClassStyle("tfError");
            
            // Si pierde el foco
            if(!newValue)
            {
                // Y no tiene path establecido
                if(outputFile.getPath().isEmpty()){
                    outputFile.setClassStyle("tfError"); // Asignamos clase de error de campos de texto
                    outputFile.setPrText("Don't leave this field empty"); // Asignamos prompt text avisando del erro al usuaior
                } else { // Si tiene ruta escrita
                    outputFile.removeClassStyle("tfError"); // Eliminamos clase de error (si la tiene)
                    outputFile.setPrText("Enter an output file"); // Establecemos el prompt text (no se verá, ya que hay algo escrito)
                }
            } else { // Si gana el foco
                outputFile.removeClassStyle("tfError"); // Quitamos clase de error
                outputFile.setPrText("Enter an output file"); // Establecemos el prompt text (no se verá debido a que tiene el foco)
            }
        }
    }
    
    // Clase que será el listener que controla el cambio de foco en el campo de la contraseña
    public class listenerChangeFocusOnPasswordField implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            // Evitamos que se duplique la clase en la lista de clases (si tien la clase la elimina, sino no hace nada
            password.getStyleClass().remove("tfError");
            
            // Si pierde el foco
            if(!newValue) {
                // Si no hay contraseña escrita [Puede deberse a dos motivos: 
                //                  - Se ha hecho click en el campo, no se ha escrito nada y se le ha quitado el foco, en cuyo caso no tiene estilo de error
                //                  - Se ha escrito algo y se ha borrado, en este caso está vacia tambien, pero tiene el estilo de error asignado
                if(password.getText().isEmpty()) {
                    // Quitamos el estilo de error (aseguramos que no se duplique en la lista de estilos del componente)
                    password.getStyleClass().remove("tfError");
                    // Añadmos estilo de error
                    password.getStyleClass().add("tfError");
                    // Establecemos prompt text avisando del error al dejar el campo vacio
                    password.setPromptText("Don't leave this field empty.");
                } else { // Si el campo tiene una contraseña escrita
                    // Y la contraseña no tiene la longitud requerida (El campo tiene ya el estilo de error al no tener 8 caracteres [listener implementado en el metodo initialize])
                    if(password.getText().length() != 8) {
                        // Quitamos el estilo de error (aseguramos que no se duplique en la lista de estilos del componente)
                        password.getStyleClass().remove("tfError");
                        // Añadmos estilo de error
                        password.getStyleClass().add("tfError");
                        // Declaramos alert
                        Alert alert = new Alert(AlertType.WARNING, "Password must have exactly 8 characters!!");
                        // Mostramos alerta y esperamos que el usuario haaga click en el boton OK
                        alert.showAndWait();
                    }
                }
            } else { // Si gana el foco
                // Quitamos estilo de error si lo hay
                password.getStyleClass().remove("tfError");
            }
        }
    }
}
