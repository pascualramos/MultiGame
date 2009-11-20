package mx.ecosur.multigame.impl.entity.oculto;

import mx.ecosur.multigame.enums.GameState;

import mx.ecosur.multigame.exception.InvalidMoveException;
import mx.ecosur.multigame.exception.InvalidRegistrationException;

import mx.ecosur.multigame.impl.Color;
import mx.ecosur.multigame.impl.model.*;
import mx.ecosur.multigame.impl.enums.oculto.*;

import mx.ecosur.multigame.model.implementation.AgentImpl;
import mx.ecosur.multigame.model.implementation.GamePlayerImpl;
import mx.ecosur.multigame.model.implementation.Implementation;
import mx.ecosur.multigame.model.implementation.MoveImpl;
import mx.ecosur.multigame.model.implementation.RegistrantImpl;
import mx.ecosur.multigame.MessageSender;

import javax.persistence.*;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.io.ResourceFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.KnowledgeBaseFactory;
import org.drools.KnowledgeBase;

import java.util.*;
import java.net.MalformedURLException;

@Entity
public class OcultoGame extends GridGame {
	
	private static final long serialVersionUID = -8395074059039838349L;

    private static final String ChangeSet = "/mx/ecosur/multigame/impl/oculto.xml";
	
	private Set<CheckCondition> checkConditions;

    private transient MessageSender messageSender;


    public OcultoGame() {
        super();
    }

    public OcultoGame(KnowledgeBase kbase) {
        this.kbase = kbase;
    }
    
    public boolean hasCondition (ConditionType type) {
    	boolean ret = false;
    	if (checkConditions != null) {
	    	for (CheckCondition condition : checkConditions) {
	    		if (condition.getType().equals(type)) {
	    			ret = true;
	    		}
	    	}
    	}
	    	
    	return ret;
    }
    @OneToMany (cascade={CascadeType.PERSIST}, fetch=FetchType.EAGER)
    public Set<CheckCondition> getCheckConditions () {
    	if (checkConditions == null)
    		checkConditions = new HashSet<CheckCondition>();
    	return checkConditions;
    }
    
    public void setCheckConditions (Set<CheckCondition> checkConstraints) {
    	this.checkConditions = checkConstraints;
    }
    
    public void addCheckCondition (CheckCondition violation) {
    	if (checkConditions == null) 
    		checkConditions = new HashSet<CheckCondition>();
    	if (!hasCondition (ConditionType.valueOf(violation.getReason())))
    		checkConditions.add(violation);
    }

	/* (non-Javadoc)
	 * @see mx.ecosur.multigame.model.Game#getFacts()
	 */
	@Override
    @Transient
	public Set<Implementation> getFacts() {
		Set<Implementation> facts = super.getFacts();
		if (checkConditions != null)
			facts.addAll(checkConditions);
		return facts;
	}


    /* (non-Javadoc)
      * @see mx.ecosur.multigame.model.Game#initialize(mx.ecosur.multigame.GameType)
      */
    public void initialize() throws MalformedURLException {
        this.setState(GameState.BEGIN);
        if (kbase == null) {
            kbase = KnowledgeBaseFactory.newKnowledgeBase();
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newInputStreamResource(getClass().getResourceAsStream (
                ChangeSet)), ResourceType.CHANGE_SET);
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        }

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        session.setGlobal("messageSender", getMessageSender());
        session.insert(this);
        for (Object fact : getFacts()) {
            session.insert(fact);
        }

        session.getAgenda().getAgendaGroup("initialize").setFocus();
        session.fireAllRules();
        session.dispose();
    }

    @Transient
    public int getMaxPlayers() {
        return 4;
    }

    public void setMaxPlayers(int maxPlayers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
      * @see mx.ecosur.multigame.impl.model.GridGame#move(mx.ecosur.multigame.model.implementation.MoveImpl)
      */
    public MoveImpl move(MoveImpl move) throws InvalidMoveException {
        if (kbase == null) {
            kbase = KnowledgeBaseFactory.newKnowledgeBase();
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newInputStreamResource(getClass().getResourceAsStream (
                ChangeSet)), ResourceType.CHANGE_SET);
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        }
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        session.setGlobal("messageSender", getMessageSender()); 
        session.insert(this);
        session.insert(move);
        for (Implementation fact : getFacts()) {
            session.insert(fact);
        }

        session.getAgenda().getAgendaGroup("verify").setFocus();
        session.fireAllRules();
        session.getAgenda().getAgendaGroup("move").setFocus();
        session.fireAllRules();
        session.getAgenda().getAgendaGroup("evaluate").setFocus();
        session.fireAllRules();
        session.dispose();

        if (moves == null)
            moves = new LinkedHashSet<GridMove>();

        moves.add((OcultoMove) move);

        return move;
    }
	
	public GamePlayerImpl registerPlayer(RegistrantImpl registrant) throws InvalidRegistrationException  {			
		OcultoPlayer player = new OcultoPlayer();
		player.setRegistrant((GridRegistrant) registrant);
		
		for (GridPlayer p : this.getPlayers()) {
			if (p.equals (player))
				throw new InvalidRegistrationException ("Duplicate Registraton!");
		}		
		
		int max = getMaxPlayers();
		if (players.size() == max)
			throw new RuntimeException ("Maximum Players reached!");
		
		List<Color> colors = getAvailableColors();
		player.setColor(colors.get(0));		
		players.add(player);

        try {
		    if (players.size() == getMaxPlayers())
			    initialize();
        } catch (MalformedURLException e) {
            throw new InvalidRegistrationException (e);
        }
		
		if (this.created == null)
		    this.setCreated(new Date());	
		if (this.state == null)
			this.state = GameState.WAITING;
		
		return player;
	}
	
	public AgentImpl registerAgent (AgentImpl agent) throws InvalidRegistrationException {
		throw new InvalidRegistrationException (
				"Agents cannot be registered with an Oculto Game!");
	}

	/* (non-Javadoc)
	 * @see mx.ecosur.multigame.impl.model.GridGame#getColors()
	 */
	@Override
    @Transient
	public List<Color> getColors() {
		List<Color> ret = new ArrayList<Color>();
		for (Color color : Color.values()) {
			if (color.equals(Color.UNKNOWN))
				continue;
			if (color.equals(Color.GREEN))
				continue;
			ret.add(color);
		}
		
		return ret;
	}


    @Transient
    public MessageSender getMessageSender() {
        if (messageSender == null)  
            messageSender = new MessageSender ();
        return messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }    


    @Transient
    public String getGameType() {
        return "Oculto";
    }

    public void setGameType (String type) {
        // do nothing;
    }    

	/* (non-Javadoc)
	 * @see mx.ecosur.multigame.impl.model.GridGame#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}
