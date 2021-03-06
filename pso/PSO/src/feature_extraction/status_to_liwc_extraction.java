package feature_extraction;

import java.sql.ResultSet;
import java.util.Map;
import dataset.database_connectivity;

//Take data from database and send it to linguistic miner of text 
// and store result to database 
public class status_to_liwc_extraction {
	database_connectivity database_connetor;
	linguistic_miner_of_text text_to_liwc;

	//Connect to database name 
	//Connect to LIWC.CAT file
	public status_to_liwc_extraction(String LIWC_dictionary_location,
			String Host, String Port, String Database_name,
			String User_name_of_database, String Password_of_user) 
	{
		database_connetor = new database_connectivity(Host, Port,
				Database_name, User_name_of_database, Password_of_user);
		text_to_liwc = new linguistic_miner_of_text(LIWC_dictionary_location);
	}

	// Function helps us to calculate LIWC feature on status that is added new
	// to Status Table
	public ResultSet new_status_in_Status_table() 
	{
		ResultSet result_new_statuses = null;
		database_connetor
				.query_set_to_prepare_statement("SELECT * FROM Status WHERE status_id not in (select status_id from Linguistic_features);");
		result_new_statuses = database_connetor.execute_set_query();
		return result_new_statuses;
	}
	
	//New Results are stored to database
	public void new_status_adder_to_Linguistic_features(ResultSet new_status_data) 
	{
		String current_status_id;
		String current_status;
		Map<String,Double> value_related_to_one_status ;  
		database_connetor
		.query_set_to_prepare_statement("INSERT INTO Linguistic_features values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
		try {
			while (new_status_data.next()) {
				current_status_id = new_status_data.getString("status_id") ; 
				current_status = new_status_data.getString("status").toLowerCase() ;  // make lower case
				/*Now call linguistic_miner_of_text to get all value related to one text*/
				value_related_to_one_status = text_to_liwc.get_liwc_value_of_text(current_status) ; 
				//System.out.println(current_status);
				//System.out.println(value_related_to_one_status);
				
				// add value to data base 
				int i = 1;
				database_connetor.set_string_to_prepared_statement(i++, current_status_id);

				for (String one_feature  : value_related_to_one_status.keySet())
				{
					database_connetor.set_double_to_prepared_statement(i++, value_related_to_one_status.get(one_feature));
				}
				
				database_connetor.execute_update_query() ; 
				System.out.println(current_status_id);
				//System.out.println(value_related_to_one_status);
			}
		} catch (Exception e) {
			System.out.println("Error : Data adding problem at Linguistic Features");
		}
	}
	
	public void close_all_connections()
	{
		database_connetor.closedatabaseconnection() ; 
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		status_to_liwc_extraction l1 = new status_to_liwc_extraction("settings/LIWC.CAT", "localhost", "3306", "facebook_database", "root", "pratik");
		ResultSet rr = l1.new_status_in_Status_table() ; 
		l1.new_status_adder_to_Linguistic_features(rr);
		l1.close_all_connections() ;
	}

}
