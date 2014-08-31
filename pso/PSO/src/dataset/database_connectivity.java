package dataset;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;


public class database_connectivity {
	
	Connection connect = null;
	java.sql.Statement statement = null;
	java.sql.PreparedStatement prepared_statement = null;

	ResultSet resultSet = null;
	
	Vector< Vector<Float> > dataset=null ;
	
	public database_connectivity(String Host, String Port, String Database_name, String User_name_of_database, String Password_of_user)
	{
		 try {
				Class.forName("com.mysql.jdbc.Driver"); //loading driver
				
				String url =  "jdbc:mysql://"+Host+":"+Port+"/"+ Database_name; 
				String username = User_name_of_database; 
				String password = Password_of_user;
				
				//connecting 
				connect = DriverManager.getConnection(url, username, password);
				
				// Statements allow to issue SQL queries to the database
			    statement = connect.createStatement();
		 }
		 catch (Exception e) 
		 {
					// TODO Auto-generated catch block
					System.out.println("Error : DATABASE OPEN PROBLEM !!!");
		 }
	}
	
	public void query_set_to_prepare_statement(String statement_of_query)
	{
		try {
			prepared_statement = connect.prepareStatement(statement_of_query);  
			//prepared statement optimized for parameterized query 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void set_string_to_prepared_statement(int counter,String string_to_set)
	{
		try {
			prepared_statement.setString(counter, string_to_set);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: String can't set");
		}
	}
	
	public void set_double_to_prepared_statement(int counter, double double_to_set)
	{
		try {
			prepared_statement.setDouble(counter, double_to_set);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: Double can't set");
		}
	}
	
	public ResultSet execute_set_query()
	{
		ResultSet result_set = null ; 
		try {
			result_set = prepared_statement.executeQuery() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: Execute Query Problem");
		} 
		return result_set ; 
	}
	
	public void execute_update_query()
	{
		try {
			prepared_statement.executeUpdate() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: Insert Update Problem");
		}
	}
	
	public void closedatabaseconnection()
	{
		 try {
			 resultSet.close() ; 
			 statement.close() ; 
			 connect.close() ; 
		 }
		 catch (Exception e) 
		 {
					// TODO Auto-generated catch block
					System.out.println("DATABASE CLOSE PROBLEM !!!");
		 }
	}
	
	public void print_prepared_statement()
	{
		System.out.println(prepared_statement.toString());
	}
	
	/*Dataset, Min, Max */
	public Vector < Vector<Float> > get_dataset(String table_name_from_which_user_data_fetch, int no_of_parameter_in_db_user)
	{
	    dataset =  new Vector<Vector<Float> >();
		this.query_set_to_prepare_statement("Select * from "+table_name_from_which_user_data_fetch+" ;");
		ResultSet ResultSet_of_all_user_data = this.execute_set_query() ; 
		
		try{
		while(ResultSet_of_all_user_data.next())
		{
			 Vector<Float> one_user = new Vector<Float>();
			 int counter_column = 0 ; 
			 // Add row to vector (One User added to one vector)
			 for(counter_column = 2; counter_column <= no_of_parameter_in_db_user ; counter_column++)
			 {
				 one_user.add(ResultSet_of_all_user_data.getFloat(counter_column));
			 }
			 //System.out.println(one_user.size());
			 //System.out.println(one_user);
			 dataset.add(one_user);
		}
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error: Database User data reading problem !!!");
		}
		//System.out.println(dataset);
		return dataset ; 
	}
	
	public Vector< Vector<Float> > get_min_and_max_value_of_dimensions(String table_name_from_which_min_value, String table_name_from_which_max_value,int no_of_parameter_in_min_max)
	{
		Vector<Float> min_values = new Vector<Float>();
		Vector<Float> max_values = new Vector<Float>();
		Vector< Vector<Float> > min_max = new Vector< Vector<Float> >();
		
		this.query_set_to_prepare_statement("Select * from "+table_name_from_which_min_value+" ;");
		ResultSet ResultSet_of_min_values = this.execute_set_query() ; 
		try{
		while(ResultSet_of_min_values.next())
		{

			 int counter_column = 0 ; 
			 // Add row to vector (One User added to one vector)
			 for(counter_column = 1; counter_column <= no_of_parameter_in_min_max ; counter_column++)
			 {
				 min_values.add(ResultSet_of_min_values.getFloat(counter_column));
			 }
			 //System.out.println(one_user.size());
			 //System.out.println(one_user);
		}
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error: Database Min values data reading problem !!!");
		}
		
		this.query_set_to_prepare_statement("Select * from "+table_name_from_which_max_value+" ;");
		ResultSet ResultSet_of_max_values = this.execute_set_query() ; 
		
		try{
			while(ResultSet_of_max_values.next())
			{

			 int counter_column = 0 ; 
			 // Add row to vector (One User added to one vector)
			 for(counter_column = 1; counter_column <= no_of_parameter_in_min_max ; counter_column++)
			 {
				 max_values.add(ResultSet_of_max_values.getFloat(counter_column));
			 }
			 //System.out.println(one_user.size());
			 //System.out.println(one_user);
			}
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error: Database Max values data reading problem !!!");
		}
		
		min_max.add(min_values);
		min_max.add(max_values);
		return min_max ; 
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		database_connectivity d1 = new database_connectivity("localhost", "3306", "facebook_database", "root", "pratik");
		
		d1.query_set_to_prepare_statement("select status_id,status from Status ; ");
		ResultSet r1 = d1.execute_set_query() ; 

		try{
		while(r1.next())
		{
			System.out.print(r1.getString("status_id"));
			System.out.print("  ");
			System.out.print(r1.getString("status"));
			System.out.println("");
		}
		}
		catch (Exception e1)
		{
			System.out.println("Error !!!");
		}
		d1.closedatabaseconnection() ; 
		
		//System.out.println(d1.get_dataset("LIWC_feature_extraction_of_user", 88));
		//System.out.println(d1.get_min_and_max_value_of_dimensions("MIN_LIWC_Feature", "MAX_LIWC_Feature", 87));
		*/
		}

}
