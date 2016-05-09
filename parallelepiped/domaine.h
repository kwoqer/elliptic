/* parallelepiped
 * Copyright (C) 2003 Frederic Vivien and Nicolas Wicker
 *
 * domaine.h
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

#include <stdlib.h>
#include <fstream>
#include <iostream>
#include <sstream>
#include <math.h>


using namespace std;


class Domaine
{
  public :

  Domaine(int);
  ~Domaine();

  int _nb_points; 
  double (*_points)[3]; //tous les points avec leurs coordonnees du masque
  double _parallelepipede[8][3]; //definis par les points de deux cotes paralleles.x

  int compute_parallelepiped();

 


  private :

  int _nb_sommets;
  int *_sommets; //indices des points qui sont des sommets de l'enveloppe convexe
  int *_nb_sommets_par_facette; //indices des points de l'enveloppe convexe
  int **_sommets_des_facettes; //indices des points de l'enveloppe convexe
  int _nb_aretes; //nb aretes de l'enveloppe convexe
  int **_aretes; //indices des points des aretes
  int _nb_facettes;
  double (*_facettes)[4];
  int **_points_antipodaux;
  int *_nb_points_antipodaux;
  double *_epaisseurs;
  int _antipodaux_presents;

  void calcul_epaisseur();
  void calcul_meilleur_triplet_methode5(int triplet_solution[3],double (*normales)[3]);
  int test_candidat(int nb_vecteurs,double (*vecteurs)[3],double normale_facette2[3]);
  void calcul_de_deux_cotes_paralleles_du_parallelepipede(int triplet_solution[3]);

  int  calcul_enveloppe_convexe_aretes_et_antipodaux_largescale();
  void calcul_plus_petit_parallelepipede_simple();
  void efface_memoire_allouee_pour_enveloppe_convexe();
};









