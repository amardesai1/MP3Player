package com.example.psyad9.mp3player;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.example.psyad9.mp3player.MP3Service.MP3Binder;

public class MainActivity extends AppCompatActivity {

    private ServiceConnection c = null;
    ListView lv;
    MP3Service myservice;
    String file = "";
    Intent MP3Intent;
    boolean loaded = false;
    boolean playing = false;
    ProgressBar pb;
    TextView progText;
    TextView dur;


    //onCreate method that sets the UI, gets handles on the UI elements, fills the list with MP3 files and makes these files clickable
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {

        //Creates UI interface
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gets access to instances of UI elements that need to be changed
        pb = findViewById(R.id.progressBar);
        progText = findViewById(R.id.progText);
        dur = findViewById(R.id.durText);

        //Creates new service intent, starts foreground service and binds the service
        MP3Intent = new Intent(this, MP3Service.class);
        ContextCompat.startForegroundService(this , MP3Intent);
        bindService(MP3Intent, connection, Context.BIND_AUTO_CREATE);

        //gets instance of listview in UI and fills it with MP3 files
        lv = (ListView) findViewById(R.id.listView);
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.IS_MUSIC + "!= 0",
                null,
                null);
        lv.setAdapter(new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[] { MediaStore.Audio.Media.DATA},
                new int[] { android.R.id.text1 }));

        //onClick listener for list view elements that starts progress bar and load method in service is called
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onItemClick(AdapterView<?> myAdapter,
                                    View myView,
                                    int myItemInt,
                                    long mylng) {
                Cursor c = (Cursor) lv.getItemAtPosition(myItemInt);
                file = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));

                pb.postDelayed(updateVal, 1000);

                myservice.loadservice(file);
                loaded = playing = true;
            }
        });

    }
    // Creates interface for monitoring the MP3Service service
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder s) {
            MP3Binder binder = (MP3Binder) s ;
            myservice = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    // Lines 95-116 are adapted from code read on stack overflow
    //Runnable that continually updates the progress bar value depending on the prgress of the playing track
    private Runnable updateVal = new Runnable() {
        @Override
        public void run() {
            if (myservice.getDur() != 0) {
                int prog = myservice.getProg() * pb.getMax() / myservice.getDur();
                pb.setProgress(prog);
                if(playing)
                    pb.postDelayed(updateVal, 1000);
                dur.setText(formatDur(myservice.getDur()/1000));
                progText.setText(formatDur(myservice.getProg()/1000));
            }
        }
    };

    //Method converts millisecond time stamps to a seconds, minute, hour format for display in the apps UI
    @SuppressLint("DefaultLocale")
    public static String formatDur(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }

    //When user presses play button in activity, play method in service is called
    public void onPlay(View view) {
        if (loaded) {
            myservice.playservice();
            playing = true;
        }
    }

    //When user presses pause button in activity, pause method in service is called
    public void onPausep(View view)
    {
        if(loaded)
        {
            myservice.pauseservice();
            playing = false;
        }
    }

    //When user presses stop button in activity, music is stopped, progress bar is reset and service is destroyed
    public void onStopp(View view)
    {
        if(loaded)
        {
            pb.setProgress(0);
            myservice.stopservice();
            loaded=playing = false;
        }

    }

    //When activity is destroyed, music playback is stopped
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unbindService(connection) ;
        Intent intent = new Intent(this , MP3Service.class) ;
        stopService(intent) ;
    }
}
