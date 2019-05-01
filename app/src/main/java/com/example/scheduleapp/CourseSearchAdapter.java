package com.example.scheduleapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseSearchAdapter extends RecyclerView.Adapter<CourseSearchAdapter.ViewHolder> {

    private List<CourseInfo> listItems;
    private Context context;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ScheduleApp";
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";

    public CourseSearchAdapter(List<CourseInfo> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_cardview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseSearchAdapter.ViewHolder viewHolder, int i) {
        CourseInfo item = listItems.get(i);
        viewHolder.txtViewHeading.setText(item.getName() + " - " + item.getType());
        viewHolder.txtViewSection.setText(item.getSection());
        viewHolder.txtViewTime.setText(item.getTime());
        viewHolder.txtViewDays.setText(item.getDays());
        viewHolder.txtViewLocation.setText(item.getLocation());
        viewHolder.txtViewCreditHours.setText(item.getCreditHours());
        viewHolder.txtViewCRN.setText(item.getCrn());
        mFirestore = FirebaseFirestore.getInstance();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCustomToken(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
        FirebaseUser user = mAuth.getCurrentUser();

        //when card is clicked.
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose section \"" + item.getSection() + "\" ?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, CourseInfo> course = new HashMap<>();
                        course.put("course", item);
                        mFirestore.collection("courses").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(context, "Class added to Firestore", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Class addition failed" + e, Toast.LENGTH_LONG).show();
                                Log.d("tag", e.toString());
                            }
                        });
                        toCreateSchedule();
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtViewHeading;
        public TextView txtViewSection;
        public TextView txtViewTime;
        public TextView txtViewDays;
        public TextView txtViewLocation;
        public TextView txtViewCreditHours;
        public TextView txtViewCRN;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewHeading = itemView.findViewById(R.id.txtViewHeading);
            txtViewSection = itemView.findViewById(R.id.txtViewSection);
            txtViewTime = itemView.findViewById(R.id.txtViewTime);
            txtViewDays = itemView.findViewById(R.id.txtViewDays);
            txtViewLocation = itemView.findViewById(R.id.txtViewLocation);
            txtViewCreditHours = itemView.findViewById(R.id.txtViewCreditHours);
            txtViewCRN = itemView.findViewById(R.id.txtViewCRN);
            linearLayout = itemView.findViewById(R.id.linLayout);

        }
    }
    private void toCreateSchedule() {

        Intent intent = new Intent(context, createSchedule.class);
        context.startActivity(intent);
    }
}
