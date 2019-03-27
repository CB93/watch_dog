package com.example.watch_dog;

import com.google.android.gms.maps.model.LatLng;

/**
 * <p>
 *     School class
 * </p>
 */
public class School {

    /**
     * <p>
     *     LatLng
     * </p>
     */
    private LatLng latLng;

    /**
     * <P>
     *     Webpage link
     * </P>
     */
    private String site;

    /**
     * <p>
     *     Constructor
     * </p>
     */
    public School(LatLng latLng, String site) {
        this.latLng = latLng;
        this.site = site;
    }

    /**
     * <p>
     *     getLatlng
     * </p>
     *
     * @return LatLng
     */
    public LatLng getLatLng() {
        return latLng;
    }

    /**
     * <p>
     *     getSite
     * </p>
     *
     * @return Site
     */
    public String getSite() {
        return site;
    }
}
