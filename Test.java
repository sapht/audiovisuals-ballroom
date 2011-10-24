import processing.core.*;
import processing.opengl.*;
// import oscP5.*;
// import netP5.*;
// import CircleCollision.Ball;

/* :!javac -cp /usr/share/processing/lib/core.jar:/home/sapht/sketchbook/libraries/oscP5/library/oscP5.jar:. % && java -cp /usr/share/processing/lib/core.jar:/home/sapht/sketchbook/libraries/oscP5/library/oscP5.jar:. %:r */

public class Test extends PApplet
{
	public static void main ( String [] args )
	{
		new Ball(1, 2, 3);
		System.out.println("hello");
		//PApplet.main ( new String [] {
			//"CircleCollision"
		//});
	}
}
