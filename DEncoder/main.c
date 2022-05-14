	/*
		Recuperacion trimestre 2
		Jose Bravo Castillo
		invocación programa: ./des_cifrador fichero_plano fichero_destino c/d password
	*/

#include "cifrado.h"

int main(int argc, char **argv)
{
	/* Comprobamos el numero de argumentos */
	if(argc != 5)
	{
		printf("Error. Me has pasado %d argumentos\n", argc - 1);
		printf("Sintaxis: ./des_cifrador fichero_origen fichero_destino orden(c|d) password\n");
		return ERROR_NOARGS;
	}

	/* Comprobamos que el cuarto argumento sea c o d, para cifrar o descifrar */
	if(strcmp(argv[3], "d") != 0 && strcmp(argv[3], "c") != 0)
	{
		printf("Error. Tienes que elegir si quieres cifrar (c) o descrifrar (d)\n");
		return ERROR_NOCD;
	}

	/* Declaracion de variables */
	//Descriptores de los ficheros
	FILE *fd_flat;
	FILE *fd_d_encode;
	//Variables para almacenar el bloque
	char *block;
	char *ext_block;
	//Variables del tamaño de los bloques (en base a contraseña)
	int block_size = strlen(argv[4]);
	int ext_block_size = block_size * 2;
	//Variables para saber el numero de bloques del fichero
	struct stat file_size;
	int block_num;
	int rest_bytes;
	//Variable para amacenar la contraseña y su cantidad de bits
	char *password =  (char *) argv[4];
	int password_bit_num = strlen(password) * 8;
	//Contador para bucles
	int i;

	//Inicializamos las variables que recogen los blockes (el nloque y el bloque extendido)
	block = (char *) malloc(block_size);
	ext_block = (char *) malloc(ext_block_size);

	//Inicializamos la semilla de random
	srand(time(NULL));

	/*Abrimos el fichero de origen. fichero plano */
	fd_flat = fopen(argv[1], "rb");
	if(fd_flat == NULL)
	{
		printf("Error al abrir el archivo\n");
		return ERROR_NOFILE;
	}


	/*Abrimos el fichero de destino. fichero des_cifrado*/
	fd_d_encode = fopen(argv[2], "wb");
	if(fd_d_encode == NULL)
	{
		printf("Error al crear el archivo\n");
		return ERROR_NOOPEN;
	}


	/* Si orden es cifrar(c), o descifrar(d) */
	if(strcmp(argv[3], "c") == 0)
	{
		//Determinamos el maximo del fichero
		stat(argv[1], &file_size);

		//Recogemos el tamaño del fichero en bloques (blocks)
		block_num = file_size.st_size / block_size;
		rest_bytes = file_size.st_size % block_size;

		/* Bucle de cifrado */
		for(i = 0; i < block_num; i++)
		{
			//Leemos el primer bloque de bytes
			fread(block, block_size, 1, fd_flat);

			//Codificamos el bloque
			d_encode(&block, password, block_size);

			//Añadimos ruido;
			noise(block, &ext_block, ext_block_size);

			//Escribimos el bloque extendido en el fichero destino
			fwrite(ext_block, ext_block_size, 1, fd_d_encode);

			//desplazamos la clave para darle dinamismo y que no se codifique siempre con los mismos valores
			shift_password(&password, i % password_bit_num);
		}

		if(rest_bytes > 0)
		{
			//Leemos el resto del bloque
			fread(block, rest_bytes, 1, fd_flat);

			//Codificamos el bloque
			d_encode(&block, password, rest_bytes);

			//Añadimos ruido
			noise(block, &ext_block, rest_bytes);

			//Escribimos el bloque extendido en el fichero destino
			fwrite(ext_block, rest_bytes * 2, 1, fd_d_encode);
		}
	}
	else
	{
		//Determinamos el maximo del fichero
		stat(argv[1], &file_size);

		//Recogemos el tamaño del fichero en bloques (blocks)
		block_num = file_size.st_size / block_size;
		rest_bytes = file_size.st_size % block_size;

		/* Bucle de cifrado (esta vez la mitad de lo que lee, ya que el fichero a descifrar ocupa el doble por el ruido) */
		for(i = 0; i < block_num / 2; i++)
		{
			//Leemos el primer bloque de bytes
			fread(ext_block, ext_block_size, 1, fd_flat);

			//Quitamos el ruido;
			denoise(&block, ext_block, ext_block_size);

			//Decodificamos el bloque
			d_encode(&block, password, block_size);

			//Escribimos el bloque en el fichero destino
			fwrite(block, block_size, 1, fd_d_encode);

			//desplazamos la clave para darle dinamismo y que no se decodifique siempre con los mismos valores (el metodo es el mismo que para cifrarlo)
			shift_password(&password, i % password_bit_num);
		}

		/* Si quedan bytes sueltos que no completan un bloque */
		if(rest_bytes > 0)
		{
			//Leemos el resto del bloque
			fread(ext_block, rest_bytes, 1, fd_flat);

			//Quitamos el ruido
			denoise(&block, ext_block, rest_bytes);

			//Decodificamos el bloque
			d_encode(&block, password, rest_bytes);

			//Escribimos el bloque en el fichero destino
			fwrite(block, rest_bytes / 2, 1, fd_d_encode);
		}
	}

	/* Cerramos los ficheros abiertos, origen (plano) y destino (des_cifrado) */
	fclose(fd_flat);
	fclose(fd_d_encode);

	/* Cerramos los punteros creados, no es necesario cerrar password, ya que no le hemos hecho malloc, directamente apunta al valor de la contraseña en los argumentos */
	free(block);
	free(ext_block);

	return 0;
}