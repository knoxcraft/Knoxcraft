package org.knoxcraft.jetty.server;

public class UserSession
{
    private boolean isInstructor;
    public UserSession() {
    }
    public boolean isInstructor() {
        return isInstructor;
    }
    public void setInstructor(boolean isInstructor) {
        this.isInstructor = isInstructor;
    }
}
