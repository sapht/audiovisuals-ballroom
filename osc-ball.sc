

// "SC_JACK_DEFAULT_OUTPUTS".setenv("system:playback_1,system:playback_2");
/*
l = { List.new } ! 2;
o = OSCresponderNode(s.addr, '/tr', { |time, resp, msg|
// msg[2] is the index
l[msg[2]].add(msg[3]);
}).add;
*/

/*
o.remove;  // when done, you need to clecleanan up the OSCresponderNode
*/

s = Server.local;
s.boot;

(
SynthDef("ball", {
	arg speed, size, x, y;
	//	SendTrig.kr(Impulse.kr(4), 0, bx);
	//	SendTrig.kr(Impulse.kr(4), 1, by);

	Out.ar (0, 
		Pan2.ar ( 
			BPF.ar (
				Saw.ar (
					(80-size) * 5 * (1.5-y), 0.1 ),
				1000,
				//				(80-size) * 10 - y * (80-size) * 5,
				0.5
			),
			x
		)
	);
	
}).send(s)
)


//s.sendMsg("/s_new", "ball", 4000, 1, 1);
//s.sendMsg("/n_set", 4000, "bx", 1);
//s.sendMsg("/n_set", 4000, "by", 150);
//s.sendMsg("/n_free", 4000);
