package com.paperrock;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;


public class Main {
    private int winningItemAmount;
    private int compMove;
    private int playerMove;
    private byte[] uniqueKey;


    // create the menu
    private String menuCreator(String[] args){

        String compTurnHMAC = this.getHMAC(this.showCompMove(this.compMove, args), this.uniqueKey);

        String menu = "HMAC:" + System.lineSeparator() +
                compTurnHMAC + System.lineSeparator() +
                "Available moves:";

        String menuEnding = System.lineSeparator() +
                "0 - exit";

        for(int i=0; i< args.length; i++){
            menu += System.lineSeparator() + (i+1) + " - " + args[i];
        }

        menu += menuEnding;
        return  menu;
    }


    private void argsValidator(String[] args){

        Set mySet = new HashSet();
        for(int i=0; i<args.length; i++) {
            if(!mySet.add(args[i])) {
                System.out.println("You should enter unique characters to play" + System.lineSeparator() +
                        "For example: paper rock scissors");
                this.exitGame(3);
            }
        }

        if(args.length < 3){
            System.out.println("You should enter 3 and more characters to play" + System.lineSeparator() +
                    "For example: paper rock scissors (3)"  );
            this.exitGame(1);
        }

        if(args.length % 2 == 0){
            System.out.println("You should enter an odd number of characters" + System.lineSeparator() +
                    "For example: paper rock scissors (3)"  );
            this.exitGame(2);
        }
    }


//gets the both comp and player moves and return the string of who won
    private String winnerDefiner(int playerTurn, int compTurn, int distance, int arrayLength) {

        if( playerTurn == compTurn) return "It's a tie!";
//distance is the amount of arguments that wins the player sign
        for(int i =1; i <= distance; i++) {

            if (playerTurn + i == compTurn) return "You lost!";
//loops the array to find the dependency
            if (playerTurn + i > arrayLength) {

                if (playerTurn + distance - arrayLength == compTurn){
                    return "You lost!";
                }
            }
        }

        return "You won!";
    }


    private void showMenu(String menu){

        System.out.println(menu);
    }


//asks player to choose the sign or to exit
    private int getPlayerMove(){

        Scanner getTurn = new Scanner(System.in);

        System.out.print("Enter your move: ");
        int playerTurn = getTurn.nextInt();

        if(playerTurn == 0) {
            System.out.println("Exiting...");
            this.exitGame(0);
        };

        return --playerTurn;
    }


// gets the number that player has entered and shows the string of what he chose
    private String showPlayerMove(int playerMove, String[] args){

        String playerTurn = "Your move: " + args[playerMove];
        return playerTurn;
    }


//calculates computer move
    private int getCompMove(String[] args){

        this.getUniqueKey();
//random num from 0 to (argument length-1)
        int randomNum = (int) (Math.random() * args.length);
        return randomNum;
    }


    private String showCompMove(int compMove, String[] args){

        String compTurn = "computerTurn: " + args[compMove];
        return compTurn;
    }

//calculate random 128 bit key
    private void getUniqueKey(){

        SecureRandom random = new SecureRandom();
        this.uniqueKey = new byte[16];
        random.nextBytes(this.uniqueKey);
    }

//gets the HMAC of the string combined from computer move turn + unique key which we generated
    private String getHMAC(String compMove, byte[] key){

        String digestStr;
        byte[] compMoveByte = compMove.getBytes(StandardCharsets.UTF_8);

        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(compMoveByte);
            messageDigest.update(key);

            byte[] digest = messageDigest.digest();
            digestStr = bytesToHex(digest);
        }
        catch (NoSuchAlgorithmException e) {

            System.err.println("I'm sorry, but It is not a valid message digest algorithm");
            digestStr = bytesToHex(compMoveByte);
        }

        return digestStr;
    }


    private void showResults(String computerTurn, String playerTurn, String whoWinnerStr, byte[] uniqueKey){

        String key = "HMAC key: " + bytesToHex(uniqueKey);

        String resultMsg = playerTurn + System.lineSeparator() +
                computerTurn + System.lineSeparator() +
                whoWinnerStr + System.lineSeparator() +
                key;

        System.out.println(resultMsg);
    }


    public void startGame(String[] args){

        this.argsValidator(args);

        this.winningItemAmount = (args.length-1)/2;

        this.compMove = getCompMove(args);

        this.showMenu(menuCreator(args));

        this.playerMove = getPlayerMove();
        if(this.playerMove == -1) return;

        showResults(showCompMove(compMove, args), showPlayerMove(playerMove, args), winnerDefiner(this.playerMove, this.compMove, winningItemAmount, args.length), this.uniqueKey);
    }


    public void exitGame(int code){
        System.exit(code);
    }

//transforms the 128 bit key to Hex
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            hex = hex.toUpperCase();
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static void main(String[] args) {

        Main game = new Main();
        game.startGame(args);
    }
}
