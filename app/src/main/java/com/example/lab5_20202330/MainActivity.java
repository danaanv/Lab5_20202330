package com.example.lab5_20202330;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TaskAdapter taskAdapter;
    private TaskStorage taskStorage;
    private String currentPucpCode;
    private RecyclerView rvTasks;
    private FloatingActionButton fabAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvTasks = findViewById(R.id.rv_tasks);
        fabAddTask = findViewById(R.id.fab_add_task);

        taskStorage = new TaskStorage(this);

        taskAdapter = new TaskAdapter(new ArrayList<>(), (task, position) -> showAddEditTaskDialog(task, position));
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("PUCP_CODE")) {
            currentPucpCode = intent.getStringExtra("PUCP_CODE");
            loadTasks(currentPucpCode);
        } else {
            Snackbar.make(rvTasks, "Código PUCP no encontrado", Snackbar.LENGTH_SHORT).show();
        }

        fabAddTask.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(currentPucpCode)) {
                showAddEditTaskDialog(null, -1);
            } else {
                Snackbar.make(rvTasks, "Ingrese su código PUCP primero", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTasks(String pucpCode) {
        List<Task> tasks = taskStorage.loadTasks(pucpCode);
        if (tasks == null) {
            tasks = new ArrayList<>();
            taskStorage.saveTasks(pucpCode, tasks); // Guarda una nueva lista vacía
            Snackbar.make(rvTasks, "Nuevo código PUCP creado. Agregue tareas.", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(rvTasks, "Tareas cargadas exitosamente.", Snackbar.LENGTH_SHORT).show();
        }
        taskAdapter.updateTasks(tasks);
    }

    private void showAddEditTaskDialog(Task task, int position) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText etTaskTitle = dialogView.findViewById(R.id.et_task_title);
        EditText etTaskDescription = dialogView.findViewById(R.id.et_task_description);
        TextView btnSetDateTime = dialogView.findViewById(R.id.btn_set_date_time);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(task == null ? "Agregar Tarea" : "Editar Tarea")
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        alertDialog.setOnShowListener(dialog -> {
            if (task != null) {
                etTaskTitle.setText(task.getTitle());
                etTaskDescription.setText(task.getDescription());
            }

            Calendar calendar = Calendar.getInstance();
            if (task != null) {
                calendar.setTimeInMillis(task.getDueDate());
            }

            btnSetDateTime.setOnClickListener(v -> showDateTimePicker(calendar, date -> {
                calendar.setTimeInMillis(date);
            }));

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String title = etTaskTitle.getText().toString();
                String description = etTaskDescription.getText().toString();
                long dueDate = calendar.getTimeInMillis();

                if (!title.isEmpty() && !description.isEmpty()) {
                    if (task == null) {
                        Task newTask = new Task(title, description, dueDate);
                        taskAdapter.addTask(newTask);
                    } else {
                        task.setTitle(title);
                        task.setDescription(description);
                        task.setDueDate(dueDate);
                        taskAdapter.updateTask(position);
                    }
                    taskStorage.saveTasks(currentPucpCode, taskAdapter.getTasks());
                    alertDialog.dismiss();
                } else {
                    Snackbar.make(rvTasks, "Complete todos los campos", Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        alertDialog.show();
    }

    private void showDateTimePicker(Calendar calendar, OnDateTimeSetListener listener) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                listener.onDateTimeSet(calendar.getTimeInMillis());
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    interface OnDateTimeSetListener {
        void onDateTimeSet(long dateTime);
    }
}
