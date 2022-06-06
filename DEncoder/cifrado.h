	/*
		Jose Bravo Castillo
	*/

/* Incluimos librerias */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>

/* Definimos constantes */
#define ERROR_NOARGS -1
#define ERROR_NOFILE -2
#define ERROR_NOOPEN -3
#define ERROR_NOCD -4


/* Prototipos de funciones */
/* Funcion que de/codifica, al ser una operacion XOR (^), al pasarlo una vez codifica y la siguiente decodifica 
Parametros: Un puntero al bloque a cifrar, La contraseña, Tamaño del bloque */
void d_encode(char **, char *, int);
/* Funcion para añadir ruido al bloque; Parametros: Bloque inicial, Puntero a bloque extendido, Tamaño del bloque extendido */
void noise(char *, char **, int);
/* Funcion para quitar el ruido al bloque; Parametros: Bloque donde ubicaremos los valores validos, Bloque con ruido, Tamaño del bloque con ruido */
void denoise(char **, char *, int);
/* Funcion para rotar los bits de los bytes de la contraseña;
Parametros: La contraseña, El bit que debemos manipular (para saber a que byte debemos rotarle los bits) */
void shift_password(char **, int);
