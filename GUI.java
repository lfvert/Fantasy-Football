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
	private JTextField playerAdd=new JTextField("", 20);
	private JTextField playerDrop=new JTextField("",20);
	private Container cp;
	private Container cpid;
	private JPanel title;
	private JPanel initialDraft;
	private JButton ConfirmButton;
	private JButton doWeek;
	private JButton next;
	private JButton swap;
	private JButton dropButton;
	public Connection conn;
	public Statement statement = null;
	public ResultSet results = null;
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
	private int[] order=Draft.getDraftOrder(7);
	private JTable teamOn;
	private int round=1;
	private boolean dropPhase=false;
	private boolean iniD=true;
	private String droppedPos="";
	private Boolean[] losers = new Boolean[7];


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
	 
	 //Title screen
	 title =new JPanel();
	 title.setLayout(new GridLayout(3,1));
	 //first item in grid
	 title.add(new JLabel("Draft Queens"));
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
	 JButton leaderboardButton=new JButton("LeaderBoard");
	 leader.add(leaderboardButton);
	 title.add(leader);
	 leaderboardButton.addActionListener(this);
	 cp.add(title);
	 
	 
        
	   //set main setting of CP
	   setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit program if close button clicked
	   setTitle("Draft Queens"); // "this" JFrame sets title
	   setSize(1000, 500);         // "this" JFrame sets initial size
	   setVisible(true);    
	}
	
	
