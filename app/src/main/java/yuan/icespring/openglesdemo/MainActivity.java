package yuan.icespring.openglesdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private static Class[] activities = {
            GLSurfaceViewDrawActivity.class,
            GLSurfaceViewNativeDrawActivity.class,
            TextureViewDrawActivity.class,
            TextureViewDraw2Activity.class
    };


    private RecyclerView listView;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);
        listView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListAdapter();
        listView.setAdapter(adapter);

    }


    private class ListAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            Button button = new Button(viewGroup.getContext());
            return new MyViewHolder(button);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
            myViewHolder.button.setText(activities[position].getSimpleName());
            myViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, activities[position]);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return activities.length;
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            button = (Button) itemView;
            button.setAllCaps(false);
        }
    }
}
