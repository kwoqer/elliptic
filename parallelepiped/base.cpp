/* parallelepiped
 * Copyright (C) 2003 Frederic Vivien and Nicolas Wicker
 *
 * base.cpp
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

/*************************************************************/
/*                                                           */
/*Procedure de test de la coplanarite d'en ensemble de points*/
/*                                                           */
/*************************************************************/

int test_coplanarite(int nb_points,double (*points)[3])
{
  /*declaration de variables*/
  int i,j,test;
  double point[3],vecteur1[3],vecteur2[3],vecteur3[3],norme,d;
  /*fin declaration de variables*/


  for(i=0;i<3;i++)
    {
      point[i]=points[0][i];
      vecteur1[i]=points[1][i]-point[i];
    }

  for(i=2;i<nb_points;i++)
    {
      for(j=0;j<3;j++)
	{
	  vecteur2[j]=points[i][j]-point[j];
	}
      calcul_produit_vectoriel(vecteur1,vecteur2,vecteur3);
    
      norme=0;
      for(j=0;j<3;j++)
	{
	  norme+=pow(vecteur3[j],2.0);
	}

      if(norme>ZERO_LIMIT)
	{
	  break;
	}
    }

  d=calcul_produit_scalaire(vecteur3,point);

  test=1;//OUI;
  for(i=1;i<nb_points;i++)
    {
      if(fabs(calcul_produit_scalaire(vecteur3,points[i])-d)>ZERO_LIMIT)
	{
	  test=0;//NON;
	  break;
	}
    }
  return test;
}

double calcul_produit_scalaire(double v1[3],double v2[3])
{
  double res;

  res=v1[0]*v2[0]+v1[1]*v2[1]+v1[2]*v2[2];

  return res;
}

void calcul_produit_vectoriel(double v1[3],double v2[3],double v3[3])
{
  v3[0]=v1[1]*v2[2]-v1[2]*v2[1];
  v3[1]=-v1[0]*v2[2]+v1[2]*v2[0];
  v3[2]=v1[0]*v2[1]-v1[1]*v2[0];
}

// void echange_lignes(int n,double **systeme,int ligne1,int ligne2)
// {
//   double temp;

//   for(int i=0;i<n+1;i++)
//     {
//       temp=systeme[ligne1][i];
//       systeme[ligne1][i]=systeme[ligne2][i];
//       systeme[ligne2][i]=temp;
//     }
// }

void resolution_systeme_lineaire(int n,double **systeme,double *solution)
{
  int i,j,k,pivot;
  double facteur;
  double temp;

  double systeme_temp[n][n+1];

  for(i=0;i<n;i++)
    {
      for(j=0;j<n+1;j++)
	{
	  systeme_temp[i][j]=systeme[i][j];
	}
    }

  for(i=0;i<n;i++)
    {
      /*on cherche un pivot non nul*/
      for(j=i;j<n;j++)
	{
	  if(fabs(systeme_temp[j][i])>ZERO_LIMIT)
	    {
	      pivot=j;
	      break;
	    }
	}

      for(int iii=0;iii<n+1;iii++)
	{
	  temp=systeme_temp[i][iii];
	  systeme_temp[i][iii]=systeme_temp[pivot][iii];
	  systeme_temp[pivot][iii]=temp;
	}


      facteur=systeme_temp[i][i];
      for(k=i;k<n+1;k++)
	{
	  systeme_temp[i][k]/=facteur;
	}
      for(j=0;j<n;j++)
	{
	  if(j!=i)
	    {
	      facteur=systeme_temp[j][i];
	      for(k=i;k<n+1;k++)
		{
		  systeme_temp[j][k]-=facteur*systeme_temp[i][k];
		}
	    }
	}
    }

  for(i=0;i<n;i++)
    {
       solution[i]=systeme_temp[i][n];
    }

}










