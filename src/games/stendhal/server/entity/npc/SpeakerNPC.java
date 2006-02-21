package games.stendhal.server.entity.npc;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.common.Rand;

import java.awt.geom.Rectangle2D;
import java.util.*;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;

import org.apache.log4j.Logger;

/** This is a finite state machine that implements a chat system.
 *  See: http://en.wikipedia.org/wiki/Finite_state_machine
 *
 *  See examples to understand how it works.
 *  RULES:
 *  - State 0 is always the initial state
 *  - State 1 is the state where only one player can talk to NPC
 *  - State -1 is used for jump from any state when the trigger is present.
 *  - States from 2 to 100 are reserved for Behaviours uses.
 *
 *  How it works: Example
 *  First we need to create a message to greet the player and attend it.
 *  We add a hi event
 *
 *  add(0, "hi", 1, "Welcome player!", null)
 *
 *  State 0 is the initial state, so once NPC is in that state and listen "hi", 
 *  it will say "Welcome player!" and pass to state 1.
 *
 *  Now let's add some options when player is in state 1 like job, offer, buy, sell, etc.
 *  
 *  add(1, "job", 1, "I work as a part time example showman",null)
 *  add(1, "offer", 1, "I sell best quality swords",null)
 *
 *  Ok, two new events: job and offer, they go from state 1 to 1, because after listening to them
 *  the NPC can listen something like job.   
 *
 *  add(1, "buy", 20, null, new ChatAction()
 *    {
 *    public void fire(Player player, String text, SpeakerNPC engine)
 *      {
 *      int i=text.indexOf(" ");
 *      String item=text.substring(i+1);
 *       
 *      if(item.equals("sword"))
 *        {
 *        engine.say(item+" costs 10 GP. Do you want to buy?");
 *        }
 *      else
 *        {
 *        engine.say("Sorry, I don't sell "+item);
 *        engine.setActualState(1);          
 *        }
 *      }
 *    });
 *
 *  Now the hard part, we listen to buy so we need to process the text, and for that we use the 
 *  ChatAction class, we create a new class that will handle the event.
 *  Also see that we move to a new state, 20, because we are replying to a question, so 
 *  only expect two possible replies: yes or no.
 *
 *  add(20, "yes", 1, null, null);  // See Behaviours.java for exact code.
 *  add(20, "no", 1, null, null); // See Behaviours.java for exact code.
 *
 *  Whatever the reply is, return to state 1 so we can listen to new things.
 *  Finally we want to finish the conversation, so whatever state we are we want to finish a conversation 
 *  with Bye.
 *
 *  add(-1, "bye", 0, "Bye!.", null);
 *
 *  We use -1 as a wildcard, so it text is bye the transition.happens.
 **/ 
