package com.example.vlad.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "MESSAGE";

    private PlayerClock player1;
    private PlayerClock player2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1 = new PlayerClock("Player 1", this, (Button)findViewById(R.id.button1));
        player2 = new PlayerClock("Player 2", this, (Button)findViewById(R.id.button2));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public void sendMessage(View view) {
//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        EditText editText = (EditText) findViewById(R.id.edit_message);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
//    }

    public void clockPressed(View view) {
        switch(view.getId()) {
            case R.id.button1:
                player1.stop();
                player2.start();
                break;
            case R.id.button2:
                player2.stop();
                player1.start();
                break;
            default:
                throw new RuntimeException("Undefined button pressed");
        }
    }

    public void handleLoss(Button button, String playerName) {
        button.setBackgroundColor(PlayerClock.COLOUR_LOST);
        Log.e("PlayerClock", playerName + " lost!");
        player1.kill();
        player2.kill();
    }

    class PlayerClock {
        private static final int COLOUR_ACTIVE = 0xFF00FF00;
        private static final int COLOUR_INACTIVE = 0xD3D3D3;
        private static final int COLOUR_LOST = 0xFFFF0000;

        private long timeLeft = 6;
        private Timer timer;
        private AppCompatActivity parentActivity;
        private Button button;
        private boolean paused = true;
        private boolean killed = false;
        private String playerName;

        private Runnable clockTick = new Runnable() {
            public void run() {
                if (paused) return;

                timeLeft--;

                button.setText(formatTime(timeLeft));

                if (timeLeft < 1) {
                    loss();
                    paused = true;
                    return;
                }
            }
        };

        public PlayerClock(String playerName, AppCompatActivity parentActivity, Button button) {
            this.playerName = playerName;
            this.parentActivity = parentActivity;
            this.button = button;

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateButton();
                }
            }, 0, 1000);
        }

        public void start() {
            if (killed) return;
            paused = false;
            button.setBackgroundColor(COLOUR_ACTIVE);
        }

        public void stop() {
            if (killed) return;
            paused = true;
            button.setBackgroundColor(COLOUR_INACTIVE);
        }

        public void kill() {
            killed = true;
            timer.cancel();
        }

        private void updateButton() {
            parentActivity.runOnUiThread(clockTick);
        }

        private void loss() {
            paused = true;
            handleLoss(button, playerName);
        }

        private String formatTime (long secondsLeft) {
            return String.format("%2d:%02d", secondsLeft / 60, secondsLeft % 60);
        }
    }
}
