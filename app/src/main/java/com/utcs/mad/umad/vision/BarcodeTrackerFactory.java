package com.utcs.mad.umad.vision;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Drew on 1/19/16.
 */
public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {

    private GraphicOverlay mGraphicOverlay;
    private VisionCallback visionCallback;

    public BarcodeTrackerFactory(GraphicOverlay graphicOverlay, VisionCallback visionCallback) {
        mGraphicOverlay = graphicOverlay;
        this.visionCallback = visionCallback;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay);
        return new GraphicTracker<>(mGraphicOverlay, graphic, visionCallback);
    }
}
