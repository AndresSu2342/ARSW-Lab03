package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private volatile boolean paused = false;  // Estado de pausa

    private volatile boolean stopped = false; // Estado de parada



    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

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
                        wait();  // Espera hasta que se reanude
                    } catch (InterruptedException e) {
                        return; // Salir del hilo si es interrumpido
                    }
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void fight(Immortal i2) {

        if (i2.getHealth() > 0) {
            i2.changeHealth(i2.getHealth() - defaultDamageValue);
            this.health += defaultDamageValue;
            updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
        } else {
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        }

    }

    // Método para poner en pausa el hilo
    public synchronized void pauseThread() {
        paused = true;
    }

    // Método para reanudar el hilo
    public synchronized void resumeThread() {
        paused = false;
        notify();
    }

    // Método para detener el hilo
    public synchronized void stopThread() {
        stopped = true;
        interrupt();  // Interrumpimos el hilo si está esperando o en un estado bloqueado
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }


    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

}
