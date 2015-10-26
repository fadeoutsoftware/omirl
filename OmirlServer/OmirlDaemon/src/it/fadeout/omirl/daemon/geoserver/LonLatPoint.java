package it.fadeout.omirl.daemon.geoserver;

/** punto lat/lon */
public class LonLatPoint {
	
	public double m_dX;
	public double m_dY;
	
	
	public LonLatPoint() {
		// TODO Auto-generated constructor stub
	}


	public LonLatPoint(double x, double y) {
		super();
		m_dX = x;
		m_dY = y;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LonLatPoint) {
			LonLatPoint oPoint = (LonLatPoint) obj;
			return (m_dX == oPoint.m_dX) && (m_dY == oPoint.m_dY);
		} else return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return "Lon: " + m_dX + " - Lat: " + m_dY;
	}
}
