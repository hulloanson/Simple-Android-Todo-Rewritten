package com.ycl.androidsimpletodorewrite;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.sql.SQLException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    ArrayAdapter<String> tasksStringAdapter;
    DbAdapter dbHelper = new DbAdapter(this);
    private ListView tasksView;
    ManageTaskList tasksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            dbHelper.open();
        }catch (SQLException e){
            spawnToast("Database Error.");
        }
        tasksList = new ManageTaskList();
        tasksStringAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasksList.getAllTaskNames());
        tasksView = (ListView) findViewById(R.id.tasksList);
        tasksView.setAdapter(tasksStringAdapter);
        tasksList.genTaskList();
        setupListViewListener();
    }

    public class ManageTaskList {
        private ArrayList<String> taskStringList = new ArrayList<>();
        private ArrayList<TaskType> tasks = new ArrayList<>();

        public void genTaskList() {
            Cursor temp = dbHelper.getAll();
            if (temp.getCount() == 0) {
                spawnToast("Empty Database.");
                temp.close();
            }else {
                temp.moveToFirst();
                addTask(temp.getLong(temp.getColumnIndex(DbAdapter.dbTaskId)), temp.getString(temp.getColumnIndex(DbAdapter.dbTaskName)));
                while(!temp.isAfterLast()){
                    temp.moveToNext();
                    addTask(temp.getLong(temp.getColumnIndex(DbAdapter.dbTaskId)), temp.getString(temp.getColumnIndex(DbAdapter.dbTaskName)));
                }
                temp.close();
            }
        }

        public ArrayList<String> getAllTaskNames() {
            return taskStringList;
        }
        public void addTask(long id, String name){
            tasks.add(new TaskType(id, name));
            taskStringList.add(name);
        }

        public long getTaskId(int pos) {
            return tasks.get(pos).getId();
        }

        public void removeTask(int pos){
            tasks.remove(pos);
        }
    }

    public class TaskType {
        private long id;
        private String name;

        public TaskType (long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() {
            return id;
        }
//
//        public String getName() {
//            return name;
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addItem(View v) {
        EditText inputTask = (EditText) findViewById(R.id.inputTasks);
        String itemText = inputTask.getText().toString();
        if (!checkNone(itemText)) {
            long result = dbHelper.writeTask(itemText);
            if (result == -1) {
                spawnToast("Database Error.");
            }else {
                tasksList.addTask(result, itemText);
                inputTask.setText("");
            }
        }else {
            spawnToast("Please enter something!");
            inputTask.setText("");
        }
    }

    private boolean checkNone(String s) {
        if (s.length() == 0) {
            return true;
        }else {
            return false;
        }
    }

    private void spawnToast(String message) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public long removeItem(int pos){
        return (dbHelper.deleteTask(tasksList.getTaskId(pos)));
    }

    private void setupListViewListener() {
        tasksView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // Remove the item within array at position
                        long dbResult = removeItem(pos);
                        if (dbResult == -1){
                            spawnToast("Database Error.");
                        }else {
                            tasksList.removeTask(pos);
                            tasksStringAdapter.notifyDataSetChanged();
                        }
                        return true;
                    }

                });
    }
}
