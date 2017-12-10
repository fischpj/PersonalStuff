/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nomad_ultrasonic;

import com.pi4j.io.gpio.*;
import java.util.EmptyStackException;

public class Nomad_Ultrasonic {
    //GPIO Pins

    double result = 0;

    private static GpioPinDigitalOutput sensorTriggerPin;
    private static GpioPinDigitalInput sensorEchoPin;

    final static GpioController gpio = GpioFactory.getInstance();

    public static void main(String[] args) throws InterruptedException {
        sensorTriggerPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29); // Trigger pin as OUTPUT
        sensorEchoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, PinPullResistance.PULL_DOWN); // Echo pin as INPUT

        new Nomad_Ultrasonic().run();
    }

    public void ping() throws InterruptedException
    {
        for(int i=1000;i>0;i--) {
            double distance = getRange();
            
            System.out.print("Range: " + distance);
            
            Thread.sleep(1000);
        }
            
    }
    
    
    public void run() throws InterruptedException {
        //sensorTriggerPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29); // Trigger pin as OUTPUT
        //sensorEchoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, PinPullResistance.PULL_DOWN); // Echo pin as INPUT

        while (true) {
            try {
                sensorTriggerPin.low();

                Thread.sleep(2000);
                sensorTriggerPin.high(); // Make trigger pin HIGH
                Thread.sleep((long) 0.01);// Delay for 10 microseconds
                sensorTriggerPin.low(); //Make trigger pin LOW

                long startTime = System.currentTimeMillis();

                while (sensorEchoPin.isLow()) { //Wait until the ECHO pin gets HIGH
//                    if ((System.currentTimeMillis() - startTime) >= 40) {
//                        break;
//                    }
                }

                long pingTime = System.nanoTime(); // Store the surrent time to calculate ECHO pin HIGH time.

                while (sensorEchoPin.isHigh()) { //Wait until the ECHO pin gets LOW
//                    if ((System.currentTimeMillis() - pingTime) >= 40) {
//                        break;
//                    }
                }

                long endTime = System.nanoTime(); // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.

                System.out.println("Distance :" + ((((endTime - pingTime) / 1000) / 2) / 29.1) + " cm"); //Printing out the distance in cm  
                Thread.sleep(1000);

                System.out.print("Looping");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public double getRange() {

        System.out.println("Range Finder Triggered");
        try {
            // fire the trigger pulse 
            sensorTriggerPin.high();

            Thread.sleep(20);
        } catch (InterruptedException e) {

            e.printStackTrace();
            System.out.println("Exception triggering range finder");
        }
        sensorTriggerPin.low();

        // wait for the result
        double startTime = System.currentTimeMillis();
        double stopTime = 0;
        do {
            stopTime = System.currentTimeMillis();
            if ((System.currentTimeMillis() - startTime) >= 40) {
                break;
            }
        } while (sensorEchoPin.getState() != PinState.HIGH);

        // calculate the range. If the loop stopped after 38 ms set the result
        // to -1 to show it timed out.
        if ((stopTime - startTime) <= 38) {
            result = (stopTime - startTime) * 165.7;
        } else {
            System.out.println("Timed out");
            result = -1;
        }

        return result;

    }
}
