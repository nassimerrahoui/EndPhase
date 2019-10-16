package app;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.examples.pingpong.components.PingPongPlayer;


public class CVM extends AbstractCVM {
	/** URI of the two way port of the first player. */
	public final static String PING_PONG_URI_1 = "player1";
	/** URI of the two way port of the second player. */
	public final static String PING_PONG_URI_2 = "player2";
	/** URI of the inbound port of the first player. */
	public final static String PLAYER1_PING_PONG_INBOUND_PORT_URI = "player1ibpURI";
	/** URI of the inbound port of the second player. */
	public final static String PLAYER2_PING_PONG_INBOUND_PORT_URI = "player2ibpURI";
	/** URI of the outbound port of the first player. */
	public final static String PLAYER1_PING_PONG_DATA_OUTBOUND_PORT_URI = "player1dobpURI";
	/** URI of the inbound port of the first player. */
	public final static String PLAYER1_PING_PONG_DATA_INBOUND_PORT_URI = "player1dibpURI";
	/** URI of the outbound port of the second player. */
	public final static String PLAYER2_PING_PONG_DATA_OUTBOUND_PORT_URI = "player2dobpURI";
	/** URI of the inbound port of the second player. */
	public final static String PLAYER2_PING_PONG_DATA_INBOUND_PORT_URI = "player2dibpURI";
	/** URI of the two way port of the first player. */
	public final static String PLAYER1_PING_PONG_TWOWAY_PORT_URI = "player1twpURI";
	/** URI of the two way port of the second player. */
	public final static String PLAYER2_PING_PONG_TWOWAY_PORT_URI = "player2twpURI";

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		// --------------------------------------------------------------------
		// Creation phase
		// --------------------------------------------------------------------

		// A first player that initially has the service.
		String pp1URI = AbstractComponent.createComponent(PingPongPlayer.class.getCanonicalName(),
				new Object[] { PING_PONG_URI_1, true, PLAYER1_PING_PONG_INBOUND_PORT_URI,
						PLAYER2_PING_PONG_INBOUND_PORT_URI, PLAYER1_PING_PONG_DATA_OUTBOUND_PORT_URI,
						PLAYER1_PING_PONG_DATA_INBOUND_PORT_URI, PLAYER2_PING_PONG_DATA_OUTBOUND_PORT_URI,
						PLAYER2_PING_PONG_DATA_INBOUND_PORT_URI, PLAYER1_PING_PONG_TWOWAY_PORT_URI,
						PLAYER2_PING_PONG_TWOWAY_PORT_URI });
		this.toggleTracing(pp1URI);

		// A second player that is initially passive.
		String pp2URI = AbstractComponent.createComponent(PingPongPlayer.class.getCanonicalName(),
				new Object[] { PING_PONG_URI_2, false, PLAYER1_PING_PONG_INBOUND_PORT_URI,
						PLAYER2_PING_PONG_INBOUND_PORT_URI, PLAYER1_PING_PONG_DATA_OUTBOUND_PORT_URI,
						PLAYER1_PING_PONG_DATA_INBOUND_PORT_URI, PLAYER2_PING_PONG_DATA_OUTBOUND_PORT_URI,
						PLAYER2_PING_PONG_DATA_INBOUND_PORT_URI, PLAYER2_PING_PONG_TWOWAY_PORT_URI,
						PLAYER1_PING_PONG_TWOWAY_PORT_URI });
		this.toggleTracing(pp2URI);

		// --------------------------------------------------------------------
		// Deployment done
		// --------------------------------------------------------------------

		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM cvm = new CVM();
			cvm.startStandardLifeCycle(60000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
// -----------------------------------------------------------------------------
