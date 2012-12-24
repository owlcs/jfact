package uk.ac.manchester.cs.jfact.helpers;

@SuppressWarnings("javadoc")
public interface LogAdapter {
    LogAdapter printTemplate(Templates t, Object... strings);

    LogAdapter print(int i);

    LogAdapter println();

    LogAdapter print(double d);

    LogAdapter print(float f);

    LogAdapter print(boolean b);

    LogAdapter print(byte b);

    LogAdapter print(char c);

    LogAdapter print(short s);

    LogAdapter print(String s);

    LogAdapter print(Object s);

    LogAdapter print(Object... s);

    LogAdapter print(Object s1, Object s2);

    LogAdapter print(Object s1, Object s2, Object s3);

    LogAdapter print(Object s1, Object s2, Object s3, Object s4);

    LogAdapter print(Object s1, Object s2, Object s3, Object s4, Object s5);
}
