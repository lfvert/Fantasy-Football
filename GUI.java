import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.awt.*;

import javax.swing.*;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;




public class GUI extends JFrame implements ActionListener, WindowListener, MouseListener {
	private JTextField teamName =new JTextField("", 20);// 20 is variable
	private JTextField playerAdd=new JTextField("", 40);
	private JTextField playerDrop=new JTextField("",40);
	private Container cp;
	private Container cpid;
	private JPanel title;
	private int week;
	private JPanel initialDraft;
	private JButton ConfirmButton;
	private JButton doWeek;
	private JButton next;
	private JButton swap;
	private JButton dropButton;
	private JButton back;
	private JButton leaderBoard;
	private JButton restart;
    private JRadioButton easy;
    private JRadioButton medium;
    private JRadioButton hard;
	private Connection conn;
	private Statement statement = null;
	private ResultSet results = null;
	private JList<String> JQBPlayers;
	private JList<String> JRBPlayers;
	private JList<String> JWRPlayers;
	private JList<String> JTEPlayers;
	private JList<String> JAllPlayers;
	private JTabbedPane posTabs;
	private JTabbedPane tabs;
	private ButtonActionListener ButtonListener = new ButtonActionListener();
	private JList<String> cTeam;
	private int qbleft=1;
	private int rbleft=2;
	private int wrleft=3;
	private int teleft=1;
	private Integer[] orderF;
	private Integer[] orderL;
	private JTable teamOn;
	private int round=1;
	private boolean dropPhase=false;
	private String droppedPos="";
	private Boolean[] losers = new Boolean[7];
	private boolean lost=false;
	private int difficulty=1;
	private boolean aiWent=false;

	public GUI() throws Exception
	{
	
	conn = DBConnection.getConnection();
	 cp = getContentPane();
	 cp.setLayout(new GridLayout(1,1,1,1));
	 //make all booleans false
	 Arrays.fill(losers, Boolean.FALSE);
	 
	 //resets players
	 DBConnection.resetPlayers(conn);
	 Scoring.resetTeams(conn);
	 this.arraySplit();
/*	 
	String className = getLookAndFeelClassName("Nimbus");
	UIManager.setLookAndFeel(className); 	 */
	 
	 
	 //Title screen
	 title();
	   //set main setting of CP
	   setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit program if close button clicked
	   setTitle("Draft Queens"); // "this" JFrame sets title
	   setSize(500, 500);         // "this" JFrame sets initial size
	   setVisible(true);    
	}
	/*
	public static String getLookAndFeelClassName(String nameSnippet) 
	{
    LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
    for (LookAndFeelInfo info : plafs) {
        if (info.getName().contains(nameSnippet)) {
            return info.getClassName();
        }
    }
    return null;
}*/
public void title()
{
	setSize(500, 500); 
	title =new JPanel();
	 title.setLayout(new GridLayout(2,1));
	 //first item in grid
	 //second item in grid
	 JPanel teamNamer=new JPanel();
	 teamNamer.setLayout(new FlowLayout());
	 teamNamer.add(new JLabel("Team Name:"));
	 teamName.setEditable(true);
	 teamNamer.add(teamName);
	 JButton teamConfirm=new JButton("Confirm");
	 teamNamer.add(teamConfirm);
	 teamConfirm.addActionListener(this);
	 //teamName.addActionListener(this);
	 title.add(teamNamer);
	 //third item in the grid
	 JPanel leader=new JPanel();
	 leader.setLayout(new FlowLayout());
	 leaderBoard=new JButton("LeaderBoard");
	 leaderBoard.addActionListener(ButtonListener);
	 leader.add(leaderBoard);
	 
    easy=new JRadioButton("Random",true);
    easy.addActionListener(ButtonListener);
    medium=new JRadioButton("Medium");
    medium.addActionListener(ButtonListener);
    hard=new JRadioButton("Hard");
    hard.addActionListener(ButtonListener);
    leader.add(easy);
    leader.add(medium);
    leader.add(hard);
    JButton help=new JButton("Help");
    String helpStr = "Welcome to Draft Queens Fantasy Football Simulation! This simulation allows you to draft a fantasy team and\n" +
"proceed through an elimination league. Every week, the team with the lowest cumulative point total will be eliminated.\n" +
"The points are calculated given the stats that your team showcased on a random week. The game proceeds in the following steps.\n" +
"   1. To begin the game, type in a team name for your team (20 characters or less). Select the level of difficulty of the AIs\n" +
"   you would like to play against.\n\n" +
"   2. The draft order will be randomly chosen and you will draft 1 player every round. The AIs will make their choices\n" +
"   and you will then again select another player until your team has no open positions.\n\n" +
"   3. Once the draft is complete, you will proceed to the first week. Every week you will have the opportunity\n" +
" to drop players on your team and swap them with another player who has not been selected onto a team\n\n" +
"   4. After every week, there will be a transition screen that will display how all teams performed during that week.\n" +
" The team that has the lowest cumulative point total will be dismissed from the league.\n\n" +
"   5. Upon being eliminated or fortunate enough to defeat all AI opponents, you will be directed to a leaderboard screen.\n" +
"This leaderboard will hold the hall of fame of playing the Draft Queens’ Fantasy Football Simulation and their teams and scores.\n\n";
    help.addActionListener(new ActionListener()
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		JOptionPane.showMessageDialog(new JFrame(),helpStr,"Help",JOptionPane.PLAIN_MESSAGE);
    	}
    });
    leader.add(help);
	 title.add(leader);
	 cp.add(title);
}
	
