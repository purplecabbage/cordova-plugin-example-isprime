
package com.kerrishotts.example.isprime;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Base64;

/**
* This class exposes methods in Cordova that can be called from JavaScript.
*/
public class IsPrime extends CordovaPlugin {

     /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback context from which we were invoked.
     */
    @SuppressLint("NewApi")
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("isPrime".equals(action)) {
            this.isPrime(args.getJSONObject(0), callbackContext);
        } else {
            return false;
        }
        return true;
    }

    private void isPrime(final JSONObject result, final CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    JSONArray factors = result.getJSONArray("factors");
                    long candidate = result.getLong("candidate");
                    long half = candidate / 2;

                    if (candidate == 2) {
                        result.put("isPrime", true);
                    } else {
                        // we are divisible by ourselves
                        factors.put(1);
                        // if we were only interested in testing primality, we could get by
                        // with sqrt(candidate), but since we want the factors, we have to
                        // go up to candidate/2.
                        for (long i = 2; i<=half; i++) {
                            if ((candidate % i) == 0) {
                                factors.put(i);
                            }
                        }
                        if (factors.length() == 1) {
                            // no factors, so we're prime
                            result.put("isPrime", true);
                            // since we're prime, clear out factors
                            factors.remove(0);
                        } else {
                            // we are divisible by ourselves as well
                            factors.put(candidate);
                        }
                    }
                    callbackContext.success(result);
                } catch (JSONException e) {
                    callbackContext.error("JSON Exception; check that the JS API is passing the right result object");
                }
            }
        });
    }
}