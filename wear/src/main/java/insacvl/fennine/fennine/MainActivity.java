package insacvl.fennine.fennine;


import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Node mNode;
    GoogleApiClient mGoogleApiClient;
    private static final String START_SERVICE = "start-service";
    private static final String STOP_SERVICE = "stop-service";

    private boolean mResolvingError=false;

    String TAG = "Wear";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        Button button =  (Button)findViewById(R.id.start);
        Button button1= (Button)findViewById(R.id.stop);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToDevice("start service",START_SERVICE);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToDevice("stop service",STOP_SERVICE);
            }
        });

    }



    private void sendMessageToDevice(final String message, final String path) {
        Log.d(TAG, "sendMessageToDevice");

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(final NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                Log.d(TAG, "sendMessageToDevice: onResult");

                final List<Node> nodes = getConnectedNodesResult.getNodes();
                for (final Node node : nodes) {
                    if (node.isNearby()) {
                        Wearable.ChannelApi.openChannel(mGoogleApiClient, node.getId(), path).setResultCallback(new ResultCallback<ChannelApi.OpenChannelResult>() {
                            @Override
                            public void onResult(ChannelApi.OpenChannelResult openChannelResult) {
                                Log.d(TAG, "sendMessageToDevice: onResult: onResult");
                                final Channel channel = openChannelResult.getChannel();
                                channel.getOutputStream(mGoogleApiClient).setResultCallback(new ResultCallback<Channel.GetOutputStreamResult>() {
                                    @Override
                                    public void onResult(final Channel.GetOutputStreamResult getOutputStreamResult) {
                                        Log.d(TAG, "sendMessageToDevice: onResult: onResult: onResult");

                                        OutputStream outputStream = null;
                                        try {
                                            outputStream = getOutputStreamResult.getOutputStream();
                                            outputStream.write(message.getBytes());
                                            Log.d(TAG, "sendMessageToDevice: onResult: onResult: onResult: Message sent: " + message);
                                        } catch (final IOException ioexception) {
                                            Log.w(TAG, "sendMessageToDevice: onResult: onResult: onResult: Could not send message from smartwatch to given node.\n" +
                                                    "Node ID: " + channel.getNodeId() + "\n" +
                                                    "Path: " + channel.getPath() + "\n" +
                                                    "Error message: " + ioexception.getMessage() + "\n" +
                                                    "Error cause: " + ioexception.getCause());
                                        } finally {
                                            try {
                                                if (outputStream != null) {
                                                    outputStream.close();
                                                }
                                            } catch (final IOException ioexception) {
                                                Log.w(TAG, "sendMessageToDevice: onResult: onResult: onResult: Could not close Output Stream from smartwatch to given node.\n" +
                                                        "Node ID: " + channel.getNodeId() + "\n" +
                                                        "Path: " + channel.getPath() + "\n" +
                                                        "Error message: " + ioexception.getMessage() + "\n" +
                                                        "Error cause: " + ioexception.getCause());
                                            } finally {
                                                // Will call onChannelClosed
                                                channel.close(mGoogleApiClient);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        break;
                    }
                }
            }
        });
    }




    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }


    private void resolveNode() {

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                }
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        resolveNode();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

