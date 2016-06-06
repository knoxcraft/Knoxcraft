package org.knoxcraft.turtle3d;

public class TurtleCommandException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TurtleCommandException() {
        super();
    }

    public TurtleCommandException(String message) {
        super(message);
    }

    public TurtleCommandException(Throwable cause) {
        super(cause);
    }

    public TurtleCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public TurtleCommandException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
