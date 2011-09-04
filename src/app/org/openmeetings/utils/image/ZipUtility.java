package org.openmeetings.utils.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * ZipUtility.java
 * @author benbac
 */

public class ZipUtility {

    @SuppressWarnings("unchecked")
	public static <T> T byteArraytoObject(byte[] data) throws Exception {       
        ByteArrayInputStream bais = new ByteArrayInputStream(data);              
        ObjectInputStream ois = new ObjectInputStream(bais);
        ois.close();
        bais.close();        
        return (T)ois.readObject();                
     }     
    
    public static byte[] objecttoByteArray(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        bos.close();
        return bos.toByteArray();
    }            
}