public void initDraft()
	{
	 //this is initial draft
	 setSize(1200, 500); 
    initialDraft=new JPanel();
     initialDraft.setLayout(new GridLayout(2,1));
     tabs =new JTabbedPane();
     
     //tab1
     
     JPanel hTab=new JPanel();
     hTab.setLayout(new GridLayout(1,1));
	 
     ArrayList<String> tempp=DBConnection.getAllTeamPosPlayers(conn,1);
	 if(tempp.size()>0)
	 {
	 JTable cTable=new JTable(tempp.size()+1,8);
	 cTable.setValueAt("Player Id",0,0);
	 cTable.setValueAt("Position",0,1);
	 cTable.setValueAt("First Name",0,2);
	 cTable.setValueAt("Last Name",0,3);
	 cTable.setValueAt("Height(Inches)",0,4);
	 cTable.setValueAt("Weight(lbs)",0,5);
	 cTable.setValueAt("College",0,6);
	 cTable.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			cTable.setValueAt(values[y],x+1,y);
		}
	}
    cTable.setTableHeader(null);
	cTable.setEnabled(false);
     hTab.add(cTable);
	 }
     tabs.addTab(teamName.getText(), hTab);
     //AI starts at 2
     //AI one
     JPanel a1Tab=new JPanel();
     a1Tab.setLayout(new GridLayout(1,1));
     
     tempp=DBConnection.getAllTeamPosPlayers(conn,2);
    if(tempp.size()>0)
	 {
	 JTable a1Table=new JTable(tempp.size()+1,8);
	 a1Table.setValueAt("Player Id",0,0);
	 a1Table.setValueAt("Position",0,1);
	 a1Table.setValueAt("First Name",0,2);
	 a1Table.setValueAt("Last Name",0,3);
	 a1Table.setValueAt("Height(Inches)",0,4);
	 a1Table.setValueAt("Weight(lbs)",0,5);
	 a1Table.setValueAt("College",0,6);
	 a1Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a1Table.setValueAt(values[y],x+1,y);
		}
	}
    a1Table.setTableHeader(null);
	a1Table.setEnabled(false);
     a1Tab.add(a1Table);
	 }
     tabs.addTab("Ai1", a1Tab);
      
     //AI two
     JPanel a2Tab=new JPanel();
     a2Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,3);
      if(tempp.size()>0)
	 {
	 JTable a2Table=new JTable(tempp.size()+1,8);
	 a2Table.setValueAt("Player Id",0,0);
	 a2Table.setValueAt("Position",0,1);
	 a2Table.setValueAt("First Name",0,2);
	 a2Table.setValueAt("Last Name",0,3);
	 a2Table.setValueAt("Height(Inches)",0,4);
	 a2Table.setValueAt("Weight(lbs)",0,5);
	 a2Table.setValueAt("College",0,6);
	 a2Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a2Table.setValueAt(values[y],x+1,y);
		}
	}
    a2Table.setTableHeader(null);
	a2Table.setEnabled(false);
     a2Tab.add(a2Table);
	 }
     tabs.addTab("Ai2", a2Tab);
     //AI three
     JPanel a3Tab=new JPanel();
     a3Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,4);
      if(tempp.size()>0)
	 {
	 JTable a3Table=new JTable(tempp.size()+1,8);
	 a3Table.setValueAt("Player Id",0,0);
	 a3Table.setValueAt("Position",0,1);
	 a3Table.setValueAt("First Name",0,2);
	 a3Table.setValueAt("Last Name",0,3);
	 a3Table.setValueAt("Height(Inches)",0,4);
	 a3Table.setValueAt("Weight(lbs)",0,5);
	 a3Table.setValueAt("College",0,6);
	 a3Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a3Table.setValueAt(values[y],x+1,y);
		}
	}
    a3Table.setTableHeader(null);
	a3Table.setEnabled(false);
     a3Tab.add(a3Table);
	 }
     tabs.addTab("Ai3", a3Tab);
      //AI four
     JPanel a4Tab=new JPanel();
     a4Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,5);
      if(tempp.size()>0)
	 {
	 JTable a4Table=new JTable(tempp.size()+1,8);
	 a4Table.setValueAt("Player Id",0,0);
	 a4Table.setValueAt("Position",0,1);
	 a4Table.setValueAt("First Name",0,2);
	 a4Table.setValueAt("Last Name",0,3);
	 a4Table.setValueAt("Height(Inches)",0,4);
	 a4Table.setValueAt("Weight(lbs)",0,5);
	 a4Table.setValueAt("College",0,6);
	 a4Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a4Table.setValueAt(values[y],x+1,y);
		}
	}
    a4Table.setTableHeader(null);
	a4Table.setEnabled(false);
     a4Tab.add(a4Table);
	 }
     tabs.addTab("Ai4", a4Tab);
     //AI five
     JPanel a5Tab=new JPanel();
     a5Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,6);
      if(tempp.size()>0)
	 {
	 JTable a5Table=new JTable(tempp.size()+1,8);
	 a5Table.setValueAt("Player Id",0,0);
	 a5Table.setValueAt("Position",0,1);
	 a5Table.setValueAt("First Name",0,2);
	 a5Table.setValueAt("Last Name",0,3);
	 a5Table.setValueAt("Height(Inches)",0,4);
	 a5Table.setValueAt("Weight(lbs)",0,5);
	 a5Table.setValueAt("College",0,6);
	 a5Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a5Table.setValueAt(values[y],x+1,y);
		}
	}
    a5Table.setTableHeader(null);
	a5Table.setEnabled(false);
     a5Tab.add(a5Table);
	 }
     tabs.addTab("Ai5", a5Tab);
        //AI six
     JPanel a6Tab=new JPanel();
     a6Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,7);
      if(tempp.size()>0)
	 {
	 JTable a6Table=new JTable(tempp.size()+1,8);
	 a6Table.setValueAt("Player Id",0,0);
	 a6Table.setValueAt("Position",0,1);
	 a6Table.setValueAt("First Name",0,2);
	 a6Table.setValueAt("Last Name",0,3);
	 a6Table.setValueAt("Height(Inches)",0,4);
	 a6Table.setValueAt("Weight(lbs)",0,5);
	 a6Table.setValueAt("College",0,6);
	 a6Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a6Table.setValueAt(values[y],x+1,y);
		}
	}
    a6Table.setTableHeader(null);
	a6Table.setEnabled(false);
     a6Tab.add(a6Table);
	 }
     tabs.addTab("Ai6", a6Tab);
         //AI seven
     JPanel a7Tab=new JPanel();
     a7Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,8);
     if(tempp.size()>0)
	 {
	 JTable a7Table=new JTable(tempp.size()+1,8);
	 a7Table.setValueAt("Player Id",0,0);
	 a7Table.setValueAt("Position",0,1);
	 a7Table.setValueAt("First Name",0,2);
	 a7Table.setValueAt("Last Name",0,3);
	 a7Table.setValueAt("Height(Inches)",0,4);
	 a7Table.setValueAt("Weight(lbs)",0,5);
	 a7Table.setValueAt("College",0,6);
	 a7Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a7Table.setValueAt(values[y],x+1,y);
		}
	}
    a7Table.setTableHeader(null);
	a7Table.setEnabled(false);
     a7Tab.add(a7Table);
	 }
     tabs.addTab("Ai7", a7Tab);
      
      
      //PLayer tab
     //tab 1 of Players Tab
     ArrayList<String> temp;
     if(qbleft==1)
     {
        //getPosPlayers removed
     temp=DBConnection.getAvailablePosPlayers(conn,true,false,false,false,1);
     String[] QBPlayers =temp.toArray(new String[temp.size()]);
     JQBPlayers=new JList<String>(QBPlayers);
     JQBPlayers.addMouseListener(this);
     JScrollPane JscrollQBPlayers=new JScrollPane(JQBPlayers);
     posTabs=new JTabbedPane();
     posTabs.add("QB",JscrollQBPlayers);
     }
     //tab 2
     if(rbleft>0)
     {
     temp=DBConnection.getAvailablePosPlayers(conn,false,true,false,false,1);
     String[] RBPlayers =temp.toArray(new String[temp.size()]);
     JRBPlayers=new JList<String>(RBPlayers);
     JRBPlayers.addMouseListener(this);
     JScrollPane JscrollRBPlayers=new JScrollPane(JRBPlayers);
     posTabs.add("RB",JscrollRBPlayers);
     }
     //tab 3
     if(wrleft>0)
     {
     temp=DBConnection.getAvailablePosPlayers(conn,false,false,true,false,1);
     String[] WRPlayers =temp.toArray(new String[temp.size()]);
     JWRPlayers=new JList<String>(WRPlayers);
     JWRPlayers.addMouseListener(this);
     JScrollPane JscrollWRPlayers=new JScrollPane(JWRPlayers);
     posTabs.add("WR",JscrollWRPlayers);
     }
     //tab 4
     if(teleft>0)
     {
        //getPosPlayers removed below
     temp=DBConnection.getAvailablePosPlayers(conn,false,false,false,true,1);
     String[] TEPlayers =temp.toArray(new String[temp.size()]);
     JTEPlayers=new JList<String>(TEPlayers);
     JTEPlayers.addMouseListener(this);
     JScrollPane JscrollTEPlayers=new JScrollPane(JTEPlayers);
     posTabs.add("TE",JscrollTEPlayers);
     }
     //tab 5
     boolean qbl=true;
     boolean rbl=true;
     boolean wrl=true;
     boolean tel=true;
     int count=4;
     if(qbleft== 0)
     {
     	qbl=false;
     	count--;
     }
     if(rbleft == 0)
     {
     	rbl=false;
     	count--;
     }
     if(wrleft== 0)
     {
     	wrl=false;
     	count--;
     }
     if(teleft== 0)
     {
     	tel=false;
     	count--;
     }
     if(count!=0)
     {
        //getPosPlayers() updates below
     temp=DBConnection.getAvailablePosPlayers(conn,qbl,rbl,wrl,tel,count);
     String[] AllPlayers =temp.toArray(new String[temp.size()]);
     JAllPlayers=new JList<String>(AllPlayers);
     JAllPlayers.addMouseListener(this);
     JScrollPane JscrollAllPlayers=new JScrollPane(JAllPlayers);
     posTabs.add("ALL",JscrollAllPlayers);
     }
     else
     {
     	posTabs.add("Players",new JLabel("no more players fit on your team"));
     }
     
     JPanel aPlayers= new JPanel();
     aPlayers.setLayout(new GridLayout());
     aPlayers.add(posTabs);
     
     tabs.addTab("Players", aPlayers);
     initialDraft.add(tabs);
     
     
     
     
     JPanel botScreen=new JPanel();
     botScreen.setLayout(new GridLayout(1,2));
     JPanel bot=new JPanel();
     bot.setLayout(new GridLayout(2,1));
     
     JPanel adder=new JPanel();
     adder.setLayout(new FlowLayout());
     adder.add(new JLabel("Add"));
     playerAdd.setEditable(false);
     //Confirm Button
     ConfirmButton= new JButton("Confirm");
     ConfirmButton.addActionListener(ButtonListener);
     adder.add(playerAdd);
     adder.add(ConfirmButton);
     bot.add(adder);
     JPanel turn =new JPanel();
     turn.setLayout(new FlowLayout());
     turn.add(new JLabel("Turn:"));
     teamName.setEditable(false);
     turn.add(teamName);
     turn.add(new JLabel("Round"));
     bot.add(turn);
     botScreen.add(bot);
	//remaining players
	JPanel remainpos=new JPanel();
	remainpos.setLayout(new GridLayout(6,2));
	remainpos.add(new JLabel("Remaining"));
	remainpos.add(new JLabel("Players"));
	remainpos.add(new JLabel("QB"));
	JTextField qbn=new JTextField(""+qbleft,1);
	qbn.setEditable(false);
	remainpos.add(qbn);
	remainpos.add(new JLabel("RB"));
	JTextField rbn=new JTextField(""+rbleft,1);
	rbn.setEditable(false);
	remainpos.add(rbn);
	remainpos.add(new JLabel("WR"));
	JTextField wrn=new JTextField(""+wrleft,1);
	wrn.setEditable(false);
	remainpos.add(wrn);
	remainpos.add(new JLabel("TE"));
	JTextField ten=new JTextField(""+teleft,1);
	ten.setEditable(false);
	remainpos.add(ten);
	int remainingPlayers=qbleft+rbleft+wrleft+teleft;
	if(remainingPlayers==0)
	{
		doWeek=new JButton("Do Week");
		doWeek.addActionListener(ButtonListener);
		remainpos.add(doWeek);
	}
	botScreen.add(remainpos);
     initialDraft.add(botScreen);
        cpid.add(initialDraft);
		
	}
	public void arraySplit()
	{
		int [] temp=Draft.getDraftOrder(8);
		ArrayList<Integer> tempF=new ArrayList<Integer>();
		ArrayList<Integer> tempB=new ArrayList<Integer>();
		boolean reachedP=false;
		for(int x=0;x<temp.length;x++)
		{
			if(reachedP)
			{
				tempB.add(temp[x]);
			}
			if(temp[x]==0)
			{
				reachedP=true;
			}
			if(!reachedP)
			{
				tempF.add(temp[x]);
			}
		}
		orderF=tempF.toArray(new Integer[tempF.size()]);
		orderL=tempB.toArray(new Integer[tempB.size()]);
	}
