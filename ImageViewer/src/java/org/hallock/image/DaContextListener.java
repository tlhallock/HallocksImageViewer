/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.image;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.hallock.images.Initialization;
import org.hallock.images.Initialization.InitializationResults;
import org.hallock.images.InitializationArgs;

/**
 *
 * @author thallock
 */

public class DaContextListener implements ServletContextListener {
    /** The servlet context with which we are associated. */
    private ServletContext context = null;

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        log("Context destroyed");
        this.context = null;
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        this.context = event.getServletContext();
        
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (ClassNotFoundException ex)
		{
			System.out.println("Error: unable to load driver class!");
			System.exit(1);
		}
		catch (IllegalAccessException ex)
		{
			System.out.println("Error: access problem while loading!");
			System.exit(2);
		} catch (InstantiationException ex)
		{
			System.out.println("Error: unable to instantiate driver!");
			System.exit(3);
		}
                
                InitializationResults results = Initialization.attemptToInitializeApp();
        
                log("Context initialized");
    }

    private void log(String message) {
        if (context != null) {
            context.log("MyServletContextListener: " + message);
        } else {
            System.out.println("MyServletContextListener: " + message);
        }
    }
}