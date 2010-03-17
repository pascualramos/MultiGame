//copyright

package mx.ecosur.multigame.impl.entity.manantiales;

import mx.ecosur.multigame.ejb.interfaces.SharedBoardLocal;
import mx.ecosur.multigame.enums.GameEvent;
import mx.ecosur.multigame.enums.MoveStatus;
import mx.ecosur.multigame.enums.SuggestionStatus;
import mx.ecosur.multigame.exception.InvalidMoveException;
import mx.ecosur.multigame.impl.Color;
import mx.ecosur.multigame.impl.enums.manantiales.Mode;
import mx.ecosur.multigame.impl.enums.manantiales.TokenType;
import mx.ecosur.multigame.impl.model.GridCell;
import mx.ecosur.multigame.impl.model.GridRegistrant;
import mx.ecosur.multigame.model.Game;
import mx.ecosur.multigame.model.Move;
import mx.ecosur.multigame.model.implementation.AgentImpl;
import mx.ecosur.multigame.model.implementation.GameImpl;
import mx.ecosur.multigame.model.implementation.MoveImpl;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.persistence.Entity;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author awaterma@ecosur.mx
 */
@Entity
public class SimpleAgent extends ManantialesPlayer implements AgentImpl {

	private static final long serialVersionUID = 8878695200931762776L;

	@EJB
    private SharedBoardLocal sharedBoard;

    private static final Logger logger = Logger.getLogger(SimpleAgent.class.getCanonicalName());

    public SimpleAgent() {
        super();        
    }

    public SimpleAgent(GridRegistrant player, Color favoriteColor) {
        super (player, favoriteColor);
    }

    public void initialize() {
        // do nothing
    }

    public void processEvent(Message message) {
        logger.info("SimpleAgent processing message: " + message);
        try {
            GameEvent gameEvent = GameEvent.valueOf(message.getStringProperty(
                "GAME_EVENT"));
            ObjectMessage msg = (ObjectMessage) message;

            if (gameEvent.equals(GameEvent.GAME_CHANGE)) {
                /* All unacknowledged suggestions addressed to this player are acknowledged */
                ManantialesGame game = (ManantialesGame) msg.getObject();
                if (game.getMode().equals(Mode.BASIC_PUZZLE) || game.getMode().equals(Mode.SILVO_PUZZLE)) {
                    Set<PuzzleSuggestion> suggestions = game.getSuggestions();
                    for (PuzzleSuggestion suggestion : suggestions) {
                        if (suggestion.getStatus().equals(SuggestionStatus.EVALUATED)) {
                            if (suggestion.getMove().getPlayer().equals(this)) {
                                suggestion.setStatus(SuggestionStatus.ACCEPT);
                                sharedBoard.doMove(new Game(game), new Move(suggestion.getMove()));
                            }
                        }
                    }
                }
            }

        } catch (JMSException e) {
            logger.warning("Not able to process game message: " + e.getMessage());
            e.printStackTrace();
        } catch (InvalidMoveException e) {
            logger.warning("Not able to process move: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean ready() {
        return isTurn();
    }

    /* Simply returns a simple move response.  No suggestions are made by the Agent */
    public MoveImpl determineNextMove(GameImpl impl) {
        logger.info ("SimpleAgent creating new move");
        ManantialesMove ret = new ManantialesMove();
        ManantialesGame game = (ManantialesGame) impl;
        Set<GridCell> cells = game.getGrid().getCells();
        for (GridCell cell : cells) {
            Ficha ficha = (Ficha) cell;
            if (ficha.getType().equals(TokenType.UNDEVELOPED)) {
                Ficha destination = new Ficha(ficha.getColumn(),ficha.getRow(),ficha.getColor(),ficha.getType());
                if (this.getForested() < 6) {
                    destination.setType (TokenType.MANAGED_FOREST);
                    ret.setDestinationCell(ficha);
                    break;
                } else if (this.getModerate() < 6) {
                    destination.setType (TokenType.MODERATE_PASTURE);
                    ret.setDestinationCell(ficha);
                    break;
                }
            }
        }

        /* Search for Moderate Pastures to upgrade */
        if (ret.getDestinationCell() == null) {
            for (GridCell cell : cells) {
                Ficha ficha = (Ficha) cell;
                /* Converte Moderate to Intensive */
                if (ficha.getType().equals(TokenType.MODERATE_PASTURE)) {
                    Ficha destination = new Ficha(ficha.getColumn(),ficha.getRow(),ficha.getColor(),ficha.getType());
                    destination.setType(TokenType.INTENSIVE_PASTURE);
                    ret.setCurrentCell (ficha);
                    ret.setDestinationCell(destination);
                    break;
                } else if (ficha.getType().equals(TokenType.MANAGED_FOREST)) {
                    Ficha destination = new Ficha(ficha.getColumn(),ficha.getRow(),ficha.getColor(),ficha.getType());
                    destination.setType(TokenType.MODERATE_PASTURE);
                    ret.setCurrentCell (ficha);
                    ret.setDestinationCell(destination);
                    break;
                }
            }
        }

        if (ret.getDestinationCell() == null) {
            try {
                ManantialesGame clone = (ManantialesGame) game.clone();
                boolean currentTurn = this.isTurn();

                for (int y = 0; y < game.getColumns(); y++) {
                    for (int x = 0; x < game.getRows(); x++) {
                        Ficha attempt = new Ficha (y, x, getColor(), TokenType.MANAGED_FOREST);
                        this.setTurn(currentTurn);
                        try {
                            ManantialesMove move = (ManantialesMove) clone.move(new ManantialesMove(this, attempt));
                            if (move.getStatus() == MoveStatus.EVALUATED) {
                                move.setStatus(MoveStatus.UNVERIFIED);
                                this.setTurn(currentTurn);
                                ret = move;
                                break;
                            }
                        } catch (InvalidMoveException e) {
                            continue;         
                        }
                    }
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return ret;
    }
}
