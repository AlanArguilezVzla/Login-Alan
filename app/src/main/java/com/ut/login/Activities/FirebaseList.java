package com.ut.login.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ut.login.Activities.Adapters.CoordinatesListAdapter;
import com.ut.login.Activities.Clases.Coordinates;
import com.ut.login.R;

import java.util.ArrayList;

public class FirebaseList extends AppCompatActivity {

    ListView listView;
    FirebaseFirestore db;
    String TAG = "FirebaseTestList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_list);

        listView = findViewById(R.id.firebaseList);
        db = FirebaseFirestore.getInstance();

        loadCoordinate();
    }

    private void loadCoordinate() {
        db.collection("Coordinates")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<Coordinates> arrayList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "-" + String.valueOf(document.get("longitude")));

                                arrayList.add(new Coordinates(
                                        document.getId(),
                                        document.getDate("date"),
                                        Float.parseFloat(String.valueOf(document.get("latitude"))),
                                        Float.parseFloat(String.valueOf(document.get("longitude")))
                                ));
                            }

                            CoordinatesListAdapter coordinatesListAdapter =
                                    new CoordinatesListAdapter(arrayList,FirebaseList.this, listView);
                            listView.setAdapter(coordinatesListAdapter);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


}