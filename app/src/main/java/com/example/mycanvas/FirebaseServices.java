package com.example.mycanvas;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseServices {

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;


    public FirebaseServices() {
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    public interface FirebaseServicesListener {
        void onError(String msg);

        void onSuccess(Object response);
    }

    // Handle firebase to register by email and password, and add another data to the database
    public void registerUser(Map<String, Object> user, String password, FirebaseServicesListener firebaseServicesListener) {
        mAuth.createUserWithEmailAndPassword((String) user.get("email"), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    firebaseServicesListener.onSuccess("[FIREBASE SERVICE] Successfully register new user!");
                                }
                            });

                        } else {
                            firebaseServicesListener.onError("[FIREBASE SERVICE] Failed to register new user!");
                        }
                    }
                });
    }

    // Handle firebase authentication to login by email and password
    public void userLogin(String email, String pwd, FirebaseServicesListener firebaseServicesListener) {
        mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseServicesListener.onSuccess("[FIREBASE SERVICE] Successfully Login");
                } else {
                    firebaseServicesListener.onError("[FIREBASE SERVICE] Failed to Login!");
                }
            }
        });
    }

    // Handle firebase service to check if user is logged in or not.
    public void isLoggedIn(FirebaseServicesListener firebaseServicesListener) {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            Log.d("[FIREBASE SERVICE]", "User: " + user);
            boolean isLogged = false;
            if (user != null) {
                isLogged = true;
            } else {
                isLogged = false;
            }
            firebaseServicesListener.onSuccess(isLogged);
        } catch (Exception err) {
            firebaseServicesListener.onError(err.toString());
        }
    }

    public void userSignOut(FirebaseServicesListener firebaseServicesListener) {
        // Handle firebase service to Sign Out!
        mAuth.signOut();
        firebaseServicesListener.onSuccess("Sign Out!");
    }

    /*
        usersMoodChange function
        parameters  int val, String id, boolean update FirebaseServiceListener firebaseServiceListener
        return void

        This function to handle insert and update request on firebase,
        when user submit their mood value from seekbar this function will do the firebase request whether the user already submit the mood value today or not.
        If not this function will create new one on the firebase
        if already submit mood value for today this function will update the value
    */
    public void userMoodChange(int val, String id, boolean update, FirebaseServicesListener firebaseServicesListener) {
        // Handle firebase service to edit / create new mood database if not exists in the same day.
        if (update) {
            Map<String, Object> mood = new HashMap<>();
            mood.put("user_id", mAuth.getCurrentUser().getUid());
            mood.put("value", val);
            mood.put("time", new Timestamp(new Date()));

            fStore.collection("user_moods").document(id)
                    .set(mood)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mood.put("id", id);
                            firebaseServicesListener.onSuccess(mood);
                            Log.d("[FIREBASE SERVICE]", "Succesfully to update user today mood!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            firebaseServicesListener.onError("Failed to update user today mood!");
                            Log.w("[FIREBASE SERVICE]", "Failed to update user today mood!", e);
                        }
                    });
            Log.d("[FIREBASE SERVICE]", "Update Mood Value of id: " + id);
        } else {
            Map<String, Object> mood = new HashMap<>();
            mood.put("user_id", mAuth.getCurrentUser().getUid());
            mood.put("value", val);
            mood.put("time", new Timestamp(new Date())); // Need to be generalized using UTC+8?

            fStore.collection("user_moods")
                    .add(mood)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            mood.put("id", documentReference.getId());
                            firebaseServicesListener.onSuccess(mood);
                            Log.d("[FIREBASE SERVICE]", "DocumentSnapshot written with ID: " + documentReference.getId() + " -> " + documentReference);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            firebaseServicesListener.onError("Failed to create new mood document!");
                            Log.w("[FIREBASE SERVICE]", "Error adding document!", e);
                        }
                    });
            Log.d("[FIREBASE SERVICE]", "Create New Mood Value");
        }
    }

    /*
        usersMoodFetch function
        parameters FirebaseServiceListener firebaseServiceListener
        return void

        This function to handle get request on firebase,
        the firebase return will return  user mood value according user mood value today.
    */
    public void userMoodFetch(FirebaseServicesListener firebaseServicesListener) {
        // Handle firebase service to get user mood
        FirebaseUser user = mAuth.getCurrentUser();
        fStore.collection("user_moods").whereEqualTo("user_id", user.getUid())
                .whereGreaterThanOrEqualTo("time", atStartOfDay(new Date())) // Need to be generalized using UTC+8?
                .whereLessThanOrEqualTo("time", atEndOfDay(new Date())) // Need to be generalized using UTC+8?
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map userMood = (Map) document.getData();
                        userMood.put("id", document.getId());
                        firebaseServicesListener.onSuccess(userMood);
                    }
                } else {
                    firebaseServicesListener.onError("[FIREBASE SERVICE] Failed to fetch \"Mood Value\" for today!");
                }
            }
        });
    }

    /*
        userMoodFetchBefore function
        parameters  int n, FirebaseServiceListener firebaseServiceListener
        return void

        This function to handle get request on firebase,
        the firebase return will return user mood value according the range of n days before today.
    */
    public void userMoodFetchBefore(int n, FirebaseServicesListener firebaseServicesListener) {
        // Handle firebase service to get user mood
        FirebaseUser user = mAuth.getCurrentUser();
        fStore.collection("user_moods").whereEqualTo("user_id", user.getUid())
                .whereGreaterThanOrEqualTo("time", nDaysBefore(n, new Date())) // Need to be generalized using UTC+8?
                .whereLessThanOrEqualTo("time", atEndOfDay(new Date())) // Need to be generalized using UTC+8?
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int averageMood = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map userMood = (Map) document.getData();
                        averageMood += (int) (long) userMood.get("value");

                    }
                    if (task.getResult().size() > 0) {
                        averageMood /= task.getResult().size();
                    } else {
                        averageMood = 0;
                    }
                    firebaseServicesListener.onSuccess(averageMood);
                } else {
                    firebaseServicesListener.onError("[FIREBASE SERVICE] Failed to fetch \"Mood Values for past n days\"!");
                }
            }
        });
    }

    /*
        usersMoodAverage function
        parameters FirebaseServiceListener firebaseServiceListener
        return void

        This function to handle get request on firebase,
        the firebase return will return bunch of users mood value in the range of today.
    */
    public void usersMoodAverage(FirebaseServicesListener firebaseServicesListener) {
        fStore.collection("user_moods")
                .whereGreaterThanOrEqualTo("time", atStartOfDay(new Date())) // Need to be generalized using UTC+8?
                .whereLessThanOrEqualTo("time", atEndOfDay(new Date())) // Need to be generalized using UTC+8?
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int averageMood = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map userMood = (Map) document.getData();
                        averageMood += (int) (long) userMood.get("value");
                    }
                    if (task.getResult().size() > 0) {
                        averageMood /= task.getResult().size();
                    } else {
                        averageMood = 0;
                    }
                    firebaseServicesListener.onSuccess(averageMood);
                } else {
                    firebaseServicesListener.onError("[FIREBASE SERVICE] Failed to fetch \"Average User Mood Value\" for today!");
                }
            }
        });
    }

    /*
        usersMoodAverageBefore function
        parameters  int n, FirebaseServiceListener firebaseServiceListener
        return void

        This function to handle get request on firebase,
        the firebase return will return bunch of users mood value according the range of n days before today.
    */
    public void usersMoodAverageBefore(int n, FirebaseServicesListener firebaseServicesListener) {
        fStore.collection("user_moods")
                .whereGreaterThanOrEqualTo("time", nDaysBefore(n, new Date())) // Need to be generalized using UTC+8?
                .whereLessThanOrEqualTo("time", atEndOfDay(new Date())) // Need to be generalized using UTC+8?
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int averageMood = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map userMood = (Map) document.getData();
                        averageMood += (int) (long) userMood.get("value");
                    }
                    if (task.getResult().size() > 0) {
                        averageMood /= task.getResult().size();
                    } else {
                        averageMood = 0;
                    }
                    firebaseServicesListener.onSuccess(averageMood);
                } else {
                    firebaseServicesListener.onError("[FIREBASE SERVICE] Failed to fetch \"Average User Mood Value\" for today!");
                }
            }
        });
    }

    /*
        addUserFavStocks function
        parameters  String name, String docId, boolean isExists, FirebaseServiceListener firebaseServiceListener
        return void

        This function will handle firebase post request to add stock name from user favorite stock list or watchlist.
        If the user does not have or have not add or create watchlist this function will also create the creation of user watchlist
    */
    public void addUserFavStock(String name, String docId, boolean isExists, FirebaseServicesListener firebaseServicesListener) {
        if (!isExists) {
            Map<String, Object> userFav = new HashMap<>();
            userFav.put("user_id", mAuth.getCurrentUser().getUid());
            ArrayList<String> favStocks = new ArrayList<>();
            favStocks.add(name);
            userFav.put("fav_stocks", favStocks);
            fStore.collection("user_fav_stocks").add(userFav).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        DocumentReference document = task.getResult();
                        Map<String, Object> response = new HashMap<>();
                        response.put("id", (String) document.getId());
                        response.put("user_id", (String) mAuth.getCurrentUser().getUid());
                        response.put("fav_stocks", favStocks);
                        // Handle async process place when done addition or task
                        firebaseServicesListener.onSuccess(response);
                    } else {
                        // Handle async process place when error doing addition or task
                        firebaseServicesListener.onError("Failed add new fav stock");
                    }
                }
            });
        } else {
            DocumentReference newFavStockRef = fStore.collection("user_fav_stocks").document(docId);
            newFavStockRef.update("fav_stocks", FieldValue.arrayUnion(name)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Handle async process place when done addition or task
                        firebaseServicesListener.onSuccess("Successfully add new fav stock");
                    } else {
                        // Handle async process place when error doing addition or task
                        firebaseServicesListener.onError("Failed add new fav stock");
                    }
                }
            });
        }
    }

    /*
        deleteUserFavStocks function
        parameters  String name, String docId, FirebaseServiceListener firebaseServiceListener
        return void

        This function will handle firebase delete request to delete stock name from user favorite stock list or watchlist.
     */
    public void deleteUserFavStock(String name, String docId, FirebaseServicesListener firebaseServicesListener) {
        DocumentReference newFavStockRef = fStore.collection("user_fav_stocks").document(docId);
        newFavStockRef.update("fav_stocks", FieldValue.arrayRemove(name)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Handle async process place when done removal or task
                    firebaseServicesListener.onSuccess("Successfully add new fav stock");
                } else {
                    // Handle async process place when error removal or task
                    firebaseServicesListener.onError("Failed add new fav stock");
                }
            }
        });
    }

    /*
        fetchUserFavStocks function
        parameters  FirebaseServiceListener firebaseServiceListener
        return void

        This function will handle firebase get request to get user favorite stock list or watchlist.
     */
    public void fetchUserFavStocks(FirebaseServicesListener firebaseServicesListener) {
        fStore.collection("user_fav_stocks")
                .whereEqualTo("user_id", mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> userFavStocks = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userFavStocks = (Map) document.getData();
                        userFavStocks.put("id", document.getId());
                    }
                    /*
                    Will return response of Map Object with keys of
                    "id" <= String
                    "user_id" <= String
                    "fav_stocks" <= ArrayList
                     */
                    firebaseServicesListener.onSuccess(userFavStocks);
                } else {
                    firebaseServicesListener.onError("Failed to get user favorite stocks");
                }
            }
        });
    }

    //---------- Helpers ----------
    public Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public Date atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date nDaysBefore(int n, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, -n);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    //---------- Helpers ----------

}