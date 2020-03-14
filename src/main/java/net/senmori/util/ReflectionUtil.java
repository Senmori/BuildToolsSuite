package net.senmori.util;

public class ReflectionUtil {
    public StackWalker getStackWalker() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    }
}
