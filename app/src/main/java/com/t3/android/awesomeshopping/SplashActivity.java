package com.t3.android.awesomeshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.t3.android.awesomeshopping.Model.User;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    public static void launch(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseUtils.enablePersistence();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseUtils.getCurrentUser() == null){
            FirebaseUtils.launchSignin(this);
        } else {
            MyListsActivity.launch(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FirebaseUtils.RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUtils.currentUser = new User(FirebaseUtils.getCurrentUser());

                FirebaseUtils.userRef().setValue(FirebaseUtils.currentUser);
                MyListsActivity.launch(this);
                finish();
            } else {
                if (response != null && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                    return;
                }

                if (response != null){
                    Log.e(TAG ,"Sign-in error: ", response.getError());
                }
            }
        }
    }
}
