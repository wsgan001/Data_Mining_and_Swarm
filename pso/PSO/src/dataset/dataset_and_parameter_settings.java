package dataset;

import java.util.Vector;

//xml file settings related to data 
public class dataset_and_parameter_settings {
	int option_of_file_or_database ; 
	String file_name ; 
	String database_name;
	Vector<Integer> parameter_to_be_considered_pso ; 
	public dataset_and_parameter_settings(int option_of_file_or_database, String file_name, String database_name, Vector<Integer> parameter_to_be_considered_pso )
	{
		this.option_of_file_or_database  = option_of_file_or_database ; 
		this.file_name = file_name ; 
		this.database_name = database_name ; 
		this.parameter_to_be_considered_pso = parameter_to_be_considered_pso ; 
	}
}
