package net.senmori.util;

public final class BTSuiteSecurityManager extends SecurityManager {
    public static final BTSuiteSecurityManager INSTANCE = new BTSuiteSecurityManager();

    private BTSuiteSecurityManager() {}


    @Override
    protected Class<?>[] getClassContext() {
        return super.getClassContext();
    }
}
