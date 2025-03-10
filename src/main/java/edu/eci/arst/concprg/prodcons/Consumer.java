/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{

    private BlockingQueue<Integer> queue;


    public Consumer(BlockingQueue<Integer> queue){
        this.queue=queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int elem = queue.take(); // Bloquea hasta que haya un elemento disponible
                System.out.println("------------------------------------------------------------------------------------------");
                System.out.println("Consumer consumes " + elem);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}