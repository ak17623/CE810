package com.fossgalaxy.game;

import com.fossgalaxy.game.orders.FuseOrder;
import com.fossgalaxy.games.tbs.GameDef;
import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.ai.AIFactory;
import com.fossgalaxy.games.tbs.ai.Controller;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.metrics.AppEvolver;
import com.fossgalaxy.games.tbs.metrics.GameMetrics;
import com.fossgalaxy.games.tbs.metrics.TurnMetrics;
import com.fossgalaxy.games.tbs.metrics.parameters.EntityProp;
import com.fossgalaxy.games.tbs.order.AttackOrder;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.order.OverdriveBuildOrder;
import com.fossgalaxy.games.tbs.parameters.GameSettings;
import io.jenetics.IntegerGene;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;

public class AppMetrics extends AppEvolver {
    private PrintStream metricData;

    public AppMetrics(SettingsIO io, GameSettings common, AIFactory ai, String mapFile) throws FileNotFoundException {
        super(io, common, ai, mapFile);
        this.metricData = new PrintStream(new File("metrics.csv"));

    }

    public static void main(String[] args) {

        SettingsIO io = SettingsIO.buildWithExtras("com.fossgalaxy.game");
        GameDef def = io.loadGameDef("space-game.json");
        AIFactory ai = new AIFactory(io, def.getEvalFileName(), def.getRuleFileName(), def.getAiFileName());


        //we need to know the settings from the file
        GameSettings settings = io.loadSettings(def);
        settings.setTurnLimit(100);

        //build the evolver using our base game
        AppMetrics evo = null;
        try {
            evo = new AppMetrics(io, settings, ai, "Level_4.json");
        } catch (FileNotFoundException e) {

        }

        /*
        START PARAMETERS
         */
        evo.addParameter(new EntityProp("Ship_1A", "movement", 1, 10, 1));
        evo.addParameter(new EntityProp("Ship_1A", "health", 1, 100, 1));
        evo.addParameter(new EntityProp("Ship_1A", "attackRange", 1, 10, 1));
        evo.addParameter(new EntityProp("Ship_1A", "atkRanged", 1, 50, 1));
        evo.addParameter(new EntityProp("Ship_2A", "movement", 1, 10, 1));
        evo.addParameter(new EntityProp("Ship_2A", "health", 1, 100, 1));
        evo.addParameter(new EntityProp("Ship_2A", "attackRange", 5, 12, 1));
        evo.addParameter(new EntityProp("Ship_2A", "atkRanged", 5, 70, 1));
        evo.addParameter(new EntityProp("Scout_A", "movement", 1, 15, 1));
        evo.addParameter(new EntityProp("Scout_A", "health", 1, 100, 1));
        evo.addParameter(new EntityProp("Scout_A", "attackRange", 3, 12, 1));
        evo.addParameter(new EntityProp("Scout_A", "atkRanged", 4, 70, 1));
        evo.addParameter(new EntityProp("Heavy_A", "movement", 1, 10, 1));
        evo.addParameter(new EntityProp("Heavy_A", "health", 1, 200, 1));
        evo.addParameter(new EntityProp("Heavy_A", "attackRange", 1, 12, 1));
        evo.addParameter(new EntityProp("Heavy_A", "atkRanged", 5, 100, 1));

        evo.addParameter(new EntityProp("Ship_1B", "movement", 1, 10, 1));
        evo.addParameter(new EntityProp("Ship_1B", "health", 1, 100, 1));
        evo.addParameter(new EntityProp("Ship_1B", "attackRange", 1, 10, 1));
        evo.addParameter(new EntityProp("Ship_1B", "atkRanged", 1, 50, 1));
        evo.addParameter(new EntityProp("Ship_2B", "movement", 1, 10, 1));
        evo.addParameter(new EntityProp("Ship_2B", "health", 1, 100, 1));
        evo.addParameter(new EntityProp("Ship_2B", "attackRange", 5, 12, 1));
        evo.addParameter(new EntityProp("Ship_2B", "atkRanged", 5, 70, 1));
        evo.addParameter(new EntityProp("Scout_B", "movement", 1, 15, 1));
        evo.addParameter(new EntityProp("Scout_B", "health", 1, 100, 1));
        evo.addParameter(new EntityProp("Scout_B", "attackRange", 3, 12, 1));
        evo.addParameter(new EntityProp("Scout_B", "atkRanged", 4, 70, 1));
        evo.addParameter(new EntityProp("Heavy_B", "movement", 1, 10, 1));
        evo.addParameter(new EntityProp("Heavy_B", "health", 1, 200, 1));
        evo.addParameter(new EntityProp("Heavy_B", "attackRange", 1, 12, 1));
        evo.addParameter(new EntityProp("Heavy_B", "atkRanged", 5, 100, 1));

        /*
        END PARAMETERS
         */


        //we run the GA to find the 'best'
        GameSettings best = evo.evolve(evo::multiGame);

        //print out the settings the GA picked
        System.out.println(best.getEntityType("Ship_1A").getProperty("atkRanged"));
        System.out.println(best.getEntityType("Ship_1B").getProperty("atkRanged"));

        //then dump everything we tried to a CSV file...
        dumpToFile(evo, "logs/summary-" + System.currentTimeMillis() + ".csv");
    }

