package nl.tudelft.kroket.user;

import java.io.DataOutputStream;
import java.net.Socket;

public class RegisteredUser extends User {

    /** The player's name. */
    private String name;
    
    public RegisteredUser(Socket socket, DataOutputStream stream, String name) {
	super(socket, stream);

	setName(name);
    
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    public String toString() {
	return String.format("User %s - %s - %s", getName(), getSocket()
		.getRemoteSocketAddress(), getType());
    }

}
