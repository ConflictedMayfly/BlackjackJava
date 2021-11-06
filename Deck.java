package blackjack;

import java.util.Random;

public class Deck {

	private Card[] playingCards;
	
	private int noOfCards;
	
	
	// Parameterless deck
	public Deck() {
		
		this(1, false);
	
	}
	
	// Create deck
	public Deck(int noOfDecks, boolean shuffle) {
		
		this.noOfCards = noOfDecks * 52;
		this.playingCards = new Card[this.noOfCards];
		
		int cardIndex = 0;
		
		for (int d = 0; d < noOfDecks; d++) {
			for (int s = 0; s < 4; s++) {
				for (int n = 1; n < 14; n++) {
					this.playingCards[cardIndex++] = new Card(Suit.values()[s], n);
				}
			}
		}
		
		if (shuffle) 
		{
			this.shuffle();
		};
		
	}
	
	private void shuffle() {
		
		Random rng = new Random();
		for (int i = 0; i < playingCards.length; i++) {
			int rngIndex = rng.nextInt(playingCards.length);
			Card temp = playingCards[rngIndex];
			playingCards[rngIndex] = playingCards[i];
			playingCards[i] = temp;
		}
		
	}
	
	public Card dealCard() {
		
		Card nextCard = this.playingCards[0];
		
		this.shift();
		
		return nextCard;
		
	}
	
	private void shift() {
		Card[] tempDeck = new Card[--this.noOfCards];
		for (int i = 1; i < this.playingCards.length; i++) {
			tempDeck[i-1] = this.playingCards[i];
		}
		this.playingCards = null;
		this.playingCards = tempDeck;
	}
	
	// Show deck - for debugging purposes
	public void revealDeck(int nextCards) {
		System.out.println();
		for (int c = 0; c < nextCards; c++) {
			System.out.print(this.playingCards[c].toString() + " ");
		}
		System.out.printf("[+ %d others]", this.noOfCards-nextCards);
	}
	
	public int getDeckLength() {
		return this.playingCards.length;
	}
}

class Card {
	
	private Suit cardSuit;
	
	private int cardValue;
	
	Card (Suit cardSuit, int cardValue) {
		this.cardSuit = cardSuit;
		this.cardValue = cardValue;
	}
	
	public int getCardValue() {
		return this.cardValue;
	}
	
	// Return card values
	@Override
	public String toString() {
		String cardValue = "JOKER";
		
		switch(this.cardValue) {
		
		case 1:
			cardValue = "A";
			break;
		
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			cardValue = Integer.toString(this.cardValue);
			break;
		
		case 11:
			cardValue = "J";
			break;
			
		case 12:
			cardValue = "Q";
			break;
			
		case 13:
			cardValue = "K";
			break;
		
		}
		
		return this.cardSuit.getIcon()+cardValue;
	}
	
}

enum Suit {
	Clubs("\u2663\uFE0F"), Diamonds("\u2666\uFE0F"), Hearts("\u2665\uFE0F"), Spades("\u2660\uFE0F");
	
	private final String icon;
	
	Suit(String icon){
		this.icon = icon;
	}
	
	// Return suit icon
	public String getIcon() {
		return this.icon;
	}
}
