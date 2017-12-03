package insacvl.fennine.fennine;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        String result = getIntent().getExtras().getString("data");
        System.out.println("result: "+result);

        String[] data =result.split("\n");

        ArrayAdapter adapter =  new ArrayAdapter<String>(this,R.layout.list_view, data);
        final ListView listView =  (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // TextView item = (TextView)findViewById(R.id.list_item);
                final String item = (String) listView.getItemAtPosition(i);
                String[] coordinates = item.split(":");

                //Toast.makeText(DisplayActivity.this,coordinates [1]+coordinates[3], Toast.LENGTH_SHORT).show();


               Uri IntentUri = Uri.parse("google.streetview:cbll="+coordinates[1]+","+coordinates[3]);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, IntentUri);
                if (mapIntent.resolveActivity(getPackageManager())!=null)

                {
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }

            }
        });


    }
}
