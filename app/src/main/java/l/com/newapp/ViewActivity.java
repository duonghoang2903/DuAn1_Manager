package l.com.newapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import l.com.newapp.rest.RestClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewActivity extends AppCompatActivity {
    private RecyclerView recyclerPlace;
    private List<Quest> listQuest;
    private List<Quest> listQuestShow;
    private QuestAdapter adapter;
    private FloatingActionButton fab;
    private ProgressDialog dialog;
    private EditText edtSearch;
    private String auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        recyclerPlace = findViewById(R.id.recyclerView);
        edtSearch = findViewById(R.id.edtSearch);
        listQuest = new ArrayList<>();
        listQuestShow = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        recyclerPlace.setLayoutManager(layoutManager1);
        auth = getIntent().getStringExtra("auth");

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewActivity.this, AddActivity.class);
                intent.putExtra("auth", auth);
                startActivityForResult(intent,321);
            }
        });
        addData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321){
            finish();
            startActivity(getIntent());
        }
    }

    private void addData() {
        showProgess();
        Call<JsonElement> callGetMedia = RestClient2.getApiInterface().getAllQuest(100,1);
        callGetMedia.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                dialog.dismiss();
                JsonElement json = response.body();
                JsonArray allMedia = json.getAsJsonArray();

                for (int i = 0; i < allMedia.size(); i++) {
                    String title = allMedia.get(i).getAsJsonObject().get("title").getAsJsonObject().get("rendered").getAsString().toUpperCase();
                    String resource = allMedia.get(i).getAsJsonObject().get("guid").getAsJsonObject().get("rendered").getAsString();
                    int id = allMedia.get(i).getAsJsonObject().get("id").getAsInt();
                    Quest quest = new Quest(id,resource,title);
                    listQuest.add(quest);
                    listQuestShow.add(quest);
                    Log.e("quest "+ i,quest.toString());
                }
                adapter = new QuestAdapter(ViewActivity.this,listQuestShow, auth);
                recyclerPlace.setAdapter(adapter);

                Log.e("listShow", listQuestShow.size()+"");
                setForSearch();
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });


    }
    private void showProgess(){
        dialog= new ProgressDialog(ViewActivity.this);
        dialog.setMessage("Loading...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    private void setForSearch(){
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = String.valueOf(edtSearch.getText().toString()).toUpperCase();

                if (text.length()!=0){
                    listQuestShow.clear();
                    for (Quest x: listQuest){
                            String name = x.getName();
                            String subName;
                            if (name.length()>text.length()-1){
                                subName = x.getName().substring(0,text.length()).toUpperCase();
                            }else{
                                subName=name;
                            }
                            if (text.equals(subName)){
                                listQuestShow.add(x);
                            }
                    }

                    adapter.notifyDataSetChanged();
                    Log.e("listShow1",listQuestShow.size()+"");
                    Log.e("list1",listQuest.size()+"");
                }else{
                    listQuestShow.clear();
                    for (Quest x: listQuest){
                        listQuestShow.add(x);
                    }

                    adapter.notifyDataSetChanged();
                }

            }
        });

    }
}
