package blackjack;

public class Player {

	private Card[] hand;
	
	private int handSize;
	
	private static int totalPlayers;
	
	private String playerID;
	
	private int totalMoney;
	
	private int bettingAmount;
	
	// Insured, Surrendered
	private boolean[] playerStates = new boolean[] {false, false};
	
	
	static {
		totalPlayers = 0;
	}
	
	// Participating player created parameterless
	public Player() {
		this(false);
	}
	
	// Dealer as a player with parameter isDealer = true
	public Player(boolean isDealer) {
		
		if (isDealer) {
			this.playerID = "Dealer";
		} else {
			this.playerID = "Player " + Integer.toString(++totalPlayers);
		}
		this.totalMoney = 100000;
		this.clearHand();
		
	}
	
	// Reset hand for next round
	public void clearHand() {
		this.handSize = 0;
		this.hand = new Card[this.handSize];
	}
	
	// Add card to hand
	public boolean addCard(Card card) {
		
		Card[] tempHand = new Card[++this.handSize];
		for (int i = 0; i < this.hand.length; i++) {
			tempHand[i] = this.hand[i];
		}
		tempHand[this.handSize-1] = card;
		this.hand = null;
		this.hand = tempHand;
		
		
		return (this.getHandValue() <= 21);
	}
	
	public Card getPlayerCard(int cardIndex) {
		return this.hand[cardIndex];
	}
	
	// Get hand value - complexity of aces is taken care in this function itself
	public int getHandValue() {
		
		int handValue = 0;
		
		boolean acesFlag = false;
		
		for (Card card : this.hand) {
			
			int cardValue = card.getCardValue();
			
			if (cardValue > 10) {
				cardValue = 10;
			}
			else if (cardValue == 1){
				acesFlag = true;
			}
			handValue += cardValue;
		}
		
		if (acesFlag && handValue + 10 <= 21) {
			handValue += 10;
		}
				
		return handValue;
		
	}
	
	public String getPlayerID() {
		return this.playerID;
	}
	
	public int getBettingAmount() {
		return this.bettingAmount;
	}
	
	public void setBettingAmount(int bettingAmount) {
		this.bettingAmount = bettingAmount;
	}
	
	// Reveal hand
	public void showHand(boolean hideFirstCard) {
		
		System.out.print(this.playerID + "'s Cards : ");
		for (int i = 0; i < this.handSize; i++) {
			if (i == 0 && hideFirstCard) {
				System.out.print(" [HOLE CARD]");
			} else {
				System.out.print("	" + this.hand[i].toString());
			}
		}
		System.out.println();
		
	}
	
	public int getTotalMoney() {
		return this.totalMoney;
	}
	public void setTotalMoney(int totalMoney) {
		this.totalMoney = totalMoney;
	}
	
	public boolean getPlayerStates(int state) {
		return this.playerStates[state];
	}
	
	public void resetTotalMoney() {
		this.totalMoney = 100000;
	}
	
	public void resetBettingAmount() {
		this.bettingAmount = 0;
		this.playerStates[0] = this.playerStates[1] = false;
	}
	
	public void removeFromGame() {
		this.setTotalMoney(-1);
	}
	
	public void blackjack() {
		this.totalMoney += (int)(1.5*this.bettingAmount);
		this.resetBettingAmount();
	}
	
	public void win() {
		this.totalMoney += this.bettingAmount;
		this.resetBettingAmount();
	}
	
	public void bust() {
		this.totalMoney -= this.bettingAmount;
		this.resetBettingAmount();
	}
	
	public void push() {
		this.resetBettingAmount();
	}
	
	public void doubleDown() {
		this.bettingAmount *= 2;
	}
	
	public void surrender() {
		this.totalMoney = (int)(this.totalMoney - 0.5*this.bettingAmount);
		this.playerStates[1] = true;
	}
	
	public void insurance() {
		this.playerStates[0] = true;
	}
	
	public boolean checkBlackjack() {
		if (this.getHandValue() == 21) {
			return true;
		}
		else {
			return false;
		}
	}
}
