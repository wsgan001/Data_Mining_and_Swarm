package dataset;

import java.io.File;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import swarm.settings;

public class xml_file_reader {
	
	settings setting_for_session ; 
	dataset_and_parameter_settings  dataset_and_parameter_settings_session; 
	public xml_file_reader(String filename)
	{
		try{
		File xmlfilereader = new File(filename);
		DocumentBuilderFactory db_factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder d_builder = db_factory.newDocumentBuilder();
		Document doc = d_builder.parse(xmlfilereader);
		doc.getDocumentElement().normalize();
		
		//Print root of xml (swarm)
		//System.out.println("root of xml file  " + doc.getDocumentElement().getNodeName());
		
		// get settings nodes 
		NodeList nodes = doc.getElementsByTagName("settings");
		// get 0th setting node (in our case only 1 setting node ) 
		Node node = nodes.item(0);
		
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			int no_of_parti = Integer.parseInt(getValue("no_of_particle", element)) ; 
			int no_of_clus = Integer.parseInt(getValue("no_of_cluster", element));
			float w_iner = Float.parseFloat(getValue("w_inertia", element));
			float c1_gloabl = Float.parseFloat(getValue("c1_global_weight_constant", element));
			float c2_local = Float.parseFloat(getValue("c2_local_weight_constant", element));
			int max_iter = Integer.parseInt(getValue("max_no_of_iterations", element));
			
			setting_for_session = new settings(no_of_clus, w_iner, c1_gloabl, c2_local, max_iter,no_of_parti);
		}
		
		//Settings Related to from Where data taken
		
		// get settings nodes 
		nodes = doc.getElementsByTagName("data_take_from");
		// get 0th setting node (in our case only 1 data_take_from node ) 
		node = nodes.item(0);

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			
			int option_of_file_or_database = Integer.parseInt(getValue("option_of_file_or_database", element));
			String file_name= getValue("data_file_name", element);
			String database_name = getValue("database_name", element);
			
			// String of numbers separated by comma(,)
			String parameters_take_into_considerations = getValue("parameters_take_into_considerations", element);
			
			// all parameter no. going to store here
			Vector<Integer> parameter_taken_for_pso = new Vector<Integer>() ; 
			// all string integes are added to vector 
			for(String indiviual_integer : parameters_take_into_considerations.split(","))
			{
				parameter_taken_for_pso.add(Integer.parseInt(indiviual_integer));
			}
			dataset_and_parameter_settings_session = new dataset_and_parameter_settings(option_of_file_or_database, file_name, database_name, parameter_taken_for_pso); 
		}		

		}//try closing
		catch(Exception e)
		{
			System.out.println("Error : Parsing xml file");
		}
	}
	
	public String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}

	// return PSO settings
	public settings get_settings()
	{
		return setting_for_session ; 
	}
	
	//returning dataset and parameter related object
	public dataset_and_parameter_settings get_dataset_and_parameter_settings()
	{
		return dataset_and_parameter_settings_session ; 
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//test
		//xml_file_reader s1 = new xml_file_reader("settings/swarm.xml") ; 
		
	}

}
