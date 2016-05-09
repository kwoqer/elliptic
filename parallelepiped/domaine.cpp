/* parallelepiped
 * Copyright (C) 2003 Frederic Vivien and Nicolas Wicker
 *
 * domaine.cpp
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


#include "base.h"
#include "domaine.h"


Domaine::Domaine(int n)
{
  _nb_points = n;
  _points=new double[_nb_points][3];
}


Domaine::~Domaine()
{
  delete [] _points;
}


int Domaine::compute_parallelepiped()
{
  int taille_chull = -1;
  

  if (_nb_points<4){
    cerr << "\n\nDegenerate case : the set must contain at least 4 points." << endl;
    cerr << endl << "Aborting !!!!!!!!!" << endl << endl ;
    return -1;
  }
  
  taille_chull = calcul_enveloppe_convexe_aretes_et_antipodaux_largescale();

  if (taille_chull>0){
    calcul_plus_petit_parallelepipede_simple();
    efface_memoire_allouee_pour_enveloppe_convexe();
  }
  else{
    return taille_chull;
  }

  return (0);
} 


void Domaine::calcul_plus_petit_parallelepipede_simple()
{
  int i;
  double (* normales)[3],norme;
  normales=new double [_nb_facettes][3];
  calcul_epaisseur();
  for(i=0;i<_nb_facettes;i++)
    {
      normales[i][0]=_facettes[i][0];
      normales[i][1]=_facettes[i][1];
      normales[i][2]=_facettes[i][2];
    
      norme=sqrt(pow(normales[i][0],2.0)+pow(normales[i][1],2.0)+pow(normales[i][2],2.0));
    
      normales[i][0]*=(_epaisseurs[i]/norme);
      normales[i][1]*=(_epaisseurs[i]/norme);
      normales[i][2]*=(_epaisseurs[i]/norme);
    }
  
  int triplet_solution[3];
  
  calcul_meilleur_triplet_methode5(triplet_solution, normales);

  calcul_de_deux_cotes_paralleles_du_parallelepipede(triplet_solution);

  delete [] normales;
}


void Domaine::calcul_meilleur_triplet_methode5(int triplet_solution[3],double (*normales)[3])
{
  int i,j,k,l,taille,compteur,**candidats_des_facettes;
  double vecteur_temp[3],produit_temp1,produit_temp2,volume_temporaire,volume_min;
  double scalaire_temp;
  int face_2, face_3;
  int depart_1, depart_2, longueur_1, longueur_2;
  int nb_candidats_par_facette[_nb_facettes];


  candidats_des_facettes=new int *[_nb_facettes];
  for(i=0; i<_nb_facettes; i++){
    candidats_des_facettes[i] = NULL;
  }
  
  int temp_candidats[_nb_facettes];
  int nb_temp_candidats;

  // Computing the maximum number of vectors to check
  taille = 0;
  int taille_temp;
  for(i=0;i<_nb_facettes;i++){
    taille_temp=_nb_sommets_par_facette[i]*_nb_points_antipodaux[i];
    if (taille_temp>taille) taille = taille_temp;
  }
  double vecteurs[taille][3];
  double vecteurs_bis[taille][3];
  int taille_bis;


  for(i=0;i<_nb_facettes;i++)
    {
      nb_temp_candidats = 0;
      nb_candidats_par_facette[i]=0;

      taille=_nb_sommets_par_facette[i]*_nb_points_antipodaux[i];

      compteur=0;

      for(j=0;j<_nb_sommets_par_facette[i];j++)
	{
	  for(k=0;k<_nb_points_antipodaux[i];k++)
	    {
	      vecteurs[compteur][0]=
		_points[_sommets_des_facettes[i][j]][0]-
		_points[_points_antipodaux[i][k]][0];
	      vecteurs[compteur][1]=
		_points[_sommets_des_facettes[i][j]][1]-
		_points[_points_antipodaux[i][k]][1];
	      vecteurs[compteur][2]=
		_points[_sommets_des_facettes[i][j]][2]-
		_points[_points_antipodaux[i][k]][2];
	      compteur++;
	    }
	}

      for(j=i+1;j<_nb_facettes;j++)
	{
	  if (test_candidat(taille,vecteurs,normales[j])){
	    taille_bis=_nb_sommets_par_facette[j]*_nb_points_antipodaux[j];
	    compteur=0;
	    for(k=0;k<_nb_sommets_par_facette[j];k++)
	      {
		for(l=0;l<_nb_points_antipodaux[j];l++)
		  {
		    vecteurs_bis[compteur][0]=
		      _points[_sommets_des_facettes[j][k]][0]-
		      _points[_points_antipodaux[j][l]][0];
		    vecteurs_bis[compteur][1]=
		      _points[_sommets_des_facettes[j][k]][1]-
		      _points[_points_antipodaux[j][l]][1];
		    vecteurs_bis[compteur][2]=
		      _points[_sommets_des_facettes[j][k]][2]-
		      _points[_points_antipodaux[j][l]][2];
		    compteur++;
		  }
	      }
	    if (test_candidat(taille_bis,vecteurs_bis,normales[i])){
	      temp_candidats[nb_candidats_par_facette[i]] = j;
	      nb_candidats_par_facette[i]++;
	    }
	  }
	}
      candidats_des_facettes[i]=new int[nb_candidats_par_facette[i]];
      for(j=0; j<nb_candidats_par_facette[i]; j++)
	candidats_des_facettes[i][j]=temp_candidats[j];
    }


  volume_min=-1.0;

  for(i=0;i<_nb_facettes;i++)
    {
      produit_temp1=_epaisseurs[i]*_epaisseurs[i];
      for(j=0;j<nb_candidats_par_facette[i];j++)
	{
	  face_2 = candidats_des_facettes[i][j];
	  calcul_produit_vectoriel(normales[i], normales[face_2], vecteur_temp);
	  produit_temp2=_epaisseurs[face_2]*_epaisseurs[face_2]*produit_temp1;

	  depart_1   = j+1;
	  longueur_1 =  nb_candidats_par_facette[i];
	  depart_2   = 0;
	  longueur_2 = nb_candidats_par_facette[face_2];	  

	  while((depart_1<longueur_1)&&(depart_2<longueur_2)){
	    while ((depart_1<longueur_1)&&
		   (depart_2<longueur_2)&&
		   (candidats_des_facettes[i][depart_1]!=candidats_des_facettes[face_2][depart_2])
		   ){
	      if (candidats_des_facettes[i][depart_1]<candidats_des_facettes[face_2][depart_2])
		depart_1++;
	      else
		depart_2++;
	    }
	    if ((depart_1<longueur_1)&&(depart_2<longueur_2)){
	      face_3 = candidats_des_facettes[i][depart_1];
	      scalaire_temp=fabs(calcul_produit_scalaire(vecteur_temp,
							 normales[face_3]));
	      
	      if(scalaire_temp>ZERO_LIMIT)
		{
		  volume_temporaire=fabs(produit_temp2*
					 _epaisseurs[face_3]*
					 _epaisseurs[face_3]/
					 scalaire_temp);
		      
		  if((volume_min<0)||(volume_temporaire<volume_min))
		    {
		      volume_min=volume_temporaire;
		      triplet_solution[0]=i;
		      triplet_solution[1]=face_2;
		      triplet_solution[2]=face_3;

		    }
		}
	      depart_1++;
	      depart_2++;
	    }
	  }
	}
    }

  for(i=0;i<_nb_facettes;i++)
    {
	delete [] (candidats_des_facettes[i]);
    }
  delete []  candidats_des_facettes;
}

int Domaine::test_candidat(int nb_vecteurs,double (*vecteurs)[3],double normale_facette2[3])
{
  double signe_de_depart,produit_scalaire;
  int i,j;

  produit_scalaire=0;
  for(j=0;j<3;j++)
    {
      produit_scalaire+=vecteurs[0][j]*normale_facette2[j];
    }
  if(produit_scalaire<0)
    {
      signe_de_depart=-1;//NEGATIF;
    }
  else
    {
      signe_de_depart=1;//POSITIF;
    }
    
  for(i=1;i<nb_vecteurs;i++)
    {
      produit_scalaire=0;
      for(j=0;j<3;j++)
	{
	  produit_scalaire+=vecteurs[i][j]*normale_facette2[j];
	}
      if((produit_scalaire<0)&&(signe_de_depart==1))//POSITIF
	{
	  return 1;//OUI;
	}
      else if((produit_scalaire>0)&&(signe_de_depart==-1))//NEGATIF
	{
	  return 1;//OUI;
	}
    }
  return 0;//NON;
}



void Domaine::calcul_epaisseur()
{
  double distance_max,distance_courante;
  int i,j;

  if (_antipodaux_presents) {
    return;
  }

  for(i=0;i<_nb_facettes;i++)
    {
      distance_max=-1.0;
      
      for(j=0;j<_nb_sommets;j++)
	{
	  /*calcul de la distance de chaque point au plan*/
	  distance_courante=
	    fabs(_facettes[i][0]*_points[_sommets[j]][0]+_facettes[i][1]*_points[_sommets[j]][1]+
		 _facettes[i][2]*_points[_sommets[j]][2]-_facettes[i][3])/
	    sqrt(pow(_facettes[i][0],2.0)+pow(_facettes[i][1],2.0)+pow(_facettes[i][2],2.0));

	  if(distance_courante>distance_max+ZERO_LIMIT)
	    {
	      distance_max=distance_courante;
	      _nb_points_antipodaux[i]=0;
	      _points_antipodaux[i][_nb_points_antipodaux[i]]=_sommets[j];
	      (_nb_points_antipodaux[i])++;
	    }
	  else if(fabs(distance_courante-distance_max)<ZERO_LIMIT)
	    {
	      _points_antipodaux[i][_nb_points_antipodaux[i]]=_sommets[j];
	      (_nb_points_antipodaux[i])++;
	    }
	}
      _epaisseurs[i]=distance_max;
    }
}



