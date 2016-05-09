lib: libparallelepiped.a libparallelepiped.so

all: libparallelepiped.a libparallelepiped.so test_parallelepiped

CC = g++



clean:
	\rm -f *.o *~ 

distclean: clean
	\rm -f libparallelepiped.a libparallelepiped.so test_parallelepiped __qconvex_input_file  __qconvex_output_file


libparallelepiped.a: domaine.o base.o 
	ar -rv libparallelepiped.a domaine.o base.o 

libparallelepiped.so: domaine.o base.o 
	$(CC) -o libparallelepiped.so -shared domaine.o base.o 

# for dynamic linking
test_parallelepiped: test_parallelepiped.o libparallelepiped.so
	$(CC) -o test_parallelepiped test_parallelepiped.o -L. -lparallelepiped -lm 

minPPD: minPPD.o libparallelepiped.so
	$(CC) -o minPPD minPPD.o -L. -lparallelepiped -lm 


# for static linking
#test_parallelepiped: test_parallelepiped.o libparallelepiped.a
#	$(CC) -static -o test_parallelepiped test_parallelepiped.o -L. -lparallelepiped -lm 

minPPD.o: minPPD.cpp domaine.h  base.h 
	$(CC) minPPD.cpp  -c

test_parallelepiped.o: test_parallelepiped.cpp domaine.h  base.h 
	$(CC) test_parallelepiped.cpp  -c 

domaine.o: domaine.cpp domaine.h base.h 
	$(CC) domaine.cpp -c 

base.o: base.cpp base.h
	$(CC) base.cpp -c
