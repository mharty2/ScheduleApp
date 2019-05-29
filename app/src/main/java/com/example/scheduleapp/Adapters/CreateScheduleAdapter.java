package com.example.scheduleapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.scheduleapp.Objects.CourseInfo;
import com.example.scheduleapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class CreateScheduleAdapter extends RecyclerView.Adapter<CreateScheduleAdapter.ViewHolder> {
    private List<CourseInfo> listItems;
    private Context context;
    //private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ScheduleApp";
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";

    public CreateScheduleAdapter(List<CourseInfo> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("onCreateViewHold","asdf");
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_cardview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d("onBindViewHOld", "asdf");
        CourseInfo item = listItems.get(i);
        Log.d("i in onBindViewHolder", "i: " + i);
        Log.d("first item in list", "Item: " + item.getName());
        viewHolder.txtViewHeading.setText(item.getName() + " - " + item.getType());
        viewHolder.txtViewSection.setText(item.getSection());
        viewHolder.txtViewTime.setText(item.getTime());
        viewHolder.txtViewDays.setText(item.getDays());
        viewHolder.txtViewLocation.setText(item.getLocation());
        viewHolder.txtViewCreditHours.setText(item.getCreditHours());
        viewHolder.txtViewCRN.setText(item.getCrn());
        //mFirestore = FirebaseFirestore.getInstance();
        //sharedPreferences = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        //mAuth = FirebaseAuth.getInstance();
        //mAuth.signInWithCustomToken(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
        //FirebaseUser user = mAuth.getCurrentUser();
    }

    @Override
    public int getItemCount() {
        if (listItems == null) {
            return 0;
        }
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
}
