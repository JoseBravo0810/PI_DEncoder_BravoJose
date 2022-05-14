/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pi_dencoder_bravojose;

import controller.DEncoderController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jose
 */
public class PI_DEncoder_BravoJose extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Resources/fxml/FXMLDEncoder.fxml"));
        root = loader.load();
        
        // Obtenemos el controlador
        DEncoderController controller = (DEncoderController) loader.getController();
        // Establecemos la escena en el controlador
        controller.setMainStage(stage);
        
        Scene scene = new Scene(root);
        
        // Enlazamos la hoja de estilos en cascada (CSS - Cascading Style Sheet)
        scene.getStylesheets().add("/Resources/css/error.css");
        
        stage.setScene(scene);
        stage.setTitle("DEncoder");
        stage.resizableProperty().set(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
