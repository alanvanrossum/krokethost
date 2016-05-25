/**
 * 
 */

package nl.tudelft.kroket.log;

import static org.junit.Assert.*;

import org.junit.Test;

public class LoggerTest {

    /** The singleton reference to the Logger instance. */
    private static final Logger log = Logger.getInstance();

    /** Get current class name, used for logging output. */
    private final String className = this.getClass().getSimpleName();

    /**
     * Test method for
     * {@link nl.tudelft.kroket.log.Logger#setLevel(nl.tudelft.kroket.log.Logger.LogLevel)}
     * .
     */
    @Test
    public void testLevel() {
	log.setLevel(Logger.LogLevel.DEBUG);
	assertEquals(Logger.LogLevel.DEBUG, log.getLevel());
    }

    /**
     * Test method for {@link nl.tudelft.kroket.log.Logger#getInstance()}.
     */
    @Test
    public void testGetInstance() {
	assertFalse(Logger.getInstance() == null);
    }

    /**
     * Test method for
     * {@link nl.tudelft.kroket.log.Logger#info(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testInfo() {
	log.info(className, "Test info message.");
    }

    /**
     * Test method for
     * {@link nl.tudelft.kroket.log.Logger#debug(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testDebug() {
	log.info(className, "Test debug message.");
    }

    /**
     * Test method for
     * {@link nl.tudelft.kroket.log.Logger#error(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testError() {
	log.info(className, "Test error message.");
    }

}