public void initDraft()
	{
	 //this is initial draft
    initialDraft=new JPanel();
     initialDraft.setLayout(new GridLayout(2,1));
     tabs =new JTabbedPane();
     
     //tab1
     
     JPanel hTab=new JPanel();
     hTab.setLayout(new GridLayout(1,1));
     ArrayList<String> tempp=DBConnection.getAllTeamPosPlayers(conn,1);
     
     String[] input=tempp.toArray(new String[tempp.size()]);
     cTeam=new JList<String>(input);
     hTab.add(cTeam);
     
     
     tabs.addTab(teamName.getText(), hTab);
     //AI starts at 2
     //AI one
     JPanel a1Tab=new JPanel();
     a1Tab.setLayout(new GridLayout(1,1));
     
      tempp=DBConnection.getAllTeamPosPlayers(conn,2);
     input=tempp.toArray(new String[tempp.size()]);
    JList<String> a1Team=new JList<String>(input);
     a1Tab.add(a1Team);
      tabs.addTab("Ai1", a1Tab);
      
     //AI two
      JPanel a2Tab=new JPanel();
      a2Tab.setLayout(new GridLayout(1,1));
      tempp=DBConnection.getAllTeamPosPlayers(conn,3);
     input=tempp.toArray(new String[tempp.size()]);
     JList<String> a2Team=new JList<String>(input);
      a2Tab.add(a2Team);
       tabs.addTab("Ai2", a2Tab);
     //AI three
       JPanel a3Tab=new JPanel();
       a3Tab.setLayout(new GridLayout(1,1));
       tempp=DBConnection.getAllTeamPosPlayers(conn,4);
     input=tempp.toArray(new String[tempp.size()]);
      JList<String> a3Team=new JList<String>(input);
       a3Tab.add(a3Team);
        tabs.addTab("Ai3", a3Tab);
      //AI four
        JPanel a4Tab=new JPanel();
        a4Tab.setLayout(new GridLayout(1,1));
        tempp=DBConnection.getAllTeamPosPlayers(conn,5);
     input=tempp.toArray(new String[tempp.size()]);
       JList<String> a4Team=new JList<String>(input);
        a4Tab.add(a4Team);
         tabs.addTab("Ai4", a4Tab);
       //AI five
         JPanel a5Tab=new JPanel();
         a5Tab.setLayout(new GridLayout(1,1));
         tempp=DBConnection.getAllTeamPosPlayers(conn,6);
     input=tempp.toArray(new String[tempp.size()]);
        JList<String> a5Team=new JList<String>(input);
         a5Tab.add(a5Team);
          tabs.addTab("Ai5", a5Tab);
        //AI six
          JPanel a6Tab=new JPanel();
          a6Tab.setLayout(new GridLayout(1,1));
          tempp=DBConnection.getAllTeamPosPlayers(conn,7);
     input=tempp.toArray(new String[tempp.size()]);
         JList<String> a6Team=new JList<String>(input);
          a6Tab.add(a6Team);
           tabs.addTab("Ai6", a6Tab);
         //AI seven
           JPanel a7Tab=new JPanel();
           a7Tab.setLayout(new GridLayout(1,1));
           tempp=DBConnection.getAllTeamPosPlayers(conn,8);
     input=tempp.toArray(new String[tempp.size()]);
          JList<String> a7Team=new JList<String>(input);
           a7Tab.add(a7Team);
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
     
     String[] input=tempp.toArray(new String[tempp.size()]);
     cTeam=new JList<String>(input);
     cTeam.addMouseListener(this);
     hTab.add(cTeam);
     
     
     tabs.addTab(teamName.getText(), hTab);
     
     //AI starts at 2
     if(!losers[0])
     {
     //AI one
     JPanel a1Tab=new JPanel();
     a1Tab.setLayout(new GridLayout(1,1));
     
      tempp=DBConnection.getAllTeamPosPlayers(conn,2);
     input=tempp.toArray(new String[tempp.size()]);
    JList<String> a1Team=new JList<String>(input);
     a1Tab.add(a1Team);
      tabs.addTab("Ai1", a1Tab);
     }
     //AI two
     if(!losers[1])
     {
      JPanel a2Tab=new JPanel();
      a2Tab.setLayout(new GridLayout(1,1));
      tempp=DBConnection.getAllTeamPosPlayers(conn,3);
     input=tempp.toArray(new String[tempp.size()]);
     JList<String> a2Team=new JList<String>(input);
      a2Tab.add(a2Team);
       tabs.addTab("Ai2", a2Tab);
     }
     //AI three
     if(!losers[2])
     {
       JPanel a3Tab=new JPanel();
       a3Tab.setLayout(new GridLayout(1,1));
       tempp=DBConnection.getAllTeamPosPlayers(conn,4);
     input=tempp.toArray(new String[tempp.size()]);
      JList<String> a3Team=new JList<String>(input);
       a3Tab.add(a3Team);
        tabs.addTab("Ai3", a3Tab);
     }
      //AI four
      if(!losers[3])
      {
        JPanel a4Tab=new JPanel();
        a4Tab.setLayout(new GridLayout(1,1));
        tempp=DBConnection.getAllTeamPosPlayers(conn,5);
     input=tempp.toArray(new String[tempp.size()]);
       JList<String> a4Team=new JList<String>(input);
        a4Tab.add(a4Team);
         tabs.addTab("Ai4", a4Tab);
      }
       //AI five
       if(!losers[4])
       {
         JPanel a5Tab=new JPanel();
         a5Tab.setLayout(new GridLayout(1,1));
         tempp=DBConnection.getAllTeamPosPlayers(conn,6);
     input=tempp.toArray(new String[tempp.size()]);
        JList<String> a5Team=new JList<String>(input);
         a5Tab.add(a5Team);
          tabs.addTab("Ai5", a5Tab);
       }
        //AI six
        if(!losers[5])
        {
          JPanel a6Tab=new JPanel();
          a6Tab.setLayout(new GridLayout(1,1));
          tempp=DBConnection.getAllTeamPosPlayers(conn,7);
     input=tempp.toArray(new String[tempp.size()]);
         JList<String> a6Team=new JList<String>(input);
          a6Tab.add(a6Team);
           tabs.addTab("Ai6", a6Tab);
        }
         //AI seven
         if(!losers[6])
         {
           JPanel a7Tab=new JPanel();
           a7Tab.setLayout(new GridLayout(1,1));
           tempp=DBConnection.getAllTeamPosPlayers(conn,8);
     input=tempp.toArray(new String[tempp.size()]);
          JList<String> a7Team=new JList<String>(input);
           a7Tab.add(a7Team);
            tabs.addTab("Ai7", a7Tab);
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
	win.add(new JLabel("Winner Screen"));
	JPanel statsScreen=new JPanel();
	statsScreen.setLayout(new FlowLayout());
	ArrayList<String> tempp=Scoring.getRoundStats(conn);
	String[] input=tempp.toArray(new String[tempp.size()]);
	JList<String> stats=new JList<String>(input);
	statsScreen.add(stats);
	cpid.add(win);
}
	
	// All methods we need to overide so they do correct function
	public void mouseClicked(MouseEvent arg0) {
		if(dropPhase)
		{
				playerDrop.setText((String)cTeam.getSelectedValue());
		}
		else
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
		DBConnection.initTeam(conn,teamName.getText());
		cpid=getContentPane();
		initDraft();
		cpid.remove(title);
		this.setContentPane(cpid);
		this.repaint();
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
		winner();
		this.setContentPane(cpid);
		this.repaint();
	}
	public void roundUpdater()
	{
		Container temp=getContentPane();
		temp.removeAll();
		JPanel roundPane=new JPanel();
		roundPane.setLayout(new GridLayout(3,1));
		roundPane.add(new JLabel("Round: "+round));
		round++;
		String loser=Scoring.getLoserTeam(conn);
		int loserAI=Integer.parseInt(loser.substring(3,4))-1;
		losers[loserAI]=true;
		Scoring.eliminateTeam(conn,loserAI+2);
		ArrayList<String> tempp=Scoring.getRoundStats(conn);
		String[] input=tempp.toArray(new String[tempp.size()]);
		JList<String> stats=new JList<String>(input);
		roundPane.add(stats);
		next=new JButton("next");
		next.addActionListener(ButtonListener);
		JPanel but=new JPanel();
		but.setLayout(new FlowLayout());
		but.add(new JLabel("Eliminated: "+loser));
		//adds proper boolean to screen
		
		but.add(next);
		roundPane.add(but);
		temp.add(roundPane);
		this.setContentPane(temp);
		this.repaint();
		
	}
	private class ButtonActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			Object source=arg0.getSource();
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
				for(int x=0;x<order.length;x++)
				{
					RandomAI.draftPlayer(conn,remainingPlayers,order[x]+2);
					//System.out.println(order[x]+"turn");
				}
				//updates the screen
				GUI.this.humanTeamUpdate();
				playerAdd.setText("");
				}
			}
			if(source==doWeek)
			{
				//we can move this to last time confirm button is hit
				if(round<8)
				{
				Random num=new Random();
				int week=num.nextInt(8);
				//needs to have 8 changed maybe to a another fucking global
				Scoring.runWeek(conn,week,8);
				//up to here
				System.out.println("doWeek executed ");
				iniD=false;
				GUI.this.roundUpdater();
				}
				else
				{
					GUI.this.winnerUpdate();
				}
				//we add the thing here
			}
			if(source==next)
			{
				System.out.println("Woo you reacher next");
				dropPhase=true;
				GUI.this.swapUpdate();
			}
			if(source==dropButton)
			{
				dropPhase=false;
				// drop player
				int remainingPlayers=qbleft+rbleft+wrleft+teleft;
				remainingPlayers=8-remainingPlayers;
				//need to have a checker
				String temp=playerDrop.getText();
				
				if(temp.length()>0)
				{
				
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
				GUI.this.swapUpdate();
			}
		}
	}
}
}