public void swapDraft()
	{
	 //this is initial draft
    initialDraft=new JPanel();
     initialDraft.setLayout(new GridLayout(2,1));
     tabs =new JTabbedPane();
     
     //tab1
     
     JPanel hTab=new JPanel();
     hTab.setLayout(new GridLayout(1,1));
     ArrayList<String> tempp=DBConnection.getAllTeamPosPlayers(conn,1);
     
     String[] inputTemp=tempp.toArray(new String[tempp.size()]);
     String[] input=new String[inputTemp.length];
     for(int x=0;x<inputTemp.length;x++)
     {
     	String[] tempSplit=inputTemp[x].split("\\|");
     	String val="";
     	for(int y=0;y<tempSplit.length;y++)
     	{
     		if(y==0)
     		{
     			val=tempSplit[y];
     		}
     		else
     		{
     		val=val+" "+tempSplit[y];
     		}
     	}
     	input[x]=val;
     }
     cTeam=new JList<String>(input);
     cTeam.addMouseListener(this);
     hTab.add(cTeam);
     
     
     tabs.addTab(teamName.getText(), hTab);
     if(dropPhase)
	 {
     //AI starts at 2
    //AI starts at 2
	if(!losers[0])
	{
     //AI one
     JPanel a1Tab=new JPanel();
     a1Tab.setLayout(new GridLayout(1,1));
     
     tempp=DBConnection.getAllTeamPosPlayers(conn,2);
    if(tempp.size()>0)
	 {
	 JTable a1Table=new JTable(tempp.size()+1,8);
	 a1Table.setValueAt("Player Id",0,0);
	 a1Table.setValueAt("Position",0,1);
	 a1Table.setValueAt("First Name",0,2);
	 a1Table.setValueAt("Last Name",0,3);
	 a1Table.setValueAt("Height(Inches)",0,4);
	 a1Table.setValueAt("Weight(lbs)",0,5);
	 a1Table.setValueAt("College",0,6);
	 a1Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a1Table.setValueAt(values[y],x+1,y);
		}
	}
    a1Table.setTableHeader(null);
	a1Table.setEnabled(false);
     a1Tab.add(a1Table);
	 }
     tabs.addTab("Ai1", a1Tab);
	 }
     //AI two
	 if(!losers[1])
	 {
     JPanel a2Tab=new JPanel();
     a2Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,3);
      if(tempp.size()>0)
	 {
	 JTable a2Table=new JTable(tempp.size()+1,8);
	 a2Table.setValueAt("Player Id",0,0);
	 a2Table.setValueAt("Position",0,1);
	 a2Table.setValueAt("First Name",0,2);
	 a2Table.setValueAt("Last Name",0,3);
	 a2Table.setValueAt("Height(Inches)",0,4);
	 a2Table.setValueAt("Weight(lbs)",0,5);
	 a2Table.setValueAt("College",0,6);
	 a2Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a2Table.setValueAt(values[y],x+1,y);
		}
	}
    a2Table.setTableHeader(null);
	a2Table.setEnabled(false);
     a2Tab.add(a2Table);
	 }
     tabs.addTab("Ai2", a2Tab);
	 }
     //AI three
	 if(!losers[2])
	 {
     JPanel a3Tab=new JPanel();
     a3Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,4);
      if(tempp.size()>0)
	 {
	 JTable a3Table=new JTable(tempp.size()+1,8);
	 a3Table.setValueAt("Player Id",0,0);
	 a3Table.setValueAt("Position",0,1);
	 a3Table.setValueAt("First Name",0,2);
	 a3Table.setValueAt("Last Name",0,3);
	 a3Table.setValueAt("Height(Inches)",0,4);
	 a3Table.setValueAt("Weight(lbs)",0,5);
	 a3Table.setValueAt("College",0,6);
	 a3Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a3Table.setValueAt(values[y],x+1,y);
		}
	}
    a3Table.setTableHeader(null);
	a3Table.setEnabled(false);
     a3Tab.add(a3Table);
	 }
     tabs.addTab("Ai3", a3Tab);
	 }
      //AI four
	  if(!losers[3])
	  {
     JPanel a4Tab=new JPanel();
     a4Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,5);
      if(tempp.size()>0)
	 {
	 JTable a4Table=new JTable(tempp.size()+1,8);
	 a4Table.setValueAt("Player Id",0,0);
	 a4Table.setValueAt("Position",0,1);
	 a4Table.setValueAt("First Name",0,2);
	 a4Table.setValueAt("Last Name",0,3);
	 a4Table.setValueAt("Height(Inches)",0,4);
	 a4Table.setValueAt("Weight(lbs)",0,5);
	 a4Table.setValueAt("College",0,6);
	 a4Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a4Table.setValueAt(values[y],x+1,y);
		}
	}
    a4Table.setTableHeader(null);
	a4Table.setEnabled(false);
     a4Tab.add(a4Table);
	 }
     tabs.addTab("Ai4", a4Tab);
	 }
     //AI five
	 if(!losers[4])
	 {
     JPanel a5Tab=new JPanel();
     a5Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,6);
      if(tempp.size()>0)
	 {
	 JTable a5Table=new JTable(tempp.size()+1,8);
	 a5Table.setValueAt("Player Id",0,0);
	 a5Table.setValueAt("Position",0,1);
	 a5Table.setValueAt("First Name",0,2);
	 a5Table.setValueAt("Last Name",0,3);
	 a5Table.setValueAt("Height(Inches)",0,4);
	 a5Table.setValueAt("Weight(lbs)",0,5);
	 a5Table.setValueAt("College",0,6);
	 a5Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a5Table.setValueAt(values[y],x+1,y);
		}
	}
    a5Table.setTableHeader(null);
	a5Table.setEnabled(false);
     a5Tab.add(a5Table);
	 }
     tabs.addTab("Ai5", a5Tab);
	 }
        //AI six
	if(!losers[5])
	{
		 JPanel a6Tab=new JPanel();
		 a6Tab.setLayout(new GridLayout(1,1));
		 tempp=DBConnection.getAllTeamPosPlayers(conn,7);
		  if(tempp.size()>0)
		 {
		 JTable a6Table=new JTable(tempp.size()+1,8);
		 a6Table.setValueAt("Player Id",0,0);
		 a6Table.setValueAt("Position",0,1);
		 a6Table.setValueAt("First Name",0,2);
		 a6Table.setValueAt("Last Name",0,3);
		 a6Table.setValueAt("Height(Inches)",0,4);
		 a6Table.setValueAt("Weight(lbs)",0,5);
		 a6Table.setValueAt("College",0,6);
		 a6Table.setValueAt("NFL Team",0,7);
		 
		 for(int x=0;x<tempp.size();x++)
		{ 
			String [] values=tempp.get(x).split("\\|");
			for(int y=0;y<values.length;y++)
			{
				a6Table.setValueAt(values[y],x+1,y);
			}
		}
		a6Table.setTableHeader(null);
		a6Table.setEnabled(false);
		 a6Tab.add(a6Table);
		 }
		 tabs.addTab("Ai6", a6Tab);
	 }
	 if(!losers[6])
	 {
         //AI seven
     JPanel a7Tab=new JPanel();
     a7Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,8);
     if(tempp.size()>0)
	 {
	 JTable a7Table=new JTable(tempp.size()+1,8);
	 a7Table.setValueAt("Player Id",0,0);
	 a7Table.setValueAt("Position",0,1);
	 a7Table.setValueAt("First Name",0,2);
	 a7Table.setValueAt("Last Name",0,3);
	 a7Table.setValueAt("Height(Inches)",0,4);
	 a7Table.setValueAt("Weight(lbs)",0,5);
	 a7Table.setValueAt("College",0,6);
	 a7Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a7Table.setValueAt(values[y],x+1,y);
		}
	}
    a7Table.setTableHeader(null);
	a7Table.setEnabled(false);
     a7Tab.add(a7Table);
	 }
     tabs.addTab("Ai7", a7Tab);
	 }
	}
      //PLayer tab
     //tab 1 of Players Tab
     ArrayList<String> temp;
     //getPosPlayers() removed below
     if(qbleft>0)
     {
	     temp=DBConnection.getAvailablePosPlayers(conn,true,false,false,false,1);
	     String[] QBPlayers =temp.toArray(new String[temp.size()]);
	     JQBPlayers=new JList<String>(QBPlayers);
	     JQBPlayers.addMouseListener(this);
	     JScrollPane JscrollQBPlayers=new JScrollPane(JQBPlayers);
	     posTabs=new JTabbedPane();
	     posTabs.add("QB",JscrollQBPlayers);
     }
     //tab 2
   if(rbleft>0)
     {
	     temp=DBConnection.getAvailablePosPlayers(conn,false,true,false,false,1);
	     String[] RBPlayers =temp.toArray(new String[temp.size()]);
	     JRBPlayers=new JList<String>(RBPlayers);
	     JRBPlayers.addMouseListener(this);
	     JScrollPane JscrollRBPlayers=new JScrollPane(JRBPlayers);
	     posTabs.add("RB",JscrollRBPlayers);
     }
     //tab 3
     if(wrleft>0)
     {
	     temp=DBConnection.getAvailablePosPlayers(conn,false,false,true,false,1);
	     String[] WRPlayers =temp.toArray(new String[temp.size()]);
	     JWRPlayers=new JList<String>(WRPlayers);
	     JWRPlayers.addMouseListener(this);
	     JScrollPane JscrollWRPlayers=new JScrollPane(JWRPlayers);
	     posTabs.add("WR",JscrollWRPlayers);
     }
     //tab 4
     //getPosPlayers removed
     if(teleft>0)
     {
	     temp=DBConnection.getAvailablePosPlayers(conn,false,false,false,true,1);
	     String[] TEPlayers =temp.toArray(new String[temp.size()]);
	     JTEPlayers=new JList<String>(TEPlayers);
	     JTEPlayers.addMouseListener(this);
	     JScrollPane JscrollTEPlayers=new JScrollPane(JTEPlayers);
	     posTabs.add("TE",JscrollTEPlayers);
     }
     //tab 5

     
     
     JPanel aPlayers= new JPanel();
     aPlayers.setLayout(new GridLayout());
     aPlayers.add(posTabs);
     if(dropPhase==false)
     {
     	tabs.addTab("Players", aPlayers);
     }
     initialDraft.add(tabs);
     //bottom screen
     JPanel swapBot=new JPanel();
     swapBot.setLayout(new GridLayout(2,1));
     JPanel dropEntry=new JPanel();
     dropEntry.setLayout(new FlowLayout());
     //drop thing
     if(dropPhase)
     {
	     dropEntry.add(new JLabel("Drop: "));
	     dropEntry.add(playerDrop);
	     playerDrop.setEditable(false);
	     dropButton=new JButton("drop");
	     dropButton.addActionListener(ButtonListener);
	     dropEntry.add(dropButton);
	     swapBot.add(dropEntry);
     }
     else
     {
	     JPanel addEntry=new JPanel();
	     addEntry.setLayout(new FlowLayout());
	     //swap thing
	     addEntry.add(new JLabel("Add: "));
	     addEntry.add(playerAdd);
	     swap=new JButton("Swap");
	     swap.addActionListener(ButtonListener);
	     addEntry.add(swap);
	     swapBot.add(addEntry);
	     tabs.setSelectedIndex(1);
     }
     JPanel turnEntry=new JPanel();
     turnEntry.setLayout(new FlowLayout());
     //turn thing
     turnEntry.add(new JLabel("Turn: "+ teamName.getText()+"    Round: "+round));
     if(dropPhase)
     {
     turnEntry.add(doWeek);
     }
     swapBot.add(turnEntry);
     initialDraft.add(swapBot);
     
        cpid.add(initialDraft);
	}    
	
