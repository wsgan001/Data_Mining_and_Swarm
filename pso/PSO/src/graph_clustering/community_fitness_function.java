package graph_clustering;

import java.util.Map ; 
import java.util.HashMap;
import java.util.Vector;

public class community_fitness_function {
	float  Q_value_for_community ; 
	int no_of_communities ; 
	Vector<Float> A_vector_inter_cluster_values ; 
	
	//community linkage cost
	Vector<Vector<Float>> community_inter_intra_linkage ;
	
	//sum of all linkage
	float sum_of_all_linkage_in_graph  ;
	
	//Graph data matrix 
	Vector<Vector<Float>> graph_data ; 
	
	//Hash map center and community
	HashMap<Integer, Vector<Integer>> centroid_no_to_element ; 
	
	public community_fitness_function(Vector<Vector<Float>> graph_data)
	{
		this.graph_data = graph_data ; 
	}
	
	//give community based linkage matrix 
	//symmetric in nature 
	//Matrix E in paper of Zhewen Shi 3.3 Section
	public void fitness_function_calculate_for_community ( HashMap<Integer, Vector<Integer>> centroid_no_to_element)
	{
		this.centroid_no_to_element = centroid_no_to_element ; 
		linkage_matrix_making() ; 
		
		Q_value_for_community =  0 ; 
		no_of_communities = community_inter_intra_linkage.size() ; 
		A_vector_inter_cluster_values = new Vector<Float>() ; 
		
		calculate_A_vector_values() ; 
		calculate_Q_value() ; 
	}
	
	public void calculate_A_vector_values ()
	{
		for (int community_counter=0; community_counter<no_of_communities ; community_counter++ )
		{
			float sum_of_all_inter_community_linkage = 0  ;
			for (int other_community_counter=0 ; other_community_counter<no_of_communities; other_community_counter++ )
			{
				// Dont consider intra community values
				if (community_counter != other_community_counter)
				{
					sum_of_all_inter_community_linkage = 
							sum_of_all_inter_community_linkage +
							community_inter_intra_linkage.get(community_counter).get(other_community_counter) ;
				}
			}
			A_vector_inter_cluster_values.add(sum_of_all_inter_community_linkage);
		}
	}
	
	public void calculate_Q_value ()
	{
		Q_value_for_community = 0 ; 
		for (int community_counter=0 ; community_counter < no_of_communities ; community_counter++)
		{
			Q_value_for_community = Q_value_for_community +
					(community_inter_intra_linkage.get(community_counter).get(community_counter) /* intra community fraction */
							-
					(float)Math.pow(A_vector_inter_cluster_values.get(community_counter), 2) /*inter community fraction*/
					) ; 
		}
	}
	
	public float get_Q_value() 
	{
		return Q_value_for_community ; 
	}
	
