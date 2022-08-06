package test01;
/*
 *** Tusaif Azmat
 *** 500660278
 *** CPS 842 - K-Means Document Clustering System
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class Invert {

	public static HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	public static TreeMap<String, HashMap<String, Integer>> postings = new TreeMap<String, HashMap<String, Integer>>();
	public static boolean stop_words = false;
	public static boolean isStop_words() {
		return stop_words;
	}

	public Invert() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void setStop_words(boolean stop_words) {
		Invert.stop_words = stop_words;
	}

	public static boolean stemming = false;
	public static boolean isStemming() {
		return stemming;
	}

	public static void setStemming(boolean stemming) {
		Invert.stemming = stemming;
	}

	public static void main(String args[]) throws IOException{
		
		FileWriter myDictionary = new FileWriter("src/test01/dictionary.txt");
		FileWriter myPostings = new FileWriter("src/test01/postings.txt");
		FileWriter myTitles = new FileWriter("src/test01/titles.txt");
		
		String word;
		ArrayList<String> stopList = new ArrayList<String>();
		Scanner stopScan = new Scanner(new BufferedReader(new FileReader("src/test01/common_words")));
		Scanner input = new Scanner(System.in);

		//adding stop_words in stopList
		while(stopScan.hasNext())
		{
			word = stopScan.next();
			stopList.add(word);
		}

		stopScan.close();

		//User input for stop word removal
		//System.out.println("Do you want to turn on the stop word removal? (y/n)");
		if (Invert.isStop_words() == true){
			stop_words = true;
		}
		else if(Invert.isStop_words() == false){
			stop_words = false;
		}
		else {
			stop_words = true;
		}
/*		String answer = input.next();
		while(!answer.equals("y") || !answer.equals("n"))
		{
			if(answer.equals("y"))
			{
				stop_words = true;
				System.out.println("Stop word removal: ON");
				break;
			}
			else if(answer.equals("n"))
			{
				stop_words = false;
				System.out.println("Stop word removal: OFF");
				break;
			}
			else
			{
				System.out.println("Invalid answer. Do you want to turn on the stop word removal? (y/n)");
				answer = input.next();
			}
		}*/
		if (Invert.isStemming() == true){
			stemming = true;
		}
		else if(Invert.isStemming() == false){
			stemming = false;
		}
		else {
			stemming = true;
		}
