package android.notifications.odk.org.odknotifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.opendatakit.activities.BaseActivity;
import org.opendatakit.application.CommonApplication;
import org.opendatakit.consts.IntentConsts;
import org.opendatakit.database.service.UserDbInterface;
import org.opendatakit.exception.ServicesAvailabilityException;
import org.opendatakit.listener.DatabaseConnectionListener;
import org.opendatakit.logging.WebLogger;
import org.opendatakit.properties.CommonToolProperties;
import org.opendatakit.utilities.ODKFileUtils;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatabaseConnectionListener {

    private String appName = "android.notifications.odk.org.odknotifications";
    private DatabaseConnectionListener mIOdkDataDatabaseListener;
    private TextView name_tv;
    private String loggedInUsername;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan = new IntentIntegrator(MainActivity.this);
                qrScan.initiateScan();
            }
        });

        String appName = getIntent().getStringExtra(IntentConsts.INTENT_KEY_APP_NAME);
        if (appName == null) {
            appName = ODKFileUtils.getOdkDefaultAppName();
        }
        this.appName = appName;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        name_tv = (TextView) headerView.findViewById(R.id.name_tv);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
               String link = result.getContents();
               Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
               startActivity(browserIntent);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public String getActiveUser() {
        try {
            return getDatabase().getActiveUser(getAppName());
        } catch (ServicesAvailabilityException e) {
            WebLogger.getLogger(getAppName()).printStackTrace(e);
            return CommonToolProperties.ANONYMOUS_USER;
        }
    }


    public UserDbInterface getDatabase() {
        return ((CommonApplication) getApplication()).getDatabase();
    }

    public String getAppName() {
        return this.appName;
    }


    @Override
    public void databaseAvailable() {

        if (mIOdkDataDatabaseListener != null) {
            mIOdkDataDatabaseListener.databaseAvailable();
        }
        loggedInUsername = getActiveUser();
        Log.e("Success", "Database available" + loggedInUsername);
        if(loggedInUsername!=null)name_tv.setText(loggedInUsername);
    }

    @Override
    public void databaseUnavailable() {

        if (mIOdkDataDatabaseListener != null) {
            mIOdkDataDatabaseListener.databaseUnavailable();
        }
        Log.e("ERROR", "Database unavailable");

    }


    @Override
    protected void onResume() {
        super.onResume();
        ((CommonApplication) getApplication()).onActivityResume(this);
        ((CommonApplication) getApplication()).establishDoNotFireDatabaseConnectionListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        ((CommonApplication) getApplication()).fireDatabaseConnectionListener();
    }

    @Override
    protected void onPause() {
        ((CommonApplication) getApplication()).onActivityPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ((CommonApplication) getApplication()).onActivityDestroy(this);
        super.onDestroy();
    }
}