public abstract class SpeakerNPC extends NPC 
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(SpeakerNPC.class);

  // FSM state table
  private List<StatePath> statesTable;
  // FSM actual state
  private int actualState;
  private int maxState;
  // Default wait message when NPC is busy
  private String waitMessage;
  // Default wait action when NPC is busy
  private ChatAction waitAction;
  
  // Timeout control value
  private long lastMessageTurn;
  private static long TIMEOUT_PLAYER_CHAT=60; // 20 seconds at 300ms.
  
  // Attended players
  private Player attending;
  
  private Map<String, Object> behavioursData;
  
  public SpeakerNPC() throws AttributeNotFoundException
    {
    super();
    createPath();
    
    statesTable=new LinkedList<StatePath>();
    actualState=0;
    maxState=0;
    lastMessageTurn=0;
    
    behavioursData=new HashMap<String, Object>();
    
    createDialog();
    }

  abstract protected void createPath();
  abstract protected void createDialog();
  
  public void setBehaviourData(String behaviour, Object data)
    {
    behavioursData.put(behaviour,data);
    }
  
  public Object getBehaviourData(String behaviour)
    {
    return behavioursData.get(behaviour);
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y+1,1,1);
    }  

  private List<Player> getNearestPlayersThatHasSpeaken(NPC npc, double range)
    {
    int x=npc.getx();
    int y=npc.gety();
    
    List<Player> players=new LinkedList<Player>();
    
    for(Player player: rp.getPlayers())
      {
      int px=player.getx();
      int py=player.gety();
      
      if(get("zoneid").equals(player.get("zoneid")) && Math.abs(px-x)<range && Math.abs(py-y)<range && player.has("text"))
        {
        players.add(player);
        }
      }
    
    return players;
    }
      
  public void onDead(RPEntity who)
    {
    setHP(getBaseHP());
    world.modify(this);
    }

  protected void dropItemsOn(Corpse corpse)
    {
    // They can't die
    }
   
  public void logic()
    {
    if(has("text")) remove("text");
    
    if(actualState==0)
      {
      Path.followPath(this,0.2);
      StendhalRPAction.move(this);
      }
    else
      {
      if(rp.getTurn()-lastMessageTurn>TIMEOUT_PLAYER_CHAT)
        {
        actualState=0;
        attending=null;
        }
      
      if(!stopped())
        {
        stop();
        }
      }
    
    List<Player> speakers=getNearestPlayersThatHasSpeaken(this,5);
    for(Player speaker: speakers)
      {
      tell(speaker, speaker.get("text"));
      }

    if(talking() && attending!=null && attending.distance(this)>8*8)
      {
      // If the player is to far away, we force him to say bye to NPC :) 
      tell(attending,"bye");
      attending=null;
      }
     
    world.modify(this);
    }
  
  public boolean talking()
    {
    return actualState!=0;
    }

  abstract public static class ChatAction
    {    
    abstract public void fire(Player player, String text, SpeakerNPC engine);
    }

  abstract public static class ChatCondition
    {    
    abstract public boolean fire(Player player, SpeakerNPC engine);
    }
    
  private class StatePath
    {
    public int state;
    public int nextState;
    public String trigger;
    public ChatCondition condition;
    public String reply;
    public ChatAction action;
    
    StatePath(int state, String trigger, ChatCondition condition, int nextState, String reply, ChatAction action)
      {
      this.state=state;
      this.condition=condition;
      this.nextState=nextState;
      this.trigger=trigger.toLowerCase();
      this.reply=reply;
      this.action=action;
      }

    public boolean absoluteJump(int state, String text)
      {
      if(this.state==-1 && trigger.equalsIgnoreCase(text))
        {
        return true;
        }
      
      return false;
      }

    public boolean equals(int state, String text)
      {
      if(state==this.state && trigger.equalsIgnoreCase(text))
        {
        return true;
        }
      
      return false;
      }

    public boolean contains(int state, String text)
      {
      text=text.toLowerCase();
      if(state==this.state && text.startsWith(trigger))
        {
        return true;
        }
      
      return false;
      }
    
    public boolean executeCondition(Player player, SpeakerNPC npc)
      {
      if(condition!=null)
        {
        return condition.fire(player,npc);
        }
      else
        {
        return true;
        }
      }
      
    public String toString()
      {
      return "["+state+","+trigger+","+nextState+"]";
      }
    }
    
  /** Message when NPC is attending another player. */
  public void addWaitMessage(String trigger, ChatAction action)
    {
    waitMessage=trigger;
    waitAction=action;
    }

  private StatePath get(int state, String trigger, ChatCondition condition)
    {
    for(StatePath i: statesTable)
      {
      if(i.equals(state,trigger))
        {
        if(i.condition==condition)
          {
          return i;
          }
        }
      }    
    
    return null;
    }  
  
  public int getFreeState()
    {
    maxState++;
    return maxState;
    }

  /** Add a new state to FSM */
  public void add(int state, String trigger, ChatCondition condition, int next_state, String reply, ChatAction action)
    {
    if(state>maxState)
      {
      maxState=state;
      }
      
    StatePath existing=get(state,trigger,condition);
    if(existing!=null)
      {
      // A previous state, trigger combination exist.
      logger.warn("Adding to "+existing+ " the state ["+state+","+trigger+","+next_state+"]");
      existing.reply=existing.reply+" "+reply;
      }
    
    StatePath item=new StatePath(state, trigger, condition, next_state, reply, action);
    statesTable.add(item);
    }

  /** Add a new set of states to FSM */
  public void add(int state, String[] triggers, int next_state, String reply, ChatAction action)
    {
    for(String trigger: triggers)
      {
      add(state, trigger, null, next_state, reply, action);
      }
    }

  public void add(int state, String[] triggers, int next_state, String[] replies, ChatAction action)
    {
    for(String trigger: triggers)
      {
      for(String reply: replies)
        {
        add(state,trigger, null, next_state, reply, action);
        }
      }
    }
  
  /** This function evolves the FSM */
  private boolean tell(Player player, String text)
    {
    // If we are no attening a player attend, this one.
    if(actualState==0)
      {
      logger.debug("Attending player "+player.getName());
      attending=player;
      }
    
    // If the attended player got idle, attend this one.
    if(rp.getTurn()-lastMessageTurn>TIMEOUT_PLAYER_CHAT)
      {
      logger.debug("Attended player "+attending+" went timeout");
      attending=player;
      actualState=0;
      }
    
    // If we are attending another player make this one waits.
    if(!attending.equals(player))
      {
      logger.debug("Already attending a player");
      if(waitMessage!=null)
        {
        say(waitMessage);
        }
        
      if(waitAction!=null)
        {
        waitAction.fire(player,text,this);
        }
       
      return true;
      }
      
    lastMessageTurn=rp.getTurn();
    
    List<StatePath> list=new LinkedList<StatePath>();
    
    // First we try to match with stateless states.
    for(StatePath state: statesTable)
      {
      if(actualState!=0 && state.absoluteJump(actualState,text) && state.executeCondition(player,this))
        {
        logger.debug("Matched a stateless state: "+state);
        list.add(state);
        //executeState(player,text,state);
        //return true;        
        }
      }
    
    if(list.size()>0)
      {
      executeState(player,text,list.get(Rand.rand(list.size())));
      return true;
      }

    // Now we try to match with the exact trigger string 
    for(StatePath state: statesTable)
      {
      if(state.equals(actualState,text) && state.executeCondition(player,this))
        {
        logger.debug("Matched a exact trigger state: "+state);
        list.add(state);
        //executeState(player,text,state);
        //return true;        
        }
      }

    if(list.size()>0)
      {
      executeState(player,text,list.get(Rand.rand(list.size())));
      return true;
      }

    // Finally we try to match with any string that starts with trigger
    for(StatePath state: statesTable)
      {
      if(state.contains(actualState,text) && state.executeCondition(player,this))
        {
        logger.debug("Matched a similar trigger state: "+state);
        list.add(state);
        //executeState(player,text,state);
        //return true;        
        }
      }

    if(list.size()>0)
      {
      // Execute one of the available options randomly.
      int i=Rand.rand(list.size());
      executeState(player,text,list.get(i));
      return true;
      }

    // Couldn't match the text with the current FSM state
    logger.debug("Couldn't match any state");
    return false;
    }
  
  public void setActualState(int state)
    {
    actualState=state;
    }
  
  private void executeState(Player player, String text, StatePath state)
    {
    int nextState=state.nextState;
    if(state.reply!=null)
      {
      say(state.reply);
      }

    actualState=nextState;

    if(state.action!=null)
      {
      state.action.fire(player,text,this);
      }
    }
  }
