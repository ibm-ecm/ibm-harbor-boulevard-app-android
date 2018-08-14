package com.futureworkshops.datacap.common.camera;

/**
 * Created by stelian on 24/10/2017.
 */

public class CameraParams {

    /**
     * The minimum area percentage (relative to the screen area )that the detected document
     * must have in order to be considered valid.
     */
    public static final float SMALLEST_AREA_PERCENTAGE = 0.15f;

    /**
     * The minimum margin required between the detection result and the screen boundaries.
     */
    public static final int MINIMUM_DETECTION_MARGIN = 25;

    /**
     * The aspect ratio required for the detection result.
     * <p> The aspect ratio comparison will use a default
     * tolerance valule.</p>
     */
    public static final float DOCUMENT_ASPECT_RATIO = -1f;
}
