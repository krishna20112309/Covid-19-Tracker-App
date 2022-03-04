package com.krishna2011.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mtodaytotal,mtotal,mactive,mtodayactive,mrecovered,mtodayrecovered,mdeaths,mtodaydeaths;

    String country;
    TextView mfilter;
    Spinner spinner;
    String[] types={
            "cases","deaths","recovered","active"
    };
    private List<ModelClass> modeClassList;
    private List<ModelClass> modelClassList2;
    PieChart mpiechart;
    private RecyclerView recyclerView;
    com.krishna2011.covidtracker.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().hide();
        countryCodePicker=findViewById(R.id.ccp);
        mtodayactive=findViewById(R.id.todayactive);
        mactive=findViewById(R.id.activecase);
        mdeaths=findViewById(R.id.todaydeath);
        mtodaydeaths=findViewById(R.id.todaydeath);
        mrecovered=findViewById(R.id.recoveredcase);
        mtodayrecovered=findViewById(R.id.todayrecovered);
        mtotal=findViewById(R.id.totalcase);
        mtodaytotal=findViewById(R.id.todaytotal);
        mpiechart=findViewById(R.id.piechart);
        spinner=findViewById(R.id.spinner);
        mfilter=findViewById(R.id.filter);
        recyclerView=findViewById(R.id.recyclerview);
        modeClassList= new ArrayList<>();
        modelClassList2=new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter( this, android.R.layout.simple_spinner_dropdown_item,types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                //adapter.notify();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
        adapter=new Adapter(getApplicationContext(),modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country=countryCodePicker.getSelectedCountryName();
                fetchdata();
            }
        });
        fetchdata();












    }

    private void fetchdata() {


        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modeClassList.addAll(response.body());
                for(int i=0;i<modeClassList.size();i++){
                    if(modeClassList.get(i).getCountry().equals(country))
                    {
                        mactive.setText((modeClassList.get(i).getActive()));
                        mtodaydeaths.setText((modeClassList.get(i).getTodayDeaths()));
                        mtodayrecovered.setText((modeClassList.get(i).getTodayRecovered()));
                        mtodaytotal.setText((modeClassList.get(i).getTodayCases()));
                        mtotal.setText((modeClassList.get(i).getCases()));
                        mdeaths.setText((modeClassList.get(i).getDeaths()));
                        mrecovered.setText((modeClassList.get(i).getRecovered()));

                        int active,total,recovered,deaths;
                        active=Integer.parseInt(modeClassList.get(i).getActive());
                        total=Integer.parseInt(modeClassList.get(i).getCases());
                        recovered=Integer.parseInt(modeClassList.get(i).getRecovered());
                        deaths=Integer.parseInt(modeClassList.get(i).getDeaths());



                        updateGraph(active,total,recovered,deaths);



                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
    }

    private void updateGraph(int active, int total, int recovered, int deaths) {
        mpiechart.clearChart();
        mpiechart.addPieSlice(new PieModel("Confirm",total, Color.parseColor( "#FFB701")));
        mpiechart.addPieSlice(new PieModel("Active",active, Color.parseColor( "#FF4CAF50")));
        mpiechart.addPieSlice(new PieModel("Recovered",recovered, Color.parseColor( "#38ACCD")));
        mpiechart.addPieSlice(new PieModel("Deaths",deaths, Color.parseColor( "#F55C47")));
        mpiechart.startAnimation();



    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


        String item=types[position];
        mfilter.setText(item);
        adapter.filter(item);


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}