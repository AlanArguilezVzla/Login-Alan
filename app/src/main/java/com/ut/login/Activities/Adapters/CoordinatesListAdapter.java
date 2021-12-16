package com.ut.login.Activities.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ut.login.Activities.Clases.Coordinates;
import com.ut.login.Activities.FirebaseList;
import com.ut.login.R;

import java.text.DateFormat;
import java.util.ArrayList;

public class CoordinatesListAdapter implements ListAdapter {

    ArrayList<Coordinates> arrayList;
    FirebaseList firebaseList;
    ListView listView;

    FirebaseFirestore db;

    public CoordinatesListAdapter(ArrayList<Coordinates> arrayList,
                                  FirebaseList firebaseList, ListView listView) {
        this.arrayList = arrayList;
        this.firebaseList = firebaseList;
        this.listView = listView;

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(firebaseList);

            view = layoutInflater.inflate(R.layout.coordinates_list_item, null);

            TextView latitudeAndLongitude = view.findViewById(R.id.txtLandL);
            latitudeAndLongitude.setText(" " + arrayList.get(i).getLatitude() + ", " + arrayList.get(i).getLongitude());

            TextView date = view.findViewById(R.id.txtDate);

            DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(firebaseList);
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(firebaseList);

            date.setText(" " + dateFormat.format(arrayList.get(i).getDate()) + " " + timeFormat.format(arrayList.get(i).getDate()));

            ImageButton delete = view.findViewById(R.id.imgDelete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("Coordinates").document(arrayList.get(i).getId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    arrayList.remove(i);
                                    if (arrayList.size() > 0) {
                                        CoordinatesListAdapter coordinatesListAdapter =
                                                new CoordinatesListAdapter(arrayList, firebaseList, listView);
                                        listView.setAdapter(coordinatesListAdapter);
                                    } else {
                                        listView.invalidateViews();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(firebaseList, "Ocurrio un error al eliminar",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=" + arrayList.get(i).getLatitude()
                                        + "," + arrayList.get(i).getLongitude()));
                        firebaseList.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(firebaseList, "Ocurrio un error obteniendo las coordenadas",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

        }
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
