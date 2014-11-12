package org.droidplanner.android.fragments.mode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ox3dr.services.android.lib.drone.event.Event;
import com.ox3dr.services.android.lib.drone.property.VehicleMode;

import org.droidplanner.R;
import org.droidplanner.android.api.Drone;
import org.droidplanner.android.fragments.helpers.ApiListenerFragment;

/**
 * Implements the flight/apm mode panel description.
 */
public class FlightModePanel extends ApiListenerFragment{

    private final static IntentFilter eventFilter = new IntentFilter();
    static {
        eventFilter.addAction(Event.EVENT_CONNECTED);
        eventFilter.addAction(Event.EVENT_DISCONNECTED);
        eventFilter.addAction(Event.EVENT_VEHICLE_MODE);
        eventFilter.addAction(Event.EVENT_TYPE_UPDATED);
        eventFilter.addAction(Event.EVENT_FOLLOW_START);
        eventFilter.addAction(Event.EVENT_FOLLOW_STOP);
    }

    private final BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onModeUpdate(getDrone());
        }
    };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_flight_mode_panel, container, false);
	}

	@Override
	public void onApiConnected() {
		// Update the mode info panel based on the current mode.
		onModeUpdate(getDrone());
        getBroadcastManager().registerReceiver(eventReceiver, eventFilter);
	}

	@Override
	public void onApiDisconnected() {
        getBroadcastManager().unregisterReceiver(eventReceiver);
	}

	private void onModeUpdate(Drone dpApi) {
		// Update the info panel fragment
		dpApi = getDrone();
		Fragment infoPanel;
		if (dpApi == null || !dpApi.isConnected()) {
			infoPanel = new ModeDisconnectedFragment();
		} else {
            VehicleMode mode = dpApi.getState().getVehicleMode();
            if(mode == null){
                infoPanel = new ModeDisconnectedFragment();
            }
            else {

                switch (mode) {
                    case COPTER_RTL:
                    case PLANE_RTL:
                    case ROVER_RTL:
                        infoPanel = new ModeRTLFragment();
                        break;

                    case COPTER_AUTO:
                    case PLANE_AUTO:
                    case ROVER_AUTO:
                        infoPanel = new ModeAutoFragment();
                        break;

                    case COPTER_LAND:
                        infoPanel = new ModeLandFragment();
                        break;

                    case COPTER_LOITER:
                    case PLANE_LOITER:
                        infoPanel = new ModeLoiterFragment();
                        break;

                    case COPTER_STABILIZE:
                    case PLANE_STABILIZE:
                        infoPanel = new ModeStabilizeFragment();
                        break;

                    case COPTER_ACRO:
                        infoPanel = new ModeAcroFragment();
                        break;

                    case COPTER_ALT_HOLD:
                        infoPanel = new ModeAltholdFragment();
                        break;

                    case COPTER_CIRCLE:
                    case PLANE_CIRCLE:
                        infoPanel = new ModeCircleFragment();
                        break;

                    case COPTER_GUIDED:
                    case PLANE_GUIDED:
                    case ROVER_GUIDED:
                        if (dpApi.getFollowState().isEnabled()) {
                            infoPanel = new ModeFollowFragment();
                        } else {
                            infoPanel = new ModeGuidedFragment();
                        }
                        break;

                    case COPTER_DRIFT:
                        infoPanel = new ModeDriftFragment();
                        break;

                    case COPTER_SPORT:
                        infoPanel = new ModeSportFragment();
                        break;

                    case COPTER_POSHOLD:
                        infoPanel = new ModePosHoldFragment();
                        break;

                    default:
                        infoPanel = new ModeDisconnectedFragment();
                        break;
                }
            }
		}

		getChildFragmentManager().beginTransaction().replace(R.id.modeInfoPanel, infoPanel)
				.commit();
	}
}