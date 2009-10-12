package jrdesktop;

import java.io.FileDescriptor;
import java.security.Permission;

/**
 * SecurityMng.java
 * @author benbac
 */
public class SecurityMng extends SecurityManager {
    
    public SecurityMng() {}

    @Override
    public void checkAccept(String host, int port) {
        //super.checkAccept(host, port);
    }

    @Override
    public void checkAccess(Thread t) {
        //super.checkAccess(t);
    }

    @Override
    public void checkConnect(String host, int port) {
        //super.checkConnect(host, port);
    }

    @Override
    public void checkAwtEventQueueAccess() {
        //super.checkAwtEventQueueAccess();
    }

    @Override
    public void checkCreateClassLoader() {
        //super.checkCreateClassLoader();
    }

    @Override
    public void checkDelete(String file) {
        //super.checkDelete(file);
    }

    @Override
    public void checkExec(String cmd) {
        //super.checkExec(cmd);
    }

    @Override
    public void checkExit(int status) {
        //super.checkExit(status);
    }

    @Override
    public void checkListen(int port) {
        //super.checkListen(port);
    }

    @Override
    public void checkPermission(Permission perm) {
        //super.checkPermission(perm);
    }

    @Override
    public void checkPropertiesAccess() {
        //super.checkPropertiesAccess();
    }

    @Override
    public void checkPropertyAccess(String key) {
        //super.checkPropertyAccess(key);
    }

    @Override
    public void checkRead(FileDescriptor fd) {
        //super.checkRead(fd);
    }

    @Override
    public void checkRead(String file) {
        //super.checkRead(file);
    }

    @Override
    public void checkSecurityAccess(String target) {
        //super.checkSecurityAccess(target);
    }

    @Override
    public void checkSetFactory() {
        //super.checkSetFactory();
    }

    @Override
    public void checkSystemClipboardAccess() {
        //super.checkSystemClipboardAccess();
    }


    @Override
    public void checkWrite(FileDescriptor fd) {
        //super.checkWrite(fd);
    }

    @Override
    public void checkWrite(String file) {
        //super.checkWrite(file);
    }
    
    
}
