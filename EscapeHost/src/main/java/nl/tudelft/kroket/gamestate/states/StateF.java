package nl.tudelft.kroket.gamestate.states;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class StateF extends GameState {
    /** Singleton reference to logger. */
    static final Logger log = Logger.getInstance();

    private static final String STATE_NAME = "F";
    /** Class simpleName, used as tag for logging. */
    private final String className = this.getClass().getSimpleName();

    private static GameState instance = new StateF();
    private ArrayList<Integer> correctSequence;

    public static GameState getInstance() {
        return instance;
    }
    @Override
    public void start() {
        log.info(className, "Starting gameState " + getName());
        setActive(true);
        newCorrectSequence();

        String message = String.format("%s[%s]%s", Protocol.COMMAND_BEGIN, getName(),
                sequenceToString());
        host.sendAll(message);

    }

    private String sequenceToString() {
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < correctSequence.size(); i++) {
            res.append("[" + correctSequence.get(i) + "]");
        }
        return res.toString();
    }

    private void newCorrectSequence() {
        correctSequence = new ArrayList<Integer>();
        Random rand = new Random();
        int firstNumber, secondNumber, thirdNumber;
        firstNumber = rand.nextInt(40);
        secondNumber = rand.nextInt(39);
        if(secondNumber == firstNumber){
            secondNumber++;
        }
        thirdNumber = rand.nextInt(38);
        if(thirdNumber==firstNumber){
            thirdNumber++;
            if(thirdNumber==secondNumber){
                thirdNumber++;
            }
        } else if(thirdNumber==secondNumber){
            thirdNumber++;
            if(thirdNumber==firstNumber){
                thirdNumber++;
            }
        }
        correctSequence.add(firstNumber);
        correctSequence.add(secondNumber);
        correctSequence.add(thirdNumber);
    }

    @Override
    public void handleInput(String input, HashMap<String, String> parsedInput){
        log.info(className, "handleInput in state " + getName());

        // make sure we received a message for the correct state
        if (!parsedInput.containsKey("param_0") || !parsedInput.get("param_0").equals(getName())) {
            return;
        }
        if (!isActive()) {

            if (parsedInput.get("command").equals("BEGIN")) {
                start();
            }

        } else if (parsedInput.get("command").equals("VERIFY")) {
            //game completes if just one player enters the correct code
            log.info(className, "Mobile players completed the TurnLock minigame");
            stop();
        }



    }

    @Override
    public String getName() {
        return STATE_NAME;
    }

}
