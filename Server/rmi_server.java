import java.rmi.registry.Registry; 
import java.rmi.registry.LocateRegistry; 
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Arrays;
import java.io.*;

/*
	ATTN: The use of this code in any form outside of proof-of-concept work is strictly
	prohibited. This software *will* present many vulnerabilities and should NOT be implemented
	in any public-facing product or service. The authors of this code do not assume ANY liability
	should it be used against these terms.
*/

public class rmi_server extends ImplExample{
	public rmi_server() {}
	
	public static void main(String args[]) {
		
		System.out.println("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-(*)-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("\t\t\tServer Software");
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-(*)-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");		
		
		try{
			System.out.print("\n[!] Attempting to read database file containing user information...");
			ImplExample obj = new ImplExample();
			
			File fis = new File("people.data");
			Boolean exists = fis.exists();
			if(!exists){
				System.out.println("\n[!] Error: No file exists. You must now create a STAFF account to manage the database.\n");
				registerAdmin();
				System.out.println("\n[!] Done! Cheers for that.");
			}else{
				System.out.println(" Success!");
			}
			
			Test stub = (Test) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.getRegistry();
			
			registry.bind("Test", stub);
			System.out.println("\n[!] Server is ready and awaiting a client...");
			
		} catch (Exception e) {
			System.out.println("Server exception: " + e.toString());
			e.printStackTrace();
			
		}
	}
	
	
	public ArrayList<person> deserialisePerson(File fis){
		ArrayList<person> readPerson = new ArrayList<person>();
		try{
			FileInputStream reader = new FileInputStream(fis);
			ObjectInputStream dongReader = new ObjectInputStream(reader);
			readPerson = (ArrayList<person>) dongReader.readObject();
			dongReader.close();
		}catch (Exception e){
			System.out.println("File exception: " + e.toString());
		}
		return readPerson;
	}
	
	public void serialisePerson(File fis, ArrayList<person> allPeople){
		try{
			FileOutputStream writer = new FileOutputStream(fis);
			ObjectOutputStream dongs = new ObjectOutputStream(writer);
			dongs.writeObject(allPeople);
			dongs.close();
		}catch (IOException e){
			System.out.println("File exception: " + e.toString());

		}
	}
	
	public static void registerAdmin(){
		try{
			Scanner in = new Scanner(System.in);
			ArrayList<String> initialAccount = new ArrayList<String>();
			System.out.print("Enter First Name: ");
			String firstname = in.next();
			
			System.out.print("Enter Last Name: ");
			String lastname = in.next();
			
			boolean isStudent = false;
			String ID = "";
			
			System.out.print("Enter a staff ID: ");
			ID = in.next();
				
			initialAccount.add(ID);
			initialAccount.add(firstname);
			initialAccount.add(lastname);
			String strisStudent = String.valueOf(isStudent);
			initialAccount.add(strisStudent);
			
			ImplExample test = new ImplExample();
				
			test.saveuserData(initialAccount);
			
		} catch (Exception e) {
			System.out.println("Client exception: " + e.toString()); 
			e.printStackTrace(); 
		}
	}

	
	public void writePerson(person newPerson){
		
		File fis = new File("people.data"); //check if file exists
		
		//JSONObject jo = new JSONObject();
		
		Boolean exists = fis.exists();
		if(!exists){
			try{
				fis.createNewFile();
				ArrayList<person> allPeople = new ArrayList<person>();
				allPeople.add(newPerson);
				serialisePerson(fis, allPeople);
				
			}catch (IOException e){
				System.out.println("File exception: " + e.toString());
			}
		}
		else{
		//append
			ArrayList<person> allPeople = new ArrayList<person>();
			allPeople = deserialisePerson(fis);
			allPeople.add(newPerson);
			serialisePerson(fis, allPeople);
			
			/*
			This is to print out a serialised person in the storage and use it
			Iterator itr = allPeople.iterator();
			while(itr.hasNext()){
				//https://www.javatpoint.com/java-arraylist
				person test = (person)itr.next();
				System.out.println(test.getpersonID() + " " + test.getfirstName() + " " + test.getlastName());

			}*/
		}	
	}
	
	public void updatePerson(person currPerson){
		
		try{
			File fis = new File("people.data");
			//updateObject update = new updateObject(updater);
			
			//this is some stuff the ObjectOutputStream Library does for security purposes, took me 3 hours to work out why this wasn't working
			//update.enabletheReplaceObject(true);
			//this is where I need to workout to update a specific person without adding duplicates or overwriting the entire .data file
			
			ArrayList<person> allPeople = new ArrayList<person>();
			allPeople = deserialisePerson(fis);
			String currID = currPerson.getpersonID();
			Iterator itr = allPeople.iterator();
			while(itr.hasNext()){
				//https://www.javatpoint.com/java-arraylist
				person aPerson = (person)itr.next();
				
				if(currID.equals(aPerson.getpersonID())){
					aPerson.setgradeStorage(currPerson.getgradeStorage());
					break;
				}
			}
			
			serialisePerson(fis, allPeople);
		}catch (Exception e){
			System.out.println("Client exception: " + e.toString()); 
			e.printStackTrace(); 
		}
		
	}
	
}
/*
class updateObject extends ObjectOutputStream{
	public updateObject(OutputStream out)throws IOException{
		super(out);
	}
	
	public void enabletheReplaceObject(boolean enable){
		enableReplaceObject(enable);
	}
}*/


class ImplExample implements Test{
	public void sendMessage(String s){	
		System.out.println("Client connected to " + s);
	}
	public String getMessage(String text){
		return "Connected successfully to " + text;
	}
	
