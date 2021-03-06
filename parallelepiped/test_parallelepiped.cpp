/* parallelepiped
 * Copyright (C) 2003 Frederic Vivien and Nicolas Wicker
 *
 * test_parallelepiped.cpp
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, 
 * USA.
 */

#include "domaine.h"
#include <time.h>

#define PI 3.141592654


int main(int argc,char **argv)
{
  Domaine * domaine;
  int n;
  double r, theta, phi;
  int success;
  
  switch(argc)
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
  

  cerr << endl << "Building a random set of " << n << " points ";
  cerr << "taken around the unit sphere.\n" << endl;


  // Allocation of the data structure
  domaine = new Domaine(n);
    
  // Storing of the points
  srandom(time(NULL));
  for(int i=0; i<domaine->_nb_points; i++){
    theta = (random()*1.0/RAND_MAX)*2*PI;
    phi   = (random()*1.0/RAND_MAX)*2*PI;
    r = 1.0;
    domaine->_points[i][0] = r*sin(theta)*cos(phi);
    domaine->_points[i][1] = r*sin(theta)*sin(phi);
    domaine->_points[i][2] = r*cos(theta);
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



