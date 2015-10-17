package com.efei.student.tablet.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.student.BaseActivity;
import com.efei.student.tablet.utils.NetUtils;
import com.efei.student.tablet.utils.TextUtils;
import com.efei.student.tablet.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity {

    private TextView mAccountView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupViews();
    }

    private void setupViews()
    {
        mAccountView = (TextView) findViewById(R.id.account);
        mPasswordView = (EditText) findViewById(R.id.password);
        // mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        findViewById(R.id.tv_go_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void attemptRegister() {
        // reset errors
        mAccountView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        // Store values at the time of the login attempt.
        String email_mobile = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();
        // String password_confirm = mPasswordConfirmView.getText().toString();

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

        /*
        // check that password is the same as password confirmation
        if (!cancel && password != password_confirm)
        {
            mPasswordConfirmView.setError("确认密码与密码不一致！");
            focusView = mPasswordView;
            cancel = true;
        }
        */

        if (cancel) {
            // There was an error; don't attempt login and focus the
            // first form field with an error.
            focusView.requestFocus();
        }
        else {
            // execute Register task
            JSONObject params = new JSONObject();
            try {
                params.put("email_mobile", email_mobile);
                params.put("password", password);
                params.put("password_confirm", password);
                RegisterTask registerTask = new RegisterTask(this);
                registerTask.execute(params);
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

    private class RegisterTask extends AsyncTask<JSONObject, Void, JSONObject> {

        RegisterActivity mActivity;

        public RegisterTask(RegisterActivity activity) {
            mActivity = activity;
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            if (params.length == 0) {
                return null;
            }
            // Send the login request to the servers
            String response = NetUtils.post(mActivity, "/account/registrations", params[0]);
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
                            ToastUtils.showToast(mActivity, "帐号已存在");
                            break;
                        default:
                            break;
                    }
                } else {
                    String auth_key = (String)retval.get("auth_key");
                    ToastUtils.showToast(mActivity, "注册成功，正在跳转，请稍后");
                    finish();
                    Intent intent = new Intent(RegisterActivity.this, InformationActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, auth_key);
                    startActivity(intent);
                }
            } catch (Exception e) {

            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
