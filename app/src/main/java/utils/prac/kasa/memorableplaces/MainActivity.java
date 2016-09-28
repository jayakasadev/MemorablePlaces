package utils.prac.kasa.memorableplaces;

import android.content.Intent;
import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String[] INITIAL_PERMS={ Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION };

    private ListView listView;

    private static List<String> list;
    private static ArrayAdapter adapter;
    private static List<LatLng> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.list);

        list = new ArrayList<>();
        list.add("Click to add a new place");

        locations = new ArrayList<>();
        locations.add(new LatLng(0,0));

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = list.get(position);

                Log.i("item", item);

                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putExtra("location", position);

                //starting activity
                startActivity(i);
            }
        });

    }

    public static List<String> getList(){
        return list;
    }

    public static ArrayAdapter getAdapter(){
        return adapter;
    }

    public static List<LatLng> getLocations(){
        return locations;
    }
}
