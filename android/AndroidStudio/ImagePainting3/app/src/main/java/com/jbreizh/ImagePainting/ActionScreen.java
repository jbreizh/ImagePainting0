package com.jbreizh.ImagePainting;

import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class ActionScreen extends LedScreen {




    private boolean isRunning, isPause;
    private int index, repeatCounter;

    // timer
    private Timer playTimer;
    private PlayTask playTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_screen);
    }




    // play
    private void play()
    {
        // if it's not running
        if (!isRunning)
        {
            // position
            if(!isPause)
            {
                //logic
                repeatCounter = repeat;
                //position
                if (isInvert)
                {
                    index = indexStop;
                }
                else
                {
                    index = indexStart;
                }
            }

            // logic
            isRunning = true;
            isPause = false;

            // launch the timer
            playTimer = new Timer();
            playTask = new PlayTask();
            playTimer.scheduleAtFixedRate(playTask,0 , delay);

        }
    }

    // pause
    private void pause()
    {
        // if it's running
        if (isRunning)
        {
            // logic
            isRunning =false;
            isPause = true;

            // cancel the timer
            playTimer.cancel();
            playTimer.purge();

            // send
            if(isEndOff)
            {
                asyncSendWrapper(frameOff.frameByte);
            }
        }
    }

    // choose the right send command depending the network
    public void sendWrapper(byte[] frame)
    {
        if(network == 1)
        {
            ClientBluetooth.send(frame);
        }
        else if (network == 2)
        {
            ClientUdp.send(frame);
        }
    }

    // choose the right send command depending the network (async version for android network policy)
    public void asyncSendWrapper(byte[] frame)
    {
        if(network == 1)
        {
            ClientBluetooth.send(frame);
        }
        else if (network == 2)
        {
            ClientUdp.asyncSend(frame);
        }
    }


    public class PlayTask  extends TimerTask
    {
        @Override
        public void run()
        {
            // if the index is in the limit
            if (indexStart <= index && index <= indexStop)
            {
                // send
                sendWrapper(Image.FrameArray[index].frameByte);
                //logic
                isRunning = true;
                // position
                if (isInvert)
                {
                    index -=1;
                }
                else
                {
                    index +=1;
                }
            }
            // the index is out of the limit
            else
            {
                // if repeat is check, go again
                if (isRepeat && repeatCounter > 1)
                {
                    // logic
                    isRunning = true;
                    repeatCounter-=1;

                    // position
                    if (isInvert)
                    {
                        index = indexStop;
                    }
                    else
                    {
                        index = indexStart;
                    }
                }
                // if bounce is check, go again
                else if ( isBounce && repeatCounter > 1)
                {
                    // logic
                    isRunning = true;
                    repeatCounter-=1;
                    isInvert = !isInvert; //invert the invert (following ??)

                    // position
                    if (isInvert)
                    {
                        index = indexStop;
                    }
                    else
                    {
                        index = indexStart;
                    }
                }
                // if repeat or bounce isn't check, stop
                else
                {
                    // logic
                    isRunning = false;

                    // send
                    if(isEndOff)
                    {
                        sendWrapper(frameOff.frameByte);
                    }

                    // cancel the timer
                    playTimer.cancel();
                    playTimer.purge();
                }
            }
        }
    }

}
