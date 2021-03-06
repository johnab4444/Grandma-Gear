package com.example.grandmagear.Patient_Main_Lobby;

import android.content.Context;
import android.icu.text.MessagePattern;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grandmagear.FirebaseHelper;
import com.example.grandmagear.FirebaseObjects;
import com.example.grandmagear.NotifDeleteFragment;
import com.example.grandmagear.R;
import com.example.grandmagear.UserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class ReportsViewAdapter extends RecyclerView.Adapter<ReportsViewAdapter.ViewHolder> {

    private ArrayList<String> reportTitle;
    private ArrayList<String> reportText;
    private ArrayList<String> reportTime;
    private FirebaseHelper firebaseHelper;
    private FirebaseObjects.UserDBO userDBO;
    private Context context;
    private boolean check;

    public ReportsViewAdapter(ArrayList<String> reportTitle, ArrayList<String> reportText,
                              ArrayList<String> reportTime, boolean check, Context context) {
        this.reportTitle = reportTitle;
        this.reportText = reportText;
        this.reportTime = reportTime;
        firebaseHelper = new FirebaseHelper();
        this.check = check;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_recycler_item,
                parent, false);
        view.setTag("reportsRecyclerView");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.mReportTitle.setText(reportTitle.get(position));
            holder.mReportText.setText(reportText.get(position));
            holder.mReportTime.setText(reportTime.get(position));
            if(reportTitle.get(position).contains("BPM")){
                holder.mReportImage.setImageResource(R.drawable.heartbeat);
            }
            if(reportTitle.get(position).contains("Fall")){
                holder.mReportImage.setImageResource(R.drawable.falling);
            }
            if(reportTitle.get(position).contains("S.O.S.")){
                holder.mReportImage.setImageResource(R.drawable.sos_icon);
            }
            if (reportTitle.get(position).contains("Battery")){
                holder.mReportImage.setImageResource(R.drawable.battery);
            }
            if(reportTitle.get(position).contains("Offline")){
                holder.mReportImage.setImageResource(R.drawable.offfline_icon);
            }
    }

    @Override
    public int getItemCount() {
        return reportTitle.size();
    }

    public void delete(final int position){
        reportTitle.remove(position);
        reportText.remove(position);
        reportTime.remove(position);
        notifyItemRemoved(position);

        firebaseHelper.firebaseFirestore.collection(FirebaseHelper.userDB)
                .document(FirebaseHelper.firebaseAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userDBO = task.getResult().toObject(FirebaseObjects.UserDBO.class);
                userDBO.notifications.remove(position);
                firebaseHelper.firebaseFirestore.collection(FirebaseHelper.userDB)
                        .document(FirebaseHelper.firebaseAuth.getCurrentUser().getUid())
                        .update("notifications", userDBO.notifications);
            }
        });
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        protected ImageView mReportImage;
        protected ImageView mDeleteReportImage;
        protected TextView mReportTitle;
        protected TextView mReportText;
        protected TextView mReportTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mReportImage = itemView.findViewById(R.id.patientImage);
            mDeleteReportImage = itemView.findViewById(R.id.deleteReport);
            mReportTitle = itemView.findViewById(R.id.reportTitle);
            mReportText = itemView.findViewById(R.id.reportText);
            mReportTime = itemView.findViewById(R.id.reportTime);

            if(check){
                mDeleteReportImage.setVisibility(View.GONE);
            }

            mDeleteReportImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToDeleteNotifView(getAdapterPosition());
                }
            });
        }
    }

    public void goToDeleteNotifView(int position){
        NotifDeleteFragment deleteFragment = new NotifDeleteFragment(this, position);
        deleteFragment.setCancelable(false);
        deleteFragment.show(((ReportsActivity)context).getSupportFragmentManager(), "NotifDeleteFragment");
    }
}
