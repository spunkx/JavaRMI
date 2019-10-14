import java.rmi.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface Test extends Remote{
	public void sendMessage(String text) throws RemoteException;
	public void saveGrades(String ID, LinkedHashMap<String, Integer> text) throws RemoteException;
	
	public int verifysuperSecret(String attempt) throws RemoteException;
	
	public String saveuserData(ArrayList<String> text) throws RemoteException;
	public String getMessage(String text) throws RemoteException;
	public String printStudents(String studentID) throws RemoteException;
	
	public ArrayList<String> userLookup(String ID) throws RemoteException;
	public LinkedHashMap<String, Integer> gethashMap(String ID) throws RemoteException;
	
	public String evaluator (String studentID) throws RemoteException; 
}