    /**
     * GA limits
     *
     * @return a list of termination criteria for the GA.
     */
    public List<Predicate<? super EvolutionResult<IntegerGene, Double>>> getLimits() {
        return Arrays.asList(
                Limits.byFixedGeneration(5),
                Limits.byExecutionTime(Duration.ofMinutes(25))
        );
    }

    public Double fakeEvaluate(GameSettings settings) {
        return Math.random();
    }

    public double groupEval(GameSettings settings) {
        GameState start = map.buildState(settings);

        Controller p1 = ai.buildAI("playerAI", settings);
        Controller p2 = ai.buildAI("spaceAI", settings);

        Controller[] controllers = new Controller[]{
                p1,
                p2
        };

        GameState state = new GameState(start);
        GameMetrics metrics = AppEvolver.runGame(state, settings, controllers);

        int maxTurnsSinceFighting = 0;
        int turnsSinceFighting = 0;
        int numberOfFuses = 0;
        int numberOfTurns = metrics.getTurns();

        int[][] amountOfEnergy = new int[metrics.getTurns()][2];

        int[][] shipsCreated = new int[metrics.getTurns()][2];
        int[]   shipsDestroyed = new int[2];

        for (int turn = 0; turn < metrics.getTurns(); turn++) {
            TurnMetrics turnMetrics = metrics.getMetricsForTurn(turn);

            GameState turnStart = turnMetrics.getStateAtStart();

            amountOfEnergy[turn][0] = turnStart.getResource(0, "Energy");
            amountOfEnergy[turn][1] = turnStart.getResource(1, "Energy");

            boolean wasFighting = false;
            Map<UUID, Order> orderMap = turnMetrics.getOrders();
            for (Order order : orderMap.values()) {
                if (order instanceof AttackOrder) {
                    wasFighting = true;

                    AttackOrder atkOrder = (AttackOrder) order;
                    Entity target = atkOrder.getTarget(turnStart);
                    if (target.getHealth() == 0) {
                        shipsDestroyed[turnMetrics.getPlayerID()]++;
                    }

                }

                if (order instanceof OverdriveBuildOrder) {
                    shipsCreated[turn][turnMetrics.getPlayerID()]++;
                }

                if (order instanceof FuseOrder) {
                    numberOfFuses++;
                }

            }

            if (wasFighting) {
                maxTurnsSinceFighting = Math.max(maxTurnsSinceFighting, turnsSinceFighting);
                turnsSinceFighting = 0;
            } else {
                turnsSinceFighting++;
            }

        }

        List<String> stringData = new ArrayList<>();
        stringData.add(Integer.toString(maxTurnsSinceFighting));
        stringData.add(Integer.toString(numberOfFuses));
        stringData.add(Integer.toString(numberOfTurns));
        stringData.add(Arrays.toString(shipsCreated[0]));
        stringData.add(Arrays.toString(shipsCreated[1]));
        stringData.add(Arrays.toString(amountOfEnergy[0]));
        stringData.add(Arrays.toString(amountOfEnergy[1]));
        stringData.add(Integer.toString(shipsDestroyed[0]));
        stringData.add(Integer.toString(shipsDestroyed[1]));

        metricData.println(String.join(",", stringData));

        int good_SC = 0;
        int bad_SC = 0;
        double Fit_SC = 0;
        for (int turnID = 0; turnID < shipsCreated.length; turnID++) {

            if (shipsCreated[turnID][0] >= 1 || shipsCreated[turnID][1] >= 1) {
                good_SC++;
            }
            else {
                bad_SC++;
            }
        }
        Fit_SC = good_SC* 1.0 / (good_SC + bad_SC);

        int good_E = 0;
        int bad_E = 0;
        double Fit_E = 0;
        for (int turnID = 1; turnID < shipsCreated.length; turnID++) {

            if (amountOfEnergy[turnID][0] == amountOfEnergy[turnID-1][0] || amountOfEnergy[turnID][1] == amountOfEnergy[turnID-1][1]) {
                bad_E++;
            } else {
                good_E++;
            }
        }
        Fit_E = good_E * 1.0 / (good_E + bad_E);

        int Fit_SD = 0;

        if (shipsDestroyed[0] >= 20 && shipsDestroyed[1] >= 20) {
            Fit_SD = 1;
        }
        else{
            Fit_SD = 0;
        }


        int Fit_NOF;
        if (numberOfFuses >= 20){
            Fit_NOF = 1;
        }
        else{
            Fit_NOF = 0;
        }

        int Fit_NOT;
        if (numberOfTurns <= 80){
            Fit_NOT = 1;
        }
        else{
            Fit_NOT = 0;
        }

        double score = Fit_E + Fit_NOF + Fit_NOT + Fit_SC + Fit_SD;
        fitnessScores.put(settings, score);

        return score;
    }


    public double multiGame(GameSettings settings) {

        double fitness = 0.0;
        for (int game=0; game<5; game++) {
            fitness += groupEval(settings);
        }
        return fitness / 5;
    }
}
