import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.io.*;

/*
	ATTN: The use of this code in any form outside of proof-of-concept work is strictly
	prohibited. This software *will* present many vulnerabilities and should NOT be implemented
	in any public-facing product or service. The authors of this code do not assume ANY liability
	should it be used against these terms.
*/


public class rmi_client {  
   private rmi_client() {}  
   public static void main(String[] args) { 
		Test stub = null;
		Scanner ipGet = new Scanner(System.in);
		System.out.println("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-(*)-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("\tWelcome to the Honours Pre-assessment System!");
		System.out.println("\t A Program by Taylor Spinks & Brandon Gordon");
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-(*)-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		
		System.out.print("[!] Enter the server IP ('localhost' for local): ");
		String IP = ipGet.next();
		
		try {  
			 Registry registry = LocateRegistry.getRegistry(IP); 
			 stub = (Test) registry.lookup("Test"); // Looking up the registry for the remote object 
		} catch (Exception e) {
			 System.err.println("rmi_client exception: " + e.toString()); 
			 e.printStackTrace(); 
		}
		
		if(stub != null){
			try{
				//String dongs = "Server!";
				//stub.sendMessage(dongs);
				//System.out.println(stub.getMessage(dongs));
			
				System.out.println("\n[!]This server requires authentication:\n");
				
				Scanner in = new Scanner(System.in);
				boolean continueQ = true;
				ArrayList<String> currStudent = new ArrayList<String>();
				
				while(continueQ){
						System.out.print("[!] Are you a staff member? Y or N: ");
						char choice = in.next().charAt(0);
					
					if(Character.toLowerCase(choice) == 'y'){
						//this is here for the adduserunit function
						System.out.print("Please enter your staffID: ");
						String ID = in.next();
						currStudent = stub.userLookup(ID);
						//boolean isStudent = true;
						System.out.print("Enter the password (admin123): ");
						String attempt = in.next();
						int getouttaHere = stub.verifysuperSecret(attempt);
						
						if((getouttaHere == -1) || (!currStudent.get(0).equals(ID))){
							//this is very insecure, fix later
							System.out.println("[!] Incorrect StaffID or password!");
							System.exit(0);
						}
						continueQ = false;
					}
					else if(Character.toLowerCase(choice) == 'n'){
						System.out.print("Enter your student ID: ");
						String ID = in.next();
						currStudent = stub.userLookup(ID);
						
						if(!currStudent.get(0).equals(ID)){
							System.out.println("[!] Incorrect login");
							System.exit(0);
						}
						
						continueQ = false;
					}
					else{
					System.out.println("[!] You entered an incorrect value!");
						continueQ = true;
					}
				}
				
				System.out.println("Welcome " + currStudent.get(1) + "!");
				
				continueQ = true;
				while(continueQ){
					int choice = 0;
					System.out.println("\nChoose from the following options:");;
					System.out.print("\t[1] Register a new student.\n"
									+"\t[2] Add units to an existing student.\n"
									+"\t[3] Display evaluations.\n"
									+"\t[9] Exit Application.\n");
					System.out.print("\t> "); 
					choice = inputInt(); //User input is checked to be an int -> desired menu choice
					
					
					boolean isStudent = false;
					
					
					isStudent = Boolean.parseBoolean(currStudent.get(3));
					
					if((choice == 1) && (isStudent == false)){
						registerStudent(stub);
					}
					else if((choice == 1) && (isStudent == true)){
						System.out.println("[!] You must be a staff member to do that!");
					}
					else if(choice == 2){
						addstudentUnits(stub, currStudent.get(0));
					}
					else if(choice == 3){
						System.out.print("Enter Student ID to see their results: \n\t> ");
						String id = in.next();
						String evalutation = stub.evaluator(id);
						System.out.println("\n\t" + evalutation);
					}
					else if(choice == 9){
						System.exit(0);
					}
					else{
						System.out.println("\n[!] Please choose a number within scope!\n");
					}
				
				}
				
			}catch (Exception e){
				System.err.println("rmi_client exception, failed to send message: " + e.toString()); 
				e.printStackTrace();
			}
		}
   }

	static int inputInt(){
	int testVal;
	Scanner scan = new Scanner(System.in);
	while (!scan.hasNextInt()){
		System.out.print("\t[!] Please enter valid input. \n\t> ");
		scan.nextLine();
	}
	testVal = scan.nextInt();
	return testVal;
}
   
	public static void addstudentUnits(Test stub, String ID){
		Scanner in = new Scanner(System.in);
		ArrayList<String> currStudent = new ArrayList<String>();
		try{
			String studentID;
			currStudent = stub.userLookup(ID);
			
			if (currStudent.get(3).equals("true")){
				studentID = currStudent.get(0);
				System.out.println("[!] As you are a student, you can only modify your own grades.");
			}
			else {
				do{				
				System.out.print("\nEnter a valid student ID: ");
				studentID = in.next();
				currStudent = stub.userLookup(studentID);
				}
				while (!currStudent.get(0).equals(studentID));
			}
			
			boolean continueQ = true;
			while(continueQ){
				if(!currStudent.get(0).equals(studentID)){			
					
					continueQ = true;
				}
				else if(currStudent.get(0).equals(studentID)){
					continueQ = false;
				}
				else{
					System.out.println("[!] Incorrect input");
					continueQ = true;
				}
			}

			System.out.print("\nThe current grades exist for " + currStudent.get(1) + " " + currStudent.get(2) + ": ");
			
			LinkedHashMap <String, Integer> setGrades = new LinkedHashMap <String, Integer>();
			setGrades = stub.gethashMap(currStudent.get(0));
			System.out.println(setGrades);
			
			System.out.print("[!] Do you want to add more grades to this student? Y or N: ");
			char choice = in.next().charAt(0);
			
			if(Character.toLowerCase(choice) == 'y'){
				if(setGrades.containsKey(null)){
					setGrades.clear();
				}
				
				int numberOfGrades = setGrades.size();	
				
				System.out.print("\nThere are currently " + numberOfGrades + " entered grades. How many more will you enter?: ");
				int loops = inputInt();
								
				for(int i = 0; i < loops && numberOfGrades < 30; i++){ //loop for as many times as the user asks for but stop at 30
					System.out.print("\nEnter the unit code: ");
					String unit = in.next();
			
					while(setGrades.containsKey(unit)){
						//change last character to thing
						unit += "+";
						
					}
					int score =0;
					boolean acceptableScore = false;
					while (acceptableScore != true){
						System.out.print("Enter the score: ");
						score = inputInt();
						if (score >= 0 && score <= 100){
							acceptableScore = true;
						}
						else {
							System.out.println("[!] You must input a valid score!\n");
						}
					}
					setGrades.put(unit, score);
					numberOfGrades ++;
					
				}
				stub.saveGrades(currStudent.get(0), setGrades);
				
			}
			
		} catch (Exception e){
			System.err.println("rmi_client exception: " + e.toString()); 
			e.printStackTrace(); 
		}
	}

	public static void registerStudent(Test stub){
		try{
			Scanner in = new Scanner(System.in);
			ArrayList<String> loldongs = new ArrayList<String>();
			System.out.print("Enter First Name: ");
			String firstname = in.next();
			
			System.out.print("Enter Last Name: ");
			String lastname = in.next();
			
			boolean continueQ = true;
			boolean isStudent = false;
			String ID = "";
			while(continueQ){
				System.out.print("[!] Is this person a student? Y or N: ");
				char choice = in.next().charAt(0);
				
				if(Character.toLowerCase(choice) == 'y'){
					System.out.print("Enter their StudentID: ");
					//verify if student already exists
					ID = in.next();
					isStudent = true;
					continueQ = false;
					
				}
				else if(Character.toLowerCase(choice) == 'n'){
					System.out.print("Enter their Staff ID: ");
					//verify if staff already exists
					ID = in.next();
					continueQ = false;
				}
				else{
					continueQ = true;
				}
			}
			loldongs.add(ID);
			loldongs.add(firstname);
			loldongs.add(lastname);
			String strisStudent = String.valueOf(isStudent);
			loldongs.add(strisStudent);

			ArrayList<String> currStudent = new ArrayList<String>();
			try{
				currStudent = stub.userLookup(ID);
				if (currStudent.get(0).equals(ID)){
					System.out.println("[!] A student already exists with that studentID! \n\tThey will not be added again.");
				}
				else{
					stub.saveuserData(loldongs);
				}
			} catch (Exception e){
				e.printStackTrace();
			}


			
			
		} catch (Exception e) {
			 System.err.println("rmi_client exception: " + e.toString()); 
			 e.printStackTrace(); 
		}
	}
}