import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {
	
	static List<Integer> characterList = new ArrayList<>();
	
	public static int readFromFile() {
		
		Scanner sc = null;
		
		String filePath =  "/Users/apple/Desktop/corpus/";
		
		File file = new File(filePath);
		
		File[] fileList = file.listFiles(); 
		
		for(int i = 0 ; i < 4 ; i++) {
			
			File f1  = fileList[i];
			
			try {
				sc = new Scanner(f1);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			datacleaning(sc);
			
		}
		
		return characterList.size();
	}

	private static void datacleaning(Scanner sc) {
		
		while(sc.hasNextLine()) {
			
			String line = sc.nextLine();
			
			for(int i = 0; i < line.length(); i++) {
				
				char character = line.charAt(i);
				
				if(Character.isAlphabetic(character)) {
					
					character = Character.toLowerCase(character);
					
					characterList.add(character - 97);	
				}
				else if(character == ' '){
					characterList.add(26);
				}
				
			}
			
		}
		
	}

}
