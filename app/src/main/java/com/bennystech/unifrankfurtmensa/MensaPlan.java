package com.bennystech.unifrankfurtmensa;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Nantero on 26.04.2015.
 */
public class MensaPlan {
    private Document doc;
    private String mensaName = "";
    private static final String TAG = "MensaPlan";

    public MensaPlan(Document doc_in) {
        if (doc_in != null) {
            try {
                this.doc = doc_in;
                ParseTitle();
            } catch (Exception e) {
                Log.e(TAG, "Error in initialization of MensaPlan class.");
            }
        }
    }

    private void ParseTitle () {
        Element mensaNameElement = doc.select("h1.csc-firstHeader").first();
        if (mensaNameElement != null) {
            if ( !mensaNameElement.hasText()) {
                mensaName = mensaNameElement.text();
            }
        }
    }


    public String getMensaTitle () {
        return mensaName;
    }
}
