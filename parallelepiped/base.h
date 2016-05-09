/* parallelepiped
 * Copyright (C) 2003 Frederic Vivien and Nicolas Wicker
 *
 * base.h
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

#include <math.h>

#define ZERO_LIMIT 0.0000001


int test_coplanarite(int nb_points,double (*points)[3]);
double calcul_produit_scalaire(double v1[3],double v2[3]);
void calcul_produit_vectoriel(double v1[3],double v2[3],double v3[3]);
void resolution_systeme_lineaire(int n,double **systeme,double *solution);


