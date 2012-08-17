package com.hornmicro.spaceinvaders

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener
import javax.sound.sampled.DataLine.Info

class Sound implements LineListener {
    Clip clip
    
    Sound(String sound) {
        File audio1 = new File(sound)
        URL url = audio1.toURI().toURL()
        
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(loadStream(url.openStream()))
        AudioFormat af = audioInputStream.getFormat()
        int size = (int) (af.getFrameSize() * audioInputStream.getFrameLength())
        
        DataLine.Info info = new DataLine.Info(Clip.class, af, size)
        
        byte[] audio = new byte[size]
        audioInputStream.read(audio, 0, size)
        
        this.clip = (Clip) AudioSystem.getLine(info)
        clip.open(af, audio, 0, size)
    }
    
    void update(LineEvent le) {
        if (le.getType().equals(LineEvent.Type.STOP)){
            println "Finished"
        }
    }
    
    void stop() {
        clip.stop()
    }
    
    void play() {
        clip.setMicrosecondPosition(0)
        clip.start()
    }
    
    void loop() {
        clip.loop(clip.LOOP_CONTINUOUSLY)
    }
    
    
    ByteArrayInputStream loadStream(InputStream inputstream) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream()
        byte[] data = new byte[1024]
        for(int i = inputstream.read(data); i != -1; i = inputstream.read(data))
        bytearrayoutputstream.write(data, 0, i)

        inputstream.close()
        bytearrayoutputstream.close()
        data = bytearrayoutputstream.toByteArray()
        return new ByteArrayInputStream(data)
    }
    
    

    static main(args) {
        new Sound("sounds/shipfire.wav").play()
        
        Thread.sleep(800)
        
        new Sound("sounds/invaderhit.wav").play()
        
        Thread.sleep(1000)
    }

    

}
