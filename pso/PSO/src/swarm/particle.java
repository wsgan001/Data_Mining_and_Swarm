package swarm;

import graph_clustering.graph_particle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import dataset.data_reader_from_file;
import dataset.database_connectivity;
import dataset.xml_file_reader;
import dataset.dataset_and_parameter_settings;

public class particle {	
	Vector< Vector<Float> > current_location ;  // now current velocity
	Vector< Vector<Float> > current_velocity ;  // now current velocity

	float pbest ;  
	Vector< Vector<Float> > pbest_position ;  // all dimension value for pbest value
	
	/*
	 * set after all particle value calculated
	 * initialization after all particle calculated
	 */
	static float gbest ; 
	static Vector< Vector<Float> > gbest_position ;  

	static settings setting_for_all ;  // setting for all particles (inertia, c1, c2 , no of cluster , dimension ,etc)
	static Vector< Vector<Float> > min_max_values ; // requires at boundary wrapping for all particle 
	
	//When object initializes position and velocity initial values set 
	public particle(Vector< Vector<Float> > intial_values ,Vector< Vector<Float> > intial_velocities  )
	{
		current_location = new Vector<Vector<Float> >();
		particle.copy_vector_inside_vector(intial_values, current_location);
		
		current_velocity = new Vector< Vector<Float> > ();
		particle.copy_vector_inside_vector(intial_velocities, current_velocity) ; 
		
		/*
		 * set pbest
		 */
		pbest_position = new Vector<Vector<Float> > (); //copy same dimension to pbest_position bz its for first time
		particle.copy_vector_inside_vector(current_location, pbest_position);
		
		swarm_based_clustering particle_cluster = new swarm_based_clustering(current_location);
		pbest = particle_cluster.get_quantization_error_value();
	}

	public float get_pbest_value()
	{
		return pbest ; 
	}
	
	public Vector<Vector<Float>> get_pbest_position()
	{
		return pbest_position ; 
	}
	
	public void set_velocity(float rando1 ,float rando2)
	{

		int vecsize_curr_velocity = current_velocity.size() ;
		for (int count_index = 0 ; count_index < vecsize_curr_velocity ; count_index++)
		{
			int dimension_size = current_velocity.get(count_index).size(); 
			for (int dimencount = 0 ; dimencount < dimension_size ; dimencount++)
			{
				float prev_vel_val = current_velocity.get(count_index).get(dimencount) ;  // this is old (below calculated is new) 
				float curr_posi_val = current_location.get(count_index).get(dimencount) ; 
				float pbest_val = pbest_position.get(count_index).get(dimencount);
				float gbest_val = gbest_position.get(count_index).get(dimencount);
				float random1 = rando1 ; 
				float random2 = rando2 ;
				float now_vel=velocity_cal_formula(prev_vel_val,curr_posi_val,pbest_val, gbest_val, random1 ,random2);
				current_velocity.get(count_index).set(dimencount, now_vel);  //this is new 
			}
		}
		
	}

