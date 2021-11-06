package blackjack;

import java.util.InputMismatchException;
import java.util.Scanner;

@SuppressWarnings("serial")
class OutsideLimitsException extends Exception{
	OutsideLimitsException(){
	}
}

public class Blackjack {

	public static void main(String[] args) {
		
		BlackjackGame BJG = new BlackjackGame();
		BJG.initializeGame();
		do {
			BJG.getBets();
			BJG.dealCards();
			BJG.takeInsurance();
			BJG.checkBlackjack();
			BJG.gamePlays();
			BJG.displayActivePlayers();
			BJG.clearTable();
		} while (BJG.playAgain());
		
	}
}

class BlackjackGame {

	private Scanner scan = new Scanner(System.in);
	private Deck playingDeck;
	private Player[] players;
	private Player dealer = new Player(true);

	
	public void initializeGame() {
		
		//Input number of players - keep a maximum of 4 to avoid command line clutter
		System.out.print("How many players are playing? (1-4) : ");
		int noOfPlayers = getIntegerResponseImplementsLimit(scan, 1, 4);
		System.out.println();
		
		this.players = new Player[noOfPlayers];
		
		// Creating player objects
		for (int i = 0; i < noOfPlayers; i++) {
			this.players[i] = new Player();
		}
		
		// Input no of playing decks
		System.out.print("How many decks do you want to play with? (1-8) : ");
		int playingDecks = getIntegerResponseImplementsLimit(scan, 1, 8);
		this.playingDeck =  new Deck(playingDecks, true);
		System.out.println();
	}
	
	public void getBets() {
		
		for (Player player : this.players) {
			if (player.getTotalMoney() > 0) {
				System.out.println(player.getPlayerID() + ", please place your bet. Current Balance : " + player.getTotalMoney());
				player.setBettingAmount(getIntegerResponseImplementsLimit(scan, 0, player.getTotalMoney()));
			}
		}
	}
	
	public void dealCards() {
		System.out.println();
		//Deal and displaying starting cards
		for (int i = 0; i < 2; i++) {
			for (Player player : this.players) {
				if (player.getBettingAmount() == 0) {
					continue;
				} else {
					player.addCard(this.playingDeck.dealCard());
				}
			}
			this.dealer.addCard(this.playingDeck.dealCard());			
		}
		
		this.dealer.showHand(true);
		for (Player player : this.players) {
			player.showHand(false);
		}
		
	}
	
	public void takeInsurance() {
		
		if(this.dealer.getPlayerCard(1).getCardValue() > 10 || this.dealer.getPlayerCard(1).getCardValue() == 1) {
			System.out.println();
			for (Player player : this.players) {
				if (player.getTotalMoney() > 0) {
					if ((player.getTotalMoney()-player.getBettingAmount()) > (2*player.getBettingAmount())) {
						System.out.println("Would " + player.getPlayerID() + " like to get insurance? ");
						System.out.println("1 > Yes");
						System.out.println("2 > No");
						int choice = getIntegerResponseImplementsLimit(scan, 1, 2);
						if (choice == 1) {
							player.insurance();
						}
					} else {
						System.out.println(player.getPlayerID() + " doesn't have enough balance for insurance.");
						continue;
					}
				}
			}
		}
	}
	
	// Checks for first round of blackjack
	public void checkBlackjack() {
		System.out.println();
		if (this.dealer.checkBlackjack()) {
			System.out.println("Dealer has blackjack");
			for (Player player : this.players) {
				
				//Won insurance
				if(player.getPlayerStates(0)) {
					System.out.println(player.getPlayerID() + " won the insurance claim.");
					player.setTotalMoney((int)(player.getTotalMoney()+0.5*player.getBettingAmount()));
				}
				
				if (player.checkBlackjack()) {
					System.out.println(player.getPlayerID() + " pushes.");
					player.push();
				}
				else {
					System.out.println(player.getPlayerID() + " loses.");
					player.bust();
				}
				player.resetBettingAmount();
			}
		}
		else {
			System.out.println("Dealer peeks and doesn't have a Blackjack.");
			for (Player player : this.players) {
				//Lost Insurance
				if(player.getPlayerStates(0)) {
					System.out.println(player.getPlayerID() + " lost the insurance.");
					player.setTotalMoney((int)(player.getTotalMoney()-0.5*player.getBettingAmount()));
				}
				if (player.checkBlackjack()) {
					System.out.println(player.getPlayerID() + " has a Blackjack!");
					player.blackjack();
				}
			}
		}
	}
	
	// Only play the game if a dealer didn't have blackjack (primary) and neither did all players (secondary)
	public void gamePlays() {
		boolean round_settled = true;
		
		for (Player player : this.players) {
			if (player.checkBlackjack() == false) {
				round_settled = false;
			}
		}
		
		if (dealer.checkBlackjack() == true) {
			round_settled = true;
		}
		
		if (round_settled == false) {
			this.playersPlay();
			this.dealerPlays();
			this.settleBets();
		}
		
	}
	
