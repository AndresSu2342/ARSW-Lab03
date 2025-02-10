/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class Producer extends Thread {

    private BlockingQueue<Integer> queue = null;

    private int dataSeed = 0;
    private Random rand=null;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
        rand = new Random(System.currentTimeMillis());
    }

    @Override
    public void run() {
        while (true) {

            dataSeed = dataSeed + rand.nextInt(100);
            System.out.println("Producer added " + dataSeed);

            try {
                ((BlockingQueue<Integer>) queue).put(dataSeed);
                // Para verificar que solo hayan n (stockLimit) elementos como maximo en la cola
                System.out.println("Size queue = " + queue.size());

            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
