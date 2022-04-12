/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources.passwordfieldskin;

import com.sun.javafx.scene.control.skin.TextFieldSkin;
import static com.sun.javafx.scene.control.skin.TextFieldSkin.BULLET;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 *
 * @author jose
 */
public class VisiblePasswordFieldSkin extends TextFieldSkin {
        
    // Declaramos un nuevo boton y una imagen de vectores (SGV)
    private final Button actionButton = new Button("View");
    private final SVGPath actionIcon = new SVGPath();

    // Un booleano que indica cuando esta enmascarada la contraseña (los simbolos del punto [Llamados BULLET])
    private boolean mask = true;

    // Constructor de la clase, toma como parametro un campo de texto de contraseñas
    public VisiblePasswordFieldSkin(PasswordField tf) {
        // Inicializamos la skin del campo de texto con su superclase, pasando como argumento el campo de texto de la contraseña
        super(tf); // Aqui, el enmascaramiento tiene unicamente el componente del campo de texto
        // Tenemos que ver la mascara (esta clase), como un binding de componentes en otros componentes, lo que nos da la capacidad de, como en este caso,
        // tener un boton "dentro" de un campo de texto. No es dentro, es que la mascara toma el tamaño del campo de texto que pasamos, y lo que añadimos
        // se ubica en este espacio

        actionButton.setId("showButton"); // Asignamos id al boton
        actionButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); // Mostramos solo el contenido, y no el boton en sí
        actionButton.setPrefSize(30,30); // Establecemos el alto y el ancho
        actionButton.setFocusTraversable(false); // Hacemos que no tenga el foco al hacer click en el campo de texto (su contenedor)
        actionButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, new Insets(0)))); // Establecemos el fondo como..
        //                                                           Transparente,      sin bordes         sin margenes

        // Añadimos el boton a la mascara (añadimos el boton al campo de texto
        getChildren().add(actionButton); // AQUI SE LANZA EL METODO layoutChildren() SOBREESCRITO
        actionButton.setCursor(Cursor.HAND); // Le damos la capacidad de ser clicado, sin esto, al hacer click en el boton, se interpretará como hacer click en el campo de texto
        // y no como click en el boton
        actionButton.toFront(); // Lo traemos al frente, visualmente, por delante del campo de texto (menor profundidad en el eje z que el campo de texto

        actionIcon.setContent(Icons.VIEWER.getContent()); // Le damos el contenido al icono que tendra e boton, al inicio, el ojo sin tachar declarado en SVG, en el tipo enumerado
        actionButton.setGraphic(actionIcon); // Establecemos el icono como el contenido grafico del boton

        actionButton.setVisible(false); // Hacemos visible el boton

        // Añadimos manejador al hacer click en el boton
        actionButton.setOnMouseClicked(event -> {

            // Si la contraseña esta enmascarada
            if(mask) {
                // Establecemos como icono del boton el ojo tachado
                actionIcon.setContent(Icons.VIEWER_OFF.getContent());
                mask = false; // Marcamos que la contraseña esta desenmascarada (visible)
            } else { // Si la contraseña esta visible
                actionIcon.setContent(Icons.VIEWER.getContent()); // Establecemos como icono del boton el ojo sin tachar
                mask = true; // MArcamos que la contraseña esta enmascarada
            }
            
            // Establecemos el texto que tendrá el campo de texto, AQUI SE LANZA EL METODO mask(String) SOBREESCRITO
            tf.setText(tf.getText());

            // Se establece el cursor al final del texto del campo de texto
            tf.end();

        });

        // Añadimos listener para hacer visible el boton solamente cuando haya contenido en el campo de texto.
        tf.textProperty().addListener((observable, oldValue, newValue) -> actionButton.setVisible(!newValue.isEmpty()));

    }

    // Metodo sobreescrito que se ejecuta al añadir un nodo hijo (componente hijo) en la skin que estamos fabricando
    // Recibe como parametros, la posicion x, la posicion y, el ancho, y el alto
    // Al ser protected el metodo que sobreescribimos, tenemos que importar esta clase donde vayamos a usarla
    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h); // Ejecutamos el metodo de la superclase para que añada el componente en el layout

        // Posicionamos el componente boton en el layout
        layoutInArea(actionButton, x, y, w, h,0, HPos.RIGHT, VPos.CENTER);
        /* Le decimos a la skin:
                - Componente a posicionar en el area (espacio que ocupa la skin)
                - La posicion x que tiene
                - La posicion y
                - El ancho
                - El alto
                - La linea base (baseline). La posicion que tendra el componente en vertical, con respecto del suelo de la mascara (el campo de texto). Al ser 0, el boton tiene su suelo
                  a ras del suelo del campo de texto
                - Lo posicionamos horizontalmente a la derecha
                - Y verticalmente en el centro
        */
    }

    // Metodo sobreescrito que se lanza al setear texto en el campo de texto gestionado por la skin (Recordemos que cada letra escrita en el campo, lanza este metodo)
    // Al ser protected el metodo que sobreescribimos, tenemos que importar esta clase donde vayamos a usarla
    @Override
    protected String maskText(String txt) {
        // Si el campo de texto es una instancia de campo de texto contraseña, y esta enmascarado
        // La primera condicion se cumplirá siempre, solo es para asegurar que no se asigna a otro componente
        if (getSkinnable() instanceof PasswordField && mask) {
            int n = txt.length(); // Numero de iteraciones del bucle para recorrer el texto contenido en el campo de texto
            
            StringBuilder passwordBuilder = new StringBuilder(n); // Creamos un constructor de cadenas de texto
            
            for (int i = 0; i < n; i++) { // De 0 a la longitud de la cadena que tenga el campo de texto
                passwordBuilder.append(BULLET); // Añadimos a la cadena que estamos construyendo el caracter del punto
            }
            
            // Devolvemos el contenido del constructor de cadenas (la cual contendrá un punto por cada caracter escrito en el campo de texto
            return passwordBuilder.toString();
        } else { // Si no se cumple la condicion (esta desenmascarado, o no es instancia de password field)
            // Se devuelve el texto sin enmascarar (el texto realmente escrito y no el caracter BULLET)
            return txt;
        }
    }
}

