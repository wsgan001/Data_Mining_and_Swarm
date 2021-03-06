package graph_clustering;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;

public class graph_make_communities {
	Vector<Float> sorted_eigen_value ;
	Vector<Vector<Float>> sorted_eigen_vector ; 
	
	//this may update by this algorithm so PSO needs 2 things here
	Vector<Float> pso_results_at_every_iteration ; 
	
	//Instead of quantization error in data clustering here it requires Q value
	// ********** Q value is optimized to max instead where 
	// ********** PSO for data clustering quantization error is optimized to minimum value
	//float Q_value_for_this_iteration ; 
	
	int n ; //number of communities center
	//pso based center for which flag greater than equal to 0.5
	// we will change this estimation we respect to eigen vector whose eigen value is more
	Vector<Float> center_estimated_by_pso ; 
	
	//actual centroids for this pso iterations
	//Vector<Vector<Float>> actual_center_selected_from_eigen_vector ; 
	
	//set of points(not repeated) which are selected for centroid
	// both are same just initially used set then converted to vector at step 4 ending
	TreeSet<Integer> node_selected_as_centroid ; 
	Vector<Integer> node_selected_as_centroid_vector ; 
	
	//Community based dictionary
	HashMap<Integer, Vector<Integer>> centroid_no_to_element_vec  ; 
	
	//number of first eigen value and vector considered (no of community -1)
	int no_of_eigen_value_to_be_considered;
	
	//Random Number Generator
	Random random_number_giver_between_0_and_1 ; 
	
	public graph_make_communities(Vector<Float> sorted_eigen_value , Vector<Vector<Float>> sorted_eigen_vector) {
		// TODO Auto-generated constructor stub
		this.sorted_eigen_value = sorted_eigen_value ; 
		this.sorted_eigen_vector = sorted_eigen_vector ; 
		this.random_number_giver_between_0_and_1 = new Random();
	}
	
	public void graph_cluster_at_every_pso_iteration(Vector<Float> pso_results_at_every_iteration)
	{
		this.pso_results_at_every_iteration = pso_results_at_every_iteration ;
		//every step is one function
		pso_results_rounding_off() ;
		centroids_size_greater_than_two_checking();
		assign_first_eigen_vector_values_to_estimated_near_centroids(); 
		combine_common_center_and_set_centroids_for_this_pso_iteration_result();
		community_find_out_of_given_centroid() ; 
	}
	/*
	public float get_Q_value_for_this_iteration()
	{
		return Q_value_for_this_iteration ; 
	}
	*/
	// this is because step 2 in research paper of "PSO based graph clustering"
	// flag value are changed flag_p = 1- 0.5 * rand(0,1) flag_q = 1- 0.5 * rand(0,1)
	// this is may or may not happen so safe side take value from here
	public Vector<Float> get_particle_new_position()
	{
		return pso_results_at_every_iteration ; 
	}
	
	public HashMap<Integer, Vector<Integer>>  get_centroid_no_to_element_vec()
	{
		return centroid_no_to_element_vec ; 
	}
	
	//this are centroids are set for graph for this pso iteration
	//now compute spectral method on this after that Q value(how fine clustering is) 
	//and return to pso so pso set particle pbest and gbest according to Q value
	/*public Vector<Vector<Float>> get_actual_center_selected_from_eigen_vector()
	{
		return actual_center_selected_from_eigen_vector ; 
	}*/
	
	//step 1 
	public void pso_results_rounding_off()
	{
		// always even // half flag half centroids
		int size_of_particle_dimension = pso_results_at_every_iteration.size() ; // 1 ... n
		int flags_are_upto= (size_of_particle_dimension/2)-1 ; // 0 is starting index 0 ... n-1
		int actual_center_start_from = flags_are_upto+1 ; // 0 is starting index n/2 ... n-1
		n = 0 ; // setting to zero
		center_estimated_by_pso = new Vector<Float>() ; //  center vector intialization
		// rounding off and center consideration
		for (int counter_flag_and_cen = 0 ; counter_flag_and_cen < (flags_are_upto+1) ; counter_flag_and_cen++)
		{
			// round
			if (Math.round(pso_results_at_every_iteration.get(counter_flag_and_cen))==1)
			{
				n = n +1 ; 
				//center added
				center_estimated_by_pso.
					add(pso_results_at_every_iteration.get(counter_flag_and_cen + actual_center_start_from));
			}
		}
	}
	
