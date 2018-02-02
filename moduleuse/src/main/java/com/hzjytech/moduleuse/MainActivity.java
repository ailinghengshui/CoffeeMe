package com.hzjytech.moduleuse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hzjytech.scan.activity.CaptureActivity;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This module used to testing other module
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private LoginButton mLoginButtonStyle1;
    private AuthInfo mAuthInfo;
    private AuthListener mLoginListener=new AuthListener();

    private Button mCurrentClickedButton;

    private LogOutRequestListener mLogoutListener=new LogOutRequestListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult= (TextView) findViewById(R.id.tvResult);

        mAuthInfo=new AuthInfo(this,Constants.APP_KEY,Constants.REDIRECT_URL,Constants.SCOPE);

        mLoginButtonStyle1=(LoginButton)findViewById(R.id.login_button_style1);
        mLoginButtonStyle1.setWeiboAuthInfo(mAuthInfo, mLoginListener);
        mLoginButtonStyle1.setStyle(LoginButton.LOGIN_INCON_STYLE_2);
        mLoginButtonStyle1.setExternalOnClickListener(mButtonClickListener);

        final Button logoutButton= (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogoutAPI(MainActivity.this,Constants.APP_KEY,AccessTokenKeeper.readAccessToken(MainActivity.this)).logout(mLogoutListener);
            }
        });
      
    }
    
    public  void onClick(View v){
        switch (v.getId()){
            /**
             * This button used to test scan module
             */
            case R.id.btnScan:
                scan();
                break;
        }
    }

    private void scan() {
        Intent intent=new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode==RESULT_OK){
//            String scanResult=data.getStringExtra(CaptureActivity.SCAN_RESULT_KEY);
//            tvResult.setText(scanResult);
//        }

        if(mCurrentClickedButton!=null){
            if(mCurrentClickedButton instanceof LoginButton){
                ((LoginButton) mCurrentClickedButton).onActivityResult(requestCode,requestCode,data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class AuthListener implements WeiboAuthListener{

        @Override
        public void onComplete(Bundle bundle) {
            Oauth2AccessToken accessToken=Oauth2AccessToken.parseAccessToken(bundle);
            if(accessToken!=null&&accessToken.isSessionValid()){
                String date=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(accessToken.getExpiresTime()));
                String format=getString(R.string.weibosdk_demo_token_to_string_format_1);
                tvResult.setText(String.format(format, accessToken.getToken(), date));

                AccessTokenKeeper.writeAccessToken(getApplicationContext(),accessToken);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onCancel() {

            Toast.makeText(MainActivity.this,R.string.weibosdk_demo_toast_auth_canceled,Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener mButtonClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof Button){
                mCurrentClickedButton=(Button)v;
            }
        }
    };

    private class LogOutRequestListener implements RequestListener{

        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    Log.d("-------",obj.toString());
                    String value = obj.getString("result");

                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(MainActivity.this);
                        tvResult.setText("_logout_success");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            tvResult.setText("logout_failed");
        }
    }
}
