/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pi_dencoder_bravojose;

import controller.DEncoderController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import netscape.javascript.JSObject;

/**
 *
 * @author jose
 */
public class Browser extends Region{
    
    //Los atributos que requiere el buscador
    private HBox toolBar;
    final Hyperlink[] hpls = new Hyperlink[captions.length];
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    final WebView smallView = new WebView();
    
    //Los labels de los links que estarán visibles en todo momento para que el usuario pueda moverse cómodamente por los ficheros de ayuda
    private static String[] captions = new String[]
    {
        "\tIndex\t",
        "\tInput File\t",
        "\tOrder\t",
        "\tPassword\t",
        "\tOutput File"
    };
    
    //Los links de los labels anteriores
    private String[] urls = new String[]
    {
        PI_DEncoder_BravoJose.class.getResource("/Resources/html/index.html").toExternalForm(),
        PI_DEncoder_BravoJose.class.getResource("/Resources/html/InputFile.html").toExternalForm(),
        PI_DEncoder_BravoJose.class.getResource("/Resources/html/Order.html").toExternalForm(),
        PI_DEncoder_BravoJose.class.getResource("/Resources/html/Password.html").toExternalForm(),
        PI_DEncoder_BravoJose.class.getResource("/Resources/html/OutputFile.html").toExternalForm(),
    };
    

    //Constructor
    public Browser(final Stage stage) 
    {
        stage.setResizable(true);
        //Convertimos los enlaces en hyperlinks funcionales
        for (int i = 0; i < captions.length; i++) 
        {
            Hyperlink hpl = hpls[i] = new Hyperlink(captions[i]);
            final String url = urls[i];
            
            //procesamos el evento de hacer click en un hyperlink
            hpl.setOnAction(new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent e) 
                {
                    webEngine.load(url);
                }
            });
        }
        
        System.out.println(urls);
        
        //create the toolbar
        toolBar = new HBox();
        toolBar.setAlignment(Pos.CENTER);
        toolBar.setStyle("-fx-font-size: 15px; -fx-alignment: center;");
        toolBar.setPadding(new Insets(0,80,0,80));
        toolBar.getChildren().addAll(hpls);
        toolBar.getChildren().add(createSpacer());
        
        smallView.setPrefSize(120, 80);
        
        //manejo de ventanas emergentes
        webEngine.setCreatePopupHandler( new Callback<PopupFeatures, WebEngine>() 
        {
            @Override
            public WebEngine call(PopupFeatures config) 
            {
                smallView.setFontScale(0.8);
                if (!toolBar.getChildren().contains(smallView)) 
                {
                    toolBar.getChildren().add(smallView);
                }
                return smallView.getEngine();
            }
        });
        getChildren().add(toolBar);
        
        //Procesamos el cargado de páginas
        webEngine.getLoadWorker().stateProperty().addListener( new ChangeListener<Worker.State>() 
        {
            @Override
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) 
            {
                if (newState == Worker.State.SUCCEEDED) 
                {
                    JSObject win = (JSObject) webEngine.executeScript("window");
                    win.setMember("app", new JavaApp());                     
                }
            }
        });
        
        // Cargamos la página por defecto, que será el índice de los topics
        webEngine.load(PI_DEncoder_BravoJose.class.getResource("/Resources/html/index.html").toExternalForm());
        
        
        getChildren().add(browser);
    }
    
    //Objeto de interfaz JavaScript
    public class JavaApp 
    {
        public void exit() 
        {
            Platform.exit();
        }
    }
    
    private Node createSpacer() 
    {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
    
    @Override
    protected void layoutChildren() 
    {
        double w = getWidth();
        double h = getHeight();
        double tbHeight = toolBar.prefHeight(w);
        layoutInArea(browser,0,0,w,h-tbHeight,0, HPos.CENTER, VPos.CENTER);
        layoutInArea(toolBar,0,h-tbHeight,w,tbHeight,0,HPos.CENTER,VPos.CENTER);
    }
    
    @Override
    protected double computePrefWidth(double height) 
    {
        return 1560;
    }
    
    @Override
    protected double computePrefHeight(double width) 
    {
        return 1080;
    }
    
}