// Enumerado con los iconos
enum Icons {

    // Icono del ojo tachado
    VIEWER_OFF("M12 6c3.79 0 7.17 2.13 8.82 5.5-.59 1.22-1.42 2.27-2." +
            "41 3.12l1.41 1.41c1.39-1.23 2.49-2.77 3.18-4.53C21.27 7.11 17 4 12 4c-1.27 " +
            "0-2.49.2-3.64.57l1.65 1.65C10.66 6.09 11.32 6 12 6zm-1.07 1.14L13 9.21c.57.25 1.03.71 " +
            "1.28 1.28l2.07 2.07c.08-.34.14-.7.14-1.07C16.5 9.01 14.48 7 12 7c-.37 0-.72.05-1.07." +
            "14zM2.01 3.87l2.68 2.68C3.06 7.83 1.77 9.53 1 11.5 2.73 15.89 7 19 12 19c1.52 0 2.98-.29 " +
            "4.32-.82l3.42 3.42 1.41-1.41L3.42 2.45 2.01 3.87zm7.5 7.5l2.61 2.61c-.04.01-.08.02-.12.02-1.38 " +
            "0-2.5-1.12-2.5-2.5 0-.05.01-.08.01-.13zm-3.4-3.4l1.75 1.75c-.23.55-.36 1.15-.36 1.78 0 2.48 2.02 " +
            "4.5 4.5 4.5.63 0 1.23-.13 1.77-.36l.98.98c-.88.24-1.8.38-2.75.38-3.79 0-7.17-2.13-8.82-5.5.7-1.43 1.72-2.61 2.93-3.53z"),

    // Icono del ojo sin tachar
    VIEWER("M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7." +
                "5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z");

    private String content; // Variable con el contenido que tendra el icono (uno u otro)

    // Metodo para establecer el contenido
    Icons(String content) {
        this.content = content;
    }

    // Metodo para obtener el contenidos
    public String getContent() {
        return content;
    }
}