	// check if n > 2
	//step 2 of research paper
	public void centroids_size_greater_than_two_checking()
	{
		if (n<2)
		{
			//two max values and respective positions
			float max_value_first = 0 ;
			float max_value_second = 0 ; 
			int position_in_pso_results_of_max_value_first = 0 ; 
			int position_in_pso_results_of_max_value_second = 0 ; 

			
			int size_of_particle_dimension = pso_results_at_every_iteration.size() ; // 1 ... n
			int flags_are_upto= (size_of_particle_dimension/2)-1 ; // 0 is starting index 0 ... n-1
			int actual_center_start_from = flags_are_upto+1 ; // 0 is starting index n/2 ... n-1
			n = 2 ; // setting to two
			center_estimated_by_pso = new Vector<Float>() ; //  center vector intialization
			// rounding off and center consideration
			for (int counter_flag_and_cen = 0 ; counter_flag_and_cen < (flags_are_upto+1) ; counter_flag_and_cen++)
			{
				//first two max value calculated
				if (pso_results_at_every_iteration.get(counter_flag_and_cen) > max_value_first)
				{
					//assign this value to lower
					position_in_pso_results_of_max_value_second = position_in_pso_results_of_max_value_first ;
					max_value_second = max_value_first ; 
					
					//assign new value here
					position_in_pso_results_of_max_value_first = counter_flag_and_cen ; 
					max_value_first = pso_results_at_every_iteration.get(counter_flag_and_cen);
				}
				
				else if (pso_results_at_every_iteration.get(counter_flag_and_cen) > max_value_second)
				{
					//assign value to second only
					position_in_pso_results_of_max_value_second = counter_flag_and_cen ; 
					max_value_second = pso_results_at_every_iteration.get(counter_flag_and_cen) ; 
				}
			}
			
			center_estimated_by_pso.add(pso_results_at_every_iteration.
					get((position_in_pso_results_of_max_value_first+actual_center_start_from)));
			center_estimated_by_pso.add(pso_results_at_every_iteration.
					get((position_in_pso_results_of_max_value_second+actual_center_start_from)));
			//upwards n set to two and center are added from second half of particle
			
			
			//now set flags value to (1-0.5*rand(0,1))
			pso_results_at_every_iteration.set(position_in_pso_results_of_max_value_first, 
				(1.0f- (0.5f *	random_number_giver_between_0_and_1.nextFloat())));
			
			pso_results_at_every_iteration.set(position_in_pso_results_of_max_value_second, 
					(1.0f- (0.5f *	random_number_giver_between_0_and_1.nextFloat())));
			
		}
						
	}
	
	//step 3
	public void assign_first_eigen_vector_values_to_estimated_near_centroids()
	{
		
		node_selected_as_centroid = new TreeSet<Integer>();
		
		// actual near points finding from 1st eigenvector
		for (int count_center_by_pso = 0 ; count_center_by_pso  < n ; count_center_by_pso++)
		{
			float min_dis_uptil_now = Float.MAX_VALUE ; //intially as high as possible
			int node_number_respective_of_closet_distance = 0 ; 
			
			for (int counter_nodes = 0 ; counter_nodes < sorted_eigen_vector.get(0).size() ; counter_nodes++)
			{
				float diff = center_estimated_by_pso.get(count_center_by_pso) - sorted_eigen_vector.get(0).get(counter_nodes);
				// diff can be -ve but this represent distance so make it +ve
				if (diff < 0 )
				{
					diff = diff * (-1.0f) ; 
				}
				if (diff < min_dis_uptil_now)
				{
					min_dis_uptil_now = diff ; 
					node_number_respective_of_closet_distance = counter_nodes ; 
				}
			}
			
			node_selected_as_centroid.add(node_number_respective_of_closet_distance);
		}
	}
	
	//step4
	public void combine_common_center_and_set_centroids_for_this_pso_iteration_result()
	{
		// actual centroid extracted from first (n-1) eigenvector and store .
		//actual_center_selected_from_eigen_vector = new Vector<Vector<Float>>() ;
		
		// centers are not repeated bz set data structure store only non repeated value
		// so repeated center are eliminated.
		
		//if after combining center size is 1 make it 2 
		//select furtherest point (long distance point)
		if (node_selected_as_centroid.size()==1)
		{
			float max_distance_between_curr = 0.0f ; 
			int node_which_is_far_from_curr = 0  ;
			
			float curr_node_eigen_vector_number_one_value = sorted_eigen_vector.get(0).get(node_selected_as_centroid.first()) ; 
			for (int node_counter = 0 ; node_counter < sorted_eigen_vector.get(0).size() ; node_counter++)
			{
				float diff = curr_node_eigen_vector_number_one_value - sorted_eigen_vector.get(0).get(node_counter);
				
				// diff can be -ve but this represent distance so make it +ve
				if (diff < 0 )
				{
					diff = diff * (-1.0f) ; 
				}
				
				if (max_distance_between_curr < diff)
				{
					max_distance_between_curr = diff ; 
					node_which_is_far_from_curr = node_counter ; 
				}
				
			}
			
			// add farthest node to set of centroids 
			node_selected_as_centroid.add(node_which_is_far_from_curr);
		}
		
		//convert treeset to vector 
		node_selected_as_centroid_vector = new Vector<Integer>() ; 
	    Iterator<Integer> iterator;
	    iterator = node_selected_as_centroid.iterator();
	    while (iterator.hasNext()){
	    	node_selected_as_centroid_vector.add(iterator.next()) ; 
	    }
	    
	    
		/*
		System.out.println(node_selected_as_centroid);
		//n-1 count
		int no_of_eigen_value_to_be_considered = node_selected_as_centroid.size() - 1; 
		
		//take (n-1) first eigenvector values for current n centroids
		for (int node_number : node_selected_as_centroid)
		{
			Vector<Float> eigen_vector_from_current_centroid_node = new Vector<Float>(); 
			for (int count_eigen_vector = 0 ; count_eigen_vector < no_of_eigen_value_to_be_considered ; count_eigen_vector++)
			{
				eigen_vector_from_current_centroid_node.
					add(sorted_eigen_vector.get(count_eigen_vector).get(node_number));
			}
			actual_center_selected_from_eigen_vector.add(eigen_vector_from_current_centroid_node);
		}
		*/
	}
	
