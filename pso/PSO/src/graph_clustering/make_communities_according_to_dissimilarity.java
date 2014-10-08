package graph_clustering;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class make_communities_according_to_dissimilarity {
	//set eigen values and eigen vectors
	Vector<Float> eigen_values ; 
	Vector< Vector<Float> > eigen_vector ;
	
	//first selected (no of centroids -1) eigen value and eigen vector  are sended
	public make_communities_according_to_dissimilarity(Vector<Float> eigen_values, Vector< Vector<Float> > eigen_vector)
	{
		if (eigen_values.size() != eigen_vector.size())
		{
			System.out.println("Error : eigen Value and eigen Vector size are not same. ");
		}
		else 
		{
			this.eigen_values = eigen_values ;
			this.eigen_vector = eigen_vector ;
		}
	}
	

	//return nodes which are closer to each other in graph matrix 
	// map of centroid number and respective vector sends
	public Map<Integer, Vector<Integer>>  community_find_out_of_given_centroid(Vector<Integer> centroid_number_in_graph)
	{
		Map<Integer, Vector<Integer>> centroid_no_to_element_vec = new HashMap<Integer, Vector<Integer>>();
		
		// setting dictionary with empty element 
		for (int centroid_counter = 0 ; centroid_counter < centroid_number_in_graph.size() ; centroid_counter++ )
		{
			centroid_no_to_element_vec.put(centroid_number_in_graph.get(centroid_counter), new Vector<Integer>()) ; 
		}
		
		Vector<Vector<Float>> centroids_respective_eigenvector_values = new Vector<Vector<Float>>();
		
		/*  // NOW BELOW METHOD GIVE ONLY NODE NUMBER AND CENTROID NUMBER // SO NO NEED
		//all centroid vectors eigenvector values
		for (int centroid_counter = 0 ; centroid_counter < centroid_number_in_graph.size() ; centroid_counter++ )
		{
			int value_taken_from = centroid_number_in_graph.get(centroid_counter);
			Vector<Float> current_centroid_values = new Vector<Float>() ; 
			for (int counter_eigen_vectors =0  ; counter_eigen_vectors < eigen_vector.size() ; counter_eigen_vectors++)
			{
				current_centroid_values.add(eigen_vector.get(counter_eigen_vectors).get(value_taken_from));
			}
			centroids_respective_eigenvector_values.add(current_centroid_values) ; 
		}
		*/
		
		// for each element 
		for (int node_counter = 0 ; node_counter < eigen_vector.get(0).size() ; node_counter++ )
		{
			/*  // NOW BELOW METHOD GIVE ONLY NODE NUMBER AND CENTROID NUMBER // SO NO NEED
			Vector<Float> eigen_vector_value_for_this_node = new Vector<Float>();
			
			// collecting eigenvector value from each vector for this node
			for (int counter_eigen_vectors =0  ; counter_eigen_vectors < eigen_vector.size() ; counter_eigen_vectors++)
			{
				eigen_vector_value_for_this_node.add(eigen_vector.get(counter_eigen_vectors).get(node_counter));
			}	
			*/
			
			//minimum of dsij value store 
			
			// Initially set to max
			float min_of_dsij = Float.MAX_VALUE ; 
			int centroid_no_corresponding_to_min_dsij = 0 ; 
			
			// finding nearest centroid
			for (int centroid_counter = 0 ; centroid_counter < centroid_number_in_graph.size() ; centroid_counter++ )
			{
				float curr_dsij_value = dis_similar_measure(centroid_number_in_graph.get(centroid_counter),node_counter);
				if (curr_dsij_value < min_of_dsij)
				{
					centroid_no_corresponding_to_min_dsij = centroid_number_in_graph.get(centroid_counter);
					min_of_dsij = curr_dsij_value ; 
				}
			}
			centroid_no_to_element_vec.get(centroid_no_corresponding_to_min_dsij).add(node_counter);
		}
		return centroid_no_to_element_vec ; 
	}
	
	//Given node number 
	//Zhewen Shi "PSO based community detection" equation one applied 
	public float dis_similar_measure(int centroid_no  , int curr_node_no)
	{
		float Dsij = 0 ; 
		
		float sum_of_diff_vki_vkj_square_mul_ak = 0 ;
		for (int counter_no_eigen_value = 0 ; counter_no_eigen_value< eigen_values.size() ; counter_no_eigen_value++)
		{
			sum_of_diff_vki_vkj_square_mul_ak = sum_of_diff_vki_vkj_square_mul_ak  + (
					(float)Math.pow(
						(eigen_vector.get(counter_no_eigen_value).get(curr_node_no) - eigen_vector.get(counter_no_eigen_value).get(centroid_no)), 2) 
					* eigen_values.get(counter_no_eigen_value)
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
		
		//Test Purpose
		Vector<Float> eigen_values = new Vector<Float>();
		Vector<Vector<Float>> eigen_vector = new Vector<Vector<Float>>() ; 
		eigen_values.add((float)1.5) ; 
		eigen_values.add((float)2) ; 
	
		
		Vector<Float> a = new Vector<Float>();
		a.add((float)4.5);
		a.add((float)5);
		a.add((float)6.9);
		a.add((float)4);
		a.add((float)10);
		eigen_vector.add(a) ; 
		
		a = new Vector<Float>();
		a.add((float)2);
		a.add((float)1.4);
		a.add((float)5.2);
		a.add((float)3);
		a.add((float)11);
		eigen_vector.add(a) ; 
		
		Vector<Integer> centroid_number_in_graph = new Vector<Integer> ();
		centroid_number_in_graph.add(2);
		centroid_number_in_graph.add(3);
		
		make_communities_according_to_dissimilarity aa = new make_communities_according_to_dissimilarity(eigen_values, eigen_vector)  ;
		System.out.println(aa.community_find_out_of_given_centroid(centroid_number_in_graph)); 
	}

}