	//graph data = whole graph linkage 
	//centroid_no_to_element : centroid and respected element
	public void linkage_matrix_making( )
	{
		int no_of_communities = centroid_no_to_element.size() ; 
		//n*n matrix initilization // community* community matrix
		community_inter_intra_linkage = new Vector<Vector<Float>>(); 
		for (int count_community= 0  ; count_community < no_of_communities ; count_community++)
		{
			Vector<Float> community_row_in_matrix = new Vector<Float>();
			//intialize with 0's
			for (int count_clm=0; count_clm < no_of_communities ; count_clm++)
			{
				community_row_in_matrix.add(0.0f);
			}
			community_inter_intra_linkage.add(community_row_in_matrix);
		}
		
		//map to vector change
		Vector<Vector<Integer>> communities_in_graph =  new Vector<Vector<Integer>>(); 
		
		//iterate over map and all vector of community
		for (Map.Entry<Integer, Vector<Integer>> entry : centroid_no_to_element.entrySet()) {
			communities_in_graph.add(entry.getValue());
		}
		
		//sum of all linkage in graph 
		//every matrix element of n*n should be divided by this
		sum_of_all_linkage_in_graph = 0 ; 
		
		//all community iterator
		for (int counter_to_community_no=0; counter_to_community_no < no_of_communities ; counter_to_community_no++)
		{
			Vector<Integer> nodes_in_one_community = communities_in_graph.get(counter_to_community_no) ; 
			float intra_cluster_sum = 0.0f ;
			
			//all internal added first (intra cluster)
			//nodes checks with forward node
			for (int node_intra_counter = 0 ; node_intra_counter < nodes_in_one_community.size(); node_intra_counter++)
			{
				for (int node_intra_counter_two = node_intra_counter ; node_intra_counter_two < nodes_in_one_community.size(); node_intra_counter_two++)
				{
					intra_cluster_sum = intra_cluster_sum + 
							graph_data.get(nodes_in_one_community.get(node_intra_counter)).get(nodes_in_one_community.get(node_intra_counter_two));
					
					//for whole graph linkage total
					sum_of_all_linkage_in_graph = sum_of_all_linkage_in_graph+
							graph_data.get(nodes_in_one_community.get(node_intra_counter)).get(nodes_in_one_community.get(node_intra_counter_two));
				}
			}
			//set diagonal matrix element which represent intra cluster value
			community_inter_intra_linkage.get(counter_to_community_no).set(counter_to_community_no, intra_cluster_sum);
			
			//inter cluster finding 
			//with other other community 
			for (int another_community_counter= counter_to_community_no+1 ; another_community_counter < no_of_communities ; another_community_counter++ )
			{
				Vector<Integer> nodes_in_another_community = communities_in_graph.get(another_community_counter) ; 
				
				float community_one_to_another_total = 0 ; 
				for (int node_counter_of_first_community = 0 ; node_counter_of_first_community < nodes_in_one_community.size() ; node_counter_of_first_community++)
				{	
					int node_number_first_community = nodes_in_one_community.get(node_counter_of_first_community) ; 
					for(int node_counter_of_second_community=0 ; node_counter_of_second_community < nodes_in_another_community.size() ; node_counter_of_second_community++ )
					{
						int node_number_second_community  = nodes_in_another_community.get(node_counter_of_second_community);
						// inter community count 
						community_one_to_another_total = community_one_to_another_total +
								graph_data.get(node_number_first_community).get(node_number_second_community) ; 
						
						sum_of_all_linkage_in_graph = sum_of_all_linkage_in_graph + 
								graph_data.get(node_number_first_community).get(node_number_second_community) ; 
						
					}
				}
				// m row n column
				community_inter_intra_linkage.get(counter_to_community_no).set(another_community_counter, community_one_to_another_total);
				// bz of symmetry set for n row m clm
				community_inter_intra_linkage.get(another_community_counter).set(counter_to_community_no, community_one_to_another_total);
			}
		}

		for (int count_community= 0 ; count_community < community_inter_intra_linkage.size() ; count_community++)
		{
			for(int count_column =0 ; count_column < community_inter_intra_linkage.get(0).size(); count_column++)
			{
				//divide all elements by total linkage
				float fraction_linkage = community_inter_intra_linkage.get(count_community).get(count_column) / sum_of_all_linkage_in_graph;
				community_inter_intra_linkage.get(count_community).set(count_column, fraction_linkage ) ; 
			}
		}
		
		//System.out.println(community_inter_intra_linkage);
		//System.out.println(sum_of_all_linkage_in_graph);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		Vector<Vector<Float>> graph_data = new Vector<Vector<Float>>();
		
		Vector<Float> row = new Vector<Float>();
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(7.0f);
		row.add(0.0f);
		row.add(4.0f);
		row.add(0.0f);
		row.add(0.0f);
		graph_data.add(row);
		
		row = new Vector<Float>();
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(2.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(1.0f);
		row.add(0.0f);
		graph_data.add(row);
		
		row = new Vector<Float>();
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(5.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		graph_data.add(row);
		
		row = new Vector<Float>();
		row.add(7.0f);
		row.add(2.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(8.0f);
		row.add(2.0f);
		row.add(0.0f);
		row.add(0.0f);
		graph_data.add(row);
		
		row = new Vector<Float>();
		row.add(0.0f);
		row.add(0.0f);
		row.add(5.0f);
		row.add(8.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(6.0f);
		row.add(2.0f);
		graph_data.add(row);
		
		
		row = new Vector<Float>();
		row.add(4.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(2.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		graph_data.add(row);
		
		row = new Vector<Float>();
		row.add(0.0f);
		row.add(1.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(6.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		graph_data.add(row);
		
		
		row = new Vector<Float>();
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(2.0f);
		row.add(0.0f);
		row.add(0.0f);
		row.add(0.0f);
		graph_data.add(row);
		
		 HashMap<Integer, Vector<Integer>> m1 = new HashMap<Integer, Vector<Integer>>();
		 
		 Vector<Integer> r1 = new Vector<Integer>();
		 r1.add(0);
		 r1.add(3);
		 r1.add(5);
		 m1.put(0, r1);
		 
		 r1 = new Vector<Integer>();
		 r1.add(1);
		 r1.add(6);
		 m1.put(6, r1);
		 
		 r1 = new Vector<Integer>();
		 r1.add(2);
		 r1.add(4);
		 r1.add(7);
		 m1.put(7, r1);
		 
		 
		 fitness_function ff1 = new fitness_function(graph_data) ;
		 ff1.fitness_function_calculate_for_community(m1);
		 System.out.println(ff1.get_Q_value());
		 */
	}

}
