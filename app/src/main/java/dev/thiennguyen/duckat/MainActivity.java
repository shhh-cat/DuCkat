package dev.thiennguyen.duckat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private View nav_header;
    public static int SIGN_IN_REQUEST_CODE = 1;
    ImageView user_avatar;
    TextView user_email,user_name;
    private String TAG;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance() ;


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu,menu);
//        return true;
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        nav_header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        //header
        user_avatar = (ImageView)nav_header.findViewById(R.id.user_avatar);
        user_email = (TextView)nav_header.findViewById(R.id.user_email);
        user_name = (TextView)nav_header.findViewById(R.id.user_name);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Check if not sign-in then navigate Signin page
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startlogin();
        }
        else
        {
            Snackbar.make(drawer,"Welcome "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),Snackbar.LENGTH_SHORT).show();
            user_name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            user_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

            //Load content
            //default menuchat open
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatFragment()).commit();
                navigationView.setCheckedItem(R.id.menu_chat);
            }
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        switch (item.getItemId()) {
            case R.id.menu_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatFragment()).commit();
                navigationView.setCheckedItem(R.id.menu_chat);
                break;
            case R.id.menu_friends:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FriendslistFragment()).commit();
                navigationView.setCheckedItem(R.id.menu_friends);
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AboutFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_about);
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(drawer,"You have been signed out.", Snackbar.LENGTH_SHORT).show();
                                startlogin();
                            }
                });
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {
                db.collection("users")
                    .document(FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid())
                    .set(new userObject(
                            auth.getCurrentUser().getUid(),
                            auth.getCurrentUser().getDisplayName(),
                            auth.getCurrentUser().getEmail()
                    ))
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(drawer,"Get user failed", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                NavigationView navigationView = findViewById(R.id.nav_view);
                Snackbar.make(drawer,"Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatFragment()).commit();
                navigationView.setCheckedItem(R.id.menu_chat);
            }
            else{
                Snackbar.make(drawer,"We couldn't sign you in.Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Snackbar.make(drawer,R.string.sign_in_cancelled, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(drawer,R.string.no_internet_connection, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Snackbar.make(drawer,R.string.unknown_error, Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    private void startlogin(){
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()
                        ))
                        .setTosAndPrivacyPolicyUrls(
                                "https://policies.google.com/terms",
                                "https://policies.google.com/privacy"
                        )
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.GreenTheme)
                        .setLogo(R.mipmap.web_hi_res_512)
                        .build(),SIGN_IN_REQUEST_CODE);
    }

}