int Domaine::calcul_enveloppe_convexe_aretes_et_antipodaux_largescale()
{
  int i,j,k,l;
  double distance_max, distance_courante;
  int sommet;

  
  /*on teste la coplanarite des points du domaine*/
  if(test_coplanarite(_nb_points,_points)==1)//OUI)
    {
      cerr << "\n\nDegenerate case : all points live in a plane." << endl;
      cerr << endl << "Aborting !!!!!!!!!" << endl << endl ;
      return -1;
    }
  
  // Building the input file for qconvex
  ofstream for__qconvex;
  for__qconvex.open("__qconvex_input_file");
  for__qconvex << 3 << endl; // dimension
  for__qconvex << _nb_points << endl; // number of points
  // writing the points
  for(i=0;i<_nb_points;i++)
    {
      for__qconvex << _points[i][0] << "\t";
      for__qconvex << _points[i][1] << "\t";
      for__qconvex << _points[i][2] << endl;
    }
  for__qconvex.close();


  // Computation of the convex hull
  system("qconvex n Fx i < __qconvex_input_file > __qconvex_output_file");


  // Reading the output file from qconvex
  ifstream from_qconvex;
  from_qconvex.open("__qconvex_output_file");
  
  int org_nb_facettes;

  // Read the number of faces and performs the necessary allocations
  int junk;
  from_qconvex >> junk;
  from_qconvex >> org_nb_facettes;
  double org_facettes[org_nb_facettes][4];

  int **org_sommets_des_facettes;
  int org_nb_sommets_par_facette[org_nb_facettes];
  org_sommets_des_facettes = new int * [org_nb_facettes];

  // Read the normal to the faces
  for(int i=0;i<org_nb_facettes;i++)
    {
      from_qconvex >> org_facettes[i][0];
      from_qconvex >> org_facettes[i][1];
      from_qconvex >> org_facettes[i][2];
      from_qconvex >> org_facettes[i][3];
      org_facettes[i][3]=-org_facettes[i][3];
    }

  // Read the number of vertices and performs the necessary
  // allocations
  from_qconvex >> _nb_sommets;

  // Read the id of the vertices
  int max_n_sommets = -1;
  _sommets=new int[_nb_sommets];
  for(int i=0;i<_nb_sommets;i++)
    {
      from_qconvex >> _sommets[i];
      if (max_n_sommets < _sommets[i]) max_n_sommets = _sommets[i];
    }


  // Systeme de conversion pour la compaction des numeros des sommets
  // de l'enveloppe convexe (entre 0 et nb_sommets -1 au lieu de entre
  // 0 et nb_points -1
  int conversion_sommets[max_n_sommets+1];
  int conv_inv_sommets[_nb_sommets];
  for(i=0; i<max_n_sommets+1; i++) conversion_sommets[i] = 0;
  for(i=0;i<_nb_sommets;i++)
    {
      conversion_sommets[_sommets[i]] = 1;
    }
  conversion_sommets[0] -= 1;
  for(i=1; i<max_n_sommets+1; i++)
    conversion_sommets[i] += conversion_sommets[i-1];
  k = 0;
  if (conversion_sommets[0]>=0){
    conv_inv_sommets[0]=0;
    k++;
  }
  for(i=1; i<max_n_sommets+1; i++)
    if (conversion_sommets[i] > conversion_sommets[i-1]){
      conv_inv_sommets[k] = i;
      k++;
    }

  from_qconvex >> junk;
  if (junk!=org_nb_facettes){
    cerr << "Bug: the output is inconsistent with the program assumptions.";
    cerr << endl << "Aborting !!!!!!!!!!!!!" << endl;
    exit(-1);
  }

  // Lecture des sommets adjacents a chaque facette
  for(int i=0;i<org_nb_facettes;i++)
    {
      org_nb_sommets_par_facette[i] = 3;
	org_sommets_des_facettes[i]=new int[org_nb_sommets_par_facette[i]];

	for(int j=0;j<org_nb_sommets_par_facette[i];j++)
	  {
	    from_qconvex >> org_sommets_des_facettes[i][j];
	  }
    }
  from_qconvex.close();


  // Calcul des aretes
  int sommet_1, sommet_2, sommet_3, sommet_4;
  int *  n_facettes_incidentes;
  int *  n_facettes_incidentes_max;
  int **   facettes_incidentes;
  int (* aretes)[4];
  int n_aretes_max;
  int n_aretes = 0;

  int n_facettes_init = 5;
  int n_aretes_init = 5*_nb_sommets;

  n_facettes_incidentes     = new int [_nb_sommets];
  n_facettes_incidentes_max = new int [_nb_sommets];
  facettes_incidentes   = new int * [_nb_sommets];
  for(i=0; i<_nb_sommets; i++){
    n_facettes_incidentes[i]     = 0;
    n_facettes_incidentes_max[i] = n_facettes_init;
    facettes_incidentes[i] = new int [n_facettes_incidentes_max[i]];
  }
  aretes = new int [n_aretes_init][4];
  n_aretes_max = n_aretes_init;

  // Compute the faces a vertex belongs to
  for(i=0;i<org_nb_facettes;i++){
    for(j=0;j<org_nb_sommets_par_facette[i];j++){
      sommet_1 = conversion_sommets[org_sommets_des_facettes[i][j]];
      if(n_facettes_incidentes[sommet_1]==n_facettes_incidentes_max[sommet_1]){
	int * temp = new int[n_facettes_incidentes_max[sommet_1]+n_facettes_init];
	for(k=0; k<n_facettes_incidentes_max[sommet_1]; k++)
	  temp[k] = facettes_incidentes[sommet_1][k];
	delete [] facettes_incidentes[sommet_1];
	facettes_incidentes[sommet_1] = temp;
	n_facettes_incidentes_max[sommet_1] += n_facettes_init;
      }
      facettes_incidentes[sommet_1][n_facettes_incidentes[sommet_1]] = i;
      n_facettes_incidentes[sommet_1]++;
    }
  }

  int facettes_communes, ind_1, ind_2, max_1, max_2, face_1, face_2, id_facette;
  int dejavisite[_nb_sommets];
  
  // Compute the edges of the convex hull
  for(i=0; i<_nb_sommets; i++){
    for(j=0; j<_nb_sommets; j++) dejavisite[j] = 0;
    dejavisite[i] = 1;

    for(j=0; j<n_facettes_incidentes[i]; j++){
      id_facette = facettes_incidentes[i][j];
      for(k=0; k<org_nb_sommets_par_facette[id_facette]; k++){
	sommet_2 = conversion_sommets[org_sommets_des_facettes[id_facette][k]];
	if ((sommet_2>i)&&(!dejavisite[sommet_2])){

	  facettes_communes = 0;
	  ind_1 = 0;
	  max_1 = n_facettes_incidentes[i];
	  ind_2 = 0;
	  max_2 = n_facettes_incidentes[sommet_2];
	  face_1 = -1;
	  face_2 = -1;

	  while((ind_1<max_1)&&(ind_2<max_2)){
	    if (facettes_incidentes[i][ind_1]!=facettes_incidentes[sommet_2][ind_2]){
	      if (facettes_incidentes[i][ind_1]<facettes_incidentes[sommet_2][ind_2])
		ind_1++;
	      else
		ind_2++;
	    }
	    else{
	      if (facettes_communes==0)
		face_1 = facettes_incidentes[i][ind_1];
	      else
		face_2 = facettes_incidentes[i][ind_1];
	      facettes_communes++;
	      ind_1++;
	      ind_2++;
	    }
	  }
	  
	  if(facettes_communes>1){
	    if(facettes_communes>2){
	      cerr << "An edge is common to more than two faces !!!"<< endl;
	    }
	    if (n_aretes==n_aretes_max){
	      int (* temp)[4] = new int [n_aretes_max+n_aretes_init][4];
	      for(l=0; l<n_aretes_max; l++){
		temp[l][0] = aretes[l][0];
		temp[l][1] = aretes[l][1];
		temp[l][2] = aretes[l][2];
		temp[l][3] = aretes[l][3];
	      }
	      delete [] aretes;
	      aretes = temp;
	      n_aretes_max+=n_aretes_init;
	    }
	    aretes[n_aretes][0] = i;
	    aretes[n_aretes][1] = sommet_2;
	    aretes[n_aretes][2] = face_1;
	    aretes[n_aretes][3] = face_2;
	    n_aretes++;
	  }
	  dejavisite[sommet_2] = 1;
	}
      }
    }
  }



  double dx1, dy1, dz1, dx2, dy2, dz2;
  int _n_facettes_edges = 0;
  int max_n_facettes_edges = 5*org_nb_facettes + 50;

  double (* facettes_additionelles)[4]; 
  int (*sommets_facettes_additionelles)[4];

  int _n_edges = n_aretes;
  int (* _edges)[4];
  double (*the_edges)[3];

  facettes_additionelles = new double [max_n_facettes_edges][4];
  sommets_facettes_additionelles = new int [max_n_facettes_edges][4];
  _edges    = new int [_n_edges][4];
  the_edges = new double [_n_edges][3];

  for(i=0; i<_n_edges; i++){
    _edges[i][0] = conv_inv_sommets[aretes[i][0]];
    _edges[i][1] = conv_inv_sommets[aretes[i][1]];
    _edges[i][2] = aretes[i][2];
    _edges[i][3] = aretes[i][3];
    the_edges[i][0] = _points[_edges[i][1]][0] - _points[_edges[i][0]][0];
    the_edges[i][1] = _points[_edges[i][1]][1] - _points[_edges[i][0]][1];
    the_edges[i][2] = _points[_edges[i][1]][2] - _points[_edges[i][0]][2];
  }


  // Computation of the faces supported by non-colinear edges
  for(i=0; i<_n_edges; i++){
    // First edge
    dx1 = the_edges[i][0];
    dy1 = the_edges[i][1];
    dz1 = the_edges[i][2];
    sommet_1=_edges[i][0];
    sommet_2=_edges[i][1];
    for(j=0; j<i; j++) // We consider any pair of edges
      {
	sommet_3=_edges[j][0];
	sommet_4=_edges[j][1];

	if ((sommet_1!=sommet_3)&&
	    (sommet_1!=sommet_4)&&
	    (sommet_2!=sommet_3)&&
	    (sommet_2!=sommet_4)){
	  // Second edge
	  dx2 = the_edges[j][0];
	  dy2 = the_edges[j][1];
	  dz2 = the_edges[j][2];
	  
	  if ((((dx1*org_facettes[_edges[j][2]][0] + 
		 dy1*org_facettes[_edges[j][2]][1] + 
		 dz1*org_facettes[_edges[j][2]][2])*
		(dx1*org_facettes[_edges[j][3]][0] + 
		 dy1*org_facettes[_edges[j][3]][1] + 
		 dz1*org_facettes[_edges[j][3]][2])) <0)&&
	      (((dx2*org_facettes[_edges[i][2]][0] + 
		 dy2*org_facettes[_edges[i][2]][1] + 
		 dz2*org_facettes[_edges[i][2]][2])*
		(dx2*org_facettes[_edges[i][3]][0] + 
		 dy2*org_facettes[_edges[i][3]][1] + 
		 dz2*org_facettes[_edges[i][3]][2])) <0)){
	    facettes_additionelles[_n_facettes_edges][0] = dy1*dz2-dz1*dy2;
	    facettes_additionelles[_n_facettes_edges][1] = dz1*dx2-dx1*dz2;
	    facettes_additionelles[_n_facettes_edges][2] = dx1*dy2-dy1*dx2;
	    
	    if ((facettes_additionelles[_n_facettes_edges][0]!=0)||
		(facettes_additionelles[_n_facettes_edges][1]!=0)||
		(facettes_additionelles[_n_facettes_edges][2]!=0)){
	      facettes_additionelles[_n_facettes_edges][3] = 
		facettes_additionelles[_n_facettes_edges][0]*_points[_edges[j][1]][0]+
		facettes_additionelles[_n_facettes_edges][1]*_points[_edges[j][1]][1]+
		facettes_additionelles[_n_facettes_edges][2]*_points[_edges[j][1]][2];
	      sommets_facettes_additionelles[_n_facettes_edges][0] = _edges[j][0];
	      sommets_facettes_additionelles[_n_facettes_edges][1] = _edges[j][1];
	      sommets_facettes_additionelles[_n_facettes_edges][2] = _edges[i][0];
	      sommets_facettes_additionelles[_n_facettes_edges][3] = _edges[i][1];
	      _n_facettes_edges++;
	      if (_n_facettes_edges==max_n_facettes_edges){
		cerr << "Nombre maximal possible de facettes dues aux aretes atteint.";
		cerr << endl << "Aborting!!!!!!!!!!!!!!!!!!!"<< endl;
		abort();
	      }
	    }
	  }
	}
      }
  }


  // Constitution de l'ensemble des facettes
  _nb_facettes = org_nb_facettes + _n_facettes_edges; 
  _facettes = new double[_nb_facettes][4];

  for(i=0; i<org_nb_facettes; i++){
    _facettes[i][0] = org_facettes[i][0];
    _facettes[i][1] = org_facettes[i][1];
    _facettes[i][2] = org_facettes[i][2];
    _facettes[i][3] = org_facettes[i][3];
  }
  for(i=0; i<_n_facettes_edges; i++){
    _facettes[i+org_nb_facettes][0] = facettes_additionelles[i][0];
    _facettes[i+org_nb_facettes][1] = facettes_additionelles[i][1];
    _facettes[i+org_nb_facettes][2] = facettes_additionelles[i][2];
    _facettes[i+org_nb_facettes][3] = facettes_additionelles[i][3];
  }
  
  
  // Nombre de sommets par facette
  _nb_sommets_par_facette=new int[_nb_facettes];
  _sommets_des_facettes= new int *[_nb_facettes];

  for(int i=0;i<org_nb_facettes;i++){
    _nb_sommets_par_facette[i] = org_nb_sommets_par_facette[i]; 
    _sommets_des_facettes[i] = org_sommets_des_facettes[i];
  }    

  for(i=0; i<_n_facettes_edges; i++){
    _nb_sommets_par_facette[i+org_nb_facettes] = 2;
    _sommets_des_facettes[i+org_nb_facettes] = new int[_nb_sommets_par_facette[i]];
    _sommets_des_facettes[i+org_nb_facettes][0] = sommets_facettes_additionelles[i][0];
    _sommets_des_facettes[i+org_nb_facettes][1] = sommets_facettes_additionelles[i][1];
  }


  // Initialisation des points antipodaux
  _points_antipodaux=new int *[_nb_facettes];
  for(int i=0;i<org_nb_facettes;i++)
    {
      _points_antipodaux[i]=new int[100];
    }
  for(int i=0;i<_n_facettes_edges;i++)
    {
      _points_antipodaux[i+org_nb_facettes]=new int[2];
    }
  _nb_points_antipodaux=new int[_nb_facettes];
  _epaisseurs=new double[_nb_facettes];
  _antipodaux_presents = 0;


  // Calcul des points antipodaux
  // ... pour les faces de l'enveloppe convexe
  for(i=0;i<org_nb_facettes;i++)
    {
      distance_max=-1.0;
      
      for(j=0;j<_nb_sommets;j++)
	{
	  /*calcul de la distance de chaque point au plan*/
	  distance_courante=
	    fabs(_facettes[i][0]*_points[_sommets[j]][0]+_facettes[i][1]*_points[_sommets[j]][1]+
		 _facettes[i][2]*_points[_sommets[j]][2]-_facettes[i][3])/
	    sqrt(pow(_facettes[i][0],2.0)+pow(_facettes[i][1],2.0)+pow(_facettes[i][2],2.0));

	  if(distance_courante>distance_max+ZERO_LIMIT)
	    {
	      distance_max=distance_courante;
	      _nb_points_antipodaux[i]=0;
	      _points_antipodaux[i][_nb_points_antipodaux[i]]=_sommets[j];
	      (_nb_points_antipodaux[i])++;
	    }
	  else if(fabs(distance_courante-distance_max)<ZERO_LIMIT)
	    {
	      _points_antipodaux[i][_nb_points_antipodaux[i]]=_sommets[j];
	      (_nb_points_antipodaux[i])++;
	    }
	}
      _epaisseurs[i]=distance_max;
    }
  // ... pour les faces dues a des paires d'aretes
  for(i=0; i<_n_facettes_edges; i++){
    _nb_points_antipodaux[i+org_nb_facettes]=2;
    _points_antipodaux[i+org_nb_facettes][0] = sommets_facettes_additionelles[i][2];
    _points_antipodaux[i+org_nb_facettes][1] = sommets_facettes_additionelles[i][3];

    sommet = sommets_facettes_additionelles[i][2];
    _epaisseurs[i+org_nb_facettes] = 
      fabs(_facettes[i+org_nb_facettes][0]*_points[sommet][0]+
	   _facettes[i+org_nb_facettes][1]*_points[sommet][1]+
	   _facettes[i+org_nb_facettes][2]*_points[sommet][2]-_facettes[i][3])
      /
      sqrt(pow(_facettes[i+org_nb_facettes][0],2.0)+
	   pow(_facettes[i+org_nb_facettes][1],2.0)+
	   pow(_facettes[i+org_nb_facettes][2],2.0));
  }
  _antipodaux_presents = 1;




  for(i=0; i<_nb_sommets; i++){
    delete [] facettes_incidentes[i];
  }
  delete [] facettes_incidentes;
  delete [] n_facettes_incidentes_max;
  delete [] n_facettes_incidentes;
  delete [] aretes;
  delete [] org_sommets_des_facettes;
  delete [] facettes_additionelles;
  delete [] sommets_facettes_additionelles;
  delete [] _edges;
  delete [] the_edges;

  return _nb_sommets;
}



