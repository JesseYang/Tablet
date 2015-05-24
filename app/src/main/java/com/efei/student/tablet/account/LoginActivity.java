package com.efei.student.tablet.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.efei.student.tablet.R;
import com.efei.student.tablet.admin.ManagementActivity;
import com.efei.student.tablet.student.BaseActivity;
import com.efei.student.tablet.student.ListActivity;
import com.efei.student.tablet.utils.NetUtils;
import com.efei.student.tablet.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {

    private TextView mAccountView;
    private EditText mPasswordView;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupViews();

    }

    private void setupViews()
    {
        mAccountView = (EditText) findViewById(R.id.account);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.sign_in_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        findViewById(R.id.tv_go_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void attemptLogin() {
        // reset errors
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email_mobile = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (!isAccountValid(email_mobile))
        {
            mAccountView.setError("请填写合法的手机号或邮箱号！");
            focusView = mAccountView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!cancel && !isPasswordValid(password))
        {
            mPasswordView.setError("请填写长度为6~16的密码！");
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the
            // first form field with an error.
            focusView.requestFocus();
        }
        else {
            // execute Login task
            JSONObject params = new JSONObject();
            try {
                params.put("email_mobile", email_mobile);
                params.put("password", password);
                LoginTask loginTask = new LoginTask();
                loginTask.execute(params);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean isPasswordValid(String password) {
        return TextUtils.isValidatePassword(password);
    }

    private boolean isAccountValid(String account) {
        return TextUtils.isMobilePhoneNumber(account) || TextUtils.isEmail(account);
    }

    private class LoginTask extends AsyncTask<JSONObject, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            if (params.length == 0) {
                return null;
            }
            // Send the login request to the servers
            String response = NetUtils.post("/account/sessions/tablet_login", params[0]);
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
                        case -1:
                            Toast.makeText(getApplicationContext(), "帐号不存在", Toast.LENGTH_SHORT).show();
                            break;
                        case -2:
                            Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                } else {

                    String auth_key = retval.getString("auth_key");
                    String student_server_id = retval.getString("student_server_id");
                    Boolean admin = retval.getBoolean("admin") || false;
                    String course_id_str = retval.getString("course_id_str");
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear().commit();
                    editor.putString("auth_key", auth_key);
                    editor.putString("student_server_id", student_server_id);
                    editor.putBoolean("admin", admin);
                    editor.putString("course_id_str", course_id_str);
                    JSONArray study_ary = retval.getJSONArray("status");
                    JSONObject course_status;
                    for (int i = 0; i < study_ary.length(); i++) {
                        course_status = study_ary.getJSONObject(i);
                        editor.putString(course_status.getString("course_id"),
                                course_status.getString("lesson_id") + "," +
                                course_status.getString("video_id") + "," +
                                course_status.getString("time"));
                    }
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "登录成功，正在跳转，请稍后", Toast.LENGTH_SHORT).show();
                    if (admin) {
                        startActivity(new Intent(LoginActivity.this, ManagementActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, ListActivity.class));
                    }
                }
            } catch (Exception e) {
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
