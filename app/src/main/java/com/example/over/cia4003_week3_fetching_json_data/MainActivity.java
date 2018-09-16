package com.example.over.cia4003_week3_fetching_json_data;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity
{
    // Hash map for ListView
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //contact List to display in List view
        contactList = new ArrayList<>();
        // Call the async task to get json. Pass the url as an argument
        new GetContacts().execute("https://api.androidhive.info/contacts/");
    }

    private void displayMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void onSearch (View v)
    {
            //Read view
            EditText etName = findViewById(R.id.etName);
            EditText etEmail = findViewById(R.id.etEmail);

            //Read views from listView
            TextView tvName = findViewById(R.id.name);
            TextView tvEmail = findViewById(R.id.email);

            //Get inputs from view
            String searchName = etName.getText().toString();
            String searchEmail = etEmail.getText().toString();

            //Get inputs from listView
            String name = tvName.getText().toString();
            String email = tvEmail.getText().toString();

            if (searchName.contains(name) || searchEmail.contains(email))
            {
                Log.i("searchName", searchName);
                Log.i("searchEmail", searchEmail);
                Log.i("name", name);
                Log.i("email", email);
            }
    }

    //Async task class to get json by making HTTP call
    private class GetContacts extends AsyncTask<String, Void, Boolean>
    {
        // show a progress dialog before making actual http call.
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Show the progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }// end on PreExecute

        // get the json from url, parse the JSON and add to HashMap to show the results in List View.
        @Override
        protected Boolean doInBackground(String... urls)
        {
            // contacts JSONArray
            JSONArray contactsArray;

            try
            {
                // Making a request to url and get the response as JSON data
                URL urlObj = new URL(urls[0]);
                //URL urlObj = new URL("https://api.androidhive.info/contacts/");
                HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
                InputStream is = urlConnection.getInputStream();
                int status = urlConnection.getResponseCode();
                StringBuffer mBuffer = new StringBuffer();

                if (status == 200)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine()) != null)
                    {
                        mBuffer.append(line);
                    }
                    String jsonStr= mBuffer.toString();

                    // start parsing the json string
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    contactsArray = jsonObj.getJSONArray("contacts");

                    // looping through the Contacts array
                    for (int i = 0; i < contactsArray.length(); i++)
                    {
                        JSONObject c = contactsArray.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        // hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);//key value pairs
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);
                        contact.put("home", home);
                        contact.put("office", office);

                        // adding contact to contact list
                        contactList.add(contact);
                    } // end for
                    return true;
                } // end if
                else
                    {
                    Log.d("JSON", "Failed to download file");
                    }
            } // end try
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return false;
        }// end doInBackground

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            // create a list adapter and assign it to list view. Update parsed JSON data into ListView
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList, R.layout.list_item,
                    new String[] { "name", "email", "mobile" }, new int[] { R.id.name, R.id.email, R.id.mobile });
            setListAdapter(adapter);// Bind  the contactList to the ListAdapter and display the data in the list view
        }
    }
}
