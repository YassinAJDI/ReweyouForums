package in.reweyou.reweyouforums;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.reweyou.reweyouforums.classes.UserSessionManager;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    SharedPreferences sharedPreferences;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
        session = new UserSessionManager(SplashScreen.this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean firstTime = sharedPreferences.getBoolean("first", true);


       /* Intent intent = new Intent(SplashScreen.this, TutorialScreen.class);
        startActivity(intent);
        finish();*/

        if (firstTime) {
            editor.putBoolean("first", false);
            editor.apply();
            Intent intent = new Intent(SplashScreen.this, TutorialScreen.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashScreen.this, Feed.class);
            startActivity(intent);
            finish();

        }
    }
}