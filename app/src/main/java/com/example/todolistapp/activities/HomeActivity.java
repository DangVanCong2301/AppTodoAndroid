package com.example.todolistapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolistapp.R;
import com.example.todolistapp.models.ModelJob;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rcvJob;
    private FloatingActionButton flaAdd;
    private DatabaseReference ref;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private String onlineUserId;
    private Button btnSave, btnCancel;
    private EditText edtTask, edtDescription;
    private ProgressDialog progressDialog;
    private TextView tvSubTitle;

    private String key = "";
    private String task;
    private String description;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUi();
        initListener();
        initUserAuth();
        setUpProgressDialog();
        setUpLinearLayoutManager();

    }

    private void initUi() {
        flaAdd = findViewById(R.id.fla_add_job);
        rcvJob = findViewById(R.id.rcv_job);
        tvSubTitle = findViewById(R.id.tv_sub_title);
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void setUpLinearLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rcvJob.setHasFixedSize(true);
        rcvJob.setLayoutManager(linearLayoutManager);
    }

    private void initListener() {
        flaAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });


    }

    private void addTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.item_add_job, null);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        edtTask = view.findViewById(R.id.edt_task);
        edtDescription = view.findViewById(R.id.edt_description);
        builder.setView(view);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTask = edtTask.getText().toString().trim();
                String mDescription = edtDescription.getText().toString().trim();
                String id = ref.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if (TextUtils.isEmpty(mTask)) {
                    edtTask.setError("Enter task...");
                    return;
                } else if (TextUtils.isEmpty(mDescription)) {
                    edtDescription.setError("Enter description");
                    return;
                } else {
                    progressDialog.show();
                    ModelJob modelJob = new ModelJob(mTask, mDescription, id, date);
                    ref.child(id).setValue(modelJob)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(HomeActivity.this, "Task has been inserted successfully", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                    } else {
                                        String error = task.getException().toString();
                                        Toast.makeText(HomeActivity.this, "Fail" + error, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void initUserAuth() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserId = mUser.getUid();
        String email = mUser.getEmail();
        tvSubTitle.setText(email);
        ref = FirebaseDatabase.getInstance().getReference("Jobs").child(onlineUserId);
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layoutItem;
        private TextView tvTask;
        private TextView tvDescription;
        private TextView tvDate;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTask = itemView.findViewById(R.id.tv_task);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDate = itemView.findViewById(R.id.tv_date);
            layoutItem = itemView.findViewById(R.id.layout_item_job);
        }

        public void setTask(String task) {
            tvTask.setText(task);
        }

        public void setDescription(String desc) {
            tvDescription.setText(desc);
        }

        public void setDate(String date) {
            tvDate.setText(date);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ModelJob> options = new FirebaseRecyclerOptions.Builder<ModelJob>()
                .setQuery(ref, ModelJob.class)
                .build();

        FirebaseRecyclerAdapter<ModelJob, JobViewHolder> adapter = new FirebaseRecyclerAdapter<ModelJob, JobViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull JobViewHolder holder, int position, @NonNull ModelJob model) {
                holder.setDate(model.getData());
                holder.setTask(model.getTask());
                holder.setDescription(model.getDescription());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        key = getRef(position).getKey();
                        task = model.getTask();
                        description = model.getDescription();

                        updateTask();
                    }
                });
            }

            @NonNull
            @Override
            public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
                return new JobViewHolder(view);
            }
        };
        rcvJob.setAdapter(adapter);
        adapter.startListening();
    }

    private void updateTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_job, null);
        builder.setView(view);

        EditText edtTaskUpdate = view.findViewById(R.id.edt_task_update);
        EditText edtDescriptionUpdate = view.findViewById(R.id.edt_description_update);
        Button btnUpdate = view.findViewById(R.id.btn_save_update);
        Button btnDelete = view.findViewById(R.id.btn_delete_update);

        edtTaskUpdate.setText(task);
        edtTaskUpdate.setSelection(task.length());

        edtDescriptionUpdate.setText(description);
        edtDescriptionUpdate.setSelection(description.length());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task = edtTaskUpdate.getText().toString().trim();
                description = edtDescriptionUpdate.getText().toString().trim();

                String dateUpdate = DateFormat.getDateInstance().format(new Date());

                ModelJob modelJob = new ModelJob(task, description, key, dateUpdate);

                ref.child(key).setValue(modelJob)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(HomeActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    String error = task.getException().toString();
                                    Toast.makeText(HomeActivity.this, "Fail" + error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child(key).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(HomeActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    String err = task.getException().toString();
                                    Toast.makeText(HomeActivity.this, "Error" + err, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        dialog = builder.create();
        dialog.show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    //@Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.logout:
//                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }
}