	public int verifysuperSecret(String attempt){
		//please never use this!
		//very bad pls don't
		int secure = 0;
		String password123 = "admin123";
		
		if(attempt.equals(password123)){
		System.out.println("[!] Password Attempt: '" + attempt +"' = successful!");
		System.out.println("\tIf you still can't log in, you might be typing your Staff ID in wrong.");
			secure = 1;
			
		}
		else{
			System.out.println("[!] Password Attempt: '" + attempt +"' = UNSUCCESSFUL!");
			secure = -1;
		}
		
		return secure;
		
	}
	
	public LinkedHashMap<String, Integer> gethashMap(String ID){
		person currPerson = personLookup(ID);
		return currPerson.getgradeStorage();
	}
	
	
	public ArrayList<String> userLookup(String ID){
		rmi_server dongs = new rmi_server();
		File fis = new File("people.data");
		ArrayList<person> allPeople = new ArrayList<person>();
		allPeople = dongs.deserialisePerson(fis);
		
		ArrayList<String> currPerson = new ArrayList<String>();
		
		Iterator itr = allPeople.iterator();
		int nothingFound = 0;
		while(itr.hasNext()){
			//https://www.javatpoint.com/java-arraylist
			person aPerson = (person)itr.next();			
			//str1.equals(str2);
			if(ID.equals(aPerson.getpersonID())){
				System.out.println("[!] Retrieving user: " + aPerson.getpersonID() + "-" + aPerson.getfirstName() + " " + aPerson.getlastName());
				currPerson.add(aPerson.getpersonID());
				currPerson.add(aPerson.getfirstName());
				currPerson.add(aPerson.getlastName());
				String strisStudent = String.valueOf(aPerson.getisStudent());
				currPerson.add(strisStudent);
				nothingFound = 0;
				/*
				currPerson.add(hashmaptoString(aPerson.getgradeStorage()));*/
				break;
			}
			else{
				nothingFound = 1;
				
			}
		}
		
		if(nothingFound == 1){
			System.out.println("[!] Failed to retrieve a valid user.");
			String nothing = "";
			currPerson.add(nothing);
		}
		
		return currPerson;
	}

