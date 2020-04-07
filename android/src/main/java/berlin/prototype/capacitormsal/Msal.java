package berlin.prototype.capacitormsal;

import android.Manifest;
import android.nfc.Tag;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.microsoft.identity.client.AcquireTokenParameters;
import com.microsoft.identity.client.AcquireTokenSilentParameters;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.Prompt;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@NativePlugin(
        permissions={
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        }
)
public class Msal extends Plugin {
    final String TAG = "MyActivity";

    @PluginMethod()
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }

    @PluginMethod()
    public void getAccessToken(final PluginCall call) {

        String clientId = call.getString("clientId");
        String authority = call.getString("authority");
        String redirectUri = "msauth://com.munichre.aviationapp";
        List<String> scopes = null;
        try {
            scopes = call.getArray("scopes").toList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final List<String> finalScopes = scopes;

        PublicClientApplication.createSingleAccountPublicClientApplication(
                getContext(),
                R.raw.msal_config,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication pca) {
                        Log.d(TAG, "--- PCA Created");

                        AcquireTokenParameters parameters = new AcquireTokenParameters.Builder()
                                .startAuthorizationFromActivity(getActivity())
                                .withScopes(finalScopes)
                                .withPrompt(Prompt.LOGIN)
                                .withCallback(authenticationCallback(call))
                                .build();

                        pca.acquireToken(parameters);
                    }

                    @Override
                    public void onError(MsalException exception) {
                        // Application could not be created.
                        // Check Exception message for details.
                        Log.d(TAG, "--- createSingleAccountPublicClientApplication onError");
                        Log.d(TAG, String.valueOf(exception));
                    }
                }
        );
    }

    @PluginMethod()
    public void getAccessTokenSilently(final PluginCall call) {
        String clientId = call.getString("clientId");
        final String authority = call.getString("authority");
        String redirectUri = "msauth://com.munichre.aviationapp";

        List<String> scopes = null;
        try {
            scopes = call.getArray("scopes").toList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final List<String> finalScopes = scopes;

        PublicClientApplication.createSingleAccountPublicClientApplication(
                getContext(),
                R.raw.msal_config,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication pca) {
                        Log.d(TAG, "--- PCA Created");

                        AcquireTokenSilentParameters parameters = new AcquireTokenSilentParameters.Builder()
                                .withScopes(finalScopes)
                                .fromAuthority(authority)
                                .withCallback(authenticationCallback(call))
                                .build();

                        pca.acquireTokenSilentAsync(parameters);
                    }

                    @Override
                    public void onError(MsalException exception) {
                        // Application could not be created.
                        // Check Exception message for details.
                        Log.d(TAG, "Acquiretoken onError");
                        Log.d(TAG, String.valueOf(exception));
                    }
                }
        );
    }

    private AuthenticationCallback authenticationCallback(final PluginCall call) {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                final String TAG = "MyActivity";
                Log.d(TAG, "--- Acquiretoken onSuccess");
                Log.d(TAG, String.valueOf(authenticationResult));

                String accessToken = authenticationResult.getAccessToken();
                JSObject ret = new JSObject();
                ret.put("accessToken", accessToken);
                call.success(ret);
            }

            @Override
            public void onError(MsalException exception) {
                final String TAG = "MyActivity";
                Log.d(TAG, "--- Acquiretoken onError");
                Log.d(TAG, String.valueOf(exception));
                // Token request was unsuccessful, inspect the exception
                JSObject ret = new JSObject();
                ret.put("exception", exception);
                call.reject(ret.toString());
            }

            @Override
            public void onCancel() {
                final String TAG = "MyActivity";
                Log.d(TAG, "--- Acquiretoken onCancel");
            }
        };
    }


    @PluginMethod()
    public void signOut(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }
}
