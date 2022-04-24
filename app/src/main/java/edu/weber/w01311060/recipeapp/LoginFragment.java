package edu.weber.w01311060.recipeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.weber.w01311060.recipeapp.models.Ingredient;
import edu.weber.w01311060.recipeapp.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private onLoginListener mCallBack;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private RecipeViewModel vm;

    public LoginFragment()
    {
        // Required empty public constructor
    }

    public interface onLoginListener
    {
        void onLogin(User user);
    }

    @Override
    public void onAttach(@NonNull Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallBack = (onLoginListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + "must implement onLoginListener");
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2)
    {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.fragment_login);
        signOut();

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);

    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>()
            {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result)
                {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result)
    {
        IdpResponse response = result.getIdpResponse();
        if(result.getResultCode() == Activity.RESULT_OK)
        {
            //successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            User newUser = new User(user.getDisplayName(), user.getEmail().replace(".", "").replace("#", "").replace("$", ""), user.getUid());

            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("users");
            //Sets recipeList, groceryList, and sync
            reference.child(newUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if (snapshot.exists())
                    {
                        Map<String, String> map = (Map<String, String>) snapshot.child("recipeIds").getValue();
                        ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) snapshot.child("groceryList").getValue();
                        String sync = (String)snapshot.child("sync").getValue();
                        if (map != null)
                        {
                            newUser.setRecipeIds(map);
                        }
                        if (list != null)
                        {
                            ArrayList<Ingredient> ingredients = new ArrayList<>();
                            for (int i = 0; i < list.size(); i++)
                            {
                                String name = "";
                                String active = "";

                                for (Map.Entry<String, String> entry : list.get(i).entrySet())
                                {
                                    if(entry.getKey().equals("name"))
                                    {
                                        name = entry.getValue();
                                    }
                                    else
                                    {
                                        active = entry.getValue();
                                    }


                                }
                                ingredients.add(new Ingredient(name, active));
                            }
                            newUser.setGroceryList(ingredients);
                        }

                        if (sync != null)
                        {
                            newUser.setSync(sync);
                        }
                    }
                    vm.setUser(newUser);
                    mCallBack.onLogin(newUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
        }
        else
        {
            //Sign in failed. If response is null the user canceled the signin flow
            //using the back button. Otherwise check response.getError().getErrorCode()
            //and handle the error
        }
    }

    public void signOut()
    {
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        //do something
                    }
                });
    }

    public void delete()
    {
        AuthUI.getInstance()
                .delete(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        //do something
                    }
                });
    }

    public void privacyAndTerms()
    {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTosAndPrivacyPolicyUrls(
                        "https://example.com/terms.html",
                        "https://example.com/privacy.html")
                .build();
        signInLauncher.launch(signInIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        vm = new ViewModelProvider(getActivity())
                .get(RecipeViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}