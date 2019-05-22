package com.example.scheduleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class loadScheduleAdapter extends RecyclerView.Adapter<loadScheduleAdapter.ViewHolder> {

    private List<Schedule> listItems;
    private Activity mActivity;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    SharedPreferences sharedPreferences;

    public loadScheduleAdapter(List<Schedule> listItems, Activity mActivity) {
        this.listItems = listItems;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.scedule_item_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Schedule item = listItems.get(i);
        viewHolder.scheduleTxtViewHeading.setText(item.getName());
        viewHolder.scheduleTxtViewClassList.setText(item.getClassList().toString());

        //when card is clicked
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Choose schedule \"" + item.getName() + "\" ?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //toCreateSchedule(i);
                        /** save for reference
                         Map<String, CourseInfo> course = new HashMap<>();
                         course.put("course", item);
                         mFirestore.collection(sharedPreferences.getString(PREF_USER_ID_TOKEN, null)).add(course).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(mActivity, "Class added to Firestore", Toast.LENGTH_LONG).show();
                        }
                        }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mActivity, "Class addition failed" + e, Toast.LENGTH_LONG).show();
                        Log.d("tag", e.toString());
                        }
                        });
                         */
                        //toCreateSchedule(i);
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
        if (listItems == null) {
            return 0;
        }
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView scheduleTxtViewHeading;
        private TextView scheduleTxtViewClassList;
        private LinearLayout linearLayout;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            scheduleTxtViewHeading = itemView.findViewById(R.id.scheduleTxtViewHeading);
            scheduleTxtViewClassList = itemView.findViewById(R.id.scheduleTxtViewClassList);
            linearLayout = itemView.findViewById(R.id.scheduleLinLayout);
        }
    }
}
