/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.images;

/**
 *
 * @author thallock
 */
public class InitializationArgs {

    public static final String SETTINGS_FILE_ARG = "--settingsFile";
    public static final String ROOT_ARG = "--root";
    public static final String URL_ARG = "--url";
    public static final String ROOT_FILE_START = "--start";
    public static final String ACTION = "--action";

    private String settingsFile;
    private String root;
    private String start;
    private String action;
    private String url;

    public InitializationArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (i + 1 < args.length && args[i].equals(SETTINGS_FILE_ARG)) {
                settingsFile = args[i + 1];
            }
            if (i + 1 < args.length && args[i].equals(ROOT_ARG)) {
                root = args[i + 1];
            }
            if (i + 1 < args.length && args[i].equals(ROOT_FILE_START)) {
                start = args[i + 1];
            }
            if (i + 1 < args.length && args[i].equals(ACTION)) {
                action = args[i + 1];
            }
            if (i + 1 < args.length && args[i].equals(URL_ARG)) {
                url = args[i + 1];
            }
        }

        if (settingsFile == null) {
            throw new NullPointerException("No settings file!!!!");
        }
        if (action == null) {
            throw new NullPointerException("No action!!!!");
        }
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public String getAction()
    {
        return action;
    }
    
    public String getRoot()
    {
        return root;
    }
    
    public String getStart()
    {
        return start;
    }
    
    public String getSettingsFile()
    {
        return settingsFile;
    }
}
