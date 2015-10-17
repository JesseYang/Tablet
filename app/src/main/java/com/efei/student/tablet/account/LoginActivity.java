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
import com.efei.student.tablet.student.BaseActivity;
import com.efei.student.tablet.student.ListActivity;
import com.efei.student.tablet.utils.NetUtils;
import com.efei.student.tablet.utils.TextUtils;
import com.efei.student.tablet.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {

    private TextView mAccountView;
    private EditText mPasswordView;
    private Button mLoginButton;
    private Toast mToast;

    private boolean no_network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupViews();
        no_network = false;
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
            ToastUtils.showToast(this, "请填写合法的手机号或邮箱号！");
            // mAccountView.setError("请填写合法的手机号或邮箱号！");
            focusView = mAccountView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!cancel && !isPasswordValid(password))
        {
            ToastUtils.showToast(this, "请填写长度为6~16的密码！");
            // mPasswordView.setError("请填写长度为6~16的密码！");
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the
            // first form field with an error.
            focusView.requestFocus();
        }
        else {
            ToastUtils.showToast(this, "正在登录，请稍后");
            // execute Login task
            JSONObject params = new JSONObject();
            try {
                params.put("email_mobile", email_mobile);
                params.put("password", password);
                LoginTask loginTask = new LoginTask(this);
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

        LoginActivity mActivity;

        public LoginTask(LoginActivity activity) {
            mActivity = activity;
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            if (mActivity.no_network) {
                return null;
            }
            if (params.length == 0) {
                return null;
            }
            // Send the login request to the servers
            String response = NetUtils.post(mActivity, "/account/sessions/tablet_login", params[0]);
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
                if (retval == null) {
                    ToastUtils.showToast(mActivity, "网络不给力，请检查网络设置");
                }
                // redirect to the course list page
                if (!no_network && !(Boolean)retval.get("success")) {
                    // show the error message
                    switch (retval.getInt("code")) {
                        case -1:
                            ToastUtils.showToast(mActivity, "帐号不存在");
                            break;
                        case -2:
                            ToastUtils.showToast(mActivity, "密码错误");
                            break;
                        default:
                            break;
                    }
                } else if (no_network) {
                    ToastUtils.showToast(mActivity, "登录成功，正在跳转");
                    startActivity(new Intent(LoginActivity.this, ListActivity.class));
                } else {
                    String auth_key = retval.getString("auth_key");
                    String student_server_id = retval.getString("student_server_id");
                    Boolean admin = retval.getBoolean("admin") || false;
                    String course_id_str = retval.getString("course_id_str");
                    String lesson_id_str = retval.getString("completed_lesson_id_str");
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear().commit();
                    editor.putString("auth_key", auth_key);
                    editor.putString("student_server_id", student_server_id);
                    editor.putBoolean("admin", admin);
                    editor.putString("course_id_str", course_id_str);
                    editor.putString("completed_lesson_id_str", lesson_id_str);
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
                    ToastUtils.showToast(mActivity, "登录成功，正在跳转");
                    Intent intent = new Intent(LoginActivity.this, ListActivity.class).
                            putExtra(Intent.EXTRA_TEXT, admin);
                    startActivity(intent);
                    /*
                    if (admin) {
                        startActivity(new Intent(LoginActivity.this, ManagementActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, ListActivity.class));
                    }
                    */
                }
            } catch (Exception e) {
                e.printStackTrace();
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