void Domaine::efface_memoire_allouee_pour_enveloppe_convexe()
{
  int i;

  delete []  _nb_points_antipodaux;
  delete []  _epaisseurs;
  delete []  _nb_sommets_par_facette;
  for(i=0;i<_nb_facettes;i++)
    {
      delete []  _sommets_des_facettes[i];
      delete []  _points_antipodaux[i];
    }
  delete []  _sommets_des_facettes;
  delete []  _points_antipodaux;
  delete []  _sommets;
  delete []  _facettes;
}


void Domaine::calcul_de_deux_cotes_paralleles_du_parallelepipede(int triplet_solution[3])
{
  int i;
  double P1[4],P2[4],P3[4],P1prime[4],P2prime[4],P3prime[4];
  double **systeme;

  for(i=0;i<4;i++)
    {
      P1[i]=_facettes[triplet_solution[0]][i];
    }

  for(i=0;i<3;i++)
    {
      P1prime[i]=P1[i];
    }

  P1prime[3]=P1prime[0]*_points[_points_antipodaux[triplet_solution[0]][0]][0]+
    P1prime[1]*_points[_points_antipodaux[triplet_solution[0]][0]][1]+
    P1prime[2]*_points[_points_antipodaux[triplet_solution[0]][0]][2];
  for(i=0;i<4;i++)
    {
      P2[i]=_facettes[triplet_solution[1]][i];
    }
  for(i=0;i<3;i++)
    {
      P2prime[i]=P2[i];
    }

  P2prime[3]=P2prime[0]*_points[_points_antipodaux[triplet_solution[1]][0]][0]+
    P2prime[1]*_points[_points_antipodaux[triplet_solution[1]][0]][1]+
    P2prime[2]*_points[_points_antipodaux[triplet_solution[1]][0]][2];
  for(i=0;i<4;i++)
    {
      P3[i]=_facettes[triplet_solution[2]][i];
    }
  for(i=0;i<3;i++)
    {
      P3prime[i]=P3[i];
    }

  P3prime[3]=P3prime[0]*_points[_points_antipodaux[triplet_solution[2]][0]][0]+
    P3prime[1]*_points[_points_antipodaux[triplet_solution[2]][0]][1]+
    P3prime[2]*_points[_points_antipodaux[triplet_solution[2]][0]][2];

  systeme = new double * [3];
  for(i=0;i<3;i++)
     {
       systeme[i]=new double[4];
     }
  
  /*on fixe un plan qui donne le sens des deux cotes paralleles : P1*/
   
  for(i=0;i<4;i++)
    {
      systeme[0][i]=P1[i];
      systeme[1][i]=P2[i];
      systeme[2][i]=P3[i];
    }
  resolution_systeme_lineaire(3,systeme,_parallelepipede[0]);

  for(i=0;i<4;i++)
    {
      systeme[0][i]=P1[i];
      systeme[1][i]=P2[i];
      systeme[2][i]=P3prime[i];
    }
  resolution_systeme_lineaire(3,systeme,_parallelepipede[1]);

  for(i=0;i<4;i++)
    {
      systeme[0][i]=P1[i];
      systeme[1][i]=P2prime[i];
      systeme[2][i]=P3prime[i];
    }
  resolution_systeme_lineaire(3,systeme,_parallelepipede[2]);

   for(i=0;i<4;i++)
    {
      systeme[0][i]=P1[i];
      systeme[1][i]=P2prime[i];
      systeme[2][i]=P3[i];
    }
  resolution_systeme_lineaire(3,systeme,_parallelepipede[3]);

  /*on fixe le plan parallele a P1 : P1prime*/
  for(i=0;i<4;i++)
    {
      systeme[0][i]=P1prime[i];
      systeme[1][i]=P2[i];
      systeme[2][i]=P3[i];
    }
  resolution_systeme_lineaire(3,systeme,_parallelepipede[4]);

  for(i=0;i<4;i++)
    {
      systeme[0][i]=P1prime[i];
      systeme[1][i]=P2[i];
      systeme[2][i]=P3prime[i];
    }
  resolution_systeme_lineaire(3,systeme,_parallelepipede[5]);

  for(i=0;i<4;i++)
    {
      systeme[0][i]=P1prime[i];
      systeme[1][i]=P2prime[i];
      systeme[2][i]=P3prime[i];
    }
  resolution_systeme_lineaire(3,systeme,_parallelepipede[6]);

  for(i=0;i<4;i++)
    {
      systeme[0][i]=P1prime[i];
      systeme[1][i]=P2prime[i];
      systeme[2][i]=P3[i];
    }
  resolution_systeme_lineaire(3,systeme,_parallelepipede[7]);

   for(i=0;i<3;i++)
     {
       delete []  systeme[i];
     }
   delete []  systeme;

}

