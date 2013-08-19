package com.glengo.pictlib;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.glengo.pictlib.RetrievingData.Read;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CurrencyConverter extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	//Converter
	Double dblConversion, dblNum;
	String strNum;
	Button butConvert;
    EditText edtConvert1;
    TextView tvAnswer, tvUpdate;
    Spinner firstUnit, secondUnit;
    
    String[] strConversions = {"AUD - Australian Dollar",
    				"CAD - Canadian Dollar",
    				"CHF - Swiss Franc",
    				"DKK - Danish Krone",
    				"EUR - Euro",
    				"GBP - Pound Sterling",
    				"HKD - Hong Kong Dollar",
    				"JPY - Japanese Yen",
    				"MXN - Mexican Paso",
    				"NZD - New Zealand Dollar",
    				"PHP - Phillippine Peso",
    				"SEK - Swedish Krona",
    				"SGD - Singapore Dollar",
    				"THB - Thailand Baht",
    				"USD - United States Dollar",
    				"ZAR - South African Rand",

    };
    
	String retrievedData;
	String firstCurrency = "CAD";
	String secondCurrency = firstCurrency;
	Double ExchangeRate = 1.00;
	int Position = 1;
	HttpClient theClient;
	JSONObject Json;
	//final static String URL = "http://currency-api.appspot.com/api/USD/CAD.json?key=483a5ac0b50e19b5fe91adeaa30bddee237616a2";
	//final static String URL = "http://openexchangerates.org/api/latest.json";
	//final static String URL = "http://api.twitter.com/1/statuses/user_timeline.json?screen_name=";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //strConversions = new String[16];
        butConvert = (Button) findViewById(R.id.buttonconvert);
        butConvert.setOnClickListener(this);
        edtConvert1 = (EditText) findViewById(R.id.convert1);
        tvAnswer = (TextView) findViewById(R.id.tvConversion);
        tvUpdate = (TextView) findViewById (R.id.tvRate);
        theClient = new DefaultHttpClient();
        
        //Spinners
        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strConversions);
        firstUnit = (Spinner) findViewById (R.id.firstConversion);
        firstUnit.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Position = firstUnit.getSelectedItemPosition();
				firstCurrency = strConversions[Position].substring(0,3);
				new Read().execute("rate");
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        firstUnit.setAdapter(Adapter);
        secondUnit = (Spinner) findViewById (R.id.secondConversion);
        secondUnit.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Position = secondUnit.getSelectedItemPosition();
				secondCurrency = strConversions[Position].substring(0,3);
				new Read().execute("rate");
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        secondUnit.setAdapter(Adapter);
        
      
		   
    }

	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		switch (view.getId()){
		
		case R.id.buttonconvert:
			dblNum = 0.00;
			dblConversion = 0.00;
			
			strNum = edtConvert1.getText().toString();
			edtConvert1.setText("");
			
	    	try{
	    		dblNum = Double.parseDouble(strNum);
	    		dblConversion = (Math.round((dblNum * ExchangeRate)*1000.0))/1000.0;
		    	tvAnswer.setText(dblNum + " " + firstCurrency + " is " + dblConversion + " " + secondCurrency);
	    	}catch(NumberFormatException e){
	    		e.printStackTrace();
	    	}
	    	
	    	break;
		}
		
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public JSONObject lastData()throws ClientProtocolException, IOException, JSONException{
		StringBuilder url = new StringBuilder("http://currency-api.appspot.com/api/" + firstCurrency+"/" + secondCurrency + ".json?key=483a5ac0b50e19b5fe91adeaa30bddee237616a2");
		
		//Get Method
		HttpGet getData = new HttpGet(url.toString());
		//Response
		HttpResponse HR = theClient.execute(getData);
		int Status = HR.getStatusLine().getStatusCode();
		System.out.println("Status: " + Status);
		if (Status == 200){
			HttpEntity HE = HR.getEntity();
			//Create data returning string of entity
			String data = EntityUtils.toString(HE);
			//System.out.println (data);

			JSONArray Info = new JSONArray("["+data+"]");
			JSONObject theRate = Info.getJSONObject(0);

			return theRate;
			
		}else{
			Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
			return null;
		}
		
	}
	
	public class Read extends AsyncTask<String, Integer, String>{
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				
				Json = lastData();
				return Json.getString(params[0]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} 
			
			
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (result != null){
				try {
					ExchangeRate = Double.parseDouble(result);
					tvUpdate.setText("1 " + firstCurrency + " to 1 " + secondCurrency +" Conversion Rate: " + ExchangeRate);
					//System.out.println ("ExchangeRate: " + ExchangeRate);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					ExchangeRate = 1.00;
					System.out.println ("ERROR OCCURED");
					e.printStackTrace();
				}
			}
		}
	}
}