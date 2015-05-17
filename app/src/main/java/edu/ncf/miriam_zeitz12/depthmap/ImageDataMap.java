package edu.ncf.miriam_zeitz12.depthmap;

import org.apache.http.annotation.Immutable;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by miriamzeitz on 5/16/15.
 */
public class ImageDataMap {

    private Map<String, String> dataMap;

    private final String NEAR_KEY = "near";
    private final String FAR_KEY = "far";
    private final String DATA_KEY = "imageData";
    private final String STRING_TYPE = "UTF-8";

    public ImageDataMap(String near, String far, byte[] data) throws UnsupportedEncodingException {
        dataMap = new HashMap<String, String>();
        dataMap.put(NEAR_KEY, near);
        dataMap.put(FAR_KEY, far);
        dataMap.put(DATA_KEY, new String(data, STRING_TYPE));

    }

    public Map<String, String> getDataMap(){
        return Collections.unmodifiableMap(dataMap);
    }
}
