package swarm;

public class settings {
	int no_of_cluster ;    // how many cluster to be formed
	int no_of_dimension ;  // dimension of one particle 
	float w ; //inertia
	float c1 , c2 ; 
	public settings(int no_of_cluster , int no_of_dimension, float w, float c1 , float c2)
	{
		this.no_of_cluster = no_of_cluster ; 
		this.no_of_dimension = no_of_dimension ; 
		this.w = w ; 
		this.c1 = c1 ; 
		this.c2 = c2 ;
	}
}
