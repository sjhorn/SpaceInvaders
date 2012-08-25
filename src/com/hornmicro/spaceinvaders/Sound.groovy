package com.hornmicro.spaceinvaders

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineEvent
import javax.sound.sampled.LineListener
import javax.sound.sampled.DataLine.Info

import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell

class Sound implements LineListener {
    Clip clip
    
    Sound(String sound) {
        InputStream soundInputStream = getClass().getResourceAsStream(sound)
        if(soundInputStream == null) {
            File audio1 = new File("sounds/${sound}")
            URL url = audio1.toURI().toURL()
            soundInputStream = url.openStream()
        }
        
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(loadStream(soundInputStream))
        AudioFormat af = audioInputStream.getFormat()
        int size = (int) (af.getFrameSize() * audioInputStream.getFrameLength())
        
        DataLine.Info info = new DataLine.Info(Clip.class, af, size)
        
        byte[] audio = new byte[size]
        audioInputStream.read(audio, 0, size)
        
        this.clip = (Clip) AudioSystem.getLine(info)
        clip.open(af, audio, 0, size)
        clip.addLineListener(this)
    }
    
    void update(LineEvent le) {
        if (le.getType().equals(LineEvent.Type.STOP)){
            //println "Finished"
        }
        if (le.getType().equals(LineEvent.Type.CLOSE)){
            //println "Should Close"
        }
    }
    
    void stop() {
        clip.stop()
    }
    
    void close() {
        clip.drain()
        clip.close()
    }
    
    void play() {
        if(clip.isRunning()) {
            clip.stop()
        }
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
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        Button button = new Button(shell, SWT.PUSH)
        button.text = "Play"
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                new Sound("shipfire.wav").play()
        
                Thread.sleep(800)
                
                new Sound("invaderhit.wav").play()
                
                Thread.sleep(1000)
            }
        })

        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
        System.exit(0)
    }

    

}
