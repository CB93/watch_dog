package com.example.watch_dog;

import java.util.List;

/**
 * Interface for onDirectFinderStart and Ondirect finder Success
 */

public interface DirectFinderListner {
        void onDirectFinderStart();
        void onDirectFinderSuccess(List<RouteFromStartToDest> route);
}
