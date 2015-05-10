package com.efei.student.tablet.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.efei.student.tablet.R;
import com.efei.student.tablet.student.BaseActivity;
import com.efei.student.tablet.utils.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class InformationActivity extends BaseActivity {

    private String mAuthKey;

    private Spinner mGradeSpinnerView;
    private EditText mRealNameView;
    private EditText mCityView;
    private EditText mSchoolView;
    private Button mFinishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Intent intent = getIntent();
        mAuthKey = intent.getStringExtra(intent.EXTRA_TEXT);
        setupViews();
    }

    private void setupViews() {
        mGradeSpinnerView = (Spinner) findViewById(R.id.grade_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grade, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGradeSpinnerView.setAdapter(adapter);

        mRealNameView = (EditText) findViewById(R.id.real_name);
        mCityView = (EditText) findViewById(R.id.city);
        mSchoolView = (EditText) findViewById(R.id.school);
        mFinishButton = (Button) findViewById(R.id.finish_register_button);
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptFinish();
            }
        });
    }

    private void attemptFinish() {

        // reset errors
        mRealNameView.setError(null);
        mCityView.setError(null);
        mSchoolView.setError(null);

        String real_name = mRealNameView.getText().toString().trim();
        String city = mCityView.getText().toString().trim();
        String school = mSchoolView.getText().toString().trim();
        String grade = mGradeSpinnerView.getSelectedItem().toString();

        boolean cancel = false;
        View focusView = null;

        if (real_name.isEmpty()) {
            mRealNameView.setError("请填写真实姓名！");
            cancel = true;
            focusView = mRealNameView;
        }

        if (!cancel && city.isEmpty()) {
            mCityView.setError("请填写城市！");
            cancel = true;
            focusView = mCityView;
        }

        if (!cancel && school.isEmpty()) {
            mSchoolView.setError("请填写学校！");
            cancel = true;
            focusView = mSchoolView;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // execute finishing register task
            JSONObject params = new JSONObject();
            try {
                params.put("real_name", real_name);
                params.put("city", city);
                params.put("school", school);
                params.put("grade", grade);
                FinishRegisterTask finishRegisterTask = new FinishRegisterTask();
                finishRegisterTask.execute(params);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class FinishRegisterTask extends AsyncTask<JSONObject, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            if (params.length == 0) {
                return null;
            }
            // Send the login request to the servers
            String response = NetUtils.post("/account/registrations/finish", params[0]);
            try {
                JSONObject jsonRes = new JSONObject(response);
                return jsonRes;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject retval) {
            try {
                // redirect to the course list page
                if (!(Boolean)retval.get("success")) {
                    // show the error message
                    switch (retval.getInt("code")) {
                        case -3:
                            Toast.makeText(getApplicationContext(), "帐号已存在", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "完成注册，正在跳转，请稍后", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity( new Intent(InformationActivity.this, InformationActivity.class));
                }
            } catch (Exception e) {

            }
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_information, menu);
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
}
