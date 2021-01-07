package com.example.psyad9.mp3player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;


public class MP3Service extends Service {

    public static final String CHANNEL_ID = "channel_id";
    private static final String TAG = "";
    Notification notification;
    NotificationManager notificationManager;
    Binder MP3Binder = new MP3Binder();
    MP3Player player;

    //Allows activity to access public methods in service
    public class MP3Binder extends Binder
    {
        MP3Service getService()
        {
            return MP3Service.this;

        }
    }

    //tells the OS not to recreate the service if the system runs out of memory
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }

    //onCreate of the service, playing MP3 indicator notification is created and displayed
    @Override
    public void onCreate() {
        super.onCreate();
        createchannel();
        buildnot();

    }

    //When the service is bound to, it makes a new MP3 player instance for media playback
    @Override
    public IBinder onBind(Intent intent) {
        player = new MP3Player();
        return MP3Binder;
    }

    //method handling rebindin
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    //method handling the activity unbinding the service
    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    //service method that takes in a URI and gives it to media player to start playback
    public void loadservice(String file)
    {
        player.load(file);

    }
    //service method that starts playback of mediaplayer instance
    public void playservice()
    {
        player.play();
    }

    //service method that pauses playback of mediaplayer instance
    public void pauseservice()
    {
        player.pause();
    }

    //service method that stops playback of mediaplayer instance
    public void stopservice()
    {
        player.stop();
    }

    //service method that returns the duration of the currently playing track from MP3Player
    public int getDur(){
        return player.getDuration();
    }

    //service method that returns the progress of the currently playing track from MP3Player
    public int getProg(){
        return player.getProgress();
    }

    //method that builds and starts a new notification with content, priority, icon and a pending intent that will return the user to the main class
    public void buildnot()
    {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setContentTitle("A track is currently playing.")
                .setContentText("Click here to return to MP3Player")
                .build();
        startForeground(1,notification);
    }

    //method that creates a channel and manager for the notification
    public void createchannel()
    {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //method that stops mp3 playback in MP3Player when the service is destroyed
    public void onDestroy()
    {
        super.onDestroy();
        player.stop();
    }
}
