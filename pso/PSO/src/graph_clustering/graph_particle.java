package graph_clustering;

import java.util.Vector;

public class graph_particle {
	
	Vector< Float > current_location ;  // now current velocity
	Vector< Float > current_velocity ;  // now current velocity
	
	float pbest ;  
	Vector< Float > pbest_position ;  // all dimension value for pbest value
	
	/*
	 * set after all particle value calculated
	 * initialization after all particle calculated
	 */
	static float gbest ; 
	static Vector< Float > gbest_position ;  
	
	//for every particle make community out of graph centroids and fitness function for this particle 
	static graph_make_communities graph_make_communities_obj ; 
	static community_fitness_function community_fitness_function_obj ; 
	
	static graph_settings setting_for_all_graph ;  // setting for all particles (inertia, c1, c2 , no of cluster , dimension ,etc)
	static Vector< Float > min_max_values_for_particle ; // requires at boundary wrapping for all particle 
	
	
	//When object initializes position and velocity initial values set 
	public graph_particle(Vector< Float > intial_values ,Vector< Float > intial_velocities  )
	{
		current_location = new Vector<Float>();
		graph_particle.copy_vector_inside_vector(intial_values, current_location);
		
		current_velocity = new Vector< Float > ();
		graph_particle.copy_vector_inside_vector(intial_velocities, current_velocity) ; 
		
		/*
		 * set pbest
		 */
		pbest_position = new Vector<Float > (); //copy same dimension to pbest_position bz its for first time
		
		// make communities out of current pbest position
		graph_make_communities_obj.graph_cluster_at_every_pso_iteration(current_location) ; 
		graph_particle.copy_vector_inside_vector(graph_make_communities_obj.get_particle_new_position(),pbest_position) ;
		
		current_location = new Vector<Float>();
		graph_particle.copy_vector_inside_vector(graph_make_communities_obj.get_particle_new_position(),current_location);
		
		//fitness function passed value of centroids and elements
		community_fitness_function_obj.fitness_function_calculate_for_community(graph_make_communities_obj.get_centroid_no_to_element_vec());
		pbest = community_fitness_function_obj.get_Q_value() ; 
	}
	
	public float get_pbest_value()
	{
		return pbest ; 
	}
	
	public Vector<Float> get_pbest_position()
	{
		return pbest_position ; 
	}
	
	public void set_velocity(float rando1 ,float rando2)
	{
		for (int counter_dimension=0; counter_dimension < current_velocity.size() ; counter_dimension++)
		{
			float prev_vel_val = current_velocity.get(counter_dimension);
			float curr_posi_val = current_location.get(counter_dimension);
			float pbest_val = pbest_position.get(counter_dimension);
			float gbest_val = gbest_position.get(counter_dimension);
			float random1 = rando1 ; 
			float random2 = rando2 ;
			float now_vel=velocity_cal_formula(prev_vel_val,curr_posi_val,pbest_val, gbest_val, random1 ,random2);
			current_velocity.set(counter_dimension, now_vel);
		}
	}
	
	public void set_position()
	{
		int half_size_of_curr_position_size = (current_location.size()/2) ; 
		for (int half_dime_count=0; half_dime_count < half_size_of_curr_position_size ; half_dime_count++)
		{
			float new_position = 
					position_cal_formula
					(current_location.get(half_dime_count), 
					 current_velocity.get(half_dime_count),
					 0.0f,  //get min value for that dimension //this all lower half always between 0 and 1
					 1.0f  // get max value for that dimension 
					 ) ; 
			current_location.set(half_dime_count, new_position);
		}
		for (int half_dime_count = half_size_of_curr_position_size ; half_dime_count < current_location.size() ; half_dime_count++)
		{
			float new_position = 
					position_cal_formula
					(current_location.get(half_dime_count), 
					 current_velocity.get(half_dime_count),
					 min_max_values_for_particle.get(0),  //get min value for that dimension //this all lower half always between 0 and 1
					 min_max_values_for_particle.get(1)  // get max value for that dimension 
					 ) ; 
			current_location.set(half_dime_count, new_position);
		}
	}
	
	public void set_pbest()
	{
		float temp_pbest ; 
		graph_make_communities_obj.graph_cluster_at_every_pso_iteration(current_location) ; 
		current_location = new Vector<Float>(); 
		graph_particle.copy_vector_inside_vector(graph_make_communities_obj.get_particle_new_position(),current_location) ;
		
		community_fitness_function_obj.fitness_function_calculate_for_community(graph_make_communities_obj.get_centroid_no_to_element_vec());
		temp_pbest = community_fitness_function_obj.get_Q_value() ; 
		
		if (temp_pbest > pbest)   //here we require bigger value as possible as
		{
			pbest = temp_pbest ; 
			pbest_position = new Vector<Float> ();
			graph_particle.copy_vector_inside_vector(current_location, pbest_position);
		}
	}
	
	//taken from swarm.particle last function in same project another package
	
	public float velocity_cal_formula(float pre_vel,float curr_posi_val, float pbest_val , float gbest_val , float random1 , float random2)
	{
		float curr_dimension_velocity = (
		setting_for_all_graph.w *  pre_vel + 
		setting_for_all_graph.c1 * random1 * (gbest_val - curr_posi_val) +
		setting_for_all_graph.c2 * random2* (pbest_val -  curr_posi_val)
		); 
		return curr_dimension_velocity ;
	}
	
	public float position_cal_formula(float pre_val , float curr_vel, float min_value, float max_value)
	{
		float curr_dimension_position = pre_val + curr_vel ; 
		// sometime particle is 2,3 .. times so take modulo 
		// and subtract so it is placed in boundary again
		float interval_between_max_and_min = max_value - min_value ; 
		//If particle go out of bound, wrap it around boundary
		//less than minimum
		if (curr_dimension_position < min_value)
		{
			float temp_value_reduced_from_max = min_value - curr_dimension_position ; 
			// modulo taken because if its magnitude is high wraps takes that much time
			curr_dimension_position = max_value - (temp_value_reduced_from_max % interval_between_max_and_min) ; 
			//System.out.println("wrapping "+ pre_val+" " + curr_vel +" " + min_value +" " + max_value + " => "+ curr_dimension_position);
		}
		//greater than maximum
		else if (curr_dimension_position > max_value)
		{
			float temp_value_increased_from_min = curr_dimension_position -max_value ; 
			curr_dimension_position  = min_value + (temp_value_increased_from_min % interval_between_max_and_min ) ;
			//System.out.println("wrapping "+ pre_val+" " + curr_vel +" " + min_value +" " + max_value + " => "+ curr_dimension_position);
		}
		return curr_dimension_position ; 
	}
	
	public static void copy_vector_inside_vector(Vector<Float> src_vec ,Vector<Float> des_vec )
	{
			for(int count_dime=0 ; count_dime < src_vec.size() ; count_dime++)
			{
				des_vec.add(src_vec.get(count_dime));
			}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		graph_settings.INFINITY = Float.MAX_VALUE ;  
		
		// as explained in paper expermiment section 
		graph_settings g1 = new graph_settings(0.65f, 2.05f , 2.05f , 1000 , 20);
		
		
	}

}