/*		System.out.println("Do you want to turn on stemming? (y/n)");
		answer = input.next();
		while(!answer.equals("y") || !answer.equals("n"))
		{
			if(answer.equals("y"))
			{
				stemming = true;
				System.out.println("Stemming: ON");
				break;
			}
			else if(answer.equals("n"))
			{
				stemming = false;
				System.out.println("Stemming: OFF");
				break;
			}
			else
			{
				System.out.println("Invalid answer. Do you want to turn on stemming? (y/n)");
				answer = input.next();
			}
		}*/
		
		input.close();
		
		File[] fileArray1=new File("src/test01/bbc").listFiles();
		
		int count = 0;
		
		
		for(File f1: fileArray1)
		{
			String category = "";
			count++;
			//System.out.println(count);
			if(count == 1)
			{
				category = "b";
			}
			else if(count == 2)
			{
				category = "e";
			}
			else if(count == 3)
			{
				category = "p";
			}
			else if(count == 4)
			{
				category = "s";
			}
			else if(count == 5)
			{
				category = "t";
			}
			File[] fileArray2=new File("" + f1).listFiles();      
			
			for(File f2: fileArray2) // loop thru all files
			{
				if(f2.getName().endsWith(".txt")) // to deal with the .txt files.
				{                 
					String docID = category + f2.getName();
					docID = docID.replace(".txt", "");
					Scanner titleScanner = new Scanner(f2);
					String title = titleScanner.nextLine();
					myTitles.write(".D " + docID + "\n" + title + "\n\n");
					titleScanner.close();
					Scanner s=new Scanner(f2); // to read the files
					//count++;
					HashMap<String, Integer> pDictionary = new HashMap<String, Integer>();
					ArrayList<String> temp = new ArrayList<String>();
					while(s.hasNext())
					{
						String next = s.next();
						String next2=next.replaceAll("[^a-zA-Z]", "").toLowerCase();
						
						if(stemming == true)
						{
							next2 = stemSteps(next2);
							//System.out.println(next2);
						}
						
						if(next2.length() > 2)
						{
						if(!temp.contains(next2))
						{
							if(!dictionary.containsKey(next2))
							{

								dictionary.put(next2, 1);
								temp.add(next2);

							}

							else
							{

								//int freq = postings.get(term).size();
								int num = dictionary.get(next2);
								dictionary.remove(next2);
								dictionary.put(next2, num+1);
								temp.add(next2);
							}			
						}
						
						if(!pDictionary.containsKey(next2))
						{
							pDictionary.put(next2, 1);
						}
						else if(pDictionary.containsKey(next2))
						{
							int num = pDictionary.get(next2);
							pDictionary.remove(next2);
							pDictionary.put(next2, num+1);
						}
						}
					}
					postings.put(docID, pDictionary);
					s.close();
				}
			}
		}
		
		if(stop_words == true)
		{
			for(int i=0; i < stopList.size(); i++)
			{
				if(dictionary.containsKey(stopList.get(i)))
				{
					dictionary.remove(stopList.get(i));
				}
			}
		}

		ArrayList<String> listVal2 = new ArrayList<String>(postings.keySet());
		if(stop_words == true)
		{
			for(int i=0; i < postings.size(); i++)
			{
				for(int j=0; j < stopList.size(); j++)
				{
					if(postings.get(listVal2.get(i)).containsKey(stopList.get(j)))
					{
						postings.get(listVal2.get(i)).remove(stopList.get(j));
					}
				}
			}
		}
		
		ArrayList<String> listVal = new ArrayList<String>(dictionary.keySet());
		Collections.sort(listVal);
		for (int i = 1; i < listVal.size(); i++) {
			myDictionary.write("\n" + listVal.get(i) + "  " + dictionary.get(listVal.get(i)));
		}
		
		
		System.out.println(listVal2.size());
		for(int i = 0; i<listVal2.size(); i++)
		{
			ArrayList<String> listVal3 = new ArrayList<String>(postings.get(listVal2.get(i)).keySet());
			Collections.sort(listVal3);
			myPostings.write("\n\n.D " + listVal2.get(i));
			for(int j = 1; j<listVal3.size(); j++)
			{
				myPostings.write("\n" + listVal3.get(j) + "  " + postings.get(listVal2.get(i)).get(listVal3.get(j)));
			}
		}

		myDictionary.close();
		myPostings.close();
		myTitles.close();
	}
	
	public static String step1(String term)
	{
		String stem;
		if(term.endsWith("sses"))
		{
			stem = term.substring(0, term.length() - 2);
			return stem;
		}
		else if(term.endsWith("ies"))
		{
			stem = term.substring(0, term.length() - 3) + "y";
			return stem;
		}
		else if(term.endsWith("ss"))
		{
			stem=term;
			return stem;
		}
		else if(term.endsWith("s"))
		{
			stem = term.substring(0, term.length() - 1);
			return stem;
		}
		else if(term.endsWith("ed"))
		{
			stem = term.substring(0, term.length() - 2);
			String stem2;
			if(stem.endsWith("at"))
			{
				stem2 = stem.substring(0, term.length() - 2) + "ate";
				return stem2;
			}
			else if(stem.endsWith("bl"))
			{
				stem2 = stem.substring(0, term.length() - 2) + "ble";
				return stem2;
			}
			else if(stem.endsWith("iz"))
			{
				stem2 = stem.substring(0, term.length() - 2) + "ize";
				return stem2;
			}
			else
			{
				return stem;
			}
		}
		else if(term.endsWith("ing"))
		{
			stem = term.substring(0, term.length() - 2);
			String stem2;
			if(stem.endsWith("at"))
			{
				stem2 = stem.substring(0, term.length() - 2) + "ate";
				return stem2;
			}
			else if(stem.endsWith("bl"))
			{
				stem2 = stem.substring(0, term.length() - 2) + "ble";
				return stem2;
			}
			else if(stem.endsWith("iz"))
			{
				stem2 = stem.substring(0, term.length() - 2) + "ize";
				return stem2;
			}
			else
			{
				return stem;
			}
		}
		else
		{
			return term;
		}
	}

	public static String step2(String term)
	{
		String stem;
		if(term.endsWith("ational"))
		{
			stem = term.replace("ational", "ate");

			return stem;
		}
		if(term.endsWith("tional"))
		{
			stem = term.replace("tional", "tion");
			return stem;
		}
		else if(term.endsWith("enci"))
		{
			stem = term.replace("enci", "ence");
			return stem;
		}
		else if(term.endsWith("anci"))
		{
			stem = term.replace("anci", "ance");
			return stem;
		}
		else if(term.endsWith("izer"))
		{
			stem = term.replace("izer", "ize");
			return stem;
		}
		else if(term.endsWith("abli"))
		{
			stem = term.replace("abli", "able");
			return stem;
		}
		else if(term.endsWith("alli"))
		{
			stem = term.replace("alli", "al");
			return stem;
		}
		else if(term.endsWith("entli"))
		{
			stem = term.replace("entli", "ent");
			return stem;
		}
		else if(term.endsWith("eli"))
		{
			stem = term.replace("eli", "e");
			return stem;
		}
		else if(term.endsWith("ousli"))
		{
			stem = term.replace("ousli", "ous");
			return stem;
		}
		else if(term.endsWith("ization"))
		{
			stem = term.replace("ization", "ize");
			return stem;
		}
		else if(term.endsWith("ation"))
		{
			//stem = term.substring(0, term.length() - 5) + "ate";
			stem = term.replace("ation", "ate");
			return stem;
		}
		else if(term.endsWith("ator"))
		{
			stem = term.replace("ator", "ate");
			return stem;
		}
		else if(term.endsWith("alism"))
		{
			stem = term.replace("alism", "al");
			return stem;
		}
		else if(term.endsWith("iveness"))
		{
			stem = term.replace("iveness", "ive");
			return stem;
		}
		else if(term.endsWith("fulness"))
		{
			stem = term.replace("fulness", "ful");
			return stem;
		}
		else if(term.endsWith("ousness"))
		{
			stem = term.replace("ousness", "ous");
			return stem;
		}
		else if(term.endsWith("aliti"))
		{
			stem = term.replace("aliti", "al");
			return stem;
		}
		else if(term.endsWith("iviti"))
		{
			stem = term.replace("iviti", "ive");
			return stem;
		}
		else if(term.endsWith("biliti"))
		{
			stem = term.replace("biliti", "ble");
			return stem;
		}

		else
		{
			return term;
		}
	}

	public static String step3(String term)
	{
		String stem;
		if(term.endsWith("icate"))
		{
			stem = term.replace("icate", "ic");
			return stem;
		}
		if(term.endsWith("ative"))
		{
			stem = term.replace("ative", "");
			return stem;
		}
		if(term.endsWith("alize"))
		{
			stem = term.replace("alize", "al");
			return stem;
		}
		if(term.endsWith("iciti"))
		{
			stem = term.replace("iciti", "ic");
			return stem;
		}
		if(term.endsWith("ical"))
		{
			stem = term.replace("ical", "ic");
			return stem;
		}
		if(term.endsWith("ful"))
		{
			stem = term.replace("ful", "");
			return stem;
		}
		if(term.endsWith("ness"))
		{
			stem = term.replace("ness", "");
			return stem;
		}
		else
		{
			return term;
		}
	}

	public static String step4(String term)
	{
		String stem;
		if(term.endsWith("ance"))
		{
			stem = term.substring(0, term.length() - 4);
			return stem;
		}
		else if(term.endsWith("ence"))
		{
			stem = term.substring(0, term.length() - 4);
			return stem;
		}
		else if(term.endsWith("er"))
		{
			stem = term.substring(0, term.length() - 2);
			return stem;
		}
		else if(term.endsWith("ic"))
		{
			stem = term.substring(0, term.length() - 2);
			return stem;
		}
		else if(term.endsWith("able"))
		{
			stem = term.substring(0, term.length() - 4);
			return stem;
		}
		else if(term.endsWith("ible"))
		{
			stem = term.substring(0, term.length() - 4);
			return stem;
		}
		else if(term.endsWith("ant"))
		{
			stem = term.substring(0, term.length() - 3);
			return stem;
		}
		else if(term.endsWith("ment"))
		{
			stem = term.substring(0, term.length() - 4);
			return stem;
		}
		else if(term.endsWith("ent"))
		{
			stem = term.substring(0, term.length() - 3);
			return stem;
		}
		else if(term.endsWith("ou"))
		{
			stem = term.substring(0, term.length() - 2);
			return stem;
		}
		else if(term.endsWith("ism"))
		{
			stem = term.substring(0, term.length() - 3);
			return stem;
		}
		else if(term.endsWith("ate"))
		{
			stem = term.substring(0, term.length() - 3) + "e";
			return stem;
		}
		else if(term.endsWith("iti"))
		{
			stem = term.substring(0, term.length() - 3);
			return stem;
		}
		else if(term.endsWith("ous"))
		{
			stem = term.substring(0, term.length() - 3);
			return stem;
		}
		else if(term.endsWith("ive"))
		{
			stem = term.substring(0, term.length() - 3);
			return stem;
		}
		else if(term.endsWith("ize"))
		{
			stem = term.substring(0, term.length() - 3);
			return stem;
		}
		else if(term.endsWith("i"))
		{
			stem = term.substring(0, term.length() - 1);
			return stem;
		}
		else
		{
			return term;
		}
	}

	public static String stemSteps(String term)
	{
		String stepOne = step1(term);
		String stepTwo = step2(stepOne);
		String stepThree = step3(stepTwo);
		String stepFour = step4(stepThree);
		return stepFour;
	}


}