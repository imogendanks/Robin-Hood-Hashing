//C1431388
//there are a couple of compile warnings, however the program
//should work correctly despite this

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;

public class RobinHood{
	//initialise the application class
	public static void main(String[] args){
		Application theHood = new Application(args);
		theHood.main();
	}
}

class Application{

	//variables needed for transferring the data from the text file
	//and the hashing functions
	public String fileName;
	public String runType;
	public ArrayList fileContent = new ArrayList();
	public String[] theContent;
	public String[][] inputContent;
	public int[][] hashTable;
	public int fileSize;
	public int tableSize;

	public Application(String[] args){
		//read the file name and run type (regular or debug)
		fileName = args[0];
		runType = args[1];
	}

	public void main(){
		//run the correct part of the program depending on the runtype
		if(runType.equals("regular")){
			regular(fileName);
		}

		else if(runType.equals("debug")){
			debug(fileName);
		}
	}

	public void regular(String theFile){
		try{
			//open the file and read content
			BufferedReader buffer = new BufferedReader(new FileReader(theFile));
			String line;

			//transfer all file content to arraylist
			while((line = buffer.readLine()) != null){
				fileContent.add(line);
			}

			//convert arraylist to normal array
			fileSize = fileContent.size();
			theContent = new String[fileSize];
			fileContent.toArray(theContent);
			
			inputContent = new String[fileSize][2];
			inputContent[0][0] = theContent[0];

			//split each row of the text file into seperate strings
			//to be entered into the hash table
			for(int a = 1; a < fileSize; a++){ 
				String[] temp = theContent[a].split(" ");
				for(int b = 0; b < 2; b++){
					inputContent[a][b] = temp[b];
				}
			}
			//begin the hash function
			hash();
		}
		//if the previous code cannot be executed,throw an error
		catch(Exception e){
			System.out.println(e);
		}
	}
	public void hash(){
		//get the table size
		int tableSize = Integer.parseInt(inputContent[0][0]);
		//check table size is less than 1000, else throw an error
		if(tableSize > 1000){
			throw new IndexOutOfBoundsException("Table is Too Big");
		}
		System.out.println("setting hash table size = " + inputContent[0][0]);
		//set the hash table size
		hashTable = new int[tableSize][3];
		
		//fill the hash table 
		System.out.println("initialising table as empty");
		for(int x = 0; x < tableSize; x++){
				hashTable[x][0] = -1;
				hashTable[x][1] = 1;
				hashTable[x][2] = 0;
		}
		
		System.out.println();
		System.out.print("input operations: ");

		//print out the input operations from the file data
		for(int a = 1; a < fileSize; a++){
			if(inputContent[a][0].equals("i")){
				System.out.print("insert ");
			}
			else if(inputContent[a][0].equals("s")){
				System.out.print("simple ");
			}
			else if(inputContent[a][0].equals("S")){
				System.out.print("search ");
			}
		}
		//print out the input data from the file data
		System.out.println();
		System.out.print("input data:       ");
		for(int b = 1; b < fileSize; b++){
			System.out.print(inputContent[b][1] + " ");
		}
		System.out.println("\n");
		
		
		int index;
		int key;
		int psl;
		int tempKey;
		int tempPsl;

		//for every line of the file except the first
		for(int c = 1; c < fileSize; c++){
			//if it is an number to be inserted
			if(inputContent[c][0].equals("i")){
				//retrieve the key, psl and calculate the hash index
				key = Integer.parseInt(inputContent[c][1]);
				index = ((1*key+929)%11311)%tableSize;
				psl = 1;
				System.out.println("insert key = " + key);
				System.out.println("hashes to index = " + index);

				//find next valid index
				//while the hash location is not empty
				while(key > -1){
					System.out.print("at location " + index + " existing key = " + hashTable[index][0] + ", probe length = " + hashTable[index][1] +"; current key = " + key + ", probe length = " + psl + "\n");
					//if the table is full, throw an error
					if(psl > tableSize){
						throw new IndexOutOfBoundsException("Table is Full");
					}
					//compare the psl and key with the exisiting ones in the hash index
					//if they meet the correct requirements for robin hood hashing
					//swap values
					if(psl > hashTable[index][1] || (psl == hashTable[index][1] && key < hashTable[index][0]) || hashTable[index][0] == -1){
						int originalhash = ((1*key+929)%11311)%tableSize;
						if(psl > hashTable[originalhash][2]){
							hashTable[originalhash][2] = psl;
						}
						tempKey	= hashTable[index][0];
						tempPsl = hashTable[index][1];
						hashTable[index][0] = key;
						hashTable[index][1] = psl;
						key = tempKey;
						psl = tempPsl;
					}
					//increment the psl and current index
					psl++;
					index++;
					//loop t index 0 when index = tablesize 
					index = index % tableSize;
				}
				
			}
			//if it is a number to smart search
			else if(inputContent[c][0].equals("S")){
				//the key to be serched for
				int theKey = Integer.parseInt(inputContent[c][1]);
				//calculate the original hash index
				index = ((1*theKey+929)%11311)%tableSize;
				//the start index for smart search is 3 from the original index
				//so index + 2, and the offset from this new location is 0.
				int offset = 0;
				int startIndex = index + 2;
				//print out key and index
				System.out.println("retrieve key (smart) = " + theKey);
				System.out.println("hashes to index = " + index);

				//while the offset is not larger than the table size (to avoid out of bounds)
				while(offset < tableSize){
					//if the max psl is larger than 3 
					if(hashTable[index][2] >= 3){
						//only execute if not checking an index less than the original  index
						//and the offset is less than the max psl - 2 (not checking futher than the max psl)
						if(offset >= -2 && offset < hashTable[index][2]-2) {
							//wrap the current index back to zero when it is the last index 
							int currentIndex = (startIndex + offset)%tableSize;
							System.out.println("check location " + currentIndex);
							//if the search key matches the key in the location in the hash table
							//return the location where the key was found and terminate the loop
							if(hashTable[currentIndex][0] == theKey){
								System.out.println("located key = " + theKey + " at location = " + currentIndex);
								break;
							}
						}
						//increment the offset as int, -int, int+1, -int-1
						//0, 1, -1, 2, -2, 3 -3, etc.
						offset = -offset + (-offset >= 0 ? 1 : 0);
					}

					//if the max psl is less than 3
					else if(hashTable[index][2] < 3){
						//wrap the index to 0 when it reaches the last index in the hash table
						int currentIndex = (startIndex + offset)%tableSize;
						System.out.println("check location " + currentIndex);
						//return index if the key is found
						if(hashTable[currentIndex][0] == theKey){
								System.out.println("located key = " + theKey + " at location = " + currentIndex);
								break;
						}
						//reduce the offset by 1 each time until the original index is met
						offset = offset - 1;


					}
				}
					

			}
			//else if it is a number to be simple searched for
			else if(inputContent[c][0].equals("s")){
				//retrieve the key and calculate the index
				key = Integer.parseInt(inputContent[c][1]);
				index = ((1*key+929)%11311)%tableSize;
				System.out.println("retrieve key (simple) = " + key);
				System.out.println("hashes to index = " + index);
				//while the key does not match the key in the hash table index
				while(hashTable[index][0] != key){
					//increment the index, and wrap at boundaries
					index++;
					index = index % tableSize;
				}
				//if the key is found, return the key and its index location
				System.out.print("located key = " + key + " at location = " + index + "\n");

			}
		}
	}
	//does the same as regular however the file is read and saved differently
	//the hash table is populated here rather than the hash function.
	public void debug(String theFile){
		try{
			BufferedReader buffer = new BufferedReader(new FileReader(theFile));
			String line;

			while((line = buffer.readLine()) != null){
				fileContent.add(line);
			}

			fileSize = fileContent.size();
			theContent = new String[fileSize];
			fileContent.toArray(theContent);
			tableSize = Integer.parseInt(theContent[0]);
			System.out.println("setting hash table size = " + tableSize);
			System.out.println("initialising table from file");
			hashTable = new int[tableSize][3];
			int tempValue;
			int tempCounter = 0;
			int hashCounter = 0;
			String[] temp = {};
			for(int a = 1; a < 4; a++){ 
				temp = theContent[a].split(" +");
				hashCounter = 0;
				for(int b = 0; b < temp.length; b++){
					if(temp[b].equals("")){
						continue;
					}
					else{
						tempValue = Integer.parseInt(temp[b]);
						hashTable[hashCounter][tempCounter] = tempValue;
						hashCounter++;
					}
				}
				tempCounter++;	
			}
			System.out.print("data: ");
			for(int x = 0; x < tableSize; x++){
				System.out.print(hashTable[x][0] + " ");
			}	
			System.out.print("\n");	
			System.out.print("probe length: ");
			for(int x = 0; x < tableSize; x++){
				System.out.print(hashTable[x][1] + " ");
			}
			System.out.print("\n");	
			System.out.print("max probe length: ");
			for(int x = 0; x < tableSize; x++){
				System.out.print(hashTable[x][2] + " ");
			}
			System.out.print("\n");	
			
			inputContent = new String[fileSize][2];

			for(int a = 4; a < fileSize; a++){ 
				temp = theContent[a].split(" ");
				for(int b = 0; b < 2; b++){
					inputContent[a][b] = temp[b];
				}
			}
			System.out.print("input operations: ");

			for(int a = 4; a < fileSize; a++){
			//System.out.println("'" + inputContent[a][0] + "'");
				if(inputContent[a][0].equals("i")){
					System.out.print("insert ");
				}
				else if(inputContent[a][0].equals("s")){
					System.out.print("simple ");
				}
				else if(inputContent[a][0].equals("S")){
					System.out.print("search ");
				}
			}
			System.out.println();
			System.out.print("input data:       ");
			for(int b = 4; b < fileSize; b++){
				System.out.print(inputContent[b][1] + " ");
			}
			System.out.println("\n");

			debughash();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	//very similar to regular hash, however the hash table is not
	//populated here, only values are inserted and searched for
	public void debughash(){
		if(tableSize > 1000){
			throw new IndexOutOfBoundsException("Table is Too Big");
		}		
		
		int index;
		int key;
		int psl;
		int tempKey;
		int tempPsl;

		for(int c = 4; c < fileSize; c++){
			if(inputContent[c][0].equals("i")){
				key = Integer.parseInt(inputContent[c][1]);
				index = ((1*key+929)%11311)%tableSize;
				psl = 1;
				System.out.println("insert key = " + key);
				System.out.println("hashes to index = " + index);

				//find next valid index

				while(key > -1){
					System.out.print("at location " + index + " existing key = " + hashTable[index][0] + ", probe length = " + hashTable[index][1] +"; current key = " + key + ", probe length = " + psl + "\n");
					if(psl > tableSize){
						throw new IndexOutOfBoundsException("Table is Full");
					}
					if(psl > hashTable[index][1] || (psl == hashTable[index][1] && key < hashTable[index][0]) || hashTable[index][0] == -1){
						tempKey	= hashTable[index][0];
						tempPsl = hashTable[index][1];
						hashTable[index][0] = key;
						hashTable[index][1] = psl;
						key = tempKey;
						psl = tempPsl;
					}
					psl++;
					index++;
					index = index % tableSize;
				}
				
			}
			else if(inputContent[c][0].equals("S")){
				int theKey = Integer.parseInt(inputContent[c][1]);
				index = ((1*theKey+929)%11311)%tableSize;
				int offset = 0;
				int startIndex = index + 2;
				System.out.println("retrieve key (smart) = " + theKey);
				System.out.println("hashes to index = " + index);
				while(offset < tableSize){

					if(hashTable[index][2] >= 3){
						if(offset >= -2 && offset < hashTable[index][2]-2) {
							int currentIndex = (startIndex + offset)%tableSize;
							System.out.println("check location " + currentIndex);

							if(hashTable[currentIndex][0] == theKey){
								System.out.println("located key = " + theKey + " at location = " + currentIndex);
								break;
							}
				
						}
						offset = -offset + (-offset >= 0 ? 1 : 0);
					}
					else if(hashTable[index][2] < 3){
						int currentIndex = (startIndex + offset)%tableSize;
						System.out.println("check location " + currentIndex);
						if(hashTable[currentIndex][0] == theKey){
								System.out.println("located key = " + theKey + " at location = " + currentIndex);
								break;
						}
						offset = offset - 1;


					}
					
				}
					
		
			}
			else if(inputContent[c][0].equals("s")){
				key = Integer.parseInt(inputContent[c][1]);
				index = ((1*key+929)%11311)%tableSize;
				System.out.println("retrieve key (simple) = " + key);
				System.out.println("hashes to index = " + index);
				while(hashTable[index][0] != key){
					index++;
					index = index % tableSize;
				}
				System.out.print("located key = " + key + " at location = " + index + "\n");
			}
		}
	}
}