	public void set_position()
	{
		int curr_position_size = current_location.size() ; 
		for (int count_position = 0 ; count_position < curr_position_size ; count_position++)
		{
			int get_dim_size = current_location.get(count_position).size() ; 
			for (int dime_counter = 0 ; dime_counter < get_dim_size ; dime_counter++)
			{
				float new_position = 
						position_cal_formula
						(current_location.get(count_position).get(dime_counter), 
						 current_velocity.get(count_position).get(dime_counter),
						 min_max_values.get(0).get(dime_counter),  //get min value for that dimension 
						 min_max_values.get(1).get(dime_counter)  // get max value for that dimension 
						 ) ; 
						/*(current_location.get(count_position).get(dime_counter)+ current_velocity.get(count_position).get(dime_counter));  */
				current_location.get(count_position).set(dime_counter, new_position);
			}
		}
	} 
	public void set_pbest()
	{
		float temp_pbest ; 
		swarm_based_clustering particle_cluster = new swarm_based_clustering(current_location);
		temp_pbest = particle_cluster.get_quantization_error_value();
		if (temp_pbest < pbest)
		{
			pbest = temp_pbest ; 
			pbest_position = new Vector<Vector<Float> > ();
			particle.copy_vector_inside_vector(current_location, pbest_position);
		}
	}
	public float velocity_cal_formula(float pre_vel,float curr_posi_val, float pbest_val , float gbest_val , float random1 , float random2)
	{
		float curr_dimension_velocity = (
		setting_for_all.w *  pre_vel + 
		setting_for_all.c1 * random1 * (gbest_val - curr_posi_val) +
		setting_for_all.c2 * random2* (pbest_val -  curr_posi_val)
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
	
	public static void copy_vector_inside_vector(Vector<Vector<Float>> src_vec ,Vector<Vector<Float>> des_vec )
	{
		int src_vec_size = src_vec.size();
		int count_vec_src = 0 ;
		for(count_vec_src=0 ; count_vec_src < src_vec_size ; count_vec_src++)
		{
			Vector<Float> clone = (Vector<Float>)src_vec.get(count_vec_src).clone();
			des_vec.add(clone);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//algorithm based on 
		//Data Clustering using Particle Swarm Optimization DW van der Merwe 
		
		//settings explained in paper are used 
		/*
		 * SET INFINITY VALUE
		 */
		settings.INFINITY = Float.MAX_VALUE ;  // max float value assigned
		/*           
		 * SET NO. OF CLUSTER                   
		 */
		// direct setting in program
		//settings settings_for_now = new settings(3,(float)0.72,(float)1.49,(float)1.49);
		
		// Setting taken from xml file 
		xml_file_reader swarm_xml_reader = new xml_file_reader("settings/swarm.xml") ; 
		particle.setting_for_all = swarm_xml_reader.get_settings() ; // set settings	
		
		// setting of data taken from swarm xml reader object
		dataset_and_parameter_settings data_taken_from = swarm_xml_reader.get_dataset_and_parameter_settings();
		random_particle_generator r1 = null ; // random particle pointer
		
		// this is for data reading from file
		if (data_taken_from.option_of_file_or_database == 1 )
		{
			data_reader_from_file d1  = new data_reader_from_file(data_taken_from.file_name,",") ; 
			swarm_based_clustering.points = d1.get_dataset() ; 
			r1 = new random_particle_generator(particle.setting_for_all.no_of_cluster, d1.get_min_and_max_value_of_dimensions()) ; 
			//set min max boundary of particle for wrapping conditions
			particle.min_max_values = d1.get_min_and_max_value_of_dimensions() ;
		}
		
		else if (data_taken_from.option_of_file_or_database == 2)
		{
			// Database Connectivity
			database_connectivity d1 = new database_connectivity("localhost", "3306", data_taken_from.database_name , "root", "pratik");
			swarm_based_clustering.points = d1.get_dataset(data_taken_from.database_user_data_table_name, data_taken_from.parameter_to_be_considered_pso);
			r1 = new random_particle_generator(particle.setting_for_all.no_of_cluster, d1.get_min_and_max_value_of_dimensions(data_taken_from.database_min_data_table_name, data_taken_from.database_max_data_table_name, data_taken_from.parameter_to_be_considered_pso)) ; 
			//set min max boundary of particle for wrapping conditions
			particle.min_max_values = d1.get_min_and_max_value_of_dimensions(data_taken_from.database_min_data_table_name, data_taken_from.database_max_data_table_name, data_taken_from.parameter_to_be_considered_pso) ;
			//Print Which dataset taken from database 
			System.out.println(swarm_based_clustering.points);
			System.out.println(d1.get_min_and_max_value_of_dimensions(data_taken_from.database_min_data_table_name, data_taken_from.database_max_data_table_name, data_taken_from.parameter_to_be_considered_pso));
			d1.closedatabaseconnection() ; 
		}
		
		else 
		{
			System.out.println("Error : Wrong value put in swarm.xml for option of file or database");
		}
		
		
		//particles information
		Vector<particle> particles_as_cen = new Vector<particle>() ;
		
		particle p ; 
				
		//centroid as 1 of particle 
		// kmeans (input is dataset , no of inital seed , no of cluster)
		simplekmeans kmeans = new simplekmeans(swarm_based_clustering.points , 10 , particle.setting_for_all.no_of_cluster ) ; 
		// random particle get (random  velocity set)
		p = r1.get_random_particle() ; 
		//location set as centroid calculated by kmeans 
		p.current_location = kmeans.get_centroids() ;
		// p best value updated because of current position set to centroid 
		p.set_pbest() ; 
		// add to particle list 
		particles_as_cen.add(p);
		
		System.out.println("Quantization Error (Weka Kmeans): "+p.pbest);
		System.out.println("Weka Centroids : "+p.current_location);
		// remaining (no_of_particle-1) particle generating 
		for (int count_particle =0  ; count_particle< (particle.setting_for_all.no_of_particle-1) ; count_particle++)
		{
			p = r1.get_random_particle() ; 
			particles_as_cen.add(p);
		}
		
		particle.gbest = settings.INFINITY ; 
		for (particle partin : particles_as_cen)
		{
			if (partin.get_pbest_value() < particle.gbest)
			{
				particle.gbest = partin.get_pbest_value() ; 
				particle.gbest_position = new Vector< Vector<Float> >(partin.get_pbest_position());
			}
		}
		System.out.println("Intialized paticle Global Best : "+particle.gbest);
		for (int iteration_counter = 0 ; iteration_counter< particle.setting_for_all.max_no_of_iterations ; iteration_counter++)
		{
			float rando1 = (float)Math.random() ; 
			float rando2 = (float)Math.random() ; 
			for(particle partin : particles_as_cen)
			{
				partin.set_velocity(rando1, rando2) ; 
				partin.set_position();
				partin.set_pbest() ; 
			}
			for (particle partin : particles_as_cen)
			{
				if (partin.get_pbest_value() < particle.gbest)
				{
					particle.gbest = partin.get_pbest_value() ; 
					particle.gbest_position = new Vector< Vector<Float> >();
					particle.copy_vector_inside_vector(partin.get_pbest_position(), particle.gbest_position);
				}
			}
		}
		System.out.println("Result Cluster Quantization Error : "+particle.gbest);
		System.out.println(particle.gbest_position);
		/*
		System.out.println("===== Printing particles ====== ");
		for (int i=0; i<setting_for_all.no_of_particle ; i++)
		{
			System.out.println("Particle no. coordinates  "+String.valueOf(i));
			System.out.println(particles_as_cen.get(i).current_location);
		}
		*/

	//only when database is selected in above equation
	if (data_taken_from.option_of_file_or_database == 2)
	{				
		//store all user  
		Vector<String> user_long_ids_of_facebook = new Vector<String>(); 
		
		swarm_based_clustering particle_cluster_last_for_database = new swarm_based_clustering(particle.gbest_position);

		database_connectivity dd1 = new database_connectivity("localhost", "3306", "facebook_database", "root", "pratik");
		
		// change this name and take from xml settings already given name
		dd1.query_set_to_prepare_statement("select user_id from user_all_features ; ");
		ResultSet r11 = dd1.execute_set_query() ; 
		
		try {
			while(r11.next())
			{
				String user = r11.getString("user_id") ;	
				user_long_ids_of_facebook.add(user) ; 
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//delete previous entry
		dd1.query_set_to_prepare_statement("truncate table ClusterandUser ; ");
		dd1.execute_update_query() ; 
		
		//in vector which user going 
		int user_no_in_vector = 0 ; 
		for (Vector<Float> each_user_point : swarm_based_clustering.points)
		{
			//intially max value //we want min value
			float curr_distance = Float.MAX_VALUE ; 
			int cluster_closer = 0 ; 
			int count_cluster_no =  0 ; 
			// no of cluster which is less
			for(Vector<Float> each_center : particle.gbest_position)
			{
				float curr_dista_curr_center = particle_cluster_last_for_database.cal_euclidean_distance(each_user_point, each_center) ; 
				if (curr_dista_curr_center < curr_distance)
				{
					// add user where he belong
					curr_distance = curr_dista_curr_center ; 
					cluster_closer = count_cluster_no ; 
				}
				count_cluster_no++ ; 
			}
			//add user to database accordingly
			dd1.query_set_to_prepare_statement("insert into ClusterandUser values ( '" + cluster_closer + "' , '" + user_long_ids_of_facebook.get(user_no_in_vector) +"' ) ; " );
			dd1.execute_update_query();
			
			// next user data will taken in next loop
			user_no_in_vector++ ; 
		}
		
		dd1.closedatabaseconnection() ; 
	}	
}

}
