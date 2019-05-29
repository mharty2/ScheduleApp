package com.example.scheduleapp.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.scheduleapp.R;
import com.example.scheduleapp.Objects.Schedule;
import com.example.scheduleapp.UI.dailySchedule;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class loadScheduleAdapter extends RecyclerView.Adapter<loadScheduleAdapter.ViewHolder> {

    private List<Schedule> listItems;
    private Activity mActivity;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    SharedPreferences sharedPreferences;
    private final String KEY_SELECTED_SCHEDULE = "KEY_SELECTED_SCHEDULE";
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";
    private static final String PREFS_NAME = "ScheduleApp";
    private FirebaseAuth mAuth;

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
        viewHolder.scheduleTxtViewClassList.setText(item.classListToString());

        //when card is clicked
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Choose schedule \"" + item.getName() + "\" ?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    //once the user confirms the choice, the name is saved to sharedpreferences and can be used to pull it
                    //to whatever activity needs it
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth = FirebaseAuth.getInstance();
                        sharedPreferences = v.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        mAuth.signInWithCustomToken(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_SELECTED_SCHEDULE, item.getName());
                        editor.apply();
                        toDailySchedule(v);
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

    public void toDailySchedule(View v) {
        Context context = v.getContext();
        Intent intent = new Intent(context, dailySchedule.class);
        context.startActivity(intent);
    }
}
