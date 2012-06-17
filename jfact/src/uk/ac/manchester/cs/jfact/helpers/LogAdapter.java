package uk.ac.manchester.cs.jfact.helpers;

public interface LogAdapter {
	void printTemplate(Templates t, Object... strings);

	void print(int i);

	void println();

	void print(double d);

	void print(float f);

	void print(boolean b);

	void print(byte b);

	void print(char c);

	void print(short s);

	void print(String s);

	void print(Object s);

	void print(Object... s);

	void print(Object s1, Object s2);

	void print(Object s1, Object s2, Object s3);

	void print(Object s1, Object s2, Object s3, Object s4);

	void print(Object s1, Object s2, Object s3, Object s4, Object s5);
}