	public String saveuserData(ArrayList<String> someData){	
		rmi_server dongs = new rmi_server();
		System.out.println("\n[!] Attempting to register user with ID: " + someData.get(0));
		String ID = someData.get(0);
		String firstName = someData.get(1);
		String lastName = someData.get(2);
		String strisStudent = someData.get(3);
		LinkedHashMap<String, Integer> currgradeStorage = new LinkedHashMap<String, Integer>();
		
		//initialise to null
		currgradeStorage.put(null, null);
		
		boolean isStudent = Boolean.parseBoolean(strisStudent);
		person newPerson = new person(ID, firstName, lastName, isStudent, currgradeStorage);
		
		System.out.print("\n[!] New user created! \t->");
		System.out.println("\t" + newPerson.toString());
		
		dongs.writePerson(newPerson);
		
		return "Hey I saved some data!";
	}
	
	public person personLookup(String studentID){
		rmi_server dongs = new rmi_server();
	
		//repeated code fix later
		LinkedHashMap<Integer, String> currhashMap = new LinkedHashMap<Integer, String>();
		person newPerson = new person(null,null,null,false,null);
		//this may break shit idk
		ArrayList<person> allPeople = new ArrayList<person>();
		
		File fis = new File("people.data");
		allPeople = dongs.deserialisePerson(fis);
		
		Iterator itr = allPeople.iterator();
		int nothingFound = 0;
		while(itr.hasNext()){
			//https://www.javatpoint.com/java-arraylist
			person aPerson = (person)itr.next();
			
			if(studentID.equals(aPerson.getpersonID())){
				System.out.println("[!] Retrieving user: " + aPerson.getpersonID() + "-" + aPerson.getfirstName() + " " + aPerson.getlastName());
				newPerson = new person(aPerson.getpersonID(), aPerson.getfirstName(), aPerson.getlastName(), aPerson.getisStudent(), aPerson.getgradeStorage());
				break;
			}
		}
		return newPerson;
	}
	
	public String printStudents(String studentID){
		person currPerson = personLookup(studentID);
		String peopleString = currPerson.toString();
		return peopleString;
	}
	
	public void saveGrades(String studentID, LinkedHashMap<String, Integer> newGrades){
		System.out.print("[!] Attempting to update grades for student: " + studentID);
		
		rmi_server dongs = new rmi_server();
		person currPerson = personLookup(studentID);

		System.out.println("\tPreviously:\t" + currPerson.getgradeStorage());
		currPerson.setgradeStorage(newGrades);
		System.out.println("\tUpdated:\t" + currPerson.getgradeStorage());
	
		dongs.updatePerson(currPerson);
	}
	

	public ArrayList<Integer> selectBest8 (ArrayList<Integer> fullSet){
		ArrayList<Integer> best8 = new ArrayList<Integer>();
		Comparator<Object> c = Collections.reverseOrder();
		Collections.sort(fullSet, c);
		for (int i=0; i<8; i++){
			best8.add(fullSet.get(i));
		}
		return best8;
	}
	
	public double calculateAverage(ArrayList<Integer> sample){
		int size = sample.size();
		double average = 0;
		double total = 0;
		for (int score : sample){
			total += score;
		}
		average = total / (double)size;
		return average;
	}
	
	
	public String qualify(String studentID, double CA, double eightMA){
		String message;
		if (CA >= 70) {
			message = studentID + ", " + Double.toString(CA) + ", QUALIFIED FOR HONOURS STUDY!";
		}
		else if (CA < 70 && eightMA >= 80) {
			message = studentID + ", " + Double.toString(CA) + ", " + Double.toString(eightMA) + ", MAY HAVE A GOOD CHANCE! Need further assessment!";
		}
		else if (CA < 70 && eightMA >= 70 && eightMA <=79) {
			message = studentID + ", " + Double.toString(CA) + ", " + Double.toString(eightMA) + ", MAY HAVE A CHANCE! Must be carefully reassessed and get the coordinator's special permission!";
		}
		else if (CA < 70 && eightMA < 70) {
			message = studentID + ", " + Double.toString(CA) + ", " + Double.toString(eightMA) + ", DOES NOT QUALIFY FOR HONORS STUDY! Try Masters by course work.";
		}
		else {
			message = "error.";
		}
		return message;
	}
	