	// Player action options
	public void playersPlay() {
		
		for (Player player : this.players) {
			if (player.getTotalMoney() > 0) {
				System.out.println();
				System.out.println(player.getPlayerID() + " Actions : ");
				System.out.println("1 > Hit");
				System.out.println("2 > Stand");
				System.out.println("3 > Double Down");
				System.out.println("4 > Surrender");
				
				if (player.getBettingAmount() > 0 && !player.checkBlackjack()) {
					System.out.println();
					int choice = getIntegerResponseImplementsLimit(scan, 1, 4);
					switch (choice) {
					case 1:
						player.addCard(this.playingDeck.dealCard());
						player.showHand(false);
						break;
					case 2:
						break;
					case 3:
						player.addCard(this.playingDeck.dealCard());
						player.doubleDown();
						player.showHand(false);
						break;
					case 4:
						player.surrender();
						break;					
					}
					
					if (choice == 1 && player.getHandValue() <= 21) {
						System.out.println();
						System.out.println("1 > Hit");
						System.out.println("2 > Stand");
						
						do {
							choice = getIntegerResponseImplementsLimit(scan, 1, 2);
							if (choice == 1) {
								player.addCard(this.playingDeck.dealCard());
								player.showHand(false);
							}
						} while (choice != 2 && player.getHandValue() <= 21);
					}
				}
			}
		}
	}
	
	public void dealerPlays() {
		
		boolean playersInGame = false;
		
		for (Player player : this.players) {
			if (player.getBettingAmount() > 0 && player.getHandValue() <= 21) {
				playersInGame = true;
			}
		}
		
		if (playersInGame) {
			while (this.dealer.getHandValue() < 17) {
				this.dealer.addCard(this.playingDeck.dealCard());
			}
		}
		this.dealer.showHand(false);
		
	}
	
	public void settleBets() {
		
		System.out.println();
		
		// If player surrendered
		for (Player player : this.players) {
			
			if (player.getPlayerStates(1)) {
				continue;
			}
			
			if (player.getBettingAmount() > 0) {
				if (player.getHandValue() > 21) {
					System.out.println(player.getPlayerID() + " has busted.");
					player.bust();
				}
				else if (player.getHandValue() == dealer.getHandValue()) {
					System.out.println(player.getPlayerID() + " has pushed.");
					player.push();
				}
				else if (player.getHandValue() < dealer.getHandValue() && dealer.getHandValue() <= 21) {
					System.out.println(player.getPlayerID() + " has lost.");
					player.bust();
				}
				else if (player.getHandValue() == 21) {
					System.out.println(player.getPlayerID() + " won with Blackjack!.");
					player.blackjack();
				}
				else {
					System.out.println(player.getPlayerID() + " won.");
					player.win();
				}
			}
		}	
	}
	
	public void displayActivePlayers() {
		for (Player player : this.players) {
			if (player.getTotalMoney() > 0) {
				System.out.println(player.getPlayerID() + " has current balance : " + player.getTotalMoney());
			}
			else {
				System.out.println(player.getPlayerID() + " is broke.");
				player.removeFromGame();
			}
		}
	}
	
	public void clearTable() {
		for (Player player : this.players) {
			player.clearHand();
		}
		this.dealer.clearHand();
	}
	
	public boolean playAgain() {
		System.out.println();
		boolean playState = true;
		
		if (noActivePlayers()) {
			playState = false;
		}
		else {
			System.out.println("Play Again?		1 > Yes		2 > No");
			
			int choice = getIntegerResponseImplementsLimit(scan, 1, 2);			
			switch(choice) {
			case 1:
				playState =  true;
				break;
			case 2:
				playState =  false;
				break;
			}
		}
		
		// Refreshed deck if low on cards
		if (this.playingDeck.getDeckLength() < 20) {
			this.playingDeck =  new Deck(2, true);
		}
		
		return playState;

	}
	
	private boolean noActivePlayers() {
		
		boolean end = false;
		
		int removedPlayers = 0;
		
		for (Player player : this.players) {
			if (player.getTotalMoney() <= 0) {
				removedPlayers++;
			}
		}
		
		if (removedPlayers == this.players.length) {
			end = true;
			System.out.println("All players are broke. Thanks for playing.");			
		}
		
		return end;
		
	}
	
	// Get a valid integer response with limits
	public static int getIntegerResponseImplementsLimit(Scanner scan, int lowerLimit, int upperLimit){
		while (true) {
			try {
				
				int response = scan.nextInt();
				
				if (response >= lowerLimit && response <= upperLimit) {
					return response;
				} else {
					throw new OutsideLimitsException();
				}
			}
			catch (InputMismatchException e) {
				scan.next();
				System.out.println("Input of erroneous type. Try Again.");
			}
			catch (OutsideLimitsException e) {
				System.out.println("Input out of suggested bounds. Try again.");
			}
		}
	}
}
