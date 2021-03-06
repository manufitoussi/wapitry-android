package fr.fitoussoft.wapisdk.tasks;

import android.app.Activity;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;

import java.util.List;

import fr.fitoussoft.wapisdk.helpers.Log;
import fr.fitoussoft.wapisdk.requests.IRequestBase;
import fr.fitoussoft.wapisdk.requests.RequestByteArray;

/**
 * Created by emmanuel.fitoussi on 30/11/2014.
 */
public abstract class RequestPictureAsyncTask extends RequestAsyncTaskBase<byte[], byte[]> {

    public static final String PARAM_ID = "id";
    public static final String PARAM_SIZE = "size";

    public RequestPictureAsyncTask(Activity origin) {
        super(origin);
    }

    @Override
    protected Parameters createParams() {
        Parameters params = new Parameters();
        params.put(PARAM_ID, "");
        params.put(PARAM_SIZE, "");
        return params;
    }

    @Override
    protected IRequestBase<byte[]> createRequest(List<NameValuePair> params) {
        return new RequestByteArray<HttpGet>(wapiClient, wapiClient.getConfig().wapiLoadPicture, params, true, HttpGet.class);
    }

    @Override
    protected byte[] onResponseParsed(byte[] pictureBytes) {
        Log.d("pictureBytes=" + pictureBytes.length);
        return pictureBytes;
    }

    @Override
    protected abstract void onPostExecute(byte[] result);
}
