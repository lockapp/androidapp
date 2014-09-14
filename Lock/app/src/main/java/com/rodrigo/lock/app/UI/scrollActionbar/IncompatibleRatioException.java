package com.rodrigo.lock.app.UI.scrollActionbar;

/**
 * Created by Rodrigo on 15/08/2014.
 */
public class IncompatibleRatioException extends RuntimeException {

    public IncompatibleRatioException() {
        super("Can't perform Ken Burns effect on rects with distinct aspect ratios!");
    }
}