package l.com.newapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import l.com.newapp.rest.RestClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.ViewHoder> {
    private List<Quest> listQuest;
    private Context context;
    private String auth;
    public QuestAdapter(Context context,List<Quest> listQuest, String auth) {
        this.listQuest = listQuest;
        this.context = context;
        this.auth = auth;
    }
    String nameUpdate;
    String name;
    @NonNull
    @Override
    public QuestAdapter.ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_list, parent, false);

        return new ViewHoder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHoder holder, final int position) {
        Quest quest = listQuest.get(position);
        holder.tvName.setText(quest.getName());
        Glide.with(context)
                .load(quest.getImage())
                .into(holder.imgImage);
        final int id = listQuest.get(position).getId();
        holder.btnDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to delete "+listQuest.get(position).getName()+ " ?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        showProgess();
                        Call<JsonElement> callDeleteItem = RestClient2.getApiInterface().deleteMediabyID(id,"true",auth);
                        callDeleteItem.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                if (response.isSuccessful()){
                                    dialog.dismiss();
                                    JsonObject check = response.body().getAsJsonObject();
                                    Log.e("respone", response.body().toString());
                                    boolean deleted = check.get("deleted").getAsBoolean();
                                    if (deleted) {
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                        builder2.setMessage("Deleted!");
                                        builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog2, int which) {
                                                dialog2.dismiss();
                                                ((Activity) context).finish();
                                                context.startActivity(((Activity) context).getIntent());
                                            }
                                        });
                                        builder2.show();
                                    }
                                }else{
                                    try {
                                        Log.e("respone", response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });



            holder.imgUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name = listQuest.get(position).getName().toUpperCase();
                    nameUpdate = holder.tvName.getText().toString().toUpperCase();
                    if (!nameUpdate.equals(name)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Do you want rename " +  name +" to " + nameUpdate);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showProgess();
                                Call<JsonElement> callUpdateMediaTitle = RestClient2.getApiInterface().updateMediabyTitle(id, nameUpdate, auth);
                                callUpdateMediaTitle.enqueue(new Callback<JsonElement>() {
                                    @Override
                                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                        if (response.isSuccessful()) {
                                            dialog.dismiss();
                                            JsonObject image = response.body().getAsJsonObject();
                                            Log.e("respone", response.body().toString());
                                            String title = image.get("title").getAsJsonObject().get("rendered").getAsString();
                                            if (title.toUpperCase().equalsIgnoreCase(nameUpdate)) {
                                                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                                builder2.setMessage("Update successfully!");
                                                builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog2, int which) {
                                                        holder.tvName.setText(nameUpdate);
                                                        name = nameUpdate;
                                                    }
                                                });
                                                builder2.show();
                                            }
                                        } else {
                                            try {
                                                Log.e("respone", response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonElement> call, Throwable t) {

                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            });

    }

    @Override
    public int getItemCount() {
        return listQuest.size();
    }

    public class ViewHoder extends RecyclerView.ViewHolder {
        private ImageView imgImage;
        private TextView tvName;
        private ImageView btnDeleteItem;
        private ImageView imgUpdate;
        public ViewHoder(@NonNull View itemView) {
            super(itemView);
            imgImage = itemView.findViewById(R.id.imgImage);
            tvName = itemView.findViewById(R.id.tvName);
            btnDeleteItem = itemView.findViewById(R.id
                    .btnDeleteItem);
            imgUpdate = itemView.findViewById(R.id.imgUpdate);
        }
    }
    ProgressDialog dialog;
    private void showProgess(){
        dialog= new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
