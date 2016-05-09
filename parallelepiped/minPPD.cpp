#include "domaine.h"
#include <iostream>
#include <cstdlib>
#include <string>
#include <fstream>
#include <time.h>

#define PI 3.141592654

using namespace std;
 

int main(int argc,char **argv)
{
  Domaine * domaine;
  int number;
  // double r, theta, phi;
  int success;
  fstream file;
  
  
  /*switch(argc)
    {
    case 2:
      n = atoi(argv[1]);
      break;
    
    default:
      cerr << endl;
      cerr << "This test program takes one argument : " ;
      cerr << "the number of points in the original set." << endl;
      n = 10;    
    }    
  */

  //cerr << endl << "Building a random set of " << n << " points ";
  //cerr << "taken around the unit sphere.\n" << endl;

  file.open("_points.txt");
  file >> number;

  // Allocation of the data structure
  domaine = new Domaine(number);
    
  /* Storing of the points
  srandom(time(NULL));
  for(int i=0; i<domaine->_nb_points; i++){
    theta = (random()*1.0/RAND_MAX)*2*PI;
    phi   = (random()*1.0/RAND_MAX)*2*PI;
    r = 1.0;
    domaine->_points[i][0] = r*sin(theta)*cos(phi);
    domaine->_points[i][1] = r*sin(theta)*sin(phi);
    domaine->_points[i][2] = r*cos(theta);
  }
  */

  //Reading points from _points.txt
  double x, y, z;
  for(int i=0; i<domaine->_nb_points; i++){
    
    file >> x; file >> y; file >> z;
    domaine->_points[i][0] = x;
    domaine->_points[i][1] = y;
    domaine->_points[i][2] = z;
  }
    
  

  // Computation of the smallest enclosing parallelepiped
  success = domaine->compute_parallelepiped();

  // Printing of the paralelepiped vertices
  if (success==0){
    cout << endl << "Vertices of the smallest enclosing parallelepiped are:";
    cout << endl << endl;

    for(int i=0; i<8; i++){
      for(int j=0; j<3; j++){
	cout << domaine->_parallelepipede[i][j] << "  \t";
      }
      cout << endl;
    }
  }
  else{
    return success;
  }


  delete domaine;
  
  return 0;
}