	public ArrayList<Integer> generateFlatListOfGrades(LinkedHashMap<String, Integer> gradeStore){
		ArrayList<Integer> scores = new ArrayList<Integer>();
		
		for(String key : gradeStore.keySet()){
			scores.add(gradeStore.get(key));
		}

		return scores;
	}
	
	public int countFails(ArrayList<Integer> input){
		int numFails = 0;
		for (Integer score : input){
			if (score < 50){
				numFails ++;
			}
		}
		return numFails;
	}
	
	
	public String evaluator(String studentID){
		person currPerson = personLookup(studentID);
		LinkedHashMap<String, Integer> userGrades = new LinkedHashMap <String, Integer>();
		userGrades = currPerson.getgradeStorage();
		
		ArrayList<Integer> flatList = new ArrayList<Integer>();
		flatList = generateFlatListOfGrades(userGrades);
		
		if (flatList.size() >= 12) { //If the student exists in our records
			System.out.print("\nDisplaying " + flatList.size() + " results in order of input:\n\t");
			for (int score : flatList) {
				System.out.print(score + " ");
			}
			System.out.print("\nDisplaying the best 8 marks:\n\t");
			ArrayList<Integer> best8 = new ArrayList<Integer>();
			best8 = selectBest8(flatList);
			for (int score : best8) {
				System.out.print(score + " ");
			}
			
			System.out.print("\nDisplaying the course average:\n\t");
			double courseAverage = calculateAverage(flatList);
			System.out.print(courseAverage);
			
			System.out.print("\nDisplaying the average of the best 8 marks:\n\t");
			double eightMarkAverage = calculateAverage(best8);
			System.out.print(eightMarkAverage);
			
			System.out.print("\nDisplaying the amount of fails: \n\t");
			int numOfFails = countFails(flatList);
			System.out.print(numOfFails);
			
			System.out.println("\n[!] Returning evaluation back to client!");
			
			if(numOfFails >5){
				String failDueToFails = studentID + ", " + Double.toString(courseAverage) + ", DOES NOT QUALIFY FOR HONORS STUDY DUE TO " + numOfFails + " FAILS!";
				return failDueToFails;
			}
			else{
				String qualification = qualify(studentID, courseAverage, eightMarkAverage);
				return qualification;
			}
		}
		else {
			String error = "\n[!] This student does not have enough records to perform analysis!";
			return error;
		}
	}
	
}

class person implements Serializable{
	private String ID;
	private String firstName;
	private String lastName;
	private boolean isStudent = false;
	private LinkedHashMap<String, Integer> gradeStorage = new LinkedHashMap<String, Integer>();
	
	public person(String currID, String currfirstName, String currlastName, boolean currisStudent, LinkedHashMap<String, Integer> currgradeStorage){
		ID = currID;
		firstName = currfirstName;
		lastName = currlastName;
		isStudent = currisStudent;
		gradeStorage = currgradeStorage;
	}
	
	public String getpersonID(){
		return ID;
	}
	
	public String getfirstName(){
		return firstName;
	}
	
	public String getlastName(){
		return lastName;
	}
	
	public boolean getisStudent(){
		return isStudent;
	}
	
	public LinkedHashMap<String, Integer> getgradeStorage(){
		return gradeStorage;
	}
	
	public void setgradeStorage(LinkedHashMap<String, Integer> newGrades){
		gradeStorage = newGrades;
	}
	
	public String toString(){
		
		StringBuilder hashmapString = new StringBuilder("{");

		for(String key : gradeStorage.keySet()){
		  hashmapString.append(key + "=" + gradeStorage.get(key) + ", ");
		}
		hashmapString.delete(hashmapString.length()-2, hashmapString.length()).append("}");
		
		String strisStudent = String.valueOf(isStudent);

		return ID + ", " + firstName + ", " + lastName + ", " + strisStudent + ", " + hashmapString; 
	}
}