import processing.core.*;
import processing.opengl.*;
import oscP5.*;
import netP5.*;
// import CircleCollision.Ball;

/* :!javac -cp /usr/share/processing/lib/core.jar:/home/sapht/sketchbook/libraries/oscP5/library/oscP5.jar:. % && java -cp /usr/share/processing/lib/core.jar:/home/sapht/sketchbook/libraries/oscP5/library/oscP5.jar:. %:r */

public class CircleCollision extends PApplet
{
	OscP5 osc;
	NetAddress s;

	int scrn_w;
	int scrn_h;

	Ball[] balls =  { 
		new Ball(100, 100, 30)
		//new Ball(200, 220, 40),
		//new Ball(200, 320, 40),
		//new Ball(200, 420, 40),
		//new Ball(200, 520, 40),
		////new Ball(200, 620, 40),
		//new Ball(200, 720, 40),
		//new Ball(300, 140, 40)
		//new Ball(200, 500, 20),
		//new Ball(300, 200, 30) 
	};

	PVector[] vels = { 
		new PVector( 0.3f, -0.4f)
		//new PVector( 0.1f, -0.4f), 
		//new PVector( 0.2f, -0.3f), 
		//new PVector( 0.3f, -0.2f), 
		//new PVector( 0.4f, -0.1f), 
		//new PVector( 0.5f, -0.0f), 
		//new PVector( 0.6f, -0.9f), 
		//new PVector( 0.7f, -0.2f), 
		//new PVector( 0.0f, -0.2f) 
		//new PVector(1.65f, 1.42f),
		//new PVector(3.65f, .42f) 
	};

	public void setup() {
		frameRate(100);
		scrn_w = screen.width;
		scrn_h = screen.height;
		//scrn_w = 640;
		//scrn_h = 480;
		size(scrn_w, scrn_h, OPENGL);

		osc = new OscP5(this, 12000);
		s   = new NetAddress("127.0.0.1", 57110);

		for ( int i=0; i < balls.length; i++ ) {
			OscMessage msg = new OscMessage("/s_new");
			msg.add("ball");
			msg.add(4000 + i);
			msg.add(0);
			msg.add(1);
			msg.add("size");
			msg.add(balls[i].r);
			osc.send(msg, s);
		}

		/* smooth(); */
		// noStroke();
	}
	public void stop () {
		for ( int i=0; i < balls.length; i++ ) {
			OscMessage msg = new OscMessage("/n_free");
			msg.add(4000 + i);
			osc.send(msg, s);
		}
	}

	public void draw() {
		background(0);
		fill(204);
		for (int i=0; i < balls.length; i++){
			balls[i].x += vels[i].x;
			balls[i].y += vels[i].y;
		
			double speed = Math.sqrt(
						Math.pow(vels[i].x, 2) + 
						Math.pow(vels[i].y, 2) );
			OscMessage msg = new OscMessage("/n_set");
			msg.add(4000 + i);
			msg.add("speed");
			msg.add((float) speed);
			osc.send(msg, s);

			double y = (double) ((balls[i].y - (scrn_h/2)) / (scrn_h/2));
			msg = new OscMessage("/n_set");
			msg.add(4000 + i);
			msg.add("y");
			msg.add((float) y);
			osc.send(msg, s);
			println("Sending b[" + i + "] y: " + y );

			double x = (double) ((balls[i].x - (scrn_w/2)) / (scrn_w/2));
			msg = new OscMessage("/n_set");
			msg.add(4000 + i);
			msg.add("x");
			msg.add((float) x);
			osc.send(msg, s);

			ellipse(balls[i].x, balls[i].y, balls[i].r*2, balls[i].r*2);
			checkBoundaryCollision(balls[i], vels[i]);
		}
		checkObjectCollision(balls, vels);
	}

