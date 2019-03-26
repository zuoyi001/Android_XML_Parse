package xmlparse.cn.com.myxmlparse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button sax;
    Button dom;
    Button pull;
    XmlParser xmlParser;
    List<Person> persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        xmlParser = new XmlParser();

        sax = (Button) findViewById(R.id.sax);
        dom = (Button) findViewById(R.id.dom);
        pull = (Button) findViewById(R.id.pull);

        sax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persons = xmlParser.readXmlBySax(xmlParser.getInputStreamFromAsset("persons.xml", MainActivity.this));

                for(Person mPerson : persons) {
                    Log.d("DEBUG", "SAX "+mPerson.getName());
                }
            }
        });
        dom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persons = xmlParser.readXmlByDom(xmlParser.getInputStreamFromAsset("persons.xml", MainActivity.this));

                for(Person mPerson : persons) {
                    Log.d("DEBUG", "DOM "+mPerson.getName());
                }
            }
        });
        pull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persons = xmlParser.readXmlByPull(xmlParser.getInputStreamFromAsset("persons.xml", MainActivity.this));

                for(Person mPerson : persons) {
                    Log.d("DEBUG", "PULL "+mPerson.getName());
                }
            }
        });
    }
}
