package interfaces;

import exceptions.InitializationException;

import java.util.Properties;

/**
 * User: nyilmaz
 * Date: 10/31/12
 * Time: 9:52 PM
 */
public interface Initializable {


   public <T extends Initializable> T initialize(Properties... properties) throws InitializationException;

}