	//step5 
	//return nodes which are closer to each other in graph matrix 
	// map of centroid number and respective vector sends
	public void community_find_out_of_given_centroid()
	{
		no_of_eigen_value_to_be_considered = node_selected_as_centroid_vector.size() -1 ; 
		
		centroid_no_to_element_vec = new HashMap<Integer, Vector<Integer>>();
		
		// setting dictionary with empty element 
		for (int centroid_counter = 0 ; centroid_counter < node_selected_as_centroid_vector.size() ; centroid_counter++ )
		{
			centroid_no_to_element_vec.put(node_selected_as_centroid_vector.get(centroid_counter), new Vector<Integer>()) ; 
		}
		
		// for each element 
		for (int node_counter = 0 ; node_counter < sorted_eigen_vector.get(0).size() ; node_counter++ )
		{
			// Initially set to max
			float min_of_dsij = Float.MAX_VALUE ; 
			int centroid_no_corresponding_to_min_dsij = 0 ; 
					
			// finding nearest centroid
			for (int centroid_counter = 0 ; centroid_counter < node_selected_as_centroid_vector.size() ; centroid_counter++ )
			{
				float curr_dsij_value = dis_similar_measure(node_selected_as_centroid_vector.get(centroid_counter),node_counter);
				if (curr_dsij_value < min_of_dsij)
				{
					centroid_no_corresponding_to_min_dsij = node_selected_as_centroid_vector.get(centroid_counter);
					min_of_dsij = curr_dsij_value ; 
				}
			}
			centroid_no_to_element_vec.get(centroid_no_corresponding_to_min_dsij).add(node_counter);
		}
		
	}
	
	//Given node number //required in step5
	//Zhewen Shi "PSO based community detection" equation one applied 
	public float dis_similar_measure(int centroid_no  , int curr_node_no)
	{
		float Dsij = 0 ; 
			
		float sum_of_diff_vki_vkj_square_mul_ak = 0 ;
		for (int counter_no_eigen_value = 0 ; counter_no_eigen_value< no_of_eigen_value_to_be_considered ; counter_no_eigen_value++)
		{
			sum_of_diff_vki_vkj_square_mul_ak = sum_of_diff_vki_vkj_square_mul_ak  + (
					(float)Math.pow(
						(sorted_eigen_vector.get(counter_no_eigen_value).get(curr_node_no) - sorted_eigen_vector.get(counter_no_eigen_value).get(centroid_no)), 2) 
						* sorted_eigen_value.get(counter_no_eigen_value)
			) ;
		}
			
		Dsij = (float)Math.sqrt(sum_of_diff_vki_vkj_square_mul_ak);

		return Dsij ; 
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		Vector<Float> eiva = new Vector<Float>() ; 
		eiva.add(0.92f) ; 
		eiva.add(0.65f);
		
		Vector<Vector<Float>> ss = new Vector<Vector<Float>>() ; 
		
		Vector<Float> eivv = new Vector<>();
		eivv.add(-0.2655134f);
		eivv.add(.77f) ; 
		eivv.add(-0.555f) ; 
		eivv.add(0.543f) ; 
		
		ss.add(eivv) ; 
		
		eivv = new Vector<>();
		eivv.add(-0.4655134f);
		eivv.add(-0.57f) ; 
		eivv.add(.45f) ; 
		eivv.add(0.73f) ; 
		
		ss.add(eivv) ; 
		
		graph_communities_and_fitness gc = new graph_communities_and_fitness(eiva, ss);
		
		//flag and center
		Vector<Float> fl_cen = new Vector<Float>();
		fl_cen.add(0.9f);
		fl_cen.add(0.3f);
		fl_cen.add(.6f) ; 
		//fl_cen.add(.55f);
		
		fl_cen.add(.5f);
		fl_cen.add(0.55f);
		fl_cen.add(.4f);
		//fl_cen.add(.45f);
		
		gc.graph_cluster_at_every_pso_iteration(fl_cen);
		System.out.println(gc.node_selected_as_centroid);
		System.out.println(gc.get_centroid_no_to_element_vec());
		*/
	}

}
