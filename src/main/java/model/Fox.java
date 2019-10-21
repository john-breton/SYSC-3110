package model;

/**
 * A class representing a Fox piece.
 * 
 * @author Abdalla El Nakla
 */
public class Fox extends Piece {
	private Direction direction;
	private FoxType foxType;
	private boolean id;

	/**
	 * An enumeration representing the head and tail of the fox
	 */
	public enum FoxType {
		HEAD, TAIL
	}
	
	/**
	 * An enumeration representing the direction the fox is going
	 */
	public enum Direction {
		VERTICAL, HORIZONTAL
	}

	/**
	 * Construct a new fox given the head or tail and the direction
	 * 
	 * @param foxType The fox type of the fox, as a FoxType
	 * @param direction The direction given for the Fox
	 * @param id The id of the fox, used to differentiate different foxes
	 */
	public Fox(FoxType foxType, Direction direction, boolean id) {
		super(PieceType.FOX);
		this.foxType = foxType;
		this.direction = direction;
		this.id = id;
	}
	
	/**
	 * @return the Direction
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * @return the FoxType, Whether it is heads or tails
	 */
	public FoxType getFoxType() {
		return foxType;
	}
	
	/**
	 * @return The id of this fox.
	 */
	public boolean getId() {
		return id;
	}

	@Override
	public boolean canMove(Move move) {
		return ((this.direction.equals(Direction.HORIZONTAL) && (move.direction() == 0))
				|| (this.direction.equals(Direction.VERTICAL) && (move.direction() == 1)));
	}

	@Override
	public String toShortString() {
		if (this.foxType.equals(FoxType.HEAD)) {
			return "FH";
		} else {
			return "FT";
		}

	}
}
