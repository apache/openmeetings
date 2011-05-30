package org.openmeetings.app.documents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.openmeetings.utils.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class LibraryChartLoader {

    private static final Logger log = new Logger();

    private static final String fileExt = ".xchart";

    private static LibraryChartLoader instance;

    private LibraryChartLoader() {
    }

    public static synchronized LibraryChartLoader getInstance() {
        if (instance == null) {
            instance = new LibraryChartLoader();
        }
        return instance;
    }

    public ArrayList loadChart(String filePath, String fileName) {
        try {
            LinkedHashMap lMap = new LinkedHashMap();
            String filepathComplete = filePath + fileName + fileExt;

            log.error("filepathComplete: " + filepathComplete);

            XStream xStream = new XStream(new XppDriver());
            xStream.setMode(XStream.NO_REFERENCES);

            BufferedReader reader = new BufferedReader(new FileReader(
                    filepathComplete));
            String xmlString = "";
            while (reader.ready()) {
                xmlString += reader.readLine();
            }

            // lMap = (LinkedHashMap) xStream.fromXML(xmlString);
            ArrayList lMapList = (ArrayList) xStream.fromXML(xmlString);

            return lMapList;
        } catch (Exception err) {
            log.error(err);
        }

        return null;

    }

}
