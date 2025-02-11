package edu.eci.arsw.highlandersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents an "Immortal" entity in the simulation, capable of fighting against other immortals.
 * The immortals compete to survive, and when one is defeated, it is removed from the simulation.
 * <p>
 * This class is designed to handle concurrent execution without using synchronization for performance reasons.
 */
public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;

    private int health;

    private int defaultDamageValue;

    private static List<Immortal> immortalsPopulation = Collections.synchronizedList(new ArrayList<>());

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private volatile boolean paused = false;  // Estado de pausa

    private volatile boolean stopped = false; // Estado de parada


    /**
     * Constructs an Immortal instance.
     *
     * @param name The name of the immortal.
     * @param immortalsPopulation The shared list of all immortals.
     * @param health Initial health points of the immortal.
     * @param defaultDamageValue The damage this immortal can deal.
     * @param ucb Callback for reporting updates.
     */
    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        Immortal.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    /**
     * Runs the fight simulation for this immortal.
     * The immortal selects an opponent at random and engages in a fight.
     * If an opponent is already dead, they are removed from the population.
     */
    public void run() {

        while (!stopped) {

            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            this.fight(im);

            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Handles the fight between this immortal and another.
     * The fight is determined by reducing the opponent's health. If the opponent's health reaches zero,
     * they are removed from the simulation.
     *
     * @param i2 The immortal to fight against.
     */
    public void fight(Immortal i2) {
        Immortal first = this.hashCode() < i2.hashCode() ? this : i2;
        Immortal second = this.hashCode() < i2.hashCode() ? i2 : this;

        synchronized (first) {
            synchronized (second) {
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                } else {
                    // Se elimina de manera segura sin problemas de concurrencia
                    //immortalsPopulation.remove(i2);
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }
    }

    /**
     * Pauses the immortal's execution.
     */
    public synchronized void pauseThread() {
        paused = true;
    }

    /**
     * Resumes the immortal's execution.
     */
    public synchronized void resumeThread() {
        paused = false;
        notify();
    }

    /**
     * Stops the immortal's execution.
     */
    public synchronized void stopThread() {
        stopped = true;
    }

    /**
     * Sets the health of the immortal.
     *
     * @param v The new health value.
     */
    public void changeHealth(int v) {
        health = v;
    }

    /**
     * Gets the health of the immortal.
     *
     * @return The current health value.
     */
    public int getHealth() {
        return health;
    }


    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

}
