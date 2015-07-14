package edu.knoxcraft.turtle3d;

public class InvalidTurtleCodeException extends Exception
{
    public InvalidTurtleCodeException(String msg) {
        super(msg);
    }
    public InvalidTurtleCodeException(Exception cause) {
        super(cause);
    }

}
