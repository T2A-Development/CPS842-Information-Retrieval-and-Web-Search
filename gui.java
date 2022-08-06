package test01;
/*
 *** Tusaif Azmat
 *** 500660278
 *** CPS 842 - K-Means Document Clustering System
 */
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.*;

public class gui extends Frame implements ActionListener{
    private JPanel p1, p2, p3, p4;
    private JLabel outputTitle, nameLabel;
    private JTextField nameField;
    private JTextArea ta, commentsArea;
    private JFrame jf;
    private JButton aboutButton, helpButton, computeButton, restartButton, quitButton, clearButton;
    private JScrollPane commentsScrollPane, outputScrollPane;
    

	public static HashMap<String, Double> IDF = new HashMap<String, Double>();
	public static HashMap<String, HashMap<String, Double>> docWeight = new HashMap<String, HashMap<String, Double>>();
    
    private String aboutInfo = "Stop word Added.";
    private String restartInfo = "Showing document Summary...";
    private String quitInfo = "Quitting...";
    private String computeInfo = "Successfully Computed!";
    private String helpInfo = "Stemming done.\n Three files created.\n 1. dictionary.txt\n 2. posting.txt\n 3. title.txt\n";

    public static void main(String[] args){
        new gui();
    }
    //In this method, both the text files that were created previously in Invert.java are scanned
    //to calculate the vector weights of each document
    public void compute() throws IOException{
    	Scanner scanDict = new Scanner(new BufferedReader(new FileReader("src/test01/dictionary.txt")));
		Scanner scanPost = new Scanner(new BufferedReader(new FileReader("src/test01/postings.txt")));

		while(scanDict.hasNext())
		{
			String term = scanDict.next();
			//Finding IDF
			double frequency = scanDict.nextInt();
			double div = 2225/frequency;
			double termIDF = Math.log10(div);
			IDF.put(term, termIDF);
		}
		String next = scanPost.next();
		while(scanPost.hasNext())
		{
			String tempDocID = "";
			HashMap<String, Double> weightList = new HashMap<String, Double>();
			if(next.equals(".D"))
			{
				tempDocID = scanPost.next();
				String next2 = scanPost.next();
				while(!(next2).equals(".D")) {
					String word = next2;
					double freq = scanPost.nextInt();
					double termFreq = 1 + Math.log10(freq);
					double weight = (IDF.get(word))*termFreq;
					weightList.put(word, weight);
					if(scanPost.hasNext())
					{
						next2 = scanPost.next();
					}
					else
					{
						break;
					}
				}
			}
			docWeight.put(tempDocID, weightList);
			//System.out.println("DocID: " + tempDocID + "\n" + weightList);
		}
		@SuppressWarnings("unused")
		Cluster clustering = new Cluster(docWeight);
		scanPost.close();
		scanDict.close();
	}
    // constructor
    public gui(){
        jf = new JFrame("CPS842 Project");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(new GridLayout(1, 2)); //for input and output
        p1 = new JPanel();	//input
        
        p1.setLayout(new GridLayout(7,2)); //for labels, text fields, buttons
        
        p2 = new JPanel();	//output
        p2.setLayout(new BorderLayout());
        
        p3 = new JPanel(); //p3 is a parent panel containing p1 and the successLabel
        p3.setLayout(new GridLayout(2,1)); //first row contains input information (p1), second row contains success message
        
        p4 = new JPanel(); // p4 is a child panel of p2 
        p4.setLayout(new GridLayout(1,2));
        
        p1.setBackground(Color.WHITE);
        p2.setBackground(Color.ORANGE);
        // p4.setBackground(Color.ORANGE);
   
        aboutButton = new JButton("stop word removal");
        helpButton = new JButton("turn on stemming");
        clearButton = new JButton("Clear");
        nameLabel = new JLabel("\tEnter K-value :");

        nameField = new JTextField();

        computeButton = new JButton("Compute"); //was jb2
        restartButton = new JButton("Show Summary");
        quitButton = new JButton("Quit");
        commentsArea = new JTextArea(5,20);
        commentsArea.setBackground(new Color(224,224,224));
        commentsArea.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
        commentsArea.setEditable(false);

        computeButton.addActionListener(this);
        aboutButton.addActionListener(this);
        helpButton.addActionListener(this);
        restartButton.addActionListener(this);
        clearButton.addActionListener(this);
        quitButton.addActionListener(this);
 


        ta = new JTextArea(5,30);	//for actual output
        ta.setEditable(false);
        
        commentsScrollPane = new JScrollPane(commentsArea);
        outputScrollPane = new JScrollPane(ta);
        //outputScrollPane.setPreferredSize(new Dimension(450, 110));
        
       // p1.add(space[0]);
        p1.add(aboutButton);
        p1.add(helpButton);
        p1.add(nameLabel);
        p1.add(nameField);

        p1.add(computeButton);
        //p1.add(space[2]);
        p1.add(restartButton);
        p1.add(clearButton);
        p1.add(quitButton);

        p3.add(p1);

        p3.add(commentsScrollPane);
        jf.add(p3);
        outputTitle = new JLabel("OUTPUT");
        outputTitle.setForeground(Color.BLACK);
        outputTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        p2.add(outputTitle, BorderLayout.NORTH);
  
        p2.add(outputScrollPane, BorderLayout.CENTER);
        p2.add(p4,BorderLayout.SOUTH);        
        jf.add(p2);
        jf.setSize(850, 650);
        jf.setVisible(true);
    }
    public void WriteFileToLabel01()
	{
		try {
			BufferedReader inStream  // Create and open the stream
			= new BufferedReader (new FileReader("src/test01/eval.txt"));
			String line = inStream.readLine(); // Read one line
			while (line != null) {         // While more text
				ta.append(line + "\n"); // Display a line
				line = inStream.readLine();      // Read next line
			}
			inStream.close();        // Close the stream
		} catch (FileNotFoundException e) {
			ta.setText("IOERROR: "+ "src/test01/eval.txt" +" NOT found\n");
			e.printStackTrace();
		} catch (IOException e) {
			ta.setText("IOERROR: " + e.getMessage() + "\n");
			e.printStackTrace();
		}
	}
    public void WriteFileToLabel02()
	{
		try {
			BufferedReader inStream  // Create and open the stream
			= new BufferedReader (new FileReader("src/test01/summaries.txt"));
			String line = inStream.readLine(); // Read one line
			while (line != null) {         // While more text
				ta.append(line + "\n"); // Display a line
				line = inStream.readLine();      // Read next line
			}
			inStream.close();        // Close the stream
		} catch (FileNotFoundException e) {
			ta.setText("IOERROR: "+ "src/test01/eval.txt" +" NOT found\n");
			e.printStackTrace();
		} catch (IOException e) {
			ta.setText("IOERROR: " + e.getMessage() + "\n");
			e.printStackTrace();
		}
	}
   // private class BListen implements ActionListener {
        public void actionPerformed(ActionEvent e) 
        {
        	String buttonClicked = e.getActionCommand();

            //Compute button pressed
            if(buttonClicked == "Compute") 
            {
        		commentsArea.setText("Clustering Data files...");	
        		try {
        			compute();
        			WriteFileToLabel01();
        			commentsArea.setText(computeInfo);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
            }
            //About button pressed
            if(buttonClicked == "stop word removal")
            {
        		Invert.setStop_words(true);
            	commentsArea.setText("Stop word added.");	
            	commentsArea.setText(aboutInfo);
            }
            
            //Help button pressed
            if(buttonClicked == "turn on stemming")
            {
        		Invert.setStemming(true);
        		try {
					Invert.main(null);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	commentsArea.setText("");	
        		commentsArea.setText(helpInfo);
            }
            
            //Show summary button pressed
            if(buttonClicked == "Show Summary")
            {
        		WriteFileToLabel02();
        		commentsArea.setText(restartInfo);
            }
            // Clear button pressed
            if(buttonClicked == "Clear")
            {
        		ta.setText("");
            	commentsArea.setText("");
            }
            // Quit button pressed
            if(buttonClicked == "Quit")
            {
        		commentsArea.setText("");
        		commentsArea.setText(quitInfo);
        		System.exit(0);
            }

        }
}