#--------------------------------
#Makefile para proyecto cifrador
#(C) codefork.com
#--------------------------------

DEncoder:main.o cifrado.o
	gcc -Wall -oDEncoder cifrado.o main.o

main.o:main.c
	gcc -Wall -c main.c

cifrado.o:cifrado.c
	gcc -Wall -c cifrado.c

clean:
	rm -f *.o

cleanall:
	rm -f *.o
	rm -f DEncoder