package interfaces;

import exceptions.InitializationException;

/**
 * User: nyilmaz
 * Date: 10/31/12
 * Time: 10:41 PM
 */
public interface LoadableConfiguration {

   public <T extends LoadableConfiguration> T initializeConfiguration() throws InitializationException;
}
