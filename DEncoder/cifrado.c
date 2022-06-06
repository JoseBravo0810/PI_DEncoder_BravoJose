	/*
		Jose Bravo Castillo
	*/

#include "cifrado.h"

/* Funcion que encripta y desencripta */
void d_encode(char **b, char *p, int bs)
{
	/* Declaracion de variables */
	//Puntero al contenido del puntero donde recibe el bloque la funcion
	char *block = (*b);
	//Contador para el bucle
	int i;

	/* Bucle que codifica byte a byte el bloque con los bytes de la contraseña */
	for(i = 0; i < bs; i++)
	{
		block[i] ^= p[i];
	}
}

/* Funcion para agregar ruido */
void noise(char *b, char **eb, int ebs)
{
	/* Declaracion de variables */
	//Puntero que apunta al contenido del bloque extendido que le pasamos como argumento */
	char *ext_block = (*eb);
	//Contadores
	int i;
	int j = 0;

	/* Bucle de 0 al tamaño en bytes del bloque extendido */
	for(i = 0; i < ebs; i++)
	{
		/* Si el resto de i entre 2 es 0 (numero par), el bloque extendido en la 
		posicion i es igual al bloque en la posicion i; Si no es un numero aleatorio de 8 bits (0 a 255) 
		e incrementa contador para que la siguiente vez que entre sea la siguiente posicion del bloque*/
		if(i % 2 == 0)
		{
			ext_block[i] = b[j];
			j++;
		}
		else
			ext_block[i] = rand() % 256;
	}
}

/* Funcion para quitar el ruido */
void denoise(char **b, char *eb, int ebs)
{
	/* Declaracion de variables */
	//Puntero al contenido del bloque donde vamos a poner los valores buenos
	char *block = (*b);
	//Contadores
	int i;
	int j = 0;

	/* Bucle de 0 al tamaño en bytes del bloque extendido */
	for(i = 0; i < ebs; i++)
		//Si es par la posicion del bloque estendido se copia en el bloque y se aumenta contador
		if(i % 2 == 0)
		{
			block[j] = eb[i];
			j++;
		}
}

/* Funcion para rotar los bits de los bytes que componen la contraseña */
void shift_password(char **p, int b)
{
	/* Declaracion de variables */
	//Puntero que apunta al contenido de p (la contraseña)
	char *password = (*p);

	/* A raiz del bit que se debe tocar pasado como argumento a la funcion, se obtiene el byte que debemos rotar
	Realizamos la operacion [Byte desplazado a la izquierda 1 posicion] | [Byte desplazado a la derecha 7 posiciones]
	De esta forma conseguimos recoger por el bit menos significativo el bit que se cae al depsplazar en una posicion a la izquierda los bits */
	password[b / strlen(password)] = (password[b / strlen(password)] << 1) | (password[b / strlen(password)] >> 7);

}