public void winner()
{
	JPanel win=new JPanel();
	win.setLayout(new GridLayout(2,1));
	
	
	//winner
	ArrayList<String> tempp=Scoring.getRoundStats(conn,true);
		JTable sList=new JTable(tempp.size()+1,2);
		sList.setValueAt("Team",0,0);
		sList.setValueAt("Score",0,1);
		for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			sList.setValueAt(values[y],x+1,y);
		}
	}
	sList.setTableHeader(null);
	sList.setEnabled(false);
     		tabs.addTab("Scores",sList);
     //tab1
    JPanel hTab=new JPanel();
     hTab.setLayout(new GridLayout(1,1));
	 
     tempp=DBConnection.getAllTeamPosPlayers(conn,1);
	 if(tempp.size()>0)
	 {
	 JTable cTable=new JTable(tempp.size()+1,8);
	 cTable.setValueAt("Player Id",0,0);
	 cTable.setValueAt("Position",0,1);
	 cTable.setValueAt("First Name",0,2);
	 cTable.setValueAt("Last Name",0,3);
	 cTable.setValueAt("Height(Inches)",0,4);
	 cTable.setValueAt("Weight(lbs)",0,5);
	 cTable.setValueAt("College",0,6);
	 cTable.setValueAt("NFL Team",0,7);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			cTable.setValueAt(values[y],x+1,y);
		}
	}
    cTable.setTableHeader(null);
	cTable.setEnabled(false);
     hTab.add(cTable);
	 }
     tabs.addTab(teamName.getText(), hTab);
     //AI starts at 2
     //AI one
     JPanel a1Tab=new JPanel();
     a1Tab.setLayout(new GridLayout(1,1));
     
     tempp=DBConnection.getAllTeamPosPlayers(conn,2);
    if(tempp.size()>0)
	 {
	 JTable a1Table=new JTable(tempp.size()+1,8);
	 a1Table.setValueAt("Player Id",0,0);
	 a1Table.setValueAt("Position",0,1);
	 a1Table.setValueAt("First Name",0,2);
	 a1Table.setValueAt("Last Name",0,3);
	 a1Table.setValueAt("Height(Inches)",0,4);
	 a1Table.setValueAt("Weight(lbs)",0,5);
	 a1Table.setValueAt("College",0,6);
	 a1Table.setValueAt("NFL Team",0,7);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a1Table.setValueAt(values[y],x+1,y);
		}
	}
    a1Table.setTableHeader(null);
	a1Table.setEnabled(false);
     a1Tab.add(a1Table);
	 }
     tabs.addTab("Ai1", a1Tab);
      
     //AI two
     JPanel a2Tab=new JPanel();
     a2Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,3);
      if(tempp.size()>0)
	 {
	 JTable a2Table=new JTable(tempp.size()+1,8);
	 a2Table.setValueAt("Player Id",0,0);
	 a2Table.setValueAt("Position",0,1);
	 a2Table.setValueAt("First Name",0,2);
	 a2Table.setValueAt("Last Name",0,3);
	 a2Table.setValueAt("Height(Inches)",0,4);
	 a2Table.setValueAt("Weight(lbs)",0,5);
	 a2Table.setValueAt("College",0,6);
	 a2Table.setValueAt("NFL Team",0,7);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a2Table.setValueAt(values[y],x+1,y);
		}
	}
    a2Table.setTableHeader(null);
	a2Table.setEnabled(false);
     a2Tab.add(a2Table);
	 }
     tabs.addTab("Ai2", a2Tab);
     //AI three
     JPanel a3Tab=new JPanel();
     a3Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,4);
      if(tempp.size()>0)
	 {
	 JTable a3Table=new JTable(tempp.size()+1,8);
	 a3Table.setValueAt("Player Id",0,0);
	 a3Table.setValueAt("Position",0,1);
	 a3Table.setValueAt("First Name",0,2);
	 a3Table.setValueAt("Last Name",0,3);
	 a3Table.setValueAt("Height(Inches)",0,4);
	 a3Table.setValueAt("Weight(lbs)",0,5);
	 a3Table.setValueAt("College",0,6);
	 a3Table.setValueAt("NFL Team",0,7);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a3Table.setValueAt(values[y],x+1,y);
		}
	}
    a3Table.setTableHeader(null);
	a3Table.setEnabled(false);
     a3Tab.add(a3Table);
	 }
     tabs.addTab("Ai3", a3Tab);
      //AI four
     JPanel a4Tab=new JPanel();
     a4Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,5);
      if(tempp.size()>0)
	 {
	 JTable a4Table=new JTable(tempp.size()+1,8);
	 a4Table.setValueAt("Player Id",0,0);
	 a4Table.setValueAt("Position",0,1);
	 a4Table.setValueAt("First Name",0,2);
	 a4Table.setValueAt("Last Name",0,3);
	 a4Table.setValueAt("Height(Inches)",0,4);
	 a4Table.setValueAt("Weight(lbs)",0,5);
	 a4Table.setValueAt("College",0,6);
	 a4Table.setValueAt("NFL Team",0,7);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a4Table.setValueAt(values[y],x+1,y);
		}
	}
    a4Table.setTableHeader(null);
	a4Table.setEnabled(false);
     a4Tab.add(a4Table);
	 }
     tabs.addTab("Ai4", a4Tab);
     //AI five
     JPanel a5Tab=new JPanel();
     a5Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,6);
      if(tempp.size()>0)
	 {
	 JTable a5Table=new JTable(tempp.size()+1,8);
	 a5Table.setValueAt("Player Id",0,0);
	 a5Table.setValueAt("Position",0,1);
	 a5Table.setValueAt("First Name",0,2);
	 a5Table.setValueAt("Last Name",0,3);
	 a5Table.setValueAt("Height(Inches)",0,4);
	 a5Table.setValueAt("Weight(lbs)",0,5);
	 a5Table.setValueAt("College",0,6);
	 a5Table.setValueAt("NFL Team",0,7);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a5Table.setValueAt(values[y],x+1,y);
		}
	}
    a5Table.setTableHeader(null);
	a5Table.setEnabled(false);
     a5Tab.add(a5Table);
	 }
     tabs.addTab("Ai5", a5Tab);
        //AI six
     JPanel a6Tab=new JPanel();
     a6Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,7);
      if(tempp.size()>0)
	 {
	 JTable a6Table=new JTable(tempp.size()+1,8);
	 a6Table.setValueAt("Player Id",0,0);
	 a6Table.setValueAt("Position",0,1);
	 a6Table.setValueAt("First Name",0,2);
	 a6Table.setValueAt("Last Name",0,3);
	 a6Table.setValueAt("Height(Inches)",0,4);
	 a6Table.setValueAt("Weight(lbs)",0,5);
	 a6Table.setValueAt("College",0,6);
	 a6Table.setValueAt("NFL Team",0,7);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a6Table.setValueAt(values[y],x+1,y);
		}
	}
    a6Table.setTableHeader(null);
	a6Table.setEnabled(false);
     a6Tab.add(a6Table);
	 }
     tabs.addTab("Ai6", a6Tab);
         //AI seven
     JPanel a7Tab=new JPanel();
     a7Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,8);
     if(tempp.size()>0)
	 {
	 JTable a7Table=new JTable(tempp.size()+1,8);
	 a7Table.setValueAt("Player Id",0,0);
	 a7Table.setValueAt("Position",0,1);
	 a7Table.setValueAt("First Name",0,2);
	 a7Table.setValueAt("Last Name",0,3);
	 a7Table.setValueAt("Height(Inches)",0,4);
	 a7Table.setValueAt("Weight(lbs)",0,5);
	 a7Table.setValueAt("College",0,6);
	 a7Table.setValueAt("NFL Team",0,7);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a7Table.setValueAt(values[y],x+1,y);
		}
	}
    a7Table.setTableHeader(null);
	a7Table.setEnabled(false);
     a7Tab.add(a7Table);
	 }
     tabs.addTab("Ai7", a7Tab);
	win.add(tabs);
	JPanel bacs=new JPanel();
	bacs.setLayout(new GridLayout(1,1));
	restart=new JButton("Restart");
	restart.addActionListener(ButtonListener);
	bacs.add(restart);
	
	win.add(restart);
	cpid.add(win);
}
public void leaderBoardGui()
{
	setSize(1200, 500); 
	JPanel leaderBoardP=new JPanel();
	leaderBoardP.setLayout(new GridLayout(2,1));
	ArrayList<String> tempp=Scoring.getLeaderboard(conn);
	if(tempp.size()>0)
	{
	JTable table=new JTable(tempp.size()+1,9);
	table.setValueAt("Team",0,0);
	table.setValueAt("Score",0,1);
	table.setValueAt("QB",0,2);
	table.setValueAt("RB",0,3);
	table.setValueAt("RB2",0,4);
	table.setValueAt("WR",0,5);
	table.setValueAt("WR2",0,6);
	table.setValueAt("WR3",0,7);
	table.setValueAt("TE",0,8);
	for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<9;y++)
		{
			table.setValueAt(values[y],x+1,y);
		}
	}
	table.setTableHeader(null);
	table.setEnabled(false);
	JScrollPane scroll=new JScrollPane();
	scroll.getViewport().add(table);
	leaderBoardP.add(scroll);
	}
	back=new JButton("back");
	back.addActionListener(ButtonListener);
	JPanel backside=new JPanel();
	backside.setLayout(new FlowLayout());
	backside.add(back);
	leaderBoardP.add(backside);
	cp.add(leaderBoardP);
}
	
	// All methods we need to overide so they do correct function
	public void mouseClicked(MouseEvent arg0) {
		if(dropPhase && tabs.getSelectedIndex()==0)
		{
				playerDrop.setText((String)cTeam.getSelectedValue());
		}
		else if(tabs.getTitleAt(tabs.getSelectedIndex()).equals("Players"))
		{
			if(posTabs.getTitleAt(posTabs.getSelectedIndex()).equals("QB"))
				playerAdd.setText((String)JQBPlayers.getSelectedValue());
			else if(posTabs.getTitleAt(posTabs.getSelectedIndex()).equals("RB"))
				playerAdd.setText((String)JRBPlayers.getSelectedValue());
			else if(posTabs.getTitleAt(posTabs.getSelectedIndex()).equals("WR"))
				playerAdd.setText((String)JWRPlayers.getSelectedValue());
			else if(posTabs.getTitleAt(posTabs.getSelectedIndex()).equals("TE"))
				playerAdd.setText((String)JTEPlayers.getSelectedValue());
			else if(posTabs.getTitleAt(posTabs.getSelectedIndex()).equals("ALL"))
				playerAdd.setText((String)JAllPlayers.getSelectedValue());
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		DBConnection.close(conn);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(teamName.getText().length()>20)
		{
			String temp=teamName.getText();
			temp=temp.substring(0,19);
			teamName.setText(temp);
		}
		if(teamName.getText().length()>0)
		{
			DBConnection.initTeam(conn,teamName.getText());
			cpid=getContentPane();
			//orderF AI needs to go
			int remainingPlayers=qbleft+rbleft+wrleft+teleft;
				remainingPlayers=8-remainingPlayers;
			for(int x=0;x<orderF.length;x++)
				{
					if(difficulty==1)
						RandomAI.draftPlayer(conn,remainingPlayers,orderF[x]+1);
					else if(difficulty==2)
						MediumAI.draftPlayer(conn,remainingPlayers,orderF[x]+1);
					else if(difficulty==3)
						HardAI.draftPlayer(conn,remainingPlayers,orderF[x]+1);
				}
			initDraft();
			cpid.remove(title);
			this.setContentPane(cpid);
			this.repaint();
		}
	}
	public void humanTeamUpdate()
	{
		cpid.removeAll();
		cpid=getContentPane();
		posTabs.removeAll();
		initDraft();
		tabs.setSelectedIndex(8);
		this.setContentPane(cpid);
		this.repaint();
	}
	public void swapUpdate()
	{
		cpid.removeAll();
		cpid=getContentPane();
		posTabs.removeAll();
		swapDraft();
		this.setContentPane(cpid);
		this.repaint();
	}
	public void winnerUpdate()
	{
		cpid.removeAll();
		cpid=getContentPane();
		posTabs.removeAll();
		tabs.removeAll();
		winner();
		this.setContentPane(cpid);
		this.repaint();
	}
	public void leaderBoardUpdate()
	{
		cp.removeAll();
		cp=getContentPane();
		this.leaderBoardGui();
		this.setContentPane(cp);
		this.repaint();
	}
	public void roundUpdater()
	{
		Container temp=getContentPane();
		temp.removeAll();
		JPanel roundPane=new JPanel();
		roundPane.setLayout(new GridLayout(2,1));
		round++;
		tabs.removeAll();
		String loser=Scoring.getLoserTeam(conn);
		Scanner sscan=new Scanner(loser);
		int loserAI=Integer.parseInt(sscan.next());
		if(loserAI >1)
		{
		losers[loserAI-2]=true;
		}
		else
		{
			lost=true;
		}
		Scoring.eliminateTeam(conn,loserAI);
		//here goes tabs
		
		ArrayList<String> tempp=Scoring.getRoundStats(conn,false);
		JTable sList=new JTable(tempp.size()+1,2);
		sList.setValueAt("Team",0,0);
		sList.setValueAt("Score",0,1);
		for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			sList.setValueAt(values[y],x+1,y);
		}
	}
	sList.setTableHeader(null);
	sList.setEnabled(false);
     		tabs.addTab("Scores",sList);
			
     //tab1
    JPanel hTab=new JPanel();
     hTab.setLayout(new GridLayout(1,1));
	 
     tempp=DBConnection.getAllTeamPosPlayers(conn,1);
	 if(tempp.size()>0)
	 {
	 JTable cTable=new JTable(tempp.size()+1,9);
	 cTable.setValueAt("Player Id",0,0);
	 cTable.setValueAt("Position",0,1);
	 cTable.setValueAt("First Name",0,2);
	 cTable.setValueAt("Last Name",0,3);
	 cTable.setValueAt("Height(Inches)",0,4);
	 cTable.setValueAt("Weight(lbs)",0,5);
	 cTable.setValueAt("College",0,6);
	 cTable.setValueAt("NFL Team",0,7);
	 cTable.setValueAt("Score",0,8);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			cTable.setValueAt(values[y],x+1,y);
		}
		cTable.setValueAt(Scoring.getPlayerScore(conn,week,values[0]),x+1,8);
	}
    cTable.setTableHeader(null);
	cTable.setEnabled(false);
     hTab.add(cTable);
	 }
     tabs.addTab(teamName.getText(), hTab);
     //AI starts at 2
     //AI one
     JPanel a1Tab=new JPanel();
     a1Tab.setLayout(new GridLayout(1,1));
     
     tempp=DBConnection.getAllTeamPosPlayers(conn,2);
    if(tempp.size()>0)
	 {
	 JTable a1Table=new JTable(tempp.size()+1,9);
	 a1Table.setValueAt("Player Id",0,0);
	 a1Table.setValueAt("Position",0,1);
	 a1Table.setValueAt("First Name",0,2);
	 a1Table.setValueAt("Last Name",0,3);
	 a1Table.setValueAt("Height(Inches)",0,4);
	 a1Table.setValueAt("Weight(lbs)",0,5);
	 a1Table.setValueAt("College",0,6);
	 a1Table.setValueAt("NFL Team",0,7);
	 a1Table.setValueAt("Score",0,8);
	 
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a1Table.setValueAt(values[y],x+1,y);
		}
		a1Table.setValueAt(Scoring.getPlayerScore(conn,week,values[0]),x+1,8);
	}
    a1Table.setTableHeader(null);
	a1Table.setEnabled(false);
     a1Tab.add(a1Table);
	 }
     tabs.addTab("Ai1", a1Tab);
      
     //AI two
     JPanel a2Tab=new JPanel();
     a2Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,3);
      if(tempp.size()>0)
	 {
	 JTable a2Table=new JTable(tempp.size()+1,9);
	 a2Table.setValueAt("Player Id",0,0);
	 a2Table.setValueAt("Position",0,1);
	 a2Table.setValueAt("First Name",0,2);
	 a2Table.setValueAt("Last Name",0,3);
	 a2Table.setValueAt("Height(Inches)",0,4);
	 a2Table.setValueAt("Weight(lbs)",0,5);
	 a2Table.setValueAt("College",0,6);
	 a2Table.setValueAt("NFL Team",0,7);
	 a2Table.setValueAt("Score",0,8);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a2Table.setValueAt(values[y],x+1,y);
		}
		a2Table.setValueAt(Scoring.getPlayerScore(conn,week,values[0]),x+1,8);
	}
    a2Table.setTableHeader(null);
	a2Table.setEnabled(false);
     a2Tab.add(a2Table);
	 }
     tabs.addTab("Ai2", a2Tab);
     //AI three
     JPanel a3Tab=new JPanel();
     a3Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,4);
      if(tempp.size()>0)
	 {
	 JTable a3Table=new JTable(tempp.size()+1,9);
	 a3Table.setValueAt("Player Id",0,0);
	 a3Table.setValueAt("Position",0,1);
	 a3Table.setValueAt("First Name",0,2);
	 a3Table.setValueAt("Last Name",0,3);
	 a3Table.setValueAt("Height(Inches)",0,4);
	 a3Table.setValueAt("Weight(lbs)",0,5);
	 a3Table.setValueAt("College",0,6);
	 a3Table.setValueAt("NFL Team",0,7);
	 a3Table.setValueAt("Score",0,8);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a3Table.setValueAt(values[y],x+1,y);
		}
		a3Table.setValueAt(Scoring.getPlayerScore(conn,week,values[0]),x+1,8);
	}
    a3Table.setTableHeader(null);
	a3Table.setEnabled(false);
     a3Tab.add(a3Table);
	 }
     tabs.addTab("Ai3", a3Tab);
      //AI four
     JPanel a4Tab=new JPanel();
     a4Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,5);
      if(tempp.size()>0)
	 {
	 JTable a4Table=new JTable(tempp.size()+1,9);
	 a4Table.setValueAt("Player Id",0,0);
	 a4Table.setValueAt("Position",0,1);
	 a4Table.setValueAt("First Name",0,2);
	 a4Table.setValueAt("Last Name",0,3);
	 a4Table.setValueAt("Height(Inches)",0,4);
	 a4Table.setValueAt("Weight(lbs)",0,5);
	 a4Table.setValueAt("College",0,6);
	 a4Table.setValueAt("NFL Team",0,7);
	 a4Table.setValueAt("Score",0,8);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a4Table.setValueAt(values[y],x+1,y);
		}
		a4Table.setValueAt(Scoring.getPlayerScore(conn,week,values[0]),x+1,8);
	}
    a4Table.setTableHeader(null);
	a4Table.setEnabled(false);
     a4Tab.add(a4Table);
	 }
     tabs.addTab("Ai4", a4Tab);
     //AI five
     JPanel a5Tab=new JPanel();
     a5Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,6);
      if(tempp.size()>0)
	 {
	 JTable a5Table=new JTable(tempp.size()+1,9);
	 a5Table.setValueAt("Player Id",0,0);
	 a5Table.setValueAt("Position",0,1);
	 a5Table.setValueAt("First Name",0,2);
	 a5Table.setValueAt("Last Name",0,3);
	 a5Table.setValueAt("Height(Inches)",0,4);
	 a5Table.setValueAt("Weight(lbs)",0,5);
	 a5Table.setValueAt("College",0,6);
	 a5Table.setValueAt("NFL Team",0,7);
	 a5Table.setValueAt("Score",0,8);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a5Table.setValueAt(values[y],x+1,y);
		}
		a5Table.setValueAt(Scoring.getPlayerScore(conn,week,values[0]),x+1,8);
	}
    a5Table.setTableHeader(null);
	a5Table.setEnabled(false);
     a5Tab.add(a5Table);
	 }
     tabs.addTab("Ai5", a5Tab);
        //AI six
     JPanel a6Tab=new JPanel();
     a6Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,7);
      if(tempp.size()>0)
	 {
	 JTable a6Table=new JTable(tempp.size()+1,9);
	 a6Table.setValueAt("Player Id",0,0);
	 a6Table.setValueAt("Position",0,1);
	 a6Table.setValueAt("First Name",0,2);
	 a6Table.setValueAt("Last Name",0,3);
	 a6Table.setValueAt("Height(Inches)",0,4);
	 a6Table.setValueAt("Weight(lbs)",0,5);
	 a6Table.setValueAt("College",0,6);
	 a6Table.setValueAt("NFL Team",0,7);
	 a6Table.setValueAt("Score",0,8);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a6Table.setValueAt(values[y],x+1,y);
		}
		a6Table.setValueAt(Scoring.getPlayerScore(conn,week,values[0]),x+1,8);
	}
    a6Table.setTableHeader(null);
	a6Table.setEnabled(false);
     a6Tab.add(a6Table);
	 }
     tabs.addTab("Ai6", a6Tab);
         //AI seven
     JPanel a7Tab=new JPanel();
     a7Tab.setLayout(new GridLayout(1,1));
     tempp=DBConnection.getAllTeamPosPlayers(conn,8);
     if(tempp.size()>0)
	 {
	 JTable a7Table=new JTable(tempp.size()+1,9);
	 a7Table.setValueAt("Player Id",0,0);
	 a7Table.setValueAt("Position",0,1);
	 a7Table.setValueAt("First Name",0,2);
	 a7Table.setValueAt("Last Name",0,3);
	 a7Table.setValueAt("Height(Inches)",0,4);
	 a7Table.setValueAt("Weight(lbs)",0,5);
	 a7Table.setValueAt("College",0,6);
	 a7Table.setValueAt("NFL Team",0,7);
	 a7Table.setValueAt("Score",0,8);
     for(int x=0;x<tempp.size();x++)
	{ 
		String [] values=tempp.get(x).split("\\|");
		for(int y=0;y<values.length;y++)
		{
			a7Table.setValueAt(values[y],x+1,y);
		}
		a7Table.setValueAt(Scoring.getPlayerScore(conn,week,values[0]),x+1,8);
	}
    a7Table.setTableHeader(null);
	a7Table.setEnabled(false);
     a7Tab.add(a7Table);
	 }
     tabs.addTab("Ai7", a7Tab);
     
		
		//
		roundPane.add(tabs);
		next=new JButton("next");
		next.addActionListener(ButtonListener);
		JPanel but=new JPanel();
		but.setLayout(new FlowLayout());
		but.add(new JLabel("Eliminated: "+loser.substring(1)));
		but.add(new JLabel("Round: "+round));
		//adds proper boolean to screen
		
		but.add(next);
		roundPane.add(but);
		temp.add(roundPane);
		
		this.setContentPane(temp);
		this.repaint();
	}
   private void rePainter()
   {
   	setSize(500, 500); 
	cp.removeAll();
	cp=getContentPane();
	title =new JPanel();
	title.setLayout(new GridLayout(2,1));
	//first item in grid
	//second item in grid
	JPanel teamNamer=new JPanel();
	teamNamer.setLayout(new FlowLayout());
	teamNamer.add(new JLabel("Team Name:"));
	teamName.setEditable(true);
	teamNamer.add(teamName);
	JButton teamConfirm=new JButton("Confirm");
	teamNamer.add(teamConfirm);
	teamConfirm.addActionListener(this);
	//teamName.addActionListener(this);
	title.add(teamNamer);
	//third item in the grid
	JPanel leader=new JPanel();
	leader.setLayout(new FlowLayout());
	leaderBoard=new JButton("LeaderBoard");
	 leaderBoard.addActionListener(ButtonListener);
	leader.add(leaderBoard);
    leader.add(easy);
    leader.add(medium);
    leader.add(hard);
    JButton help=new JButton("Help");
    String helpStr = "Welcome to Draft Queens Fantasy Football Simulation! This simulation allows you to draft a fantasy team and\n" +
"proceed through an elimination league. Every week, the team with the lowest cumulative point total will be eliminated.\n" +
"The points are calculated given the stats that your team showcased on a random week. The game proceeds in the following steps.\n" +
"   1. To begin the game, type in a team name for your team (20 characters or less). Select the level of difficulty of the AIs\n" +
"   you would like to play against.\n\n" +
"   2. The draft order will be randomly chosen and you will draft 1 player every round. The AIs will make their choices\n" +
"   and you will then again select another player until your team has no open positions.\n\n" +
"   3. Once the draft is complete, you will proceed to the first week. Every week you will have the opportunity\n" +
" to drop players on your team and swap them with another player who has not been selected onto a team\n\n" +
"   4. After every week, there will be a transition screen that will display how all teams performed during that week.\n" +
" The team that has the lowest cumulative point total will be dismissed from the league.\n\n" +
"   5. Upon being eliminated or fortunate enough to defeat all AI opponents, you will be directed to a leaderboard screen.\n" +
"This leaderboard will hold the hall of fame of playing the Draft Queens’ Fantasy Football Simulation and their teams and scores.\n\n";
    help.addActionListener(new ActionListener()
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		JOptionPane.showMessageDialog(new JFrame(),helpStr,"Help",JOptionPane.PLAIN_MESSAGE);
    	}
    });
    leader.add(help);
	title.add(leader);
	cp.add(title);
	this.setContentPane(cp);
    this.repaint();
   }
	private class ButtonActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			Object source=arg0.getSource();
         if(source==easy)
         {
            difficulty=1;
            easy=new JRadioButton("Random",true);
            easy.addActionListener(ButtonListener);
            medium=new JRadioButton("Medium");
            medium.addActionListener(ButtonListener);
            hard=new JRadioButton("Hard");
            hard.addActionListener(ButtonListener);
            GUI.this.rePainter();
         }
         if(source==medium)
         {
            difficulty=2;
            easy=new JRadioButton("Random");
            easy.addActionListener(ButtonListener);
            medium=new JRadioButton("Medium",true);
            medium.addActionListener(ButtonListener);
            hard=new JRadioButton("Hard");
            hard.addActionListener(ButtonListener);
            GUI.this.rePainter();
         }
         if(source==hard)
         {
            difficulty=3;
            medium=new JRadioButton("Medium");
            medium.addActionListener(ButtonListener);
            easy=new JRadioButton("Random");
            easy.addActionListener(ButtonListener);
            hard=new JRadioButton("Hard",true);
            hard.addActionListener(ButtonListener);
            GUI.this.rePainter();
         }
			if(source==ConfirmButton)
			{
				int remainingPlayers=qbleft+rbleft+wrleft+teleft;
				remainingPlayers=8-remainingPlayers;
				//need to have a checker
				String temp=playerAdd.getText();
				if(temp.length()>0)
				{
				
				String playerId=temp.substring(0,7);
				String pos=temp.substring(8,10);
				if(pos.equals("RB") && rbleft>0)
				{
				pos=pos+rbleft;
				DBConnection.updatePlayer(conn,playerId,1);
				DBConnection.updateTeam(conn,pos,playerId,1);
				rbleft--;
				}
				if(pos.equals("WR") && wrleft>0)
				{
				pos=pos+wrleft;
				DBConnection.updatePlayer(conn,playerId,1);
				DBConnection.updateTeam(conn,pos,playerId,1);
				wrleft--;
				}
				else if(pos.equals("QB") && qbleft>0)
				{
				DBConnection.updatePlayer(conn,playerId,1);
				DBConnection.updateTeam(conn,pos,playerId,1);
				qbleft--;
				}
				else if(pos.equals("TE") && teleft>0)
				{
				DBConnection.updatePlayer(conn,playerId,1);
				DBConnection.updateTeam(conn,pos,playerId,1);	
				teleft--;
				}
				
				//AI drafting
				for(int x=0;x<orderL.length;x++)
				{
					if(difficulty==1)
						RandomAI.draftPlayer(conn,remainingPlayers,orderL[x]+1);
					else if(difficulty==2)
						MediumAI.draftPlayer(conn,remainingPlayers,orderL[x]+1);
					else if(difficulty==3)
						HardAI.draftPlayer(conn,remainingPlayers,orderL[x]+1);
				}
				if(orderF.length<7)
				{
					
					for(int x=0;x<orderF.length;x++)
				{
					if(difficulty==1)
						RandomAI.draftPlayer(conn,remainingPlayers+1,orderF[x]+1);
					else if(difficulty==2)
						MediumAI.draftPlayer(conn,remainingPlayers+1,orderF[x]+1);
					else if(difficulty==3)
						HardAI.draftPlayer(conn,remainingPlayers+1,orderF[x]+1);
				}
				}
					
				//updates the screen
				GUI.this.humanTeamUpdate();
				playerAdd.setText("");
				}
			}
			if(source==doWeek)
			{
				playerDrop.setText("");
				//we can move this to last time confirm button is hit
				aiWent=false;
				if(round<7)
				{
				Random num=new Random();
				week=num.nextInt(8);
				//needs to have 8 changed maybe to a another fucking global
				if(week==0)
				{
				Scoring.runWeek(conn,week+1,8);
				}
				else
				{
					Scoring.runWeek(conn,week,8);
				}
				//up to here
				GUI.this.roundUpdater();
				}
				else
				{
					Scoring.addTeamToLeaderboard(conn,1);
					GUI.this.winnerUpdate();
				}
				//we add the thing here
			}
			if(source==next)
			{
				if(lost)
				{
					Scoring.addTeamToLeaderboard(conn,1);
					GUI.this.winnerUpdate();
				}
				else
				{
				dropPhase=true;
				GUI.this.swapUpdate();
				}
			}
			if(source==dropButton)
			{
				
				// drop player
				int remainingPlayers=qbleft+rbleft+wrleft+teleft;
				remainingPlayers=8-remainingPlayers;
				//need to have a checker
				String temp=playerDrop.getText();
			
				if(temp.length()>0)
				{
				dropPhase=false;
				String playerId=temp.substring(0,7);
				String pos=temp.substring(8,10);
				droppedPos=DBConnection.getPosColName(conn,playerId,pos,1);
				if(pos.equals("RB"))
				{
				DBConnection.updatePlayer(conn,playerId,0);
				DBConnection.dropPlayerFromTeam(conn,1,droppedPos);
				rbleft++;
				}
				if(pos.equals("WR"))
				{
				DBConnection.updatePlayer(conn,playerId,0);
				DBConnection.dropPlayerFromTeam(conn,1,droppedPos);
				wrleft++;
				}
				else if(pos.equals("QB"))
				{
				DBConnection.updatePlayer(conn,playerId,0);
				DBConnection.dropPlayerFromTeam(conn,1,pos);
				qbleft++;
				}
				else if(pos.equals("TE"))
				{
				DBConnection.updatePlayer(conn,playerId,0);
				DBConnection.dropPlayerFromTeam(conn,1,pos);	
				teleft++;
				}
				//end logic
				playerDrop.setText("");
				playerAdd.setText("");
				GUI.this.swapUpdate();
			}
			}
			if(source==swap)
			{
				int remainingPlayers=qbleft+rbleft+wrleft+teleft;
				remainingPlayers=8-remainingPlayers;
				//need to have a checker
				String temp=playerAdd.getText();
				if(temp.length()>0)
				{
				
				String playerId=temp.substring(0,7);
				String pos=temp.substring(8,10);
				if(pos.equals("RB") && rbleft>0)
				{
				pos=droppedPos;
				DBConnection.updatePlayer(conn,playerId,1);
				DBConnection.updateTeam(conn,pos,playerId,1);
				rbleft--;
				}
				if(pos.equals("WR") && wrleft>0)
				{
				pos=droppedPos;
				DBConnection.updatePlayer(conn,playerId,1);
				DBConnection.updateTeam(conn,pos,playerId,1);
				wrleft--;
				}
				else if(pos.equals("QB") && qbleft>0)
				{
				DBConnection.updatePlayer(conn,playerId,1);
				DBConnection.updateTeam(conn,pos,playerId,1);
				qbleft--;
				}
				else if(pos.equals("TE") && teleft>0)
				{
				DBConnection.updatePlayer(conn,playerId,1);
				DBConnection.updateTeam(conn,pos,playerId,1);	
				teleft--;
				}
				dropPhase=true;
				droppedPos="";
				playerAdd.setText("");
				
				}
				if(aiWent==false)
				{
					for(int x=0;x<orderF.length;x++)
					{
						if(difficulty==1)
							RandomAI.swapPlayerRandom(conn,orderF[x]+1);
						else if(difficulty==2)
							MediumAI.swapPlayerRandom(conn,orderF[x]+1);
						else if(difficulty==3)
							HardAI.swapPlayerRandom(conn,orderF[x]+1);
					}
					for(int x=0;x<orderL.length;x++)
					{
						if(difficulty==1)
							RandomAI.swapPlayerRandom(conn,orderL[x]+1);
						else if(difficulty==2)
							MediumAI.swapPlayerRandom(conn,orderL[x]+1);
						else if(difficulty==3)
							HardAI.swapPlayerRandom(conn,orderL[x]+1);
					}
				aiWent=true;
				}
				playerDrop.setText("");
				playerAdd.setText("");
				GUI.this.swapUpdate();
				
			}
			if(source==leaderBoard)
			{
				GUI.this.leaderBoardUpdate();
			}
			if(source==back)
			{
				GUI.this.rePainter();
			}
			if(source==restart)
			{
				//add shit that has to be reset
				qbleft=1;
				rbleft=2;
				wrleft=3;
				teleft=1;
				round=1;
				lost=false;
				GUI.this.arraySplit();
				Arrays.fill(losers, Boolean.FALSE);
				DBConnection.resetPlayers(conn);
				Scoring.resetTeams(conn);
				GUI.this.rePainter();
			}
		}
	}
}
