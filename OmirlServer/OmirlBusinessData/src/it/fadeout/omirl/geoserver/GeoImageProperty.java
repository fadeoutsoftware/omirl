package it.fadeout.omirl.geoserver;


public class GeoImageProperty {

	public LonLatPoint m_oSW;
	public LonLatPoint m_oNE;
	public int m_iResolutionX;
	public int m_iResolutionY;
	
	
	public GeoImageProperty(LonLatPoint m_osw, LonLatPoint m_one, int resolutionX, int resolutionY) {
		super();
		m_oSW = m_osw;
		m_oNE = m_one;
		m_iResolutionX = resolutionX;
		m_iResolutionY = resolutionY;
	}

	public GeoImageProperty(String s) throws Exception {
		String as[] = s.split(";");
		if (as.length != 6) throw new Exception("Invalid string: " + s);
		
		try {
			m_oSW = new LonLatPoint(Double.valueOf(as[0]), Double.valueOf(as[1]));
			m_oNE = new LonLatPoint(Double.valueOf(as[2]), Double.valueOf(as[3]));
			m_iResolutionX = Integer.valueOf(as[4]);
			m_iResolutionY = Integer.valueOf(as[5]);
		} catch (NumberFormatException e) {
			throw new Exception("Number format exception in " + s);
		}
	}
	
	@Override
	public String toString() {
		return "" + m_oSW.m_dX +
				";" + m_oSW.m_dY +
				";" + m_oNE.m_dX +
				";" + m_oNE.m_dY +
				";" + m_iResolutionX +
				";" + m_iResolutionY;
	}
	
	public String GetFileName() {
		return "" + m_oSW.m_dX +
		"_" + m_oSW.m_dY +
		"_" + m_oNE.m_dX +
		"_" + m_oNE.m_dY +
		"_" + m_iResolutionX +
		"_" + m_iResolutionY;
} 	
}