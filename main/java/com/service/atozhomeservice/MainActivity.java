package com.service.atozhomeservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.service.atozhomeservice.databinding.ActivityMainBinding;
import com.service.atozhomeservice.view.LocationTracker;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Instance Variables
    private ActivityMainBinding binding;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //static variables
    private static final byte RC_SIGN_IN = 123;
    private boolean isLoggedIn = false;


    /**
     * Initializes the main activity. Sets up edge-to-edge layout,
     * initializes Firebase Authentication and Firestore, configures
     * Google Sign-In options, and checks for an already signed-in user
     * to proceed directly to the home screen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Initialize Firebase Auth & Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        // Check if already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            navigateToHome();
            return;
        }

        googleSignInOptionGenerator();


    /*    if (currentUser != null) {
            checkUserExistsInFirestore(currentUser);
        }*/


    }

    /*************************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : googleSignInOptionGenerator
     * Description    : Configures Google Sign-In options and initializes the sign-in client.
     *                  Sets up the Google Sign-In button's click listener to launch the authentication flow.
     * Called By      : onCreate(Bundle savedInstanceState)
     * Parameters     : None
     * Return         : None
     *************************************************************************************************************/

    private void googleSignInOptionGenerator()
    {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        binding.googleSignInButton.setOnClickListener(v->
            signInWithGoogle()
        );

    }
    /*************************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : signInWithGoogle
     * Description    : Initiates the Google Sign-In flow. It first signs out any previously authenticated user,
     *                  revokes access to reset session state, and then launches the Google Sign-In intent,
     *                  allowing the user to choose a Gmail account for authentication.
     * Called By      : googleSignInOptionGenerator()
     * Parameters     : None
     * Return         : None
     *************************************************************************************************************/

    private void signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            googleSignInClient.revokeAccess().addOnCompleteListener(this, revokeTask -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });
        });
    }

    /*************************************************************************************************************
     * Method Name   : onActivityResult
     * Description   : Receives the result from the Google Sign-In intent. If the request code matches RC_SIGN_IN,
     *                 it attempts to retrieve the GoogleSignInAccount from the intent data. If successful,
     *                 it passes the ID token to Firebase for authentication. Displays appropriate toast messages
     *                 in case of failure or exception.
     * Called By     : System callback after calling startActivityForResult()
     * Parameters    : @param requestCode - The request code passed to startActivityForResult()
     *                 @param resultCode  - The result code returned by the child activity
     *                 @param data        - The Intent returned from the launched activity
     * Return        : void
     *************************************************************************************************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    Toast.makeText(this, "Google Sign-In Failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                // Log.e("LoginScreen", "Google Sign-In Failed", e);
                Toast.makeText(this, "Google Sign-In Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*************************************************************************************************************
     * Method Name   : firebaseAuthWithGoogle
     * Description   : Authenticates a user with Firebase using the ID token retrieved from Google Sign-In.
     *                 On successful authentication, retrieves the current FirebaseUser and checks if the user
     *                 exists in Firestore. Displays a toast message if authentication fails.
     * Called By     : onActivityResult() during Google Sign-In flow
     * Parameters    : @param idToken - The ID token returned from GoogleSignInAccount
     * Return        : void
     *************************************************************************************************************/

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserExistsInFirestore(user);
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*************************************************************************************************************
     * Method Name   : checkUserExistsInFirestore
     * Description   : Checks whether a Firebase-authenticated user has an existing document in the "users"
     *                 collection within Firestore. If the document exists and contains data, the user is
     *                 considered registered and redirected to the home screen. If not, the user is signed out
     *                 and redirected to the signup screen. Also handles Firestore query failures with a toast.
     * Called By     : firebaseAuthWithGoogle(String idToken)
     * Parameters    : @param user - The currently authenticated FirebaseUser
     * Return        : void
     *************************************************************************************************************/
    private void checkUserExistsInFirestore(FirebaseUser user) {
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getData() != null && !documentSnapshot.getData().isEmpty()) {
                        navigateToHome();
                    } else {
                        // 🚀 Auto-register new user
                        Map<String, Object> defaultUser = new HashMap<>();
                        defaultUser.put("customerId", user.getUid());
                        defaultUser.put("email", user.getEmail());
                        defaultUser.put("createdAt", FieldValue.serverTimestamp());

                        db.collection("users").document(user.getUid()).set(defaultUser)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(MainActivity.this, "Welcome, new user! 🎉", Toast.LENGTH_SHORT).show();
                                    navigateToHome();
                                })
                                .addOnFailureListener(err -> {
                                    Toast.makeText(MainActivity.this, "Could not register user: " + err.getMessage(), Toast.LENGTH_SHORT).show();
                                    logoutAndNavigateToSignup();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error checking user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /*************************************************************************************************************
     * Method Name   : logoutAndNavigateToSignup
     * Description   : Signs the user out from both Firebase Authentication and Google Sign-In. It also revokes
     *                 the user's access token to prevent automatic re-login. After revocation, prompts the user
     *                 with a toast message indicating that their account is not registered and they need to sign up.
     * Called By     : checkUserExistsInFirestore(FirebaseUser user)
     * Parameters    : None
     * Return        : void
     *************************************************************************************************************/

    private void logoutAndNavigateToSignup() {
        //Firebase Signout
        FirebaseAuth.getInstance().signOut();
        //Google Signout
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            googleSignInClient.revokeAccess().addOnCompleteListener(this, revokeTask -> {
                Toast.makeText(MainActivity.this, "Account not registered. Please sign up.", Toast.LENGTH_LONG).show();
            });
        });
    }

    /*************************************************************************************************************
     * Method Name   : navigateToHome
     * Description   : Creates an intent to navigate from the current activity to the HomeFragment. Sets intent
     *                 flags to clear the current task stack and start a new one, ensuring that the login screen
     *                 is removed from the back stack. Launches the new activity and finishes the current one.
     * Called By     : checkUserExistsInFirestore(FirebaseUser user)
     * Parameters    : None
     * Return        : void
     *************************************************************************************************************/

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, LocationTracker.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}