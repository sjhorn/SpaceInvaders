package com.hornmicro.spaceinvaders

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

class PlaySound {

    static main(args) {
        
        println Math.random()
        
        /*
        Clip clip2 = null;
        
        if (clip2==null) {
        clip2 = AudioSystem.getClip();
        // Use URL (instead of File) to read from disk and JAR.
        URL url = new File("sounds/test.wav").toURI().toURL() //this.getClass().getResource("sounds/test.wav");
        // Set up an audio input stream piped from the sound file.
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
        // Get a clip resource.
        // Open audio clip and load samples from the audio input stream.
        clip2.open(audioInputStream);
        }
        // Stop the player if it is still running
        if (clip2.isRunning()) clip2.stop();
        clip2.setFramePosition(0); // rewind to the beginning
        clip2.loop(3)
        clip2.start();     // Start playing
        while(clip2.isRunning()) {
            Thread.sleep(1000)
        }
        */
        
    }

}
