package com.example.scheduleapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CourseSearchAdapter extends RecyclerView.Adapter<CourseSearchAdapter.ViewHolder> {

    private List<CourseInfo> listItems;
    private Context context;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

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

        //when card is clicked.
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "You clicked " + item.getHeading(), Toast.LENGTH_LONG).show();
                builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose section \"" + item.getSection() + "\" ?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
