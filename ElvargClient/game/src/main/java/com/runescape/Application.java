package com.runescape;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;


public class Application implements AppletStub {

    /**
     * The host address
     */
    private final String host = "127.0.0.1";

    /**
     * The parameters of the client.
     */
    private final Map<String, String> map = new HashMap<>();

    /**
     * The main entry point of the current application.
     *
     * @param args
     *            The command line arguments.
     * @throws IOException
     * @throws MalformedURLException
     */
    public static void main(final String[] args) throws Exception {
        final Application applet = new Application();
        applet.initialize();

        final Class<?> client = Class.forName("com.runescape.Client");
        final Applet instance = (Applet) client.getConstructor().newInstance();

        final JFrame frame = new JFrame("RSPSApp");
        frame.add(instance);
        frame.setVisible(true);
        frame.setSize(781, 541);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        client.getSuperclass().getMethod("setStub", AppletStub.class).invoke(instance, applet);
        client.getMethod("init").invoke(instance);
        client.getMethod("start").invoke(instance);
    }

    /**
     * Reads the parameters text file, and stores the parameters.
     *
     * @throws IOException
     * @throws MalformedURLException
     */
    private void initialize() throws MalformedURLException, IOException {

    }

    /*
     * (non-Javadoc)
     *
     * @see java.applet.AppletStub#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(final String paramName) {
        return map.get(paramName);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.applet.AppletStub#getDocumentBase()
     */
    @Override
    public URL getDocumentBase() {
        try {
            return new URL("http://" + host);
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.applet.AppletStub#getCodeBase()
     */
    @Override
    public URL getCodeBase() {
        try {
            return new URL("http://" + host);
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.applet.AppletStub#isActive()
     */
    @Override
    public boolean isActive() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.applet.AppletStub#getAppletContext()
     */
    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.applet.AppletStub#appletResize(int, int)
     */
    @Override
    public void appletResize(final int width, final int height) {
    }

}