	void checkObjectCollision(Ball[] b, PVector[] v){
		int i;
		int j;
		PVector bVect = new PVector();
		Ball[] bTemp     = { new Ball(),     new Ball()             };
		PVector[] vTemp  = { new PVector(),  new PVector()          };
		PVector[] vFinal = { new PVector(),  new PVector()          };
		Ball[] bFinal    = { new Ball(),     new Ball()             };

		for ( i = 0; i < b.length; i++ ) {
			for ( j = i+1; j < b.length; j++ ) {
				if ( i == j ) continue;


				// get distances between the balls components
				bVect.x = b[j].x - b[i].x;
				bVect.y = b[j].y - b[i].y;

				// calculate magnitude of the vector separating the balls
				float bVectMag = sqrt(bVect.x * bVect.x + bVect.y * bVect.y);
				if (bVectMag < b[i].r + b[j].r) {
					println ( "Found collision: " + i + ":" + j );
					// get angle of bVect
					float theta  = atan2(bVect.y, bVect.x);
					// precalculate trig values
					float sine = sin(theta);
					float cosine = cos(theta);

					/* bTemp will hold rotated ball positions. You 
						 just need to worry about bTemp[1] position*/

					/* b[j]'s position is relative to b[i]'s
						 so you can use the vector between them (bVect) as the 
						 reference point in the rotation expressions.
						 bTemp[0].x and bTemp[0].y will initialize
						 automatically to 0.0, which is what you want
						 since b[j] will rotate around b[i] */
					bTemp[1].x  = cosine * bVect.x + sine * bVect.y;
					bTemp[1].y  = cosine * bVect.y - sine * bVect.x;

					// rotate Temporary velocities
					vTemp[0].x  = cosine * v[i].x + sine * v[i].y;
					vTemp[0].y  = cosine * v[i].y - sine * v[i].x;
					vTemp[1].x  = cosine * v[j].x + sine * v[j].y;
					vTemp[1].y  = cosine * v[j].y - sine * v[j].x;

					/* Now that velocities are rotated, you can use 1D
						 conservation of momentum equations to calculate 
						 the final velocity along the x-axis. */
					// final rotated velocity for b[i]
					vFinal[0].x = ((b[i].m - b[j].m) * vTemp[0].x + 2 * b[j].m * 
							vTemp[1].x) / (b[i].m + b[j].m);
					vFinal[0].y = vTemp[0].y;
					// final rotated velocity for b[i]
					vFinal[1].x = ((b[j].m - b[i].m) * vTemp[1].x + 2 * b[i].m * 
							vTemp[0].x) / (b[i].m + b[j].m);
					vFinal[1].y = vTemp[1].y;

					println ( vTemp[0].x  );
					println ( vFinal[0].x );

					// hack to avoid clumping
					bTemp[0].x += vFinal[0].x;
					bTemp[1].x += vFinal[1].x;

					/* Rotate ball positions and velocities back
						 Reverse signs in trig expressions to rotate 
						 in the opposite direction */
					// rotate balls
					bFinal[0].x = cosine * bTemp[0].x - sine * bTemp[0].y;
					bFinal[0].y = cosine * bTemp[0].y + sine * bTemp[0].x;
					bFinal[1].x = cosine * bTemp[1].x - sine * bTemp[1].y;
					bFinal[1].y = cosine * bTemp[1].y + sine * bTemp[1].x;

					// update balls to screen position
					b[j].x = b[i].x + bFinal[1].x;
					b[j].y = b[i].y + bFinal[1].y;
					b[i].x = b[i].x + bFinal[0].x;
					b[i].y = b[i].y + bFinal[0].y;

					// update velocities
					v[i].x = cosine * vFinal[0].x - sine * vFinal[0].y;
					println("Updating " + i +".x to " + v[i].x );
					v[i].y = cosine * vFinal[0].y + sine * vFinal[0].x;
					println("Updating " + i +".y to " + v[i].y );
					v[j].x = cosine * vFinal[1].x - sine * vFinal[1].y;
					println("Updating " + j +".x to " + v[j].x );
					v[j].y = cosine * vFinal[1].y + sine * vFinal[1].x;
					println("Updating " + j +".y to " + v[j].y );
				}
			}
		}

		/* 
		// get distances between the balls components
		PVector bVect = new PVector();
		bVect.x = b[1].x - b[0].x;
		bVect.y = b[1].y - b[0].y;

		// calculate magnitude of the vector separating the balls
		float bVectMag = sqrt(bVect.x * bVect.x + bVect.y * bVect.y);
		if (bVectMag < b[0].r + b[1].r){
			// get angle of bVect
			float theta  = atan2(bVect.y, bVect.x);
			// precalculate trig values
			float sine = sin(theta);
			float cosine = cos(theta);

			/* bTemp will hold rotated ball positions. You 
				 just need to worry about bTemp[1] position/
			Ball[] bTemp = {  
				new Ball(), new Ball()          };

			/* b[1]'s position is relative to b[0]'s
				 so you can use the vector between them (bVect) as the 
				 reference point in the rotation expressions.
				 bTemp[0].x and bTemp[0].y will initialize
				 automatically to 0.0, which is what you want
				 since b[1] will rotate around b[0] /
			bTemp[1].x  = cosine * bVect.x + sine * bVect.y;
			bTemp[1].y  = cosine * bVect.y - sine * bVect.x;

			// rotate Temporary velocities
			PVector[] vTemp = { 
				new PVector(), new PVector()         };
			vTemp[0].x  = cosine * v[0].x + sine * v[0].y;
			vTemp[0].y  = cosine * v[0].y - sine * v[0].x;
			vTemp[1].x  = cosine * v[1].x + sine * v[1].y;
			vTemp[1].y  = cosine * v[1].y - sine * v[1].x;

			/* Now that velocities are rotated, you can use 1D
				 conservation of momentum equations to calculate 
				 the final velocity along the x-axis. /
			PVector[] vFinal = {  
				new PVector(), new PVector()          };
			// final rotated velocity for b[0]
			vFinal[0].x = ((b[0].m - b[1].m) * vTemp[0].x + 2 * b[1].m * 
					vTemp[1].x) / (b[0].m + b[1].m);
			vFinal[0].y = vTemp[0].y;
			// final rotated velocity for b[0]
			vFinal[1].x = ((b[1].m - b[0].m) * vTemp[1].x + 2 * b[0].m * 
					vTemp[0].x) / (b[0].m + b[1].m);
			vFinal[1].y = vTemp[1].y;

			// hack to avoid clumping
			bTemp[0].x += vFinal[0].x;
			bTemp[1].x += vFinal[1].x;

			/* Rotate ball positions and velocities back
				 Reverse signs in trig expressions to rotate 
				 in the opposite direction /
			// rotate balls
			Ball[] bFinal = { 
				new Ball(), new Ball()         };
			bFinal[0].x = cosine * bTemp[0].x - sine * bTemp[0].y;
			bFinal[0].y = cosine * bTemp[0].y + sine * bTemp[0].x;
			bFinal[1].x = cosine * bTemp[1].x - sine * bTemp[1].y;
			bFinal[1].y = cosine * bTemp[1].y + sine * bTemp[1].x;

			// update balls to screen position
			b[1].x = b[0].x + bFinal[1].x;
			b[1].y = b[0].y + bFinal[1].y;
			b[0].x = b[0].x + bFinal[0].x;
			b[0].y = b[0].y + bFinal[0].y;

			// update velocities
			v[0].x = cosine * vFinal[0].x - sine * vFinal[0].y;
			v[0].y = cosine * vFinal[0].y + sine * vFinal[0].x;
			v[1].x = cosine * vFinal[1].x - sine * vFinal[1].y;
			v[1].y = cosine * vFinal[1].y + sine * vFinal[1].x;
		}
		*/
	}

	void checkBoundaryCollision(Ball ball, PVector vel) {
		if (ball.x > width-ball.r) {
			ball.x = width-ball.r;
			vel.x *= -1;
		} 
		else if (ball.x < ball.r) {
			ball.x = ball.r;
			vel.x *= -1;
		} 
		else if (ball.y > height-ball.r) {
			ball.y = height-ball.r;
			vel.y *= -1;
		} 
		else if (ball.y < ball.r) {
			ball.y = ball.r;
			vel.y *= -1;
		}
	}

	public static void main ( String [] args )
	{
		PApplet.main ( new String [] {
			"CircleCollision"
		});
